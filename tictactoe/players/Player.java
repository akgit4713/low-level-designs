package tictactoe.players;

import tictactoe.models.Board;
import tictactoe.models.Move;
import tictactoe.models.Symbol;

/**
 * Abstract base class for all players.
 * Follows Liskov Substitution Principle - all player types can be used interchangeably.
 * Follows Dependency Inversion - Game depends on this abstraction, not concrete implementations.
 */
public abstract class Player {
    
    protected final String name;
    protected final Symbol symbol;

    public Player(String name, Symbol symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    /**
     * Template method pattern - defines the algorithm skeleton.
     * Subclasses implement how to get the move.
     */
    public abstract Move makeMove(Board board);

    /**
     * Hook method for validation - can be overridden.
     */
    protected boolean isValidMove(Board board, int row, int col) {
        return board.isCellEmpty(row, col);
    }

    @Override
    public String toString() {
        return name + " (" + symbol + ")";
    }
}

