package chess.pieces;

import chess.enums.Color;
import chess.enums.PieceType;
import chess.models.Piece;
import chess.models.Position;
import chess.strategies.QueenMoveStrategy;

/**
 * Represents the Queen piece.
 * The most powerful piece - combines Rook and Bishop movement.
 */
public class Queen extends Piece {

    public Queen(Color color, Position position) {
        super(color, position, PieceType.QUEEN, new QueenMoveStrategy());
    }

    private Queen(Color color, Position position, boolean hasMoved) {
        super(color, position, PieceType.QUEEN, new QueenMoveStrategy());
        this.hasMoved = hasMoved;
    }

    @Override
    public Piece copy() {
        return new Queen(color, position, hasMoved);
    }

    @Override
    public Piece copyTo(Position newPosition) {
        return new Queen(color, newPosition, hasMoved);
    }
}


