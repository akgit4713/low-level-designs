package tictactoe.models;

import tictactoe.players.Player;

/**
 * Immutable class representing a move in the game.
 * Single Responsibility: Encapsulating move data.
 */
public class Move {
    private final int row;
    private final int col;
    private final Player player;
    private final long timestamp;

    public Move(int row, int col, Player player) {
        this.row = row;
        this.col = col;
        this.player = player;
        this.timestamp = System.currentTimeMillis();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Player getPlayer() {
        return player;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("Move{player=%s, position=(%d,%d)}", 
            player.getName(), row, col);
    }
}

