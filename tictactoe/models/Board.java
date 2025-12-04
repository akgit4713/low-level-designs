package tictactoe.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game board.
 * Single Responsibility: Managing board state and cell access.
 * Open/Closed: Can be extended for different board sizes.
 */
public class Board {
    private final int size;
    private final Cell[][] grid;

    public Board(int size) {
        this.size = size;
        this.grid = new Cell[size][size];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Cell getCell(int row, int col) {
        if (isValidPosition(row, col)) {
            return grid[row][col];
        }
        throw new IllegalArgumentException("Invalid position: (" + row + ", " + col + ")");
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public boolean isCellEmpty(int row, int col) {
        return isValidPosition(row, col) && grid[row][col].isEmpty();
    }

    public boolean placeSymbol(int row, int col, Symbol symbol) {
        if (isCellEmpty(row, col)) {
            grid[row][col].setSymbol(symbol);
            return true;
        }
        return false;
    }

    public List<Cell> getEmptyCells() {
        List<Cell> emptyCells = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j].isEmpty()) {
                    emptyCells.add(grid[i][j]);
                }
            }
        }
        return emptyCells;
    }

    public boolean isFull() {
        return getEmptyCells().isEmpty();
    }

    public void reset() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j].clear();
            }
        }
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public void display() {
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.print(" ");
            for (int j = 0; j < size; j++) {
                System.out.print(" " + grid[i][j].getSymbol().getDisplayChar() + " ");
                if (j < size - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (i < size - 1) {
                System.out.println("  " + "-".repeat(size * 4 - 1));
            }
        }
        System.out.println();
    }
}

