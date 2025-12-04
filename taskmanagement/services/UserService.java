package taskmanagement.services;

import taskmanagement.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user operations.
 */
public interface UserService {
    
    /**
     * Creates a new user.
     */
    User createUser(String username, String email, String name);
    
    /**
     * Gets a user by ID.
     */
    Optional<User> getUserById(String userId);
    
    /**
     * Gets a user by username.
     */
    Optional<User> getUserByUsername(String username);
    
    /**
     * Gets all users.
     */
    List<User> getAllUsers();
    
    /**
     * Updates user information.
     */
    User updateUser(String userId, String email, String name);
    
    /**
     * Deletes a user.
     */
    boolean deleteUser(String userId);
    
    /**
     * Checks if a user exists.
     */
    boolean userExists(String userId);
    
    /**
     * Checks if a username is already taken.
     */
    boolean usernameExists(String username);
}



