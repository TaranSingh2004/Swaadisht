package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String getAllUsers(Model m){
        List<User> users = userService.getUsers("ROLE_USER");
        m.addAttribute("users", users);
        return "/admin/users";
    }

    @GetMapping("/updateSts")
    public String updateAccountStatus(@RequestParam String id, @RequestParam Boolean status, HttpSession session){
        boolean f = userService.updateAccountStatus(id, status);
        if(f){
            session.setAttribute("succMsg", "Account Ststus updated");
        }else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }
        return "redirect:/admin/users";
    }

}
