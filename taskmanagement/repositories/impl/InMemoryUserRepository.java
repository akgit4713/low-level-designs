package taskmanagement.repositories.impl;

import taskmanagement.models.User;
import taskmanagement.repositories.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of User repository.
 */
public class InMemoryUserRepository implements Repository<User, String> {
    
    private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        usersById.put(user.getId(), user);
        usersByUsername.put(user.getUsername().toLowerCase(), user);
        usersByEmail.put(user.getEmail().toLowerCase(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public boolean delete(String id) {
        User user = usersById.remove(id);
        if (user != null) {
            usersByUsername.remove(user.getUsername().toLowerCase());
            usersByEmail.remove(user.getEmail().toLowerCase());
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(String id) {
        return usersById.containsKey(id);
    }

    @Override
    public long count() {
        return usersById.size();
    }

    @Override
    public void deleteAll() {
        usersById.clear();
        usersByUsername.clear();
        usersByEmail.clear();
    }

    /**
     * Finds a user by username (case-insensitive).
     */
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username.toLowerCase()));
    }

    /**
     * Finds a user by email (case-insensitive).
     */
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(usersByEmail.get(email.toLowerCase()));
    }

    /**
     * Checks if a username exists (case-insensitive).
     */
    public boolean existsByUsername(String username) {
        return usersByUsername.containsKey(username.toLowerCase());
    }

    /**
     * Checks if an email exists (case-insensitive).
     */
    public boolean existsByEmail(String email) {
        return usersByEmail.containsKey(email.toLowerCase());
    }
}



