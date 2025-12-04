package tictactoe.strategies;

import tictactoe.models.Board;
import tictactoe.models.Cell;
import tictactoe.models.Symbol;

/**
 * Strategy to check for diagonal wins (both diagonals).
 * Single Responsibility: Only checks diagonal-based winning.
 */
public class DiagonalWinningStrategy implements WinningStrategy {

    @Override
    public boolean checkWin(Board board, Symbol symbol) {
        return checkMainDiagonal(board, symbol) || checkAntiDiagonal(board, symbol);
    }

    private boolean checkMainDiagonal(Board board, Symbol symbol) {
        int size = board.getSize();
        Cell[][] grid = board.getGrid();
        
        for (int i = 0; i < size; i++) {
            if (grid[i][i].getSymbol() != symbol) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAntiDiagonal(Board board, Symbol symbol) {
        int size = board.getSize();
        Cell[][] grid = board.getGrid();
        
        for (int i = 0; i < size; i++) {
            if (grid[i][size - 1 - i].getSymbol() != symbol) {
                return false;
            }
        }
        return true;
    }
}

