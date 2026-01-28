package packagelocker.services;

import java.time.LocalDateTime;

/**
 * Abstraction for time operations.
 * Allows for easy testing by injecting a mock clock.
 */
public interface Clock {
    
    /**
     * Returns the current date and time.
     */
    LocalDateTime now();
    
    /**
     * Default implementation using system clock.
     */
    static Clock systemClock() {
        return LocalDateTime::now;
    }
}
