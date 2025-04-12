package co.swaadisht.swaadisht.config;

import co.swaadisht.swaadisht.Repository.UserRepository;
import co.swaadisht.swaadisht.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");

        User user = userRepo.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setName((String) attributes.get("name"));
            user.setProfilePic((String) attributes.get("picture"));
            user.setRoleList(List.of("ROLE_USER"));
            user.setEnabled(true);
            user = userRepo.save(user);
        }

        List<String> roles = user.getRoleList();
        if (roles == null || roles.isEmpty()) {
            roles = List.of("ROLE_USER");
            user.setRoleList(roles);
            userRepo.save(user);
        }


        Collection<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .collect(Collectors.toList());

        return new DefaultOAuth2User(
                authorities,
                attributes,
                userRequest.getClientRegistration().getProviderDetails()
                        .getUserInfoEndpoint().getUserNameAttributeName()
        );
    }
}