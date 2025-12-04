package movierating.services;

import movierating.models.User;
import movierating.models.UserLevel;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user operations.
 * 
 * Interface Segregation: Only user-related operations.
 * Dependency Inversion: High-level modules depend on this abstraction.
 */
public interface UserService {
    
    /**
     * Register a new user.
     * @param user The user to register
     * @return The registered user
     */
    User registerUser(User user);
    
    /**
     * Get a user by ID.
     * @param userId The user ID
     * @return Optional containing the user if found
     */
    Optional<User> getUserById(String userId);
    
    /**
     * Get a user by username.
     * @param username The username
     * @return Optional containing the user if found
     */
    Optional<User> getUserByUsername(String username);
    
    /**
     * Get all users.
     * @return List of all users
     */
    List<User> getAllUsers();
    
    /**
     * Get users by level.
     * @param level The user level to filter by
     * @return List of users at the specified level
     */
    List<User> getUsersByLevel(UserLevel level);
    
    /**
     * Update a user's level.
     * @param userId The user ID
     * @param newLevel The new level
     * @return The updated user
     */
    User updateUserLevel(String userId, UserLevel newLevel);
    
    /**
     * Delete a user.
     * @param userId The user ID to delete
     * @return true if user was deleted
     */
    boolean deleteUser(String userId);
}

