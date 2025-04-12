package co.swaadisht.swaadisht.Services.Impl;

import co.swaadisht.swaadisht.Repository.UserRepository;
import co.swaadisht.swaadisht.Services.UserService;
import co.swaadisht.swaadisht.entities.User;
import co.swaadisht.swaadisht.helpers.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        user.setRole("ROLE_USER");
        String encodePassword = passwordEncoder.encode(user.getPassword()); // Now this will work
        user.setPassword(encodePassword);
        return userRepo.save(user);
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }


//    @Override
//    public Optional<User> upadteUser(User user) {
//        User user2 = userRepo.findById(user.getUserId()).orElseThrow(()-> new ResourceNotFoundException("User not found"));
//        user2.setName(user.getName());
//        user2.setEmail(user.getEmail());
//        user2.setPassword(user.getPassword());
//        user2.setPhoneNumber(user.getPhoneNumber());
//        user2.setEnabled(user.isEnabled(status));
//        user2.setEmailVerified(user.isEmailVerified());
//        user2.setPhoneVerified(user.isPhoneVerified());
//        user2.setProvider(user.getProvider());
//        user2.setProviderId(user.getProviderId());
//
//        User save = userRepo.save(user2);
//        return Optional.ofNullable(save);
//    }

    @Override
    public void deleteUser(String id) {
        User user2 = userRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found"));
        userRepo.delete(user2);
    }

    @Override
    public boolean isUserExist(String userId) {
        User user2 = userRepo.findById(userId).orElse(null);
        return user2 != null;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User user = (User) userRepo.findByEmail(email);
        return user !=null;
    }

    @Override
    public List<User> getAllUser() {
        return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return (User) userRepo.findByEmail(email);
    }

    public boolean emailExists(String email) {
        return userRepo.findByEmail(email) != null;
    }

    @Override
    public List<User> getUsers(String roleUser) {
        return userRepo.findByRole(roleUser);
    }

    @Override
    public boolean updateAccountStatus(String id, boolean status) {
        Optional<User> findByuser = getUserById(id);
        if(findByuser.isPresent()){
            User userDtls=findByuser.get();
            userDtls.setEnabled(status);
            userRepo.save(userDtls);
            return true;
        }
        return false;
    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        User findByEmail = userRepo.findByEmail(email);
        findByEmail.setResetToken(resetToken);
        userRepo.save(findByEmail);
    }

    @Override
    public User getUserByToken(String token) {
        return userRepo.findByResetToken(token);
    }

    @Override
    public User updateUser(User user) {
        return userRepo.save(user);
    }

}
