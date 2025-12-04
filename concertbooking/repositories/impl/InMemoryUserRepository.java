package concertbooking.repositories.impl;

import concertbooking.models.User;
import concertbooking.repositories.UserRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of UserRepository
 */
public class InMemoryUserRepository implements UserRepository {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    
    @Override
    public User save(User entity) {
        users.put(entity.getId(), entity);
        return entity;
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
        return users.remove(id) != null;
    }
    
    @Override
    public boolean existsById(String id) {
        return users.containsKey(id);
    }
    
    @Override
    public long count() {
        return users.size();
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(email))
            .findFirst();
    }
}



