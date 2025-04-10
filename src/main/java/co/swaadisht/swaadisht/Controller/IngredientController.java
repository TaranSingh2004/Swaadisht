package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.CustomizationIngredientService;
import co.swaadisht.swaadisht.entities.CustomizationIngredient;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class IngredientController {

    @Autowired
    private CustomizationIngredientService ingredientService;

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
        boolean deleteIngredient = ingredientService.deleteIngredient(id);
        if(deleteIngredient){
            session.setAttribute("succMsg", "Ingredient deleted succesfully");
        } else {
            session.setAttribute("errMsg", "something wrong on server");
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
