package splitwise.repositories;

import splitwise.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User persistence operations.
 */
public interface UserRepository {
    
    /**
     * Save a user (create or update).
     */
    User save(User user);
    
    /**
     * Find a user by ID.
     */
    Optional<User> findById(String userId);
    
    /**
     * Find a user by email.
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Get all users.
     */
    List<User> findAll();
    
    /**
     * Delete a user by ID.
     */
    void deleteById(String userId);
    
    /**
     * Check if a user exists by ID.
     */
    boolean existsById(String userId);
    
    /**
     * Check if a user exists by email.
     */
    boolean existsByEmail(String email);
}



