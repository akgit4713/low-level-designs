package chess.pieces;

import chess.enums.Color;
import chess.enums.PieceType;
import chess.models.Piece;
import chess.models.Position;
import chess.strategies.KnightMoveStrategy;

/**
 * Represents the Knight piece.
 * Moves in an L-shape and can jump over other pieces.
 */
public class Knight extends Piece {

    public Knight(Color color, Position position) {
        super(color, position, PieceType.KNIGHT, new KnightMoveStrategy());
    }

    private Knight(Color color, Position position, boolean hasMoved) {
        super(color, position, PieceType.KNIGHT, new KnightMoveStrategy());
        this.hasMoved = hasMoved;
    }

    @Override
    public Piece copy() {
        return new Knight(color, position, hasMoved);
    }

    @Override
    public Piece copyTo(Position newPosition) {
        return new Knight(color, newPosition, hasMoved);
    }
}


