package splitwise.repositories.impl;

import splitwise.models.User;
import splitwise.repositories.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of UserRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryUserRepository implements UserRepository {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> emailIndex = new ConcurrentHashMap<>(); // email -> userId
    
    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        emailIndex.put(user.getEmail(), user.getId());
        return user;
    }
    
    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        String userId = emailIndex.get(email);
        if (userId == null) {
            return Optional.empty();
        }
        return findById(userId);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public void deleteById(String userId) {
        User user = users.remove(userId);
        if (user != null) {
            emailIndex.remove(user.getEmail());
        }
    }
    
    @Override
    public boolean existsById(String userId) {
        return users.containsKey(userId);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return emailIndex.containsKey(email);
    }
}



