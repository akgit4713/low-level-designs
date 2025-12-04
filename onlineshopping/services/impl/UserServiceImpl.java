package onlineshopping.services.impl;

import onlineshopping.enums.UserRole;
import onlineshopping.exceptions.UserException;
import onlineshopping.models.Address;
import onlineshopping.models.User;
import onlineshopping.repositories.impl.InMemoryUserRepository;
import onlineshopping.services.UserService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserService
 */
public class UserServiceImpl implements UserService {
    
    private final InMemoryUserRepository userRepository;

    public UserServiceImpl(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(String email, String name, String password, UserRole role) {
        if (userRepository.existsByEmail(email)) {
            throw UserException.emailAlreadyExists(email);
        }
        
        User user = User.builder()
            .id("USER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
            .email(email)
            .name(name)
            .passwordHash(hashPassword(password))
            .role(role)
            .build();
        
        return userRepository.save(user);
    }

    @Override
    public Optional<User> authenticate(String email, String password) {
        return userRepository.findByEmail(email)
            .filter(user -> user.getPasswordHash().equals(hashPassword(password)))
            .filter(User::isActive);
    }

    @Override
    public Optional<User> getUser(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateProfile(String userId, String name, String phone) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> UserException.notFound(userId));
        
        // Since User is mostly immutable, we would need to rebuild
        // For simplicity, we just update mutable fields
        user.setPhoneNumber(phone);
        
        return userRepository.save(user);
    }

    @Override
    public void addAddress(String userId, Address address) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> UserException.notFound(userId));
        
        user.addAddress(address);
        userRepository.save(user);
    }

    @Override
    public void removeAddress(String userId, String addressId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> UserException.notFound(userId));
        
        user.removeAddress(addressId);
        userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deactivateUser(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> UserException.notFound(userId));
        
        user.setActive(false);
        userRepository.save(user);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}



