package chess.strategies;

import chess.models.Board;
import chess.models.Move;
import chess.models.Piece;
import chess.models.Position;

import java.util.List;

/**
 * Strategy interface for piece movement.
 * Each piece type has its own strategy implementation.
 * 
 * Implements the Strategy Pattern - allows different movement algorithms
 * to be used interchangeably for different piece types.
 */
public interface MoveStrategy {
    
    /**
     * Returns all valid moves for the given piece on the board.
     * Note: Does not check for check conditions - that's handled by MoveValidator.
     * 
     * @param board The current board state
     * @param piece The piece to get moves for
     * @return List of valid moves (excluding check validation)
     */
    List<Move> getValidMoves(Board board, Piece piece);

    /**
     * Checks if the piece can move from its current position to the target position.
     * This validates the movement pattern only, not check conditions.
     * 
     * @param board The current board state
     * @param piece The piece attempting to move
     * @param from The source position
     * @param to The target position
     * @return true if the move follows valid movement pattern
     */
    boolean canMove(Board board, Piece piece, Position from, Position to);
}


