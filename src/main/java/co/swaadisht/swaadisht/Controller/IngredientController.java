package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.CustomizationIngredientService;
import co.swaadisht.swaadisht.Services.Impl.IngredientToppingService;
import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.CustomizationIngredient;
import co.swaadisht.swaadisht.entities.User;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class IngredientController {

    @Autowired
    private CustomizationIngredientService ingredientService;

    @Autowired
    private IngredientToppingService ingredientToppingService;

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

    @GetMapping("/ingredients")
    public String listIngredients(Model model) {
        model.addAttribute("ingredients", ingredientService.getAllIngredients());
        return "admin/ingredients/ingredients";
    }

    @PostMapping("/saveIngredient")
    public String saveIngredient(@ModelAttribute CustomizationIngredient ingredient, HttpSession session) {
        if (ingredientService.existIngredient(ingredient.getName())) {
            session.setAttribute("errorMsg", "Category Name already exists");
            return "redirect:/admin/category";
        }
        try {
            CustomizationIngredient savedIngredients = ingredientService.saveIngredient(ingredient);
            if (savedIngredients == null) {
                session.setAttribute("errorMsg", "Not saved! Internal server error");
            } else {
                session.setAttribute("succMsg", "Saved successfully");
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/ingredients";
    }

    @GetMapping("/deleteIngredient/{id}")
    public String deleteIngredient(@PathVariable int id, HttpSession session) {
        try {
            boolean deleteIngredient = ingredientService.deleteIngredient(id);
            if(deleteIngredient){
                session.setAttribute("succMsg", "Ingredient deleted successfully");
            } else {
                session.setAttribute("errMsg", "Failed to delete ingredient");
            }
        } catch (ResourceNotFoundException e) {
            session.setAttribute("errMsg", "Ingredient not found");
        } catch (Exception e) {
            session.setAttribute("errMsg", "Server error: " + e.getMessage());
        }
        return "redirect:/admin/ingredients";
    }

    @GetMapping("/editIngredient/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        model.addAttribute("ingredient", ingredientService.getIngredientById(id));
        return "admin/ingredients/editIngredient";
    }

    @PostMapping("/updateIngredient")
    public String updateIngredient(@ModelAttribute CustomizationIngredient ingredient, HttpSession session){
        try {
            CustomizationIngredient oldIngredient = ingredientService.getIngredientById(ingredient.getId());
            if(oldIngredient == null){
                session.setAttribute("errorMsg", "Category not found");
                return "redirect:/admin/ingredients";
            }
            oldIngredient.setName(ingredient.getName());
            oldIngredient.setActive(ingredient.isActive());

            CustomizationIngredient updatedIngredient = ingredientService.saveIngredient(oldIngredient);
            if (updatedIngredient != null) {
                session.setAttribute("succMsg", "Ingredient updated successfully!");
            } else {
                session.setAttribute("errorMsg", "Failed to update Ingredient");
            }
            return "redirect:/admin/editIngredient/" + ingredient.getId();
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Error updating category: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/editIngredient/" + ingredient.getId();
        }
    }
}
