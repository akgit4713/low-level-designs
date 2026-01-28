package chess.pieces;

import chess.enums.Color;
import chess.enums.PieceType;
import chess.models.Piece;
import chess.models.Position;
import chess.strategies.RookMoveStrategy;

/**
 * Represents the Rook piece.
 * Moves horizontally or vertically any number of squares.
 */
public class Rook extends Piece {

    public Rook(Color color, Position position) {
        super(color, position, PieceType.ROOK, new RookMoveStrategy());
    }

    private Rook(Color color, Position position, boolean hasMoved) {
        super(color, position, PieceType.ROOK, new RookMoveStrategy());
        this.hasMoved = hasMoved;
    }

    @Override
    public Piece copy() {
        return new Rook(color, position, hasMoved);
    }

    @Override
    public Piece copyTo(Position newPosition) {
        return new Rook(color, newPosition, hasMoved);
    }
}


