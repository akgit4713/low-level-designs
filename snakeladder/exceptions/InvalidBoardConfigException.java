package snakeladder.exceptions;

/**
 * Exception thrown when board configuration is invalid.
 * For example, snake head at same position as ladder base.
 */
public class InvalidBoardConfigException extends SnakeLadderException {
    
    public InvalidBoardConfigException(String message) {
        super(message);
    }

    public InvalidBoardConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}



