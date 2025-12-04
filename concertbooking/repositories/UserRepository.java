package concertbooking.repositories;

import concertbooking.models.User;

import java.util.Optional;

/**
 * Repository interface for User entity
 */
public interface UserRepository extends Repository<User, String> {
    
    Optional<User> findByEmail(String email);
}



