package socialnetwork.services.impl;

import socialnetwork.exceptions.AuthenticationException;
import socialnetwork.exceptions.UserNotFoundException;
import socialnetwork.exceptions.ValidationException;
import socialnetwork.models.Session;
import socialnetwork.models.User;
import socialnetwork.repositories.SessionRepository;
import socialnetwork.repositories.UserRepository;
import socialnetwork.services.AuthService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Implementation of AuthService.
 * Handles user registration, login, and session management.
 */
public class AuthServiceImpl implements AuthService {
    
    private static final int SESSION_EXPIRATION_HOURS = 24;
    
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public AuthServiceImpl(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public User register(String name, String email, String password) {
        validateEmail(email);
        validatePassword(password);
        
        if (userRepository.existsByEmail(email)) {
            throw ValidationException.emailAlreadyExists();
        }

        User user = User.builder()
                .name(name)
                .email(email)
                .passwordHash(hashPassword(password))
                .build();

        return userRepository.save(user);
    }

    @Override
    public Session login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(AuthenticationException::invalidCredentials);

        if (!user.isActive()) {
            throw AuthenticationException.accountDeactivated();
        }

        if (!verifyPassword(password, user.getPasswordHash())) {
            throw AuthenticationException.invalidCredentials();
        }

        // Invalidate any existing sessions
        sessionRepository.invalidateAllForUser(user.getId());

        // Create new session
        Session session = new Session(user.getId(), SESSION_EXPIRATION_HOURS);
        return sessionRepository.save(session);
    }

    @Override
    public void logout(String token) {
        sessionRepository.invalidate(token);
    }

    @Override
    public User validateSession(String token) {
        Session session = sessionRepository.findByToken(token)
                .orElseThrow(AuthenticationException::invalidSession);

        if (!session.isValid()) {
            throw AuthenticationException.sessionExpired();
        }

        return userRepository.findById(session.getUserId())
                .orElseThrow(() -> new UserNotFoundException(session.getUserId()));
    }

    @Override
    public void changePassword(String userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!verifyPassword(oldPassword, user.getPasswordHash())) {
            throw AuthenticationException.invalidCredentials();
        }

        validatePassword(newPassword);
        user.setPasswordHash(hashPassword(newPassword));
        userRepository.save(user);

        // Invalidate all sessions to force re-login
        sessionRepository.invalidateAllForUser(userId);
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw ValidationException.invalidEmail();
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || 
            !password.matches(".*[A-Za-z].*") || !password.matches(".*\\d.*")) {
            throw ValidationException.weakPassword();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    private boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
}



