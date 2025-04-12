package co.swaadisht.swaadisht.Controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DebugController {

    @GetMapping("/debug/auth")
    public Authentication debugAuth(Authentication authentication) {
        return authentication;
    }

    @GetMapping("/debug/principal")
    public Object debugPrincipal(Principal principal) {
        return principal;
    }

    @GetMapping("/session-info")
    public Map<String, Object> getSessionInfo(HttpSession session) {
        Map<String, Object> sessionData = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            sessionData.put(name, session.getAttribute(name));
        }
        return sessionData;
    }
}