package fooddelivery.repositories.impl;

import fooddelivery.enums.UserRole;
import fooddelivery.models.User;
import fooddelivery.repositories.UserRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of UserRepository.
 */
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> emailIndex = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        emailIndex.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String userId = emailIndex.get(email);
        return userId != null ? findById(userId) : Optional.empty();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        return users.values().stream()
                .filter(u -> u.getRole() == role)
                .toList();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(String id) {
        User user = users.remove(id);
        if (user != null) {
            emailIndex.remove(user.getEmail());
        }
    }

    @Override
    public boolean existsById(String id) {
        return users.containsKey(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return emailIndex.containsKey(email);
    }
}



