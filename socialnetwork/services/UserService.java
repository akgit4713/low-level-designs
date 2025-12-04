package socialnetwork.services;

import socialnetwork.enums.PrivacyLevel;
import socialnetwork.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user profile operations.
 */
public interface UserService {
    
    /**
     * Get user by ID.
     */
    Optional<User> getUser(String userId);
    
    /**
     * Get user by email.
     */
    Optional<User> getUserByEmail(String email);
    
    /**
     * Update user profile.
     */
    User updateProfile(String userId, String name, String bio, String interests);
    
    /**
     * Update profile picture.
     */
    User updateProfilePicture(String userId, String pictureUrl);
    
    /**
     * Update privacy settings.
     */
    User updatePrivacySettings(String userId, PrivacyLevel profilePrivacy, 
                               PrivacyLevel defaultPostPrivacy);
    
    /**
     * Search users by name.
     */
    List<User> searchUsers(String name);
    
    /**
     * Deactivate user account.
     */
    void deactivateAccount(String userId);
}



