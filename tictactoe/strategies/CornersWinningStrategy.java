package tictactoe.strategies;

import tictactoe.models.Board;
import tictactoe.models.Cell;
import tictactoe.models.Symbol;

/**
 * Custom winning strategy: Win by occupying all four corners.
 * Demonstrates extensibility of the Strategy Pattern.
 */
public class CornersWinningStrategy implements WinningStrategy {

    @Override
    public boolean checkWin(Board board, Symbol symbol) {
        int size = board.getSize();
        Cell[][] grid = board.getGrid();
        
        // Check all four corners
        return grid[0][0].getSymbol() == symbol &&
               grid[0][size - 1].getSymbol() == symbol &&
               grid[size - 1][0].getSymbol() == symbol &&
               grid[size - 1][size - 1].getSymbol() == symbol;
    }
}

