package chess.factories;

import chess.enums.Color;
import chess.enums.PieceType;
import chess.models.Piece;
import chess.models.Position;
import chess.pieces.*;

/**
 * Factory for creating chess pieces.
 * 
 * Factory Pattern: Encapsulates piece creation logic.
 */
public class PieceFactory {

    /**
     * Creates a piece of the specified type and color at the given position.
     */
    public static Piece createPiece(PieceType type, Color color, Position position) {
        return switch (type) {
            case KING -> new King(color, position);
            case QUEEN -> new Queen(color, position);
            case ROOK -> new Rook(color, position);
            case BISHOP -> new Bishop(color, position);
            case KNIGHT -> new Knight(color, position);
            case PAWN -> new Pawn(color, position);
        };
    }

    /**
     * Creates a piece from algebraic notation position (e.g., "e4").
     */
    public static Piece createPiece(PieceType type, Color color, String algebraicPosition) {
        Position position = Position.fromAlgebraic(algebraicPosition);
        return createPiece(type, color, position);
    }
}


