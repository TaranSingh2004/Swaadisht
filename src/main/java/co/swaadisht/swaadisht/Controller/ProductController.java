package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.CategoryServices;
import co.swaadisht.swaadisht.Services.ImageService;
import co.swaadisht.swaadisht.Services.ProductService;
import co.swaadisht.swaadisht.entities.Category;
import co.swaadisht.swaadisht.entities.Product;
import com.cloudinary.Cloudinary;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class ProductController {

    @Autowired
    private CategoryServices categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ImageService imageService;

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model m) {
        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categories", categories);
        return "admin/addProduct";
    }

    @PostMapping("/saveProduct")
    public String saveProduct(
            @ModelAttribute Product product,
            @RequestParam("file") MultipartFile image,
            @RequestParam("categoryId") int categoryId,  // Make sure this matches the form field name
            HttpSession session) {

        try {
            // 1. Find category
            Category category = categoryService.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));

            product.setCategory(category);

            // 2. Handle image upload
            String imageUrl = image.isEmpty() ?
                    "default.jpg" :
                    imageService.uploadImage(image, "product_" + UUID.randomUUID());
            product.setProductImage(imageUrl);

            // 3. Save product
            Product savedProduct = productService.saveProduct(product);

            session.setAttribute("succMsg", "Product saved successfully");
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/admin/loadAddProduct";
    }

}
