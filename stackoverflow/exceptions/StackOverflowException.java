package stackoverflow.exceptions;

/**
 * Base exception for Stack Overflow system.
 */
public class StackOverflowException extends RuntimeException {
    public StackOverflowException(String message) {
        super(message);
    }

    public StackOverflowException(String message, Throwable cause) {
        super(message, cause);
    }
}



