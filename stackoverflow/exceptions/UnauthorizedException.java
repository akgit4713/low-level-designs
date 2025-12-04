package stackoverflow.exceptions;

/**
 * Exception thrown when a user is not authorized to perform an action.
 */
public class UnauthorizedException extends StackOverflowException {
    public UnauthorizedException(String message) {
        super(message);
    }
}



