package snakeladder.exceptions;

/**
 * Exception thrown when a player is not found.
 */
public class PlayerNotFoundException extends SnakeLadderException {
    
    public PlayerNotFoundException(String playerId) {
        super("Player not found with ID: " + playerId);
    }

    public PlayerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}



