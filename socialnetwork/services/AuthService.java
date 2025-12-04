package socialnetwork.services;

import socialnetwork.models.Session;
import socialnetwork.models.User;

/**
 * Service interface for authentication operations.
 */
public interface AuthService {
    
    /**
     * Register a new user.
     */
    User register(String name, String email, String password);
    
    /**
     * Login and create a session.
     */
    Session login(String email, String password);
    
    /**
     * Logout and invalidate session.
     */
    void logout(String token);
    
    /**
     * Validate a session token and get the user.
     */
    User validateSession(String token);
    
    /**
     * Change password for authenticated user.
     */
    void changePassword(String userId, String oldPassword, String newPassword);
}



