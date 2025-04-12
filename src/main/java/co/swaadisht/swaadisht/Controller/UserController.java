package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Repository.UserRepository;
import co.swaadisht.swaadisht.Services.CategoryServices;
import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.Category;
import co.swaadisht.swaadisht.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryServices categoryService;

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m){
        if(p!=null){
            String email= p.getName();
            User user = userService.getUserByEmail(email);
            m.addAttribute("user", user);
        }
        List<Category> list = categoryService.getAllActiveCategory();
        m.addAttribute("categorys", list);
    }

    @GetMapping("/")
    public String userHome(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            // Handle OAuth2 users
            if (principal instanceof OAuth2User oauthUser) {
                model.addAttribute("userName", oauthUser.getAttribute("name"));
                model.addAttribute("userEmail", oauthUser.getAttribute("email"));
                model.addAttribute("userPicture", oauthUser.getAttribute("picture"));
                model.addAttribute("isOAuthUser", true);
            }
            // Handle regular UserDetails
            else if (principal instanceof UserDetails userDetails) {
                model.addAttribute("userName", userDetails.getUsername());
                model.addAttribute("isRegularUser", true);
            }
        }
        return "user/home";
    }

    @GetMapping("/test")
    public String testAccess() {
        return "You have USER access!";
    }

    @GetMapping("/dashboard")
    public String userDashboard(Model model, Authentication authentication) {
        // Add user-specific data to model
        model.addAttribute("user", authentication.getPrincipal());
        return "index"; // Your Thymeleaf template name
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model, Authentication authentication) {
        if (authentication != null) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email);

            if (user != null) {
                model.addAttribute("user", user);
            } else if (authentication.getPrincipal() instanceof OAuth2User) {
                // Handle OAuth users
                OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
                model.addAttribute("user", convertOAuth2UserToUserDto(oauthUser));
            }
        }
        return "user/profile";
    }

    private User convertOAuth2UserToUserDto(OAuth2User oauthUser) {
        User user = new User();
        user.setName(oauthUser.getAttribute("name"));
        user.setEmail(oauthUser.getAttribute("email"));
        user.setProfilePic(oauthUser.getAttribute("picture"));
        return user;
    }
}
