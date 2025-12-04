package tictactoe.players.botstrategies;

import tictactoe.models.Board;
import tictactoe.models.Move;
import tictactoe.players.Player;

/**
 * Strategy interface for bot playing behavior.
 * Allows different AI strategies (random, minimax, etc.)
 */
public interface BotPlayingStrategy {
    
    /**
     * Decides the next move for the bot.
     * @param board Current board state
     * @param player The bot player making the move
     * @return The decided move
     */
    Move decideMove(Board board, Player player);
}

