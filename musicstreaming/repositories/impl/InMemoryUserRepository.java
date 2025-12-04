package musicstreaming.repositories.impl;

import musicstreaming.models.User;
import musicstreaming.repositories.UserRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of UserRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryUserRepository implements UserRepository {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> usernameIndex = new ConcurrentHashMap<>();
    private final Map<String, String> emailIndex = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        usernameIndex.put(user.getUsername().toLowerCase(), user.getId());
        emailIndex.put(user.getEmail().toLowerCase(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String userId = usernameIndex.get(username.toLowerCase());
        return userId != null ? Optional.ofNullable(users.get(userId)) : Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String userId = emailIndex.get(email.toLowerCase());
        return userId != null ? Optional.ofNullable(users.get(userId)) : Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(String id) {
        User user = users.remove(id);
        if (user != null) {
            usernameIndex.remove(user.getUsername().toLowerCase());
            emailIndex.remove(user.getEmail().toLowerCase());
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return usernameIndex.containsKey(username.toLowerCase());
    }

    @Override
    public boolean existsByEmail(String email) {
        return emailIndex.containsKey(email.toLowerCase());
    }
}



