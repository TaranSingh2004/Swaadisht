package co.swaadisht.swaadisht.Services;

import co.swaadisht.swaadisht.entities.OrderAddress;
import co.swaadisht.swaadisht.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User saveUser(User user);

    Optional<User> getUserById(String id);

//    Optional<User> upadteUser(User user);

    void deleteUser(String id);

    boolean isUserExist(String userId);

    boolean isUserExistByEmail(String email);

    List<User> getAllUser();

    User getUserByEmail(String email);

    boolean emailExists(String email);

    List<User> getUsers(String roleUser);

    boolean updateAccountStatus(String id, boolean status);

    public void updateUserResetToken(String email, String resetToken);

    public User getUserByToken(String token);

    public User updateUser(User user);

    User findByEmail(String username);

    OrderAddress saveAddress(int id, OrderAddress address);
}
