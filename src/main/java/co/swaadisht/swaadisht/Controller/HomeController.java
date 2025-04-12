package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Services.CategoryServices;
import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.Category;
import co.swaadisht.swaadisht.entities.User;
import co.swaadisht.swaadisht.forms.UserFormDto;
import co.swaadisht.swaadisht.helpers.Message;
import co.swaadisht.swaadisht.helpers.MessageType;
import co.swaadisht.swaadisht.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryServices categoryServices;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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

    @GetMapping("/forgot-password")
    public String showForgotPassword(HttpSession session, Model model){
        model.addAttribute("succMsg", session.getAttribute("succMsg"));
        model.addAttribute("errorMsg", session.getAttribute("errorMsg"));
        session.removeAttribute("succMsg");
        session.removeAttribute("errorMsg");
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email,
                                        HttpSession session,
                                        HttpServletRequest request)
            throws MessagingException, UnsupportedEncodingException {
        User userDtls = userService.getUserByEmail(email);
        if(ObjectUtils.isEmpty(userDtls)){
            session.setAttribute("errorMsg","Invalid email");
        }else{

            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email, resetToken);

            //generate Url = http://localhost:8080/reset-password?token=lkjhgfdsxcvlkjhgfdfghjkkjh

            String url = CommonUtil.generateUrl(request)+"/reset-password?token="+ resetToken;

            Boolean sendMail = commonUtil.sendMail(url, email);
            if(sendMail){
                session.setAttribute("succMsg","please check your email.. password reset link is sent.");
            } else {
                session.setAttribute("errorMsg", "Something wrong on server ! mail not sent");
            }
        }
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token, HttpSession session, Model m){
        User userByToken = userService.getUserByToken(token);
        if(userByToken==null){
            m.addAttribute("msg", "Your link is invalid or expired !!");
            return "message";
        }
        m.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String password,
                                HttpSession session,
                                Model m,
                                RedirectAttributes redirectAttributes){
        User userByToken = userService.getUserByToken(token);
        if(userByToken==null){
            redirectAttributes.addFlashAttribute("msg", "Your link is invalid or expired !!");
            return "redirect:/signin";
        }else {
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUser(userByToken);
            session.setAttribute("succMsg", "Password Changed successfully");
            redirectAttributes.addFlashAttribute("msg", "Password Changed successfully");
            return "redirect:/signin";
        }
    }
}
