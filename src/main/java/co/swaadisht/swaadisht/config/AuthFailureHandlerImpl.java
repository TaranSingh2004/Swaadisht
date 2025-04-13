package co.swaadisht.swaadisht.config;

import co.swaadisht.swaadisht.Repository.UserRepository;
import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        User userDtls = userRepository.findByEmail(email);
        if (userDtls != null) {
            if (userDtls.getEnabled()) {
                exception = new LockedException("Your account is locked !! Please try with another account or contact the owner.....");
            } else {
                exception = new LockedException("Your account is inActive");
            }
        } else {
            exception=new LockedException("Email & Password is inValid");
        }
        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);

    }
}
