package chess.players;

import chess.enums.Color;
import chess.models.Board;
import chess.models.Move;

/**
 * Abstract base class for chess players.
 * 
 * Template Method Pattern: Defines the contract for getting moves,
 * with concrete implementations deciding how to obtain moves.
 */
public abstract class Player {
    
    protected final String name;
    protected final Color color;

    protected Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    /**
     * Gets the next move from the player.
     * 
     * @param board The current board state
     * @return The move the player wants to make
     */
    public abstract Move makeMove(Board board);

    @Override
    public String toString() {
        return name + " (" + color.getDisplayName() + ")";
    }
}


