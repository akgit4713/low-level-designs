package onlineshopping.services;

import onlineshopping.enums.UserRole;
import onlineshopping.models.Address;
import onlineshopping.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user management
 */
public interface UserService {
    
    /**
     * Register a new user
     */
    User register(String email, String name, String password, UserRole role);
    
    /**
     * Authenticate user
     */
    Optional<User> authenticate(String email, String password);
    
    /**
     * Get user by ID
     */
    Optional<User> getUser(String userId);
    
    /**
     * Get user by email
     */
    Optional<User> getUserByEmail(String email);
    
    /**
     * Update user profile
     */
    User updateProfile(String userId, String name, String phone);
    
    /**
     * Add address to user
     */
    void addAddress(String userId, Address address);
    
    /**
     * Remove address from user
     */
    void removeAddress(String userId, String addressId);
    
    /**
     * Get all users (admin only)
     */
    List<User> getAllUsers();
    
    /**
     * Deactivate user
     */
    void deactivateUser(String userId);
}



