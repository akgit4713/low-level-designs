package bookmyshow.services.impl;

import bookmyshow.exceptions.EntityNotFoundException;
import bookmyshow.exceptions.InvalidOperationException;
import bookmyshow.models.User;
import bookmyshow.repositories.UserRepository;
import bookmyshow.services.UserService;
import java.util.Optional;

/**
 * Implementation of UserService.
 */
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(User user) {
        // Check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new InvalidOperationException("User with email " + user.getEmail() + " already exists");
        }
        
        userRepository.save(user);
        return user;
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
    public void updateUser(User user) {
        if (!userRepository.exists(user.getId())) {
            throw new EntityNotFoundException("User", user.getId());
        }
        userRepository.save(user);
    }

    @Override
    public void deleteUser(String userId) {
        if (!userRepository.exists(userId)) {
            throw new EntityNotFoundException("User", userId);
        }
        userRepository.delete(userId);
    }
}



