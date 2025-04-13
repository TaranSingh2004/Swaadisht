package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.CartService;
import co.swaadisht.swaadisht.Services.CategoryServices;
import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.Cart;
import co.swaadisht.swaadisht.entities.Category;
import co.swaadisht.swaadisht.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryServices categoryService;

    @Autowired
    private CartService cartService;


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
    public String home() {
        return "user/home";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam Integer productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            @RequestParam String customizationType,
            @RequestParam(required = false) List<Integer> selectedIngredients,
            @RequestParam(required = false) List<Integer> selectedToppings,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            boolean isCustomized = "customized".equals(customizationType);
            User user = userService.findByEmail(principal.getName());
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            // Handle null lists for non-customized items
            if (!isCustomized) {
                selectedIngredients = new ArrayList<>();
                selectedToppings = new ArrayList<>();
            }

            cartService.saveCart(productId, user.getId(), isCustomized,
                    selectedIngredients, selectedToppings, quantity);

            redirectAttributes.addFlashAttribute("success", "Item added to cart!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/product/" + productId;
    }
}
