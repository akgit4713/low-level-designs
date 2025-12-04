package socialnetwork.services.impl;

import socialnetwork.enums.PrivacyLevel;
import socialnetwork.enums.UserStatus;
import socialnetwork.exceptions.UserNotFoundException;
import socialnetwork.models.User;
import socialnetwork.repositories.UserRepository;
import socialnetwork.services.UserService;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService.
 * Handles user profile management.
 */
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUser(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateProfile(String userId, String name, String bio, String interests) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        if (bio != null) {
            user.setBio(bio);
        }
        if (interests != null) {
            user.setInterests(interests);
        }

        return userRepository.save(user);
    }

    @Override
    public User updateProfilePicture(String userId, String pictureUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setProfilePictureUrl(pictureUrl);
        return userRepository.save(user);
    }

    @Override
    public User updatePrivacySettings(String userId, PrivacyLevel profilePrivacy, 
                                       PrivacyLevel defaultPostPrivacy) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (profilePrivacy != null) {
            user.setProfilePrivacy(profilePrivacy);
        }
        if (defaultPostPrivacy != null) {
            user.setDefaultPostPrivacy(defaultPostPrivacy);
        }

        return userRepository.save(user);
    }

    @Override
    public List<User> searchUsers(String name) {
        return userRepository.searchByName(name);
    }

    @Override
    public void deactivateAccount(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setStatus(UserStatus.DEACTIVATED);
        userRepository.save(user);
    }
}



