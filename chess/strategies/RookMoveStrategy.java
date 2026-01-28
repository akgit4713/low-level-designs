package chess.strategies;

import chess.enums.MoveType;
import chess.models.Board;
import chess.models.Move;
import chess.models.Piece;
import chess.models.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Movement strategy for the Rook piece.
 * Rook can move any number of squares horizontally or vertically.
 */
public class RookMoveStrategy implements MoveStrategy {

    // Horizontal and vertical directions
    private static final int[][] DIRECTIONS = {
        {-1, 0},  // Down
        {1, 0},   // Up
        {0, -1},  // Left
        {0, 1}    // Right
    };

    @Override
    public List<Move> getValidMoves(Board board, Piece piece) {
        List<Move> moves = new ArrayList<>();
        Position from = piece.getPosition();

        for (int[] dir : DIRECTIONS) {
            addMovesInDirection(board, piece, from, dir[0], dir[1], moves);
        }

        return moves;
    }

    @Override
    public boolean canMove(Board board, Piece piece, Position from, Position to) {
        if (!to.isValid()) return false;
        if (from.equals(to)) return false;

        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getCol() - from.getCol();

        // Must move in a straight line (horizontal or vertical, not both)
        boolean isHorizontal = rowDiff == 0 && colDiff != 0;
        boolean isVertical = colDiff == 0 && rowDiff != 0;

        if (!isHorizontal && !isVertical) {
            return false;
        }

        // Path must be clear
        if (!board.isPathClear(from, to)) {
            return false;
        }

        // Target must be empty or enemy piece
        Piece targetPiece = board.getPieceAt(to);
        return targetPiece == null || targetPiece.getColor() != piece.getColor();
    }

    private void addMovesInDirection(Board board, Piece piece, Position from,
                                      int rowDir, int colDir, List<Move> moves) {
        Position current = from.offset(rowDir, colDir);
        
        while (current.isValid()) {
            Piece targetPiece = board.getPieceAt(current);
            
            if (targetPiece == null) {
                // Empty square - can move here
                moves.add(new Move.Builder(from, current)
                        .withPiece(piece)
                        .withMoveType(MoveType.NORMAL)
                        .build());
            } else if (targetPiece.getColor() != piece.getColor()) {
                // Enemy piece - can capture, but stop here
                moves.add(new Move.Builder(from, current)
                        .withPiece(piece)
                        .withMoveType(MoveType.CAPTURE)
                        .withCapturedPiece(targetPiece)
                        .build());
                break;
            } else {
                // Own piece - blocked
                break;
            }
            
            current = current.offset(rowDir, colDir);
        }
    }
}


