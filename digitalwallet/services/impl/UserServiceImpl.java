package digitalwallet.services.impl;

import digitalwallet.enums.AccountStatus;
import digitalwallet.exceptions.AuthenticationException;
import digitalwallet.exceptions.UserNotFoundException;
import digitalwallet.exceptions.WalletException;
import digitalwallet.models.User;
import digitalwallet.repositories.UserRepository;
import digitalwallet.services.UserService;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of UserService.
 */
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Override
    public User createUser(String name, String email, String phoneNumber, String pin) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(email)) {
            throw new WalletException("Email already registered: " + email, "EMAIL_EXISTS");
        }

        // Create user with hashed PIN
        User user = User.builder()
            .id(UUID.randomUUID().toString())
            .name(name)
            .email(email)
            .phoneNumber(phoneNumber)
            .hashedPin(hashPin(pin))
            .status(AccountStatus.PENDING_VERIFICATION)
            .build();

        return userRepository.save(user);
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
    public User updateUser(String userId, String name, String phoneNumber) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        if (name != null) {
            user.updateName(name);
        }
        if (phoneNumber != null) {
            user.updatePhoneNumber(phoneNumber);
        }

        return userRepository.save(user);
    }

    @Override
    public boolean verifyPin(String userId, String pin) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        String hashedInput = hashPin(pin);
        boolean valid = hashedInput.equals(user.getHashedPin());

        if (valid) {
            user.recordSuccessfulLogin();
        } else {
            user.recordFailedLogin();
        }
        userRepository.save(user);

        return valid;
    }

    @Override
    public void updatePin(String userId, String oldPin, String newPin) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        // Verify old PIN
        if (!verifyPin(userId, oldPin)) {
            throw new AuthenticationException(userId, 
                AuthenticationException.AuthFailureReason.INVALID_PIN);
        }

        // Update to new PIN
        user.updatePin(hashPin(newPin));
        userRepository.save(user);
    }

    @Override
    public void verifyKyc(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        user.markKycVerified();
        userRepository.save(user);
    }

    @Override
    public void suspendUser(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        user.updateStatus(AccountStatus.SUSPENDED);
        userRepository.save(user);
    }

    @Override
    public void reactivateUser(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.getStatus().canReactivate()) {
            throw new WalletException("Cannot reactivate user from status: " + user.getStatus());
        }

        user.updateStatus(AccountStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    public void closeAccount(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        user.updateStatus(AccountStatus.CLOSED);
        userRepository.save(user);
    }

    /**
     * Hash PIN using SHA-256
     * In production, use BCrypt or similar
     */
    private String hashPin(String pin) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new WalletException("Failed to hash PIN", e);
        }
    }
}



