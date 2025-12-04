package snakeladder.exceptions;

/**
 * Exception thrown when an operation is attempted in an invalid game state.
 */
public class InvalidGameStateException extends SnakeLadderException {
    
    public InvalidGameStateException(String message) {
        super(message);
    }

    public InvalidGameStateException(String message, Throwable cause) {
        super(message, cause);
    }
}



