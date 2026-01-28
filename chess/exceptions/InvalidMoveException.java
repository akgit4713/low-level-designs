package chess.exceptions;

/**
 * Exception thrown when an invalid chess move is attempted.
 */
public class InvalidMoveException extends ChessException {
    
    public InvalidMoveException(String message) {
        super(message);
    }

    public InvalidMoveException(String from, String to, String reason) {
        super(String.format("Invalid move from %s to %s: %s", from, to, reason));
    }
}


