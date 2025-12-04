package stackoverflow.repositories;

import stackoverflow.models.User;

import java.util.Optional;

/**
 * Repository interface for User operations.
 */
public interface UserRepository extends Repository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}



