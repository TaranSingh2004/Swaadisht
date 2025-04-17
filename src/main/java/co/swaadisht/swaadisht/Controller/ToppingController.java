package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.ToppingService;
import co.swaadisht.swaadisht.entities.Toppings;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class ToppingController {

    @Autowired
    private ToppingService toppingService;

    @GetMapping("/toppings")
    public String listToppings(Model model) {
        model.addAttribute("toppings", toppingService.getAllToppings());
        return "admin/toppings/toppings";
    }

    @PostMapping("/saveTopping")
    public String saveTopping(@ModelAttribute Toppings toppings, HttpSession session) {
        if (toppingService.existTopping(toppings.getName())) {
            session.setAttribute("errorMsg", "Topping Name already exists");
            return "redirect:/admin/toppings";
        }
        try {
            Toppings savedToppings = toppingService.saveTopping(toppings);
            if (savedToppings == null) {
                session.setAttribute("errorMsg", "Not saved! Internal server error");
            } else {
                session.setAttribute("succMsg", "Saved successfully");
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/toppings";
    }

    @GetMapping("/deleteTopping/{id}")
    public String deleteTopping(@PathVariable int id, HttpSession session) {
        boolean deleteTopping = toppingService.deleteTopping(id);
        if(deleteTopping){
            session.setAttribute("succMsg", "Topping deleted succesfully");
        } else {
            session.setAttribute("errMsg", "something wrong on server");
        }
        return "redirect:/admin/toppings";
    }

    @GetMapping("/editTopping/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        model.addAttribute("topping", toppingService.getToppingById(id));
        return "admin/toppings/editTopping";
    }

    @PostMapping("/updateTopping")
    public String updateTopping(@ModelAttribute Toppings toppings, HttpSession session){
        try {
            Toppings oldTopping = toppingService.getToppingById(toppings.getId());
            if(oldTopping == null){
                session.setAttribute("errorMsg", "Topping not found");
                return "redirect:/admin/toppings";
            }
            oldTopping.setName(toppings.getName());
            oldTopping.setActive(toppings.isActive());

            Toppings updatedTopping = toppingService.saveTopping(oldTopping);
            if (updatedTopping != null) {
                session.setAttribute("succMsg", "Topping updated successfully!");
            } else {
                session.setAttribute("errorMsg", "Failed to update Topping");
            }
            return "redirect:/admin/editTopping/" + toppings.getId();
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Error updating Topping: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/admin/editTopping/" + toppings.getId();
        }
    }
}
