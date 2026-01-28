package chess.models;

import java.util.Objects;

/**
 * Represents a position on the chess board.
 * Uses 0-indexed row and column (0-7 for both).
 * 
 * Board layout:
 *   Row 0 = White's back rank (1 in chess notation)
 *   Row 7 = Black's back rank (8 in chess notation)
 *   Col 0 = 'a' file
 *   Col 7 = 'h' file
 */
public class Position {
    
    public static final int BOARD_SIZE = 8;
    
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Creates a position from algebraic notation (e.g., "e4", "a1").
     */
    public static Position fromAlgebraic(String notation) {
        if (notation == null || notation.length() != 2) {
            throw new IllegalArgumentException("Invalid algebraic notation: " + notation);
        }
        
        char file = Character.toLowerCase(notation.charAt(0));
        char rank = notation.charAt(1);
        
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Invalid algebraic notation: " + notation);
        }
        
        int col = file - 'a';
        int row = rank - '1';
        
        return new Position(row, col);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    /**
     * Checks if this position is within the valid board boundaries.
     */
    public boolean isValid() {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    /**
     * Returns a new position offset by the given row and column deltas.
     */
    public Position offset(int rowDelta, int colDelta) {
        return new Position(row + rowDelta, col + colDelta);
    }

    /**
     * Returns the algebraic notation for this position (e.g., "e4").
     */
    public String toAlgebraic() {
        if (!isValid()) {
            return "??";
        }
        char file = (char) ('a' + col);
        char rank = (char) ('1' + row);
        return "" + file + rank;
    }

    /**
     * Calculates the row difference to another position.
     */
    public int rowDiff(Position other) {
        return other.row - this.row;
    }

    /**
     * Calculates the column difference to another position.
     */
    public int colDiff(Position other) {
        return other.col - this.col;
    }

    /**
     * Calculates the absolute row distance to another position.
     */
    public int rowDistance(Position other) {
        return Math.abs(rowDiff(other));
    }

    /**
     * Calculates the absolute column distance to another position.
     */
    public int colDistance(Position other) {
        return Math.abs(colDiff(other));
    }

    /**
     * Checks if this position is on the same row as another.
     */
    public boolean isSameRow(Position other) {
        return this.row == other.row;
    }

    /**
     * Checks if this position is on the same column as another.
     */
    public boolean isSameCol(Position other) {
        return this.col == other.col;
    }

    /**
     * Checks if this position is on the same diagonal as another.
     */
    public boolean isDiagonal(Position other) {
        return rowDistance(other) == colDistance(other) && !this.equals(other);
    }

    /**
     * Checks if this position is adjacent to another (including diagonally).
     */
    public boolean isAdjacent(Position other) {
        return rowDistance(other) <= 1 && colDistance(other) <= 1 && !this.equals(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return toAlgebraic();
    }
}


