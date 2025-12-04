package socialnetwork.repositories.impl;

import socialnetwork.models.User;
import socialnetwork.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of UserRepository.
 * Uses ConcurrentHashMap for thread safety.
 */
public class InMemoryUserRepository implements UserRepository {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> emailIndex = new ConcurrentHashMap<>();

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
    public Optional<User> findByEmail(String email) {
        String userId = emailIndex.get(email.toLowerCase());
        if (userId == null) return Optional.empty();
        return findById(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> findByIds(List<String> ids) {
        return ids.stream()
                .map(users::get)
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return emailIndex.containsKey(email.toLowerCase());
    }

    @Override
    public void delete(String id) {
        User user = users.remove(id);
        if (user != null) {
            emailIndex.remove(user.getEmail().toLowerCase());
        }
    }

    @Override
    public List<User> searchByName(String name) {
        String searchTerm = name.toLowerCase();
        return users.values().stream()
                .filter(user -> user.getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
}



