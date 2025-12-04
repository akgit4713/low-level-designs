package tictactoe.game;

import tictactoe.models.Move;
import tictactoe.players.Player;

/**
 * Observer Pattern: Interface for game event listeners.
 * Allows loose coupling between game logic and UI/logging/analytics.
 */
public interface GameEventListener {
    
    /**
     * Called when the game starts.
     */
    default void onGameStart(Game game) {}
    
    /**
     * Called when a move is made.
     */
    default void onMoveMade(Move move) {}
    
    /**
     * Called when the turn changes to a new player.
     */
    default void onTurnChange(Player currentPlayer) {}
    
    /**
     * Called when the game ends.
     */
    default void onGameEnd(GameResult result) {}
    
    /**
     * Called when an invalid move is attempted.
     */
    default void onInvalidMove(Player player, int row, int col) {}
}

