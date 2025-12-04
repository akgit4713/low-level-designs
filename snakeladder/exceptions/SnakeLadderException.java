package snakeladder.exceptions;

/**
 * Base exception for all Snake and Ladder game exceptions.
 */
public class SnakeLadderException extends RuntimeException {
    
    public SnakeLadderException(String message) {
        super(message);
    }

    public SnakeLadderException(String message, Throwable cause) {
        super(message, cause);
    }
}



