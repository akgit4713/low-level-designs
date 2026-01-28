package chess.models;

import chess.enums.Color;
import chess.enums.PieceType;
import chess.exceptions.InvalidPositionException;
import chess.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the 8x8 chess board.
 * Manages piece positions and board state.
 * 
 * Single Responsibility: Manage board state and piece positions.
 */
public class Board {
    
    public static final int SIZE = 8;
    
    private final Piece[][] grid;
    private Position enPassantTarget;  // Square where en passant capture can occur
    private Move lastMove;

    public Board() {
        this.grid = new Piece[SIZE][SIZE];
    }

    /**
     * Creates a new board with standard starting position.
     */
    public static Board createStandardBoard() {
        Board board = new Board();
        board.setupStandardPosition();
        return board;
    }

    /**
     * Sets up the standard chess starting position.
     */
    private void setupStandardPosition() {
        // White pieces (row 0)
        setupBackRank(Color.WHITE, 0);
        setupPawnRank(Color.WHITE, 1);
        
        // Black pieces (row 7)
        setupBackRank(Color.BLACK, 7);
        setupPawnRank(Color.BLACK, 6);
    }

    private void setupBackRank(Color color, int row) {
        grid[row][0] = new Rook(color, new Position(row, 0));
        grid[row][1] = new Knight(color, new Position(row, 1));
        grid[row][2] = new Bishop(color, new Position(row, 2));
        grid[row][3] = new Queen(color, new Position(row, 3));
        grid[row][4] = new King(color, new Position(row, 4));
        grid[row][5] = new Bishop(color, new Position(row, 5));
        grid[row][6] = new Knight(color, new Position(row, 6));
        grid[row][7] = new Rook(color, new Position(row, 7));
    }

    private void setupPawnRank(Color color, int row) {
        for (int col = 0; col < SIZE; col++) {
            grid[row][col] = new Pawn(color, new Position(row, col));
        }
    }

    /**
     * Gets the piece at the given position.
     */
    public Piece getPieceAt(Position position) {
        if (!isValidPosition(position)) {
            return null;
        }
        return grid[position.getRow()][position.getCol()];
    }

    /**
     * Gets the piece at the given row and column.
     */
    public Piece getPieceAt(int row, int col) {
        if (!isValidPosition(row, col)) {
            return null;
        }
        return grid[row][col];
    }

    /**
     * Sets a piece at the given position.
     */
    public void setPieceAt(Position position, Piece piece) {
        validatePosition(position);
        grid[position.getRow()][position.getCol()] = piece;
        if (piece != null) {
            piece.setPosition(position);
        }
    }

    /**
     * Removes and returns the piece at the given position.
     */
    public Piece removePiece(Position position) {
        validatePosition(position);
        Piece piece = grid[position.getRow()][position.getCol()];
        grid[position.getRow()][position.getCol()] = null;
        return piece;
    }

    /**
     * Moves a piece from one position to another.
     * Returns the captured piece if any.
     */
    public Piece movePiece(Position from, Position to) {
        validatePosition(from);
        validatePosition(to);
        
        Piece piece = grid[from.getRow()][from.getCol()];
        Piece captured = grid[to.getRow()][to.getCol()];
        
        // Move the piece
        grid[to.getRow()][to.getCol()] = piece;
        grid[from.getRow()][from.getCol()] = null;
        
        if (piece != null) {
            piece.setPosition(to);
            piece.setHasMoved(true);
        }
        
        return captured;
    }

    /**
     * Checks if a position is within board boundaries.
     */
    public boolean isValidPosition(Position position) {
        return position != null && position.isValid();
    }

    /**
     * Checks if row and column are within board boundaries.
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    private void validatePosition(Position position) {
        if (!isValidPosition(position)) {
            throw new InvalidPositionException(position.getRow(), position.getCol());
        }
    }

    /**
     * Checks if a position is occupied by any piece.
     */
    public boolean isOccupied(Position position) {
        return getPieceAt(position) != null;
    }

    /**
     * Checks if a position is occupied by a piece of the given color.
     */
    public boolean isOccupiedByColor(Position position, Color color) {
        Piece piece = getPieceAt(position);
        return piece != null && piece.getColor() == color;
    }

    /**
     * Checks if the path between two positions is clear (for rooks, bishops, queens).
     * Does not include the start and end positions.
     */
    public boolean isPathClear(Position from, Position to) {
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getCol() - from.getCol();
        
        // Determine step direction
        int rowStep = Integer.signum(rowDiff);
        int colStep = Integer.signum(colDiff);
        
        // Check each position along the path
        int currentRow = from.getRow() + rowStep;
        int currentCol = from.getCol() + colStep;
        
        while (currentRow != to.getRow() || currentCol != to.getCol()) {
            if (grid[currentRow][currentCol] != null) {
                return false;
            }
            currentRow += rowStep;
            currentCol += colStep;
        }
        
        return true;
    }

    /**
     * Gets all pieces of the given color.
     */
    public List<Piece> getPiecesByColor(Color color) {
        List<Piece> pieces = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Piece piece = grid[row][col];
                if (piece != null && piece.getColor() == color) {
                    pieces.add(piece);
                }
            }
        }
        return pieces;
    }

    /**
     * Finds the king of the given color.
     */
    public Optional<Piece> findKing(Color color) {
        for (Piece piece : getPiecesByColor(color)) {
            if (piece.getType() == PieceType.KING) {
                return Optional.of(piece);
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the position of the king of the given color.
     */
    public Position getKingPosition(Color color) {
        return findKing(color)
                .map(Piece::getPosition)
                .orElse(null);
    }

    /**
     * Gets all pieces on the board.
     */
    public List<Piece> getAllPieces() {
        List<Piece> pieces = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] != null) {
                    pieces.add(grid[row][col]);
                }
            }
        }
        return pieces;
    }

    public Position getEnPassantTarget() {
        return enPassantTarget;
    }

    public void setEnPassantTarget(Position enPassantTarget) {
        this.enPassantTarget = enPassantTarget;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
    }

    /**
     * Creates a deep copy of this board.
     */
    public Board copy() {
        Board copy = new Board();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] != null) {
                    copy.grid[row][col] = grid[row][col].copy();
                }
            }
        }
        copy.enPassantTarget = this.enPassantTarget;
        copy.lastMove = this.lastMove;
        return copy;
    }

    /**
     * Displays the board in the console.
     */
    public void display() {
        System.out.println();
        System.out.println("    a   b   c   d   e   f   g   h");
        System.out.println("  ┌───┬───┬───┬───┬───┬───┬───┬───┐");
        
        for (int row = SIZE - 1; row >= 0; row--) {
            System.out.print((row + 1) + " │");
            for (int col = 0; col < SIZE; col++) {
                Piece piece = grid[row][col];
                if (piece != null) {
                    System.out.print(" " + piece.getSymbol() + " │");
                } else {
                    System.out.print("   │");
                }
            }
            System.out.println(" " + (row + 1));
            
            if (row > 0) {
                System.out.println("  ├───┼───┼───┼───┼───┼───┼───┼───┤");
            }
        }
        
        System.out.println("  └───┴───┴───┴───┴───┴───┴───┴───┘");
        System.out.println("    a   b   c   d   e   f   g   h");
        System.out.println();
    }

    /**
     * Displays the board with Unicode chess pieces.
     */
    public void displayUnicode() {
        System.out.println();
        System.out.println("    a   b   c   d   e   f   g   h");
        System.out.println("  ┌───┬───┬───┬───┬───┬───┬───┬───┐");
        
        for (int row = SIZE - 1; row >= 0; row--) {
            System.out.print((row + 1) + " │");
            for (int col = 0; col < SIZE; col++) {
                Piece piece = grid[row][col];
                if (piece != null) {
                    System.out.print(" " + piece.getUnicodeSymbol() + " │");
                } else {
                    System.out.print("   │");
                }
            }
            System.out.println(" " + (row + 1));
            
            if (row > 0) {
                System.out.println("  ├───┼───┼───┼───┼───┼───┼───┼───┤");
            }
        }
        
        System.out.println("  └───┴───┴───┴───┴───┴───┴───┴───┘");
        System.out.println("    a   b   c   d   e   f   g   h");
        System.out.println();
    }
}


