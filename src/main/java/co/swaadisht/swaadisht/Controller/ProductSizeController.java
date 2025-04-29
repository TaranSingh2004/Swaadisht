package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.ProductService;
import co.swaadisht.swaadisht.Services.ProductSizeService;
import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.Category;
import co.swaadisht.swaadisht.entities.Product;
import co.swaadisht.swaadisht.entities.ProductSize;
import co.swaadisht.swaadisht.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class ProductSizeController {

    @Autowired
    private ProductSizeService sizeService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if(p!=null){
            String email= p.getName();
            User user = userService.getUserByEmail(email);
            m.addAttribute("user", user);
        }
    }

    @GetMapping("/productSize")
    public String listProductSizes(Model model) {
        // Use a repository method that fetches sizes with their products
        List<ProductSize> productSizes = sizeService.getAllActiveProductSize();
        model.addAttribute("productSize", productSizes);
        model.addAttribute("products", productService.getAllActiveProducts());
        return "admin/productSize/productSize";
    }

    @PostMapping("/saveSize")
    public String saveSize(@ModelAttribute ProductSize productSize,
                           @RequestParam(required = false) List<Integer> productIds,
                           HttpSession session) {
        try {
            // Clear existing products first
            if (productSize.getProducts() == null) {
                productSize.setProducts(new ArrayList<>());
            } else {
                productSize.getProducts().clear();
            }

            // Associate with selected products
            if (productIds != null && !productIds.isEmpty()) {
                List<Product> products = productService.getProductsByIds(productIds);
                if (products.isEmpty()) {
                    session.setAttribute("errorMsg", "No valid products found with the provided IDs");
                    return "redirect:/admin/productSize";
                }

                // Update both sides of the relationship
                for (Product product : products) {
                    productSize.addProduct(product); // Using our helper method
                }
            }

            ProductSize savedSize = sizeService.saveProductSize(productSize);
            if (savedSize == null) {
                session.setAttribute("errorMsg", "Not saved! Internal server error");
            } else {
                session.setAttribute("succMsg", "Size saved successfully with " + savedSize.getProducts().size() + " products");
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/productSize";
    }

    @GetMapping("/deleteProductSize/{id}")
    public String deleteProductSize(@PathVariable int id, HttpSession session) {
        boolean deleteProductSize = sizeService.deleteProductSize(id);
        if(deleteProductSize){
            session.setAttribute("succMsg", "Size deleted succesfully");
        } else {
            session.setAttribute("errMsg", "something wrong on server");
        }
        return "redirect:/admin/productSize";
    }

    @GetMapping("/editProductSize/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        model.addAttribute("productSize", sizeService.getProductSizeById(id));
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("assignedProducts", sizeService.getProductsBySizeId(id));
        return "admin/productSize/editProductSize";
    }

    @PostMapping("/updateProductSize")
    public String updateProductSize(@ModelAttribute ProductSize productSize,
                                    @RequestParam(required = false) List<Integer> productIds,
                                    HttpSession session){
        try {
            // Update product associations
            List<Product> products = productIds != null ?
                    productService.getProductsByIds(productIds) : new ArrayList<>();
            productSize.setProducts(products);

            ProductSize updatedSize = sizeService.saveProductSize(productSize);
            if (updatedSize != null) {
                session.setAttribute("succMsg", "Size updated successfully!");
            } else {
                session.setAttribute("errorMsg", "Failed to update size");
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Error updating size: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/productSize";
    }
}
