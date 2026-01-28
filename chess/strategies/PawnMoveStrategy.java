package chess.strategies;

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
 * Movement strategy for the Pawn piece.
 * 
 * Pawn movement rules:
 * - Moves forward one square (or two from starting position)
 * - Captures diagonally forward
 * - En passant capture
 * - Promotion when reaching the opposite end
 */
public class PawnMoveStrategy implements MoveStrategy {

    @Override
    public List<Move> getValidMoves(Board board, Piece piece) {
        List<Move> moves = new ArrayList<>();
        Position from = piece.getPosition();
        Color color = piece.getColor();
        int direction = color.getPawnDirection();

        // Forward move (one square)
        Position oneForward = from.offset(direction, 0);
        if (oneForward.isValid() && !board.isOccupied(oneForward)) {
            addMove(moves, from, oneForward, piece, board, false);

            // Double move from starting position
            if (!piece.hasMoved()) {
                Position twoForward = from.offset(2 * direction, 0);
                if (twoForward.isValid() && !board.isOccupied(twoForward)) {
                    moves.add(new Move.Builder(from, twoForward)
                            .withPiece(piece)
                            .withMoveType(MoveType.PAWN_DOUBLE_MOVE)
                            .build());
                }
            }
        }

        // Diagonal captures
        addDiagonalCaptures(board, piece, from, direction, moves);

        // En passant
        addEnPassantMoves(board, piece, from, direction, moves);

        return moves;
    }

    @Override
    public boolean canMove(Board board, Piece piece, Position from, Position to) {
        if (!to.isValid()) return false;

        Color color = piece.getColor();
        int direction = color.getPawnDirection();
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getCol() - from.getCol();

        // Moving in correct direction
        if (direction > 0 && rowDiff <= 0) return false;
        if (direction < 0 && rowDiff >= 0) return false;

        int absRowDiff = Math.abs(rowDiff);
        int absColDiff = Math.abs(colDiff);

        // Forward move (no column change)
        if (absColDiff == 0) {
            // One square forward
            if (absRowDiff == 1) {
                return !board.isOccupied(to);
            }
            // Two squares forward from starting position
            if (absRowDiff == 2 && !piece.hasMoved()) {
                Position oneForward = from.offset(direction, 0);
                return !board.isOccupied(oneForward) && !board.isOccupied(to);
            }
            return false;
        }

        // Diagonal move (capture)
        if (absColDiff == 1 && absRowDiff == 1) {
            // Regular capture
            Piece targetPiece = board.getPieceAt(to);
            if (targetPiece != null && targetPiece.getColor() != color) {
                return true;
            }

            // En passant
            Position enPassantTarget = board.getEnPassantTarget();
            if (enPassantTarget != null && enPassantTarget.equals(to)) {
                return true;
            }
        }

        return false;
    }

    private void addDiagonalCaptures(Board board, Piece piece, Position from,
                                      int direction, List<Move> moves) {
        // Left diagonal capture
        Position leftCapture = from.offset(direction, -1);
        if (leftCapture.isValid()) {
            Piece target = board.getPieceAt(leftCapture);
            if (target != null && target.getColor() != piece.getColor()) {
                addMove(moves, from, leftCapture, piece, board, true);
            }
        }

        // Right diagonal capture
        Position rightCapture = from.offset(direction, 1);
        if (rightCapture.isValid()) {
            Piece target = board.getPieceAt(rightCapture);
            if (target != null && target.getColor() != piece.getColor()) {
                addMove(moves, from, rightCapture, piece, board, true);
            }
        }
    }

    private void addEnPassantMoves(Board board, Piece piece, Position from,
                                    int direction, List<Move> moves) {
        Position enPassantTarget = board.getEnPassantTarget();
        if (enPassantTarget == null) return;

        // Check if pawn is adjacent to en passant square
        Position leftCapture = from.offset(direction, -1);
        Position rightCapture = from.offset(direction, 1);

        if (enPassantTarget.equals(leftCapture) || enPassantTarget.equals(rightCapture)) {
            moves.add(new Move.Builder(from, enPassantTarget)
                    .withPiece(piece)
                    .withMoveType(MoveType.EN_PASSANT)
                    .build());
        }
    }

    private void addMove(List<Move> moves, Position from, Position to,
                         Piece piece, Board board, boolean isCapture) {
        Color color = piece.getColor();
        int promotionRank = color.getPromotionRank();

        if (to.getRow() == promotionRank) {
            // Add promotion moves for each piece type
            for (PieceType promoteTo : new PieceType[]{
                    PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT}) {
                moves.add(new Move.Builder(from, to)
                        .withPiece(piece)
                        .withMoveType(MoveType.PAWN_PROMOTION)
                        .withCapturedPiece(isCapture ? board.getPieceAt(to) : null)
                        .withPromotionPiece(promoteTo)
                        .build());
            }
        } else {
            MoveType moveType = isCapture ? MoveType.CAPTURE : MoveType.NORMAL;
            moves.add(new Move.Builder(from, to)
                    .withPiece(piece)
                    .withMoveType(moveType)
                    .withCapturedPiece(isCapture ? board.getPieceAt(to) : null)
                    .build());
        }
    }
}


