package tictactoe.players;

import tictactoe.models.Symbol;
import tictactoe.players.botstrategies.BotPlayingStrategy;
import tictactoe.players.botstrategies.RandomBotStrategy;
import tictactoe.players.botstrategies.SmartBotStrategy;

/**
 * Factory Pattern for creating different types of players.
 * Encapsulates player creation logic.
 * Follows Open/Closed - new player types can be added by extending.
 */
public class PlayerFactory {

    public enum PlayerType {
        HUMAN, BOT_EASY, BOT_MEDIUM, BOT_HARD
    }

    public static Player createPlayer(PlayerType type, String name, Symbol symbol) {
        switch (type) {
            case HUMAN:
                return new HumanPlayer(name, symbol);
            case BOT_EASY:
                return new BotPlayer(name, symbol, BotPlayer.DifficultyLevel.EASY, 
                                    new RandomBotStrategy());
            case BOT_MEDIUM:
            case BOT_HARD:
                return new BotPlayer(name, symbol, BotPlayer.DifficultyLevel.HARD, 
                                    new SmartBotStrategy());
            default:
                throw new IllegalArgumentException("Unknown player type: " + type);
        }
    }

    public static Player createHumanPlayer(String name, Symbol symbol) {
        return new HumanPlayer(name, symbol);
    }

    public static Player createBotPlayer(String name, Symbol symbol, 
                                         BotPlayer.DifficultyLevel level,
                                         BotPlayingStrategy strategy) {
        return new BotPlayer(name, symbol, level, strategy);
    }
}

