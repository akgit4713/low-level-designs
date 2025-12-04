package socialnetwork.repositories;

import socialnetwork.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User data access.
 */
public interface UserRepository {
    
    User save(User user);
    
    Optional<User> findById(String id);
    
    Optional<User> findByEmail(String email);
    
    List<User> findAll();
    
    List<User> findByIds(List<String> ids);
    
    boolean existsByEmail(String email);
    
    void delete(String id);
    
    List<User> searchByName(String name);
}



