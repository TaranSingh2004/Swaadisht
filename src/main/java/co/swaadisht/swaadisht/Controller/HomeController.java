package co.swaadisht.swaadisht.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "index";
    }


    @GetMapping("/about")
    public String aboutPage() {
        return "about"; // looks for templates/about.html (Thymeleaf) or similar
    }

    @GetMapping("/register")
    public String register() {
        return "login-signup/login";
    }

}
