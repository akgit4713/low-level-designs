package splitwise.services.impl;

import splitwise.exceptions.SplitwiseException;
import splitwise.exceptions.UserNotFoundException;
import splitwise.models.User;
import splitwise.repositories.UserRepository;
import splitwise.services.UserService;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService.
 */
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User registerUser(String name, String email, String phone) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new SplitwiseException("User with email already exists: " + email);
        }
        
        User user = new User(name, email, phone);
        return userRepository.save(user);
    }
    
    @Override
    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
    
    @Override
    public Optional<User> findUser(String userId) {
        return userRepository.findById(userId);
    }
    
    @Override
    public User updateUser(String userId, String name, String email, String phone) {
        User user = getUser(userId);
        
        // Check if new email conflicts with another user
        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new SplitwiseException("Email already in use: " + email);
            }
            user.setEmail(email);
        }
        
        if (name != null) {
            user.setName(name);
        }
        if (phone != null) {
            user.setPhone(phone);
        }
        
        return userRepository.save(user);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public boolean userExists(String userId) {
        return userRepository.existsById(userId);
    }
}



