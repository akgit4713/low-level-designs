package chess.validators;

import chess.enums.Color;
import chess.enums.PieceType;
import chess.models.Board;
import chess.models.Move;
import chess.models.Piece;
import chess.models.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects check, checkmate, and stalemate conditions.
 * 
 * Single Responsibility: Detect game-ending conditions.
 */
public class CheckDetector {
    
    private final Board board;

    public CheckDetector(Board board) {
        this.board = board;
    }

    /**
     * Checks if the king of the given color is in check.
     */
    public boolean isInCheck(Color kingColor) {
        Position kingPosition = board.getKingPosition(kingColor);
        if (kingPosition == null) {
            return false;  // No king found (shouldn't happen in valid game)
        }

        Color opponentColor = kingColor.opposite();
        return isSquareAttackedBy(kingPosition, opponentColor);
    }

    /**
     * Checks if the given color is in checkmate.
     * Checkmate = in check AND no legal moves available.
     */
    public boolean isCheckmate(Color color) {
        if (!isInCheck(color)) {
            return false;
        }

        // Try all possible moves for all pieces of this color
        return !hasAnyLegalMoves(color);
    }

    /**
     * Checks if the given color is in stalemate.
     * Stalemate = NOT in check AND no legal moves available.
     */
    public boolean isStalemate(Color color) {
        if (isInCheck(color)) {
            return false;
        }

        return !hasAnyLegalMoves(color);
    }

    /**
     * Gets all pieces that are attacking the given position.
     */
    public List<Piece> getAttackers(Position position, Color attackerColor) {
        List<Piece> attackers = new ArrayList<>();
        
        for (Piece piece : board.getPiecesByColor(attackerColor)) {
            if (piece.getMoveStrategy().canMove(board, piece, piece.getPosition(), position)) {
                attackers.add(piece);
            }
        }
        
        return attackers;
    }

    /**
     * Checks if a square is attacked by any piece of the given color.
     */
    public boolean isSquareAttackedBy(Position position, Color attackerColor) {
        for (Piece piece : board.getPiecesByColor(attackerColor)) {
            if (piece.getMoveStrategy().canMove(board, piece, piece.getPosition(), position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the player has any legal moves.
     */
    private boolean hasAnyLegalMoves(Color color) {
        MoveValidator validator = new MoveValidator(board);
        
        for (Piece piece : board.getPiecesByColor(color)) {
            List<Move> validMoves = validator.getValidMoves(piece);
            if (!validMoves.isEmpty()) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Checks for insufficient material (automatic draw).
     * Returns true if neither side can checkmate.
     */
    public boolean isInsufficientMaterial() {
        List<Piece> whitePieces = board.getPiecesByColor(Color.WHITE);
        List<Piece> blackPieces = board.getPiecesByColor(Color.BLACK);

        // Filter out kings
        List<Piece> whitePiecesNoKing = whitePieces.stream()
                .filter(p -> p.getType() != PieceType.KING)
                .toList();
        List<Piece> blackPiecesNoKing = blackPieces.stream()
                .filter(p -> p.getType() != PieceType.KING)
                .toList();

        int whiteCount = whitePiecesNoKing.size();
        int blackCount = blackPiecesNoKing.size();

        // King vs King
        if (whiteCount == 0 && blackCount == 0) {
            return true;
        }

        // King + minor piece vs King
        if ((whiteCount == 0 && blackCount == 1 && isMinorPiece(blackPiecesNoKing.get(0))) ||
            (blackCount == 0 && whiteCount == 1 && isMinorPiece(whitePiecesNoKing.get(0)))) {
            return true;
        }

        // King + Bishop vs King + Bishop (same color bishops)
        if (whiteCount == 1 && blackCount == 1) {
            Piece whitePiece = whitePiecesNoKing.get(0);
            Piece blackPiece = blackPiecesNoKing.get(0);
            
            if (whitePiece.getType() == PieceType.BISHOP && 
                blackPiece.getType() == PieceType.BISHOP) {
                // Check if bishops are on same color squares
                boolean whiteBishopOnLight = (whitePiece.getPosition().getRow() + 
                                              whitePiece.getPosition().getCol()) % 2 == 0;
                boolean blackBishopOnLight = (blackPiece.getPosition().getRow() + 
                                              blackPiece.getPosition().getCol()) % 2 == 0;
                return whiteBishopOnLight == blackBishopOnLight;
            }
        }

        return false;
    }

    private boolean isMinorPiece(Piece piece) {
        return piece.getType() == PieceType.BISHOP || piece.getType() == PieceType.KNIGHT;
    }
}


