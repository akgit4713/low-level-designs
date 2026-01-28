package chess.pieces;

import chess.enums.Color;
import chess.enums.PieceType;
import chess.models.Piece;
import chess.models.Position;
import chess.strategies.PawnMoveStrategy;

/**
 * Represents the Pawn piece.
 * Moves forward, captures diagonally, with special first move and promotion.
 */
public class Pawn extends Piece {

    public Pawn(Color color, Position position) {
        super(color, position, PieceType.PAWN, new PawnMoveStrategy());
    }

    private Pawn(Color color, Position position, boolean hasMoved) {
        super(color, position, PieceType.PAWN, new PawnMoveStrategy());
        this.hasMoved = hasMoved;
    }

    @Override
    public Piece copy() {
        return new Pawn(color, position, hasMoved);
    }

    @Override
    public Piece copyTo(Position newPosition) {
        return new Pawn(color, newPosition, hasMoved);
    }
}


