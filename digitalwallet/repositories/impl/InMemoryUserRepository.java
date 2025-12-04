package digitalwallet.repositories.impl;

import digitalwallet.models.User;
import digitalwallet.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of UserRepository.
 * Uses ConcurrentHashMap for thread-safety.
 */
public class InMemoryUserRepository implements UserRepository {
    
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> emailIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> phoneIndex = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        emailIndex.put(user.getEmail().toLowerCase(), user.getId());
        if (user.getPhoneNumber() != null) {
            phoneIndex.put(user.getPhoneNumber(), user.getId());
        }
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
            if (user.getPhoneNumber() != null) {
                phoneIndex.remove(user.getPhoneNumber());
            }
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

    @Override
    public Optional<User> findByEmail(String email) {
        String userId = emailIndex.get(email.toLowerCase());
        return userId != null ? findById(userId) : Optional.empty();
    }

    @Override
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        String userId = phoneIndex.get(phoneNumber);
        return userId != null ? findById(userId) : Optional.empty();
    }

    @Override
    public boolean existsByEmail(String email) {
        return emailIndex.containsKey(email.toLowerCase());
    }
}



