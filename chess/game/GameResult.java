package chess.game;

import chess.enums.GameStatus;
import chess.players.Player;

/**
 * Immutable object representing the result of a chess game.
 */
public class GameResult {
    
    private final GameStatus status;
    private final Player winner;
    private final int totalMoves;
    private final long durationMillis;

    public GameResult(GameStatus status, Player winner, int totalMoves, long durationMillis) {
        this.status = status;
        this.winner = winner;
        this.totalMoves = totalMoves;
        this.durationMillis = durationMillis;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Player getWinner() {
        return winner;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public boolean isDraw() {
        return status.isDraw();
    }

    public boolean isWin() {
        return status.isWin();
    }

    @Override
    public String toString() {
        if (winner != null) {
            return status.getDescription() + " - Winner: " + winner.getName();
        }
        return status.getDescription();
    }
}


