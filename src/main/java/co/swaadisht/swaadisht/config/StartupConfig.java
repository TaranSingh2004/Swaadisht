package co.swaadisht.swaadisht.config;

import co.swaadisht.swaadisht.Repository.UserRepository;
import co.swaadisht.swaadisht.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.List;
import java.util.stream.Collectors;
// other imports...

@Configuration
public class StartupConfig {

    @Autowired
    private UserRepository userRepo;

    private static final Logger logger = LoggerFactory.getLogger(StartupConfig.class);

    @EventListener(ApplicationReadyEvent.class)
    public void validateUserRoles() {
        List<User> usersWithoutRoles = userRepo.findAll().stream()
                .filter(u -> u.getRoleList() == null || u.getRoleList().isEmpty())
                .collect(Collectors.toList());

        if (!usersWithoutRoles.isEmpty()) {
            usersWithoutRoles.forEach(user -> {
                user.setRoleList(List.of("ROLE_USER"));
                userRepo.save(user);
            });
            logger.info("Fixed roles for {} users", usersWithoutRoles.size());
        }
    }
}