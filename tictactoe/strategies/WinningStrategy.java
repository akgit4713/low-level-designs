package tictactoe.strategies;

import tictactoe.models.Board;
import tictactoe.models.Symbol;

/**
 * Strategy interface for determining win conditions.
 * Follows Open/Closed Principle - new strategies can be added without modifying existing code.
 * Follows Interface Segregation - single method interface.
 */
public interface WinningStrategy {
    
    /**
     * Checks if the given symbol has won on the board.
     * @param board The game board to check
     * @param symbol The symbol to check for winning
     * @return true if the symbol has won, false otherwise
     */
    boolean checkWin(Board board, Symbol symbol);
    
    /**
     * Returns the name of this strategy for debugging/display purposes.
     */
    default String getStrategyName() {
        return this.getClass().getSimpleName();
    }
}

