package chess.pieces;

import chess.enums.Color;
import chess.enums.PieceType;
import chess.models.Piece;
import chess.models.Position;
import chess.strategies.BishopMoveStrategy;

/**
 * Represents the Bishop piece.
 * Moves diagonally any number of squares.
 */
public class Bishop extends Piece {

    public Bishop(Color color, Position position) {
        super(color, position, PieceType.BISHOP, new BishopMoveStrategy());
    }

    private Bishop(Color color, Position position, boolean hasMoved) {
        super(color, position, PieceType.BISHOP, new BishopMoveStrategy());
        this.hasMoved = hasMoved;
    }

    @Override
    public Piece copy() {
        return new Bishop(color, position, hasMoved);
    }

    @Override
    public Piece copyTo(Position newPosition) {
        return new Bishop(color, newPosition, hasMoved);
    }
}


