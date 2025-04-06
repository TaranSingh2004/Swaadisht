package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.User;
import co.swaadisht.swaadisht.forms.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

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
        UserForm userForm = new UserForm();
        m.addAttribute("userForm", userForm);
        return "login-signup/login";
    }

    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@ModelAttribute UserForm userForm){
//        System.out.println(userForm);
        User user = User.builder()
                .name(userForm.getName())
                .email(userForm.getEmail())
                .password(userForm.getPassword())
                .phoneNumber(userForm.getPhoneNumber())
                .profilePic("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.vecteezy.com%2Ffree-vector%2Fdefault-profile-picture&psig=AOvVaw1iyEPc3lJJRtMa74rzjCaP&ust=1744055862434000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCJj6yseYxIwDFQAAAAAdAAAAABAE")
                .build();
        User savedUser = userService.saveUser(user);
        System.out.println("User saved");
        System.out.println(savedUser);
        return "redirect:/register";
    }

}
