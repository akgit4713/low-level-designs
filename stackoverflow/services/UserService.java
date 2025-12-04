package stackoverflow.services;

import stackoverflow.exceptions.StackOverflowException;
import stackoverflow.exceptions.UserNotFoundException;
import stackoverflow.models.User;
import stackoverflow.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service for user management operations.
 */
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, String email) {
        // Check for existing username
        if (userRepository.findByUsername(username).isPresent()) {
            throw new StackOverflowException("Username already exists: " + username);
        }
        
        // Check for existing email
        if (userRepository.findByEmail(email).isPresent()) {
            throw new StackOverflowException("Email already registered: " + email);
        }

        User user = new User(username, email);
        return userRepository.save(user);
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void updateReputation(User user, int change) {
        int oldReputation = user.getReputation();
        user.updateReputation(change);
        userRepository.save(user);
    }

    public List<User> getTopUsersByReputation(int limit) {
        return userRepository.findAll().stream()
                .sorted((a, b) -> Integer.compare(b.getReputation(), a.getReputation()))
                .limit(limit)
                .toList();
    }
}



