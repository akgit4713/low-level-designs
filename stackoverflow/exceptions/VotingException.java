package stackoverflow.exceptions;

/**
 * Exception thrown when a voting operation fails.
 */
public class VotingException extends StackOverflowException {
    public VotingException(String message) {
        super(message);
    }
}



