package chess.exceptions;

/**
 * Base exception class for all chess-related exceptions.
 */
public class ChessException extends RuntimeException {
    
    public ChessException(String message) {
        super(message);
    }

    public ChessException(String message, Throwable cause) {
        super(message, cause);
    }
}


