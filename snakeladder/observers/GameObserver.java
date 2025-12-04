package snakeladder.observers;

import snakeladder.models.Game;
import snakeladder.models.MoveOutcome;
import snakeladder.models.Player;

/**
 * Observer interface for game events.
 * Follows Observer Pattern - decouples event producers from consumers.
 * Follows ISP - specific event methods instead of one generic handler.
 */
public interface GameObserver {
    
    /**
     * Called when a game starts.
     */
    default void onGameStart(Game game) {}

    /**
     * Called when a player makes a move.
     */
    default void onPlayerMove(MoveOutcome outcome) {}

    /**
     * Called when a player encounters a snake.
     */
    default void onSnakeEncounter(MoveOutcome outcome) {}

    /**
     * Called when a player climbs a ladder.
     */
    default void onLadderClimb(MoveOutcome outcome) {}

    /**
     * Called when a player wins the game.
     */
    default void onPlayerWin(Player player, Game game) {}

    /**
     * Called when a game ends.
     */
    default void onGameEnd(Game game) {}

    /**
     * Called when a player's turn changes.
     */
    default void onTurnChange(Player currentPlayer, Player nextPlayer) {}
}



