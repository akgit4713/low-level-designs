package taskmanagement.services.impl;

import taskmanagement.exceptions.UserException;
import taskmanagement.models.User;
import taskmanagement.repositories.impl.InMemoryUserRepository;
import taskmanagement.services.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserService.
 */
public class UserServiceImpl implements UserService {
    
    private final InMemoryUserRepository userRepository;

    public UserServiceImpl(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String username, String email, String name) {
        validateUsername(username);
        validateEmail(email);
        
        if (userRepository.existsByUsername(username)) {
            throw UserException.usernameExists(username);
        }
        
        if (userRepository.existsByEmail(email)) {
            throw UserException.emailExists(email);
        }
        
        String id = "USER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        User user = new User(id, username, email, name);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(String userId, String email, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.notFound(userId));
        
        if (email != null && !email.equals(user.getEmail())) {
            validateEmail(email);
            if (userRepository.existsByEmail(email)) {
                throw UserException.emailExists(email);
            }
            user.setEmail(email);
        }
        
        if (name != null) {
            user.setName(name);
        }
        
        return userRepository.save(user);
    }

    @Override
    public boolean deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw UserException.notFound(userId);
        }
        return userRepository.delete(userId);
    }

    @Override
    public boolean userExists(String userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("Username must be between 3 and 50 characters");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, and underscores");
        }
    }
    
    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        // Simple email validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw UserException.invalidEmail(email);
        }
    }
}



