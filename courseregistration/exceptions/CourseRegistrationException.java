package courseregistration.exceptions;

/**
 * Base exception for course registration system.
 */
public class CourseRegistrationException extends RuntimeException {
    
    public CourseRegistrationException(String message) {
        super(message);
    }
    
    public CourseRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}



