package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.CategoryServices;
import co.swaadisht.swaadisht.Services.ImageService;
import co.swaadisht.swaadisht.entities.Category;
import com.cloudinary.Cloudinary;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
            session.setAttribute("succMsg", "category deleted succesfully");
        } else {
            session.setAttribute("errMsg", "something wrong on server");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/product")
    public String addProduct(){
        return "admin/addProduct";
    }
}
