package digitalwallet.services;

import digitalwallet.models.User;
import java.util.Optional;

/**
 * Service interface for user management.
 */
public interface UserService {
    
    /**
     * Create a new user
     */
    User createUser(String name, String email, String phoneNumber, String pin);
    
    /**
     * Get user by ID
     */
    Optional<User> getUser(String userId);
    
    /**
     * Get user by email
     */
    Optional<User> getUserByEmail(String email);
    
    /**
     * Update user information
     */
    User updateUser(String userId, String name, String phoneNumber);
    
    /**
     * Verify user's PIN
     */
    boolean verifyPin(String userId, String pin);
    
    /**
     * Update user's PIN
     */
    void updatePin(String userId, String oldPin, String newPin);
    
    /**
     * Mark user's KYC as verified
     */
    void verifyKyc(String userId);
    
    /**
     * Suspend user account
     */
    void suspendUser(String userId);
    
    /**
     * Reactivate user account
     */
    void reactivateUser(String userId);
    
    /**
     * Close user account
     */
    void closeAccount(String userId);
}



