package tictactoe.players.botstrategies;

import tictactoe.models.Board;
import tictactoe.models.Cell;
import tictactoe.models.Move;
import tictactoe.models.Symbol;
import tictactoe.players.Player;

import java.util.List;

/**
 * Smarter bot strategy that tries to win or block opponent.
 * Priority: Win > Block > Center > Corner > Side
 */
public class SmartBotStrategy implements BotPlayingStrategy {

    @Override
    public Move decideMove(Board board, Player player) {
        Symbol mySymbol = player.getSymbol();
        Symbol opponentSymbol = (mySymbol == Symbol.X) ? Symbol.O : Symbol.X;
        int size = board.getSize();
        
        // Try to win
        Cell winningCell = findWinningMove(board, mySymbol, size);
        if (winningCell != null) {
            return new Move(winningCell.getRow(), winningCell.getCol(), player);
        }
        
        // Block opponent from winning
        Cell blockingCell = findWinningMove(board, opponentSymbol, size);
        if (blockingCell != null) {
            return new Move(blockingCell.getRow(), blockingCell.getCol(), player);
        }
        
        // Take center if available
        int center = size / 2;
        if (board.isCellEmpty(center, center)) {
            return new Move(center, center, player);
        }
        
        // Take a corner
        int[][] corners = {{0, 0}, {0, size-1}, {size-1, 0}, {size-1, size-1}};
        for (int[] corner : corners) {
            if (board.isCellEmpty(corner[0], corner[1])) {
                return new Move(corner[0], corner[1], player);
            }
        }
        
        // Take any available cell
        List<Cell> emptyCells = board.getEmptyCells();
        if (!emptyCells.isEmpty()) {
            Cell cell = emptyCells.get(0);
            return new Move(cell.getRow(), cell.getCol(), player);
        }
        
        throw new IllegalStateException("No valid moves available!");
    }

    private Cell findWinningMove(Board board, Symbol symbol, int size) {
        Cell[][] grid = board.getGrid();
        
        // Check rows
        for (int row = 0; row < size; row++) {
            Cell emptyCell = null;
            int count = 0;
            for (int col = 0; col < size; col++) {
                if (grid[row][col].getSymbol() == symbol) {
                    count++;
                } else if (grid[row][col].isEmpty()) {
                    emptyCell = grid[row][col];
                }
            }
            if (count == size - 1 && emptyCell != null) {
                return emptyCell;
            }
        }
        
        // Check columns
        for (int col = 0; col < size; col++) {
            Cell emptyCell = null;
            int count = 0;
            for (int row = 0; row < size; row++) {
                if (grid[row][col].getSymbol() == symbol) {
                    count++;
                } else if (grid[row][col].isEmpty()) {
                    emptyCell = grid[row][col];
                }
            }
            if (count == size - 1 && emptyCell != null) {
                return emptyCell;
            }
        }
        
        // Check main diagonal
        Cell emptyCell = null;
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (grid[i][i].getSymbol() == symbol) {
                count++;
            } else if (grid[i][i].isEmpty()) {
                emptyCell = grid[i][i];
            }
        }
        if (count == size - 1 && emptyCell != null) {
            return emptyCell;
        }
        
        // Check anti-diagonal
        emptyCell = null;
        count = 0;
        for (int i = 0; i < size; i++) {
            if (grid[i][size - 1 - i].getSymbol() == symbol) {
                count++;
            } else if (grid[i][size - 1 - i].isEmpty()) {
                emptyCell = grid[i][size - 1 - i];
            }
        }
        if (count == size - 1 && emptyCell != null) {
            return emptyCell;
        }
        
        return null;
    }
}

