package snakeladder.exceptions;

/**
 * Exception thrown when an invalid move is attempted.
 */
public class InvalidMoveException extends SnakeLadderException {
    
    public InvalidMoveException(String message) {
        super(message);
    }

    public InvalidMoveException(String message, Throwable cause) {
        super(message, cause);
    }
}



