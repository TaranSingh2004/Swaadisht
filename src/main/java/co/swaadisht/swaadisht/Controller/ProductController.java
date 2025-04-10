package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.*;
import co.swaadisht.swaadisht.entities.Category;
import co.swaadisht.swaadisht.entities.CustomizationIngredient;
import co.swaadisht.swaadisht.entities.Product;
import co.swaadisht.swaadisht.entities.Toppings;
import co.swaadisht.swaadisht.forms.ProductFormDto;
import com.cloudinary.Cloudinary;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private CustomizationIngredientService ingredientService;

    @Autowired
    private ToppingService toppingService;

    @GetMapping("/products")
    public String loadViewProduct(Model m){
        List<Product> products = productService.getAllProductsWithIngredients();
        m.addAttribute("products", products);
        return "admin/products";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model model) {
        model.addAttribute("product", new ProductFormDto());
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("allIngredients", ingredientService.getAllActiveIngredients());
        model.addAttribute("allToppings", toppingService.getAllActiveToppings());
        return "admin/addProduct";
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute @Valid ProductFormDto productDto,
                              @RequestParam("file") MultipartFile file,
                              BindingResult bindingResult,
                              HttpSession session) {

//        if (bindingResult.hasErrors()) {
//            session.setAttribute("errorMsg", "Please fill all required fields correctly");
//            return "redirect:/admin/loadAddProduct";
//        }
        if (file.isEmpty()) {
            session.setAttribute("errorMsg", "Please select an image file");
            return "redirect:/admin/category";
        }

        try {
            // 1. Convert DTO to Product entity
            Product product = new Product();
            product.setName(productDto.getName());
            product.setPrice(productDto.getPrice());
            product.setDiscount(0);
            product.setDescription(productDto.getDescription());
            product.setDiscountPrice(productDto.getPrice());
            product.setStatus(true);
            product.setStock(productDto.getStock());
            product.setCustomizable(productDto.isCustomizable());

            // 2. Handle category
            Category category = categoryService.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
            product.setCategory(category);

            // 3. Handle image upload
//            if (productDto.getImageFile() != null && !productDto.getImageFile().isEmpty()) {
//                try {
//                    String publicId = "product_" + UUID.randomUUID();
//                    String imageUrl = imageService.uploadImage(String.valueOf(productDto.getImageFile()), publicId);
//                    product.setProductImage(imageUrl);
//                } catch (IOException e) {
//                    session.setAttribute("errorMsg", "Image upload failed: " + e.getMessage());
//                    return "redirect:/admin/loadAddProduct";
//                }
//            } else {
//                product.setProductImage(imageService.getDefaultImageUrl());
//            }

            Map<String, Object> uploadOptions = new HashMap<>();
            uploadOptions.put("public_id", "product_" + UUID.randomUUID());
            uploadOptions.put("folder", "product_images");

            // Upload to Cloudinary
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);

            // Set the URL in the category
            product.setProductImage((String) uploadResult.get("secure_url"));


            // 4. Save product with ingredients
            if (productDto.getSelectedIngredientIds() != null && !productDto.getSelectedIngredientIds().isEmpty()) {
                List<CustomizationIngredient> ingredients = ingredientService.findAllByIds(productDto.getSelectedIngredientIds());
                product.setAvailableIngredients(ingredients);
            } else {
                product.setAvailableIngredients(new ArrayList<>());
            }

            // 5. Save product with toppings
            if (productDto.getSelectedToppingIds() != null && !productDto.getSelectedToppingIds().isEmpty()) {
                List<Toppings> toppings = toppingService.findAllByIds(productDto.getSelectedToppingIds());
                product.setAvailableToppings(toppings);
            } else {
                product.setAvailableToppings(new ArrayList<>());
            }

            Product savedProduct = productService.saveProduct(product);

            if (savedProduct != null) {
                session.setAttribute("succMsg", "Product saved successfully");
            } else {
                session.setAttribute("errorMsg", "Failed to save product");
            }

        } catch (Exception e) {
            session.setAttribute("errorMsg", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session){
        boolean deleteProduct = productService.deleteProduct(id);
        if(deleteProduct){
            session.setAttribute("succMsg", "product deleted successfully");
        } else {
            session.setAttribute("errMsg", "something wrong on server");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id, Model m){
        Product product = productService.getProductById(id);
        ProductFormDto productDto = convertToDto(product);

        m.addAttribute("product", productDto);
        m.addAttribute("categories", categoryService.getAllCategory());
        m.addAttribute("allIngredients", ingredientService.getAllActiveIngredients());
        m.addAttribute("allToppings", toppingService.getAllActiveToppings());
        return "admin/editProduct";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute @Valid ProductFormDto productFormDto,
                                @RequestParam("file") MultipartFile file,
                                BindingResult bindingResult,
                                HttpSession session){
        if(bindingResult.hasErrors()){
            session.setAttribute("errorMsg", " Please find all required fields correctly");
            return "redirect:/admin/editProduct/"+ productFormDto.getId();
        }

        try{
            if(productFormDto.getDiscount()<0 || productFormDto.getDiscount()>100){
                session.setAttribute("errorMsg", "invalid discount");
            }
            Product product = productService.getProductById(productFormDto.getId());

            product.setName(productFormDto.getName());
            product.setDescription(productFormDto.getDescription());
            product.setStatus(productFormDto.isStatus());
            product.setStock(productFormDto.getStock());
            product.setPrice(productFormDto.getPrice());
            product.setDiscount(productFormDto.getDiscount());
            product.setDiscountPrice(productFormDto.getPrice()*(100 - productFormDto.getDiscountPrice()) / 100);
            product.setCustomizable(productFormDto.isCustomizable());

            Category category = categoryService.findById(productFormDto.getCategoryId()).orElseThrow(()-> new IllegalArgumentException("Invalid category ID"));
            product.setCategory(category);


            String imageUrl = product.getProductImage();
            if(!file.isEmpty()){
//                try {
//                    String publicId = "product_" + UUID.randomUUID();
//                    String imageUrl = imageService.uploadImage(String.valueOf(file), publicId);
//                    product.setProductImage(imageUrl);
//                } catch (IOException e) {
//                    session.setAttribute("errorMsg", "Image upload failed: " + e.getMessage());
//                    return "redirect:/admin/editProduct/" + productFormDto.getId();
//                }

                // Delete old image from Cloudinary if exists
                if (product.getProductImage() != null) {
                    String publicId = extractPublicId(product.getProductImage());
                    cloudinary.uploader().destroy(publicId, Collections.emptyMap());
                }

                // Upload new image with same options as saveCategory
                Map<String, Object> uploadOptions = new HashMap<>();
                uploadOptions.put("public_id", "product_" + UUID.randomUUID());
                uploadOptions.put("folder", "product_images");

                Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
                imageUrl = (String) uploadResult.get("secure_url");

            }
            product.setProductImage(imageUrl);

            if(productFormDto.getSelectedIngredientIds() != null){
                List<CustomizationIngredient> ingredients = ingredientService.findAllByIds(productFormDto.getSelectedIngredientIds());
                product.setAvailableIngredients(ingredients);
            } else {
                product.setAvailableIngredients(new ArrayList<>());
            }
            if(productFormDto.getSelectedToppingIds() != null){
                List<Toppings> toppings = toppingService.findAllByIds(productFormDto.getSelectedToppingIds());
                product.setAvailableToppings(toppings);
            } else {
                product.setAvailableToppings(new ArrayList<>());
            }
            Product updatedProduct = productService.saveProduct(product);
            session.setAttribute("succMsg", " product updated Successfully");
        } catch (Exception e){
            session.setAttribute("errorMsg", "Error Updating product: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/products";
    }

    private ProductFormDto convertToDto(Product product) {
        ProductFormDto dto = new ProductFormDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setStatus(product.isStatus());
        dto.setStock(product.getStock());
        dto.setPrice(product.getPrice());
        dto.setDiscount(product.getDiscount());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCustomizable(product.isCustomizable());

        if (product.getAvailableIngredients() != null) {
            dto.setSelectedIngredientIds(
                    product.getAvailableIngredients().stream()
                            .map(CustomizationIngredient::getId)
                            .collect(Collectors.toList())
            );
        }
        if (product.getAvailableToppings() != null) {
            dto.setSelectedToppingIds(
                    product.getAvailableToppings().stream()
                            .map(Toppings::getId)
                            .collect(Collectors.toList())
            );
        }
        return dto;
    }

    private String extractPublicId(String imageUrl) {
        // Example URL: https://res.cloudinary.com/demo/image/upload/v123/category_images/category_123.jpg
        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1];
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

}

