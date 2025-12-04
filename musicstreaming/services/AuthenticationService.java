package musicstreaming.services;

import musicstreaming.models.User;

/**
 * Service interface for user authentication.
 */
public interface AuthenticationService {
    
    /**
     * Register a new user.
     */
    User register(String username, String email, String password);
    
    /**
     * Login with username/email and password.
     * Returns an authentication token.
     */
    String login(String usernameOrEmail, String password);
    
    /**
     * Validate an authentication token.
     */
    User validateToken(String token);
    
    /**
     * Logout and invalidate the token.
     */
    void logout(String token);
    
    /**
     * Change user password.
     */
    void changePassword(String userId, String oldPassword, String newPassword);
}



