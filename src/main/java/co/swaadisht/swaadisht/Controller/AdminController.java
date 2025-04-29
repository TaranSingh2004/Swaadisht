package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.CategoryServices;
import co.swaadisht.swaadisht.Services.ImageService;
import co.swaadisht.swaadisht.Services.OrderService;
import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.Category;
import co.swaadisht.swaadisht.entities.ProductOrder;
import co.swaadisht.swaadisht.entities.User;
import co.swaadisht.swaadisht.util.CommonUtil;
import co.swaadisht.swaadisht.util.OrderStatus;
import com.cloudinary.Cloudinary;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.apache.hc.client5.http.psl.PublicSuffixList;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryServices categoryService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private Cloudinary cloudinary;

    Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommonUtil commonUtil;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if(p!=null){
            String email= p.getName();
            User user = userService.getUserByEmail(email);
            m.addAttribute("user", user);
        }
        List<Category> list = categoryService.getAllActiveCategory();
        m.addAttribute("categorys", list);
    }

    @GetMapping("/")
    public String index() {
        return "admin/index";
    }
//
    @GetMapping("/category")
    public String category(Model m){
        m.addAttribute("category", categoryService.getAllCategory());
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category,
                               @RequestParam("file") MultipartFile file,
                               HttpSession session) {

        if (file.isEmpty()) {
            session.setAttribute("errorMsg", "Please select an image file");
            return "redirect:/admin/category";
        }

        if (categoryService.existCategory(category.getName())) {
            session.setAttribute("errorMsg", "Category Name already exists");
            return "redirect:/admin/category";
        }

        try {
            // Create upload options using HashMap
            Map<String, Object> uploadOptions = new HashMap<>();
            uploadOptions.put("public_id", "category_" + UUID.randomUUID());
            uploadOptions.put("folder", "category_images");

            // Upload to Cloudinary
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);

            // Set the URL in the category
            category.setCategoryImage((String) uploadResult.get("secure_url"));

            // Save the category
            Category savedCategory = categoryService.saveCategory(category);

            if (savedCategory == null) {
                session.setAttribute("errorMsg", "Not saved! Internal server error");
            } else {
                session.setAttribute("succMsg", "Saved successfully");
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session){
        boolean deleteCategory=categoryService.deleteCategory(id);
        if(deleteCategory){
            session.setAttribute("succMsg", "category deleted successfully");
        } else {
            session.setAttribute("errMsg", "something wrong on server");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/editCategory/{id}")
    public String editCategory(@PathVariable int id, Model m){
        m.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/editCategory";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category,
                                 @RequestParam("file") MultipartFile file,
                                 HttpSession session) {

        try {
            // 1. Get existing category with null check
            Category oldCategory = categoryService.getCategoryById(category.getId());
            if (oldCategory == null) {
                session.setAttribute("errorMsg", "Category not found");
                return "redirect:/admin/category";
            }

            // 2. Handle image upload to Cloudinary
            String imageUrl = oldCategory.getCategoryImage(); // Keep old image by default

            if (!file.isEmpty()) {
                // Delete old image from Cloudinary if exists
                if (oldCategory.getCategoryImage() != null) {
                    String publicId = extractPublicId(oldCategory.getCategoryImage());
                    cloudinary.uploader().destroy(publicId, Collections.emptyMap());
                }

                // Upload new image with same options as saveCategory
                Map<String, Object> uploadOptions = new HashMap<>();
                uploadOptions.put("public_id", "category_" + UUID.randomUUID());
                uploadOptions.put("folder", "category_images");

                Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
                imageUrl = (String) uploadResult.get("secure_url");
            }

            // 3. Update category details
            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setCategoryImage(imageUrl);

            // 4. Save to database
            Category updatedCategory = categoryService.saveCategory(oldCategory);

            if (updatedCategory != null) {
                session.setAttribute("succMsg", "Category updated successfully!");
            } else {
                session.setAttribute("errorMsg", "Failed to update category");
            }

            return "redirect:/admin/editCategory/" + category.getId();

        } catch (Exception e) {
            session.setAttribute("errorMsg", "Error updating category: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/editCategory/" + category.getId();
        }
    }

    private String extractPublicId(String imageUrl) {
        // Example URL: https://res.cloudinary.com/demo/image/upload/v123/category_images/category_123.jpg
        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1];
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    @GetMapping("/orders")
    public String getAllOrders(Model m, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize){
        Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
        m.addAttribute("orders", page.getContent());

        m.addAttribute("pageNo", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

        m.addAttribute("srch", false);
        return "/admin/orders";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) throws MessagingException, UnsupportedEncodingException {
        String status = null;
        for(OrderStatus orderStatus : OrderStatus.values()){
            if(orderStatus.getId().equals(st)){  // Compare with getId() instead of direct enum comparison
                status = orderStatus.getName();
                break;
            }
        }

        if(status == null) {
            session.setAttribute("errorMsg", "Invalid status value");
            return "redirect:/admin/orders";
        }

        ProductOrder updatedOrder = orderService.updateOrderStatus(id, status);

        try {
            if(updatedOrder != null){
                commonUtil.sendMailForProductOrder(updatedOrder, status);
                session.setAttribute("succMsg", "Status Updated");
            } else {
                session.setAttribute("errorMsg", "Status not updated");
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Status updated but email failed to send");
            logger.error("Error sending email notification", e);
        }

        return "redirect:/admin/orders";
    }

    @GetMapping("/search-order")
    public String searchProduct(@RequestParam String orderId, Model m, HttpSession session,
                                @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        if (orderId != null && !orderId.trim().isEmpty()) {
            try {
                ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());
                if (order == null) {
                    session.setAttribute("errorMsg", "Order not found with ID: " + orderId);
                    m.addAttribute("orderDtls", null);
                } else {
                    // Ensure all relationships are loaded
                    Hibernate.initialize(order.getOrderItems());
                    order.getOrderItems().forEach(item -> {
                        Hibernate.initialize(item.getProduct());
                        Hibernate.initialize(item.getSelectedSize());
                        Hibernate.initialize(item.getSelectedIngredients());
                        Hibernate.initialize(item.getSelectedToppings());
                    });
                    m.addAttribute("orderDtls", order);
                }
                m.addAttribute("srch", true);
            } catch (Exception e) {
                session.setAttribute("errorMsg", "Error searching order: " + e.getMessage());
                m.addAttribute("orderDtls", null);
                m.addAttribute("srch", true);
            }
        } else {
            Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
            m.addAttribute("orders", page);
            m.addAttribute("srch", false);
            m.addAttribute("pageNo", page.getNumber());
            m.addAttribute("pageSize", pageSize);
            m.addAttribute("totalElements", page.getTotalElements());
            m.addAttribute("totalPages", page.getTotalPages());
            m.addAttribute("isFirst", page.isFirst());
            m.addAttribute("isLast", page.isLast());
        }
        return "/admin/orders";
    }
}
