package movierating.services.impl;

import movierating.models.User;
import movierating.models.UserLevel;
import movierating.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of UserService.
 * 
 * Single Responsibility: Only handles user storage and retrieval.
 * Liskov Substitution: Can be replaced with any other UserService implementation.
 */
public class InMemoryUserService implements UserService {
    
    private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, User> usersByUsername = new ConcurrentHashMap<>();
    
    @Override
    public User registerUser(User user) {
        if (usersByUsername.containsKey(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        usersById.put(user.getId(), user);
        usersByUsername.put(user.getUsername(), user);
        return user;
    }
    
    @Override
    public Optional<User> getUserById(String userId) {
        return Optional.ofNullable(usersById.get(userId));
    }
    
    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }
    
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(usersById.values());
    }
    
    @Override
    public List<User> getUsersByLevel(UserLevel level) {
        return usersById.values().stream()
                .filter(u -> u.getLevel() == level)
                .collect(Collectors.toList());
    }
    
    @Override
    public User updateUserLevel(String userId, UserLevel newLevel) {
        User user = usersById.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        user.setLevel(newLevel);
        return user;
    }
    
    @Override
    public boolean deleteUser(String userId) {
        User user = usersById.remove(userId);
        if (user != null) {
            usersByUsername.remove(user.getUsername());
            return true;
        }
        return false;
    }
}


