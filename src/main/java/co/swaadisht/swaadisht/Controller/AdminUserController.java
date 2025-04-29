package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Repository.UserRepository;
import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.User;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if(p!=null){
            String email= p.getName();
            User user = userService.getUserByEmail(email);
            m.addAttribute("user", user);
        }
    }

    @GetMapping("/users")
    public String getAllUsers(Model m){
        List<User> users = userService.getUsers("ROLE_USER");
        m.addAttribute("users", users);
        return "/admin/users";
    }

    @GetMapping("/updateSts")
    public String updateAccountStatus(@RequestParam String id,
                                      @RequestParam Boolean status,
                                      HttpSession session,
                                      @RequestParam(required = false, defaultValue = "cart") String source){
        boolean f = userService.updateAccountStatus(id, status);
        if(f){
            session.setAttribute("succMsg", "Account Ststus updated");
        }else {
            session.setAttribute("errorMsg", "Something wrong on server");
        }
        return "redirect:/admin/" + source;
    }

    @GetMapping("/admins")
    public String viewAllAdmins(Model m){
        List<User> admins = userService.getAdmins("ROLE_ADMIN");
        m.addAttribute("admins", admins);
        return "/admin/view_admin";
    }

    @GetMapping("/add-admin")
    public String loadAdminAdd(){

        return "/admin/add_admin";
    }

    @PostMapping("/save-admin")
    public String saveUser(@ModelAttribute User user, HttpSession session, RedirectAttributes redirectAttributes) throws IOException {

        if (userRepository.existsByEmail(user.getEmail())) {
            session.setAttribute("errorMsg", "email already registered.");
            return "redirect:/admin/add-admin";
        }
        User saveUser = userService.saveAdmin(user);
        if(!ObjectUtils.isEmpty(saveUser)){
            session.setAttribute("succMsg", "Registered successfully");
        } else {
            session.setAttribute("errorMsg", "something wrong on server");
        }
        return "redirect:/admin/add-admin";
    }

}
