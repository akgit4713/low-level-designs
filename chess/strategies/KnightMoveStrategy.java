package chess.strategies;

import chess.enums.MoveType;
import chess.models.Board;
import chess.models.Move;
import chess.models.Piece;
import chess.models.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Movement strategy for the Knight piece.
 * Knight moves in an "L" shape: 2 squares in one direction and 1 square perpendicular.
 * Knights can jump over other pieces.
 */
public class KnightMoveStrategy implements MoveStrategy {

    // All possible L-shaped moves
    private static final int[][] MOVES = {
        {-2, -1}, {-2, 1},   // 2 down, 1 left/right
        {-1, -2}, {-1, 2},   // 1 down, 2 left/right
        {1, -2},  {1, 2},    // 1 up, 2 left/right
        {2, -1},  {2, 1}     // 2 up, 1 left/right
    };

    @Override
    public List<Move> getValidMoves(Board board, Piece piece) {
        List<Move> moves = new ArrayList<>();
        Position from = piece.getPosition();

        for (int[] move : MOVES) {
            Position to = from.offset(move[0], move[1]);
            
            if (to.isValid() && canMoveToSquare(board, piece, to)) {
                Piece targetPiece = board.getPieceAt(to);
                MoveType moveType = (targetPiece != null) ? MoveType.CAPTURE : MoveType.NORMAL;
                
                moves.add(new Move.Builder(from, to)
                        .withPiece(piece)
                        .withMoveType(moveType)
                        .withCapturedPiece(targetPiece)
                        .build());
            }
        }

        return moves;
    }

    @Override
    public boolean canMove(Board board, Piece piece, Position from, Position to) {
        if (!to.isValid()) return false;

        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());

        // L-shape: (2,1) or (1,2)
        boolean isLShape = (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
        
        if (!isLShape) {
            return false;
        }

        // Knights can jump over pieces, so no path checking needed
        // Target must be empty or enemy piece
        return canMoveToSquare(board, piece, to);
    }

    private boolean canMoveToSquare(Board board, Piece piece, Position to) {
        Piece targetPiece = board.getPieceAt(to);
        return targetPiece == null || targetPiece.getColor() != piece.getColor();
    }
}


