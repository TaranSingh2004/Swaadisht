package co.swaadisht.swaadisht.config;

import co.swaadisht.swaadisht.Repository.UserRepository;
import co.swaadisht.swaadisht.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User userDtls = userRepository.findByEmail(username);

        if (userDtls == null) {
            throw new UsernameNotFoundException("user not found");
        }

        return new CustomUser(userDtls);
    }
}
