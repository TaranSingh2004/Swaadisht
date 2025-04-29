package co.swaadisht.swaadisht.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ErrorController {

    @RequestMapping(value = "/error/403", method = {RequestMethod.GET, RequestMethod.POST})
    public String accessDenied() {
        return "error/403";
    }
}
