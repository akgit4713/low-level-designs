package chess.exceptions;

/**
 * Exception thrown when an invalid board position is referenced.
 */
public class InvalidPositionException extends ChessException {
    
    public InvalidPositionException(String message) {
        super(message);
    }

    public InvalidPositionException(int row, int col) {
        super(String.format("Invalid position: row=%d, col=%d (must be 0-7)", row, col));
    }
}


