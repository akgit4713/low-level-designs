package musicstreaming.repositories;

import musicstreaming.models.User;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User persistence operations.
 * Follows the Repository pattern for data access abstraction.
 */
public interface UserRepository {
    
    User save(User user);
    
    Optional<User> findById(String id);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findAll();
    
    void delete(String id);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}



