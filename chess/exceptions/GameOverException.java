package chess.exceptions;

import chess.enums.GameStatus;

/**
 * Exception thrown when a move is attempted on a finished game.
 */
public class GameOverException extends ChessException {
    
    private final GameStatus status;

    public GameOverException(GameStatus status) {
        super("Game is already over: " + status.getDescription());
        this.status = status;
    }

    public GameStatus getStatus() {
        return status;
    }
}


