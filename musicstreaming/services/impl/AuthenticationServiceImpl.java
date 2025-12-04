package musicstreaming.services.impl;

import musicstreaming.exceptions.AuthenticationException;
import musicstreaming.exceptions.UserNotFoundException;
import musicstreaming.models.User;
import musicstreaming.repositories.UserRepository;
import musicstreaming.services.AuthenticationService;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of AuthenticationService with token-based authentication.
 */
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final Map<String, String> activeTokens = new ConcurrentHashMap<>(); // token -> userId

    public AuthenticationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(String username, String email, String password) {
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new AuthenticationException("Invalid email address");
        }
        if (password == null || password.length() < 6) {
            throw new AuthenticationException("Password must be at least 6 characters");
        }

        // Check for duplicates
        if (userRepository.existsByUsername(username)) {
            throw new AuthenticationException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new AuthenticationException("Email already registered: " + email);
        }

        // Create and save user
        String userId = UUID.randomUUID().toString();
        String passwordHash = hashPassword(password);
        User user = new User(userId, username, email, passwordHash);
        return userRepository.save(user);
    }

    @Override
    public String login(String usernameOrEmail, String password) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new AuthenticationException("Invalid credentials")));

        if (!user.isActive()) {
            throw new AuthenticationException("Account is deactivated");
        }

        String passwordHash = hashPassword(password);
        if (!passwordHash.equals(user.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials");
        }

        // Generate and store token
        String token = UUID.randomUUID().toString();
        activeTokens.put(token, user.getId());
        user.updateLastLogin();
        userRepository.save(user);

        return token;
    }

    @Override
    public User validateToken(String token) {
        String userId = activeTokens.get(token);
        if (userId == null) {
            throw new AuthenticationException("Invalid or expired token");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Override
    public void logout(String token) {
        activeTokens.remove(token);
    }

    @Override
    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String oldHash = hashPassword(oldPassword);
        if (!oldHash.equals(user.getPasswordHash())) {
            throw new AuthenticationException("Current password is incorrect");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new AuthenticationException("New password must be at least 6 characters");
        }

        user.setPasswordHash(hashPassword(newPassword));
        userRepository.save(user);

        // Invalidate all tokens for this user
        activeTokens.entrySet().removeIf(entry -> entry.getValue().equals(userId));
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }
}



