package movierating.factories;

import movierating.models.User;
import movierating.models.UserLevel;

/**
 * Factory for creating User objects.
 * 
 * Factory Pattern: Encapsulates user creation logic.
 * Single Responsibility: Only handles user object creation.
 */
public class UserFactory {
    
    /**
     * Create a new user with default NOVICE level.
     * @param username The username
     * @param email The email address
     * @return The created user
     */
    public static User createUser(String username, String email) {
        return new User(username, email);
    }
    
    /**
     * Create a user with a specific starting level (for testing or admin purposes).
     * @param username The username
     * @param email The email address
     * @param level The starting level
     * @return The created user
     */
    public static User createUserWithLevel(String username, String email, UserLevel level) {
        User user = new User(username, email);
        user.setLevel(level);
        return user;
    }
}


