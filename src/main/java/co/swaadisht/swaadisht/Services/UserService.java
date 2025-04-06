package co.swaadisht.swaadisht.Services;

import co.swaadisht.swaadisht.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User saveUser(User user);

    Optional<User> getUserById(String id);

    Optional<User> updateUser(User user);

    Optional<User> upadteUser(User user);

    void deleteUser(String id);

    boolean isUserExist(String userId);

    boolean isUserExistByEmail(String email);

    List<User> getAllUser();

    User getUserByEmail(String email);
}
