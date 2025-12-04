package bookmyshow.repositories.impl;

import bookmyshow.models.User;
import bookmyshow.repositories.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of UserRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> emailIndex = new ConcurrentHashMap<>();  // email -> userId

    @Override
    public void save(User user) {
        users.put(user.getId(), user);
        emailIndex.put(user.getEmail().toLowerCase(), user.getId());
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String userId = emailIndex.get(email.toLowerCase());
        return userId != null ? findById(userId) : Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public void delete(String id) {
        User user = users.remove(id);
        if (user != null) {
            emailIndex.remove(user.getEmail().toLowerCase());
        }
    }

    @Override
    public boolean exists(String id) {
        return users.containsKey(id);
    }
}



