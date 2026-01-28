package chess.strategies;

import chess.enums.MoveType;
import chess.enums.PieceType;
import chess.models.Board;
import chess.models.Move;
import chess.models.Piece;
import chess.models.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Movement strategy for the King piece.
 * King can move one square in any direction.
 * Special move: Castling (kingside and queenside).
 */
public class KingMoveStrategy implements MoveStrategy {

    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };

    @Override
    public List<Move> getValidMoves(Board board, Piece piece) {
        List<Move> moves = new ArrayList<>();
        Position from = piece.getPosition();

        // Regular king moves (one square in any direction)
        for (int[] dir : DIRECTIONS) {
            Position to = from.offset(dir[0], dir[1]);
            if (to.isValid() && canMoveToSquare(board, piece, to)) {
                MoveType moveType = board.isOccupied(to) ? MoveType.CAPTURE : MoveType.NORMAL;
                moves.add(new Move.Builder(from, to)
                        .withPiece(piece)
                        .withMoveType(moveType)
                        .withCapturedPiece(board.getPieceAt(to))
                        .build());
            }
        }

        // Castling moves
        addCastlingMoves(board, piece, moves);

        return moves;
    }

    @Override
    public boolean canMove(Board board, Piece piece, Position from, Position to) {
        if (!to.isValid()) return false;
        
        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());

        // Regular move: one square in any direction
        if (rowDiff <= 1 && colDiff <= 1 && (rowDiff > 0 || colDiff > 0)) {
            return canMoveToSquare(board, piece, to);
        }

        // Castling: king moves 2 squares horizontally
        if (rowDiff == 0 && colDiff == 2) {
            return canCastle(board, piece, from, to);
        }

        return false;
    }

    private boolean canMoveToSquare(Board board, Piece piece, Position to) {
        Piece targetPiece = board.getPieceAt(to);
        // Can move if empty or occupied by enemy piece
        return targetPiece == null || targetPiece.getColor() != piece.getColor();
    }

    private void addCastlingMoves(Board board, Piece piece, List<Move> moves) {
        if (piece.hasMoved()) return;

        Position kingPos = piece.getPosition();
        int row = kingPos.getRow();

        // Kingside castling (O-O)
        Position kingsideTarget = new Position(row, 6);
        if (canCastle(board, piece, kingPos, kingsideTarget)) {
            moves.add(new Move.Builder(kingPos, kingsideTarget)
                    .withPiece(piece)
                    .withMoveType(MoveType.CASTLING_KINGSIDE)
                    .build());
        }

        // Queenside castling (O-O-O)
        Position queensideTarget = new Position(row, 2);
        if (canCastle(board, piece, kingPos, queensideTarget)) {
            moves.add(new Move.Builder(kingPos, queensideTarget)
                    .withPiece(piece)
                    .withMoveType(MoveType.CASTLING_QUEENSIDE)
                    .build());
        }
    }

    private boolean canCastle(Board board, Piece king, Position from, Position to) {
        // King must not have moved
        if (king.hasMoved()) return false;

        // Must be on correct row
        int row = from.getRow();
        if (row != king.getColor().getBackRank()) return false;

        // Determine which side we're castling to
        boolean kingside = to.getCol() > from.getCol();
        int rookCol = kingside ? 7 : 0;
        Position rookPos = new Position(row, rookCol);

        // Rook must exist and not have moved
        Piece rook = board.getPieceAt(rookPos);
        if (rook == null || rook.getType() != PieceType.ROOK || rook.hasMoved()) {
            return false;
        }

        // Path between king and rook must be clear
        int startCol = Math.min(from.getCol(), rookCol) + 1;
        int endCol = Math.max(from.getCol(), rookCol);
        for (int col = startCol; col < endCol; col++) {
            if (board.isOccupied(new Position(row, col))) {
                return false;
            }
        }

        // Note: Checking if king passes through or ends in check
        // should be handled by MoveValidator, not here
        return true;
    }
}


