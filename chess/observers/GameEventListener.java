package chess.observers;

import chess.enums.Color;
import chess.game.Game;
import chess.game.GameResult;
import chess.models.Move;
import chess.players.Player;

/**
 * Observer interface for game events.
 * 
 * Observer Pattern: Allows decoupled notification of game events
 * to various listeners (console, logging, network, etc.).
 */
public interface GameEventListener {
    
    /**
     * Called when the game starts.
     */
    default void onGameStart(Game game) {}

    /**
     * Called when a move is made.
     */
    default void onMoveMade(Move move, Player player) {}

    /**
     * Called when a player's turn begins.
     */
    default void onTurnChange(Player player) {}

    /**
     * Called when a king is in check.
     */
    default void onCheck(Color kingColor) {}

    /**
     * Called when checkmate occurs.
     */
    default void onCheckmate(Color loserColor, Player winner) {}

    /**
     * Called when stalemate occurs.
     */
    default void onStalemate() {}

    /**
     * Called when the game ends.
     */
    default void onGameEnd(GameResult result) {}

    /**
     * Called when an invalid move is attempted.
     */
    default void onInvalidMove(Move move, String reason) {}

    /**
     * Called when a player resigns.
     */
    default void onResignation(Player player) {}
}


