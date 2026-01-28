package chess.pieces;

import chess.enums.Color;
import chess.enums.PieceType;
import chess.models.Piece;
import chess.models.Position;
import chess.strategies.KingMoveStrategy;

/**
 * Represents the King piece.
 * The most important piece - game ends when it's checkmated.
 */
public class King extends Piece {

    public King(Color color, Position position) {
        super(color, position, PieceType.KING, new KingMoveStrategy());
    }

    private King(Color color, Position position, boolean hasMoved) {
        super(color, position, PieceType.KING, new KingMoveStrategy());
        this.hasMoved = hasMoved;
    }

    @Override
    public Piece copy() {
        return new King(color, position, hasMoved);
    }

    @Override
    public Piece copyTo(Position newPosition) {
        return new King(color, newPosition, hasMoved);
    }
}


