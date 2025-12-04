package digitalwallet.repositories;

import digitalwallet.models.User;
import java.util.Optional;

/**
 * Repository interface for User entity.
 */
public interface UserRepository extends Repository<User, String> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by phone number
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    /**
     * Check if email is already registered
     */
    boolean existsByEmail(String email);
}



