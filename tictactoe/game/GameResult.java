package tictactoe.game;

import tictactoe.players.Player;

/**
 * Immutable class representing the result of a completed game.
 */
public class GameResult {
    
    private final GameState finalState;
    private final Player winner;
    private final int totalMoves;
    private final long gameDurationMs;

    public GameResult(GameState finalState, Player winner, int totalMoves, long gameDurationMs) {
        this.finalState = finalState;
        this.winner = winner;
        this.totalMoves = totalMoves;
        this.gameDurationMs = gameDurationMs;
    }

    public GameState getFinalState() {
        return finalState;
    }

    public Player getWinner() {
        return winner;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public long getGameDurationMs() {
        return gameDurationMs;
    }

    public boolean isDraw() {
        return finalState == GameState.DRAW;
    }

    public boolean hasWinner() {
        return finalState == GameState.WIN && winner != null;
    }

    @Override
    public String toString() {
        if (hasWinner()) {
            return String.format("Winner: %s | Moves: %d | Duration: %dms", 
                               winner.getName(), totalMoves, gameDurationMs);
        } else {
            return String.format("Result: DRAW | Moves: %d | Duration: %dms", 
                               totalMoves, gameDurationMs);
        }
    }
}

