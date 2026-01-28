package chess.models;

import chess.enums.Color;
import chess.enums.PieceType;
import chess.strategies.MoveStrategy;

import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for all chess pieces.
 * Each concrete piece type (King, Queen, etc.) extends this class.
 * 
 * Single Responsibility: Represent a chess piece with its properties and movement strategy.
 */
public abstract class Piece {
    
    protected final Color color;
    protected Position position;
    protected final PieceType type;
    protected final MoveStrategy moveStrategy;
    protected boolean hasMoved;

    protected Piece(Color color, Position position, PieceType type, MoveStrategy moveStrategy) {
        this.color = color;
        this.position = position;
        this.type = type;
        this.moveStrategy = moveStrategy;
        this.hasMoved = false;
    }

    public Color getColor() {
        return color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public PieceType getType() {
        return type;
    }

    public MoveStrategy getMoveStrategy() {
        return moveStrategy;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Returns the symbol for this piece.
     * Uppercase for white, lowercase for black.
     */
    public char getSymbol() {
        char symbol = type.getSymbol();
        return color == Color.WHITE ? symbol : Character.toLowerCase(symbol);
    }

    /**
     * Returns the Unicode chess symbol for display.
     */
    public char getUnicodeSymbol() {
        return type.getUnicodeSymbol(color);
    }

    /**
     * Returns the value of this piece.
     */
    public int getValue() {
        return type.getValue();
    }

    /**
     * Gets all valid moves for this piece on the given board.
     * Delegates to the move strategy.
     */
    public List<Move> getValidMoves(Board board) {
        return moveStrategy.getValidMoves(board, this);
    }

    /**
     * Checks if this piece can move to the target position.
     * Delegates to the move strategy.
     */
    public boolean canMoveTo(Board board, Position to) {
        return moveStrategy.canMove(board, this, position, to);
    }

    /**
     * Creates a copy of this piece at a new position.
     */
    public abstract Piece copy();

    /**
     * Creates a copy of this piece at the specified position.
     */
    public abstract Piece copyTo(Position newPosition);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return color == piece.color && type == piece.type && Objects.equals(position, piece.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, position, type);
    }

    @Override
    public String toString() {
        return color.getDisplayName() + " " + type.getDisplayName() + " at " + position;
    }
}


