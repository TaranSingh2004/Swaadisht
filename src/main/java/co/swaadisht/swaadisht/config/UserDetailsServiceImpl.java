package co.swaadisht.swaadisht.config;

import co.swaadisht.swaadisht.Repository.UserRepository;
import co.swaadisht.swaadisht.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User userDtls = userRepository.findByEmail(username);

        if (userDtls == null) {
            throw new UsernameNotFoundException("user not found");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userDtls.getRole())); // Just "USER" or "ADMIN"

        return new org.springframework.security.core.userdetails.User(
                userDtls.getEmail(),
                userDtls.getPassword(),
                userDtls.isEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                authorities
        );

//        return new CustomUser(userDtls);
    }
}
