package bookmyshow.repositories;

import bookmyshow.models.User;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 */
public interface UserRepository {
    void save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void delete(String id);
    boolean exists(String id);
}



