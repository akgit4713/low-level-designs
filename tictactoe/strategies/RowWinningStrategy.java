package tictactoe.strategies;

import tictactoe.models.Board;
import tictactoe.models.Cell;
import tictactoe.models.Symbol;

/**
 * Strategy to check for horizontal (row) wins.
 * Single Responsibility: Only checks row-based winning.
 */
public class RowWinningStrategy implements WinningStrategy {

    @Override
    public boolean checkWin(Board board, Symbol symbol) {
        int size = board.getSize();
        Cell[][] grid = board.getGrid();
        
        for (int row = 0; row < size; row++) {
            boolean rowWin = true;
            for (int col = 0; col < size; col++) {
                if (grid[row][col].getSymbol() != symbol) {
                    rowWin = false;
                    break;
                }
            }
            if (rowWin) {
                return true;
            }
        }
        return false;
    }
}

