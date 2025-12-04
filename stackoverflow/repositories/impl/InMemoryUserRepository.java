package stackoverflow.repositories.impl;

import stackoverflow.models.User;
import stackoverflow.repositories.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of UserRepository.
 */
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        usersByUsername.put(user.getUsername().toLowerCase(), user);
        usersByEmail.put(user.getEmail().toLowerCase(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(String id) {
        User user = users.remove(id);
        if (user != null) {
            usersByUsername.remove(user.getUsername().toLowerCase());
            usersByEmail.remove(user.getEmail().toLowerCase());
        }
    }

    @Override
    public boolean exists(String id) {
        return users.containsKey(id);
    }

    @Override
    public long count() {
        return users.size();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username.toLowerCase()));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email.toLowerCase()));
    }
}



