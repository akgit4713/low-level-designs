package splitwise.services;

import splitwise.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user management operations.
 */
public interface UserService {
    
    /**
     * Register a new user.
     */
    User registerUser(String name, String email, String phone);
    
    /**
     * Get user by ID.
     */
    User getUser(String userId);
    
    /**
     * Get user by ID, returning Optional.
     */
    Optional<User> findUser(String userId);
    
    /**
     * Update user profile.
     */
    User updateUser(String userId, String name, String email, String phone);
    
    /**
     * Get all users.
     */
    List<User> getAllUsers();
    
    /**
     * Check if user exists.
     */
    boolean userExists(String userId);
}



