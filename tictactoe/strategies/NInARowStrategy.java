package tictactoe.strategies;

import tictactoe.models.Board;
import tictactoe.models.Cell;
import tictactoe.models.Symbol;

/**
 * Configurable N-in-a-row strategy for larger boards.
 * Win by getting N consecutive symbols in any direction.
 * Demonstrates Open/Closed Principle with configurable win condition.
 */
public class NInARowStrategy implements WinningStrategy {
    
    private final int requiredInARow;
    
    public NInARowStrategy(int requiredInARow) {
        this.requiredInARow = requiredInARow;
    }

    @Override
    public boolean checkWin(Board board, Symbol symbol) {
        int size = board.getSize();
        Cell[][] grid = board.getGrid();
        
        // Check all possible directions from each cell
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (checkFromPosition(grid, row, col, symbol, size)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkFromPosition(Cell[][] grid, int row, int col, Symbol symbol, int size) {
        // Directions: horizontal, vertical, diagonal, anti-diagonal
        int[][] directions = {
            {0, 1},   // Horizontal
            {1, 0},   // Vertical
            {1, 1},   // Diagonal
            {1, -1}   // Anti-diagonal
        };
        
        for (int[] dir : directions) {
            if (checkDirection(grid, row, col, dir[0], dir[1], symbol, size)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDirection(Cell[][] grid, int startRow, int startCol, 
                                   int dRow, int dCol, Symbol symbol, int size) {
        int count = 0;
        int row = startRow;
        int col = startCol;
        
        while (row >= 0 && row < size && col >= 0 && col < size && count < requiredInARow) {
            if (grid[row][col].getSymbol() != symbol) {
                return false;
            }
            count++;
            row += dRow;
            col += dCol;
        }
        
        return count == requiredInARow;
    }

    @Override
    public String getStrategyName() {
        return "NInARowStrategy(" + requiredInARow + ")";
    }
}

