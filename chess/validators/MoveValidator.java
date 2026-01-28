package chess.validators;

import chess.enums.Color;
import chess.enums.MoveType;
import chess.enums.PieceType;
import chess.models.Board;
import chess.models.Move;
import chess.models.Piece;
import chess.models.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates chess moves according to game rules.
 * 
 * Single Responsibility: Validate if moves are legal.
 */
public class MoveValidator {
    
    private final Board board;
    private final CheckDetector checkDetector;

    public MoveValidator(Board board) {
        this.board = board;
        this.checkDetector = new CheckDetector(board);
    }

    /**
     * Validates if a move is legal.
     * Checks:
     * 1. Piece exists at source
     * 2. Destination is valid
     * 3. Move follows piece movement rules
     * 4. Move doesn't leave own king in check
     * 5. Castling through check validation
     */
    public boolean isValidMove(Move move, Color currentTurn) {
        Position from = move.getFrom();
        Position to = move.getTo();

        // Basic validation
        if (!to.isValid()) return false;
        if (from.equals(to)) return false;

        // Get the piece
        Piece piece = board.getPieceAt(from);
        if (piece == null) return false;

        // Must be the current player's piece
        if (piece.getColor() != currentTurn) return false;

        // Can't capture own piece
        Piece targetPiece = board.getPieceAt(to);
        if (targetPiece != null && targetPiece.getColor() == currentTurn) {
            return false;
        }

        // Check if piece can make this move (movement pattern)
        if (!piece.getMoveStrategy().canMove(board, piece, from, to)) {
            return false;
        }

        // Special castling validation - can't castle through check
        if (move.isCastling() && !isValidCastling(piece, move)) {
            return false;
        }

        // Move must not leave own king in check
        if (wouldLeaveKingInCheck(move, currentTurn)) {
            return false;
        }

        return true;
    }

    /**
     * Gets all valid moves for a piece, filtering out moves that leave king in check.
     */
    public List<Move> getValidMoves(Piece piece) {
        List<Move> allMoves = piece.getValidMoves(board);
        List<Move> validMoves = new ArrayList<>();

        for (Move move : allMoves) {
            if (!wouldLeaveKingInCheck(move, piece.getColor())) {
                // Additional castling validation
                if (move.isCastling()) {
                    if (isValidCastling(piece, move)) {
                        validMoves.add(move);
                    }
                } else {
                    validMoves.add(move);
                }
            }
        }

        return validMoves;
    }

    /**
     * Gets all valid moves for a color.
     */
    public List<Move> getAllValidMoves(Color color) {
        List<Move> allMoves = new ArrayList<>();
        for (Piece piece : board.getPiecesByColor(color)) {
            allMoves.addAll(getValidMoves(piece));
        }
        return allMoves;
    }

    /**
     * Checks if the move would leave the player's king in check.
     */
    public boolean wouldLeaveKingInCheck(Move move, Color color) {
        // Simulate the move on a copy of the board
        Board boardCopy = board.copy();
        simulateMove(boardCopy, move);
        
        // Check if king is in check after the move
        CheckDetector tempDetector = new CheckDetector(boardCopy);
        return tempDetector.isInCheck(color);
    }

    private void simulateMove(Board boardCopy, Move move) {
        Position from = move.getFrom();
        Position to = move.getTo();
        Piece piece = boardCopy.getPieceAt(from);

        // Handle en passant
        if (move.getMoveType() == MoveType.EN_PASSANT) {
            int capturedPawnRow = from.getRow();
            boardCopy.removePiece(new Position(capturedPawnRow, to.getCol()));
        }

        // Handle castling
        if (move.getMoveType() == MoveType.CASTLING_KINGSIDE) {
            int row = from.getRow();
            boardCopy.movePiece(new Position(row, 7), new Position(row, 5));
        } else if (move.getMoveType() == MoveType.CASTLING_QUEENSIDE) {
            int row = from.getRow();
            boardCopy.movePiece(new Position(row, 0), new Position(row, 3));
        }

        // Regular move
        boardCopy.movePiece(from, to);

        // Handle promotion
        if (move.getMoveType() == MoveType.PAWN_PROMOTION && move.getPromotionPiece() != null) {
            Piece promotedPiece = createPromotedPiece(piece.getColor(), to, move.getPromotionPiece());
            boardCopy.setPieceAt(to, promotedPiece);
        }
    }

    private Piece createPromotedPiece(Color color, Position position, PieceType type) {
        return switch (type) {
            case QUEEN -> new chess.pieces.Queen(color, position);
            case ROOK -> new chess.pieces.Rook(color, position);
            case BISHOP -> new chess.pieces.Bishop(color, position);
            case KNIGHT -> new chess.pieces.Knight(color, position);
            default -> throw new IllegalArgumentException("Cannot promote to " + type);
        };
    }

    private boolean isValidCastling(Piece king, Move move) {
        // King must not be in check
        if (checkDetector.isInCheck(king.getColor())) {
            return false;
        }

        Position from = move.getFrom();
        Position to = move.getTo();
        int row = from.getRow();
        int direction = to.getCol() > from.getCol() ? 1 : -1;

        // Check each square the king passes through
        for (int col = from.getCol() + direction; col != to.getCol(); col += direction) {
            Position passingSquare = new Position(row, col);
            if (wouldBeInCheckAt(king, passingSquare)) {
                return false;
            }
        }

        // Check final square
        return !wouldBeInCheckAt(king, to);
    }

    private boolean wouldBeInCheckAt(Piece king, Position position) {
        Board boardCopy = board.copy();
        boardCopy.movePiece(king.getPosition(), position);
        CheckDetector tempDetector = new CheckDetector(boardCopy);
        return tempDetector.isInCheck(king.getColor());
    }
}


