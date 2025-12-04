package snakeladder.exceptions;

/**
 * Exception thrown when a game is not found.
 */
public class GameNotFoundException extends SnakeLadderException {
    
    public GameNotFoundException(String gameId) {
        super("Game not found with ID: " + gameId);
    }

    public GameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}



