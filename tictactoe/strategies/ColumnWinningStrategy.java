package tictactoe.strategies;

import tictactoe.models.Board;
import tictactoe.models.Cell;
import tictactoe.models.Symbol;

/**
 * Strategy to check for vertical (column) wins.
 * Single Responsibility: Only checks column-based winning.
 */
public class ColumnWinningStrategy implements WinningStrategy {

    @Override
    public boolean checkWin(Board board, Symbol symbol) {
        int size = board.getSize();
        Cell[][] grid = board.getGrid();
        
        for (int col = 0; col < size; col++) {
            boolean colWin = true;
            for (int row = 0; row < size; row++) {
                if (grid[row][col].getSymbol() != symbol) {
                    colWin = false;
                    break;
                }
            }
            if (colWin) {
                return true;
            }
        }
        return false;
    }
}

