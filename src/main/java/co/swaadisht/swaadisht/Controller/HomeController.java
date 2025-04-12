package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.CategoryServices;
import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.Category;
import co.swaadisht.swaadisht.entities.User;
import co.swaadisht.swaadisht.forms.UserFormDto;
import co.swaadisht.swaadisht.helpers.Message;
import co.swaadisht.swaadisht.helpers.MessageType;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryServices categoryServices;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if(p!=null){
            String email= p.getName();
            User user = userService.getUserByEmail(email);
            m.addAttribute("user", user);
        }
        List<Category> list = categoryServices.getAllActiveCategory();
        m.addAttribute("categorys", list);
    }

    @GetMapping("/")
    public String home(){
        return "index";
    }


    @GetMapping("/about")
    public String aboutPage() {
        return "about"; // looks for templates/about.html (Thymeleaf) or similar
    }

    @GetMapping("/register")
    public String register(Model m) {
        UserFormDto userForm = new UserFormDto();
        m.addAttribute("userForm", userForm);
        return "register";
    }

    @PostMapping("/do-register")
    public String processRegister(@Valid @ModelAttribute UserFormDto userForm,
                                  BindingResult result,
                                  HttpSession session) {

        if(result.hasErrors()) {
            return "register";
        }

        if(userService.emailExists(userForm.getEmail())) {
            session.setAttribute("message",
                    new Message("Email already exists", MessageType.red));
            return "redirect:/register";
        }

        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword()); // Will be encoded in service
        user.setEnabled(true);
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setProfilePic("default.jpg");

        try {
            User savedUser = userService.saveUser(user);
            session.setAttribute("message",
                    new Message("Registration successful!", MessageType.green));
            return "redirect:/signin";
        } catch (Exception e) {
            session.setAttribute("message",
                    new Message("Registration failed: " + e.getMessage(), MessageType.red));
            return "redirect:/register";
        }
    }

    @GetMapping("/signin")
    public String login() {
        return "login";
    }
}
