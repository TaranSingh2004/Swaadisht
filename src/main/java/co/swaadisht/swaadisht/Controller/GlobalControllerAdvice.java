package co.swaadisht.swaadisht.Controller;

import co.swaadisht.swaadisht.Repository.UserRepository;
import co.swaadisht.swaadisht.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    //    @ModelAttribute
//    public void addUserToModel(Model model, Authentication authentication) {
//        if (authentication != null && authentication.isAuthenticated()) {
//            String email = authentication.getName();
//            User user = userRepository.findByEmail(email);
//            if (user != null) {
//                model.addAttribute("user", user);
//            } else if (authentication.getPrincipal() instanceof OAuth2User) {
//                // Handle OAuth2 users
//                OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
//                User oauthUserEntity = new User();
//                oauthUserEntity.setName(oauthUser.getAttribute("name"));
//                oauthUserEntity.setEmail(oauthUser.getAttribute("email"));
//                model.addAttribute("user", oauthUserEntity);
//            }
//        }
//    }
    @ModelAttribute
    public void addUserToModel(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof OAuth2User oauthUser) {
                model.addAttribute("displayName", oauthUser.getAttribute("name"));
            } else if (authentication.getPrincipal() instanceof UserDetails userDetails) {
                model.addAttribute("displayName", userDetails.getUsername());
            }
        }
    }
}