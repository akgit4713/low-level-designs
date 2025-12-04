package bookmyshow.services;

import bookmyshow.models.User;
import java.util.Optional;

/**
 * Service interface for user management.
 */
public interface UserService {
    User registerUser(User user);
    Optional<User> getUser(String userId);
    Optional<User> getUserByEmail(String email);
    void updateUser(User user);
    void deleteUser(String userId);
}



