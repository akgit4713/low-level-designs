package onlineshopping.repositories.impl;

import onlineshopping.models.User;
import onlineshopping.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of user repository
 */
public class InMemoryUserRepository implements Repository<User, String> {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> emailIndex = new ConcurrentHashMap<>(); // email -> userId

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        emailIndex.put(user.getEmail().toLowerCase(), user.getId());
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
    public boolean deleteById(String id) {
        User user = users.remove(id);
        if (user != null) {
            emailIndex.remove(user.getEmail().toLowerCase());
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(String id) {
        return users.containsKey(id);
    }

    @Override
    public long count() {
        return users.size();
    }

    /**
     * Find user by email
     */
    public Optional<User> findByEmail(String email) {
        String userId = emailIndex.get(email.toLowerCase());
        if (userId != null) {
            return Optional.ofNullable(users.get(userId));
        }
        return Optional.empty();
    }

    /**
     * Check if email is already registered
     */
    public boolean existsByEmail(String email) {
        return emailIndex.containsKey(email.toLowerCase());
    }
}



