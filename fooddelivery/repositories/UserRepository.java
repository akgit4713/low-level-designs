package fooddelivery.repositories;

import fooddelivery.enums.UserRole;
import fooddelivery.models.User;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entities.
 */
public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    List<User> findAll();
    void delete(String id);
    boolean existsById(String id);
    boolean existsByEmail(String email);
}



