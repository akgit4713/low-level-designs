package snakeladder.factories;

import snakeladder.enums.GamePiece;
import snakeladder.models.Board;
import snakeladder.models.Game;
import snakeladder.models.Player;
import snakeladder.observers.ConsoleGameObserver;
import snakeladder.strategies.DiceStrategy;
import snakeladder.strategies.StandardDiceStrategy;

import java.util.List;

/**
 * Factory for creating game instances.
 * Encapsulates game creation logic and provides common configurations.
 */
public class GameFactory {

    private GameFactory() {
        // Private constructor for utility class
    }

    /**
     * Creates a standard 2-player game on a standard board.
     */
    public static Game createStandardGame(String player1Name, String player2Name) {
        Board board = BoardFactory.createStandardBoard();
        
        Player player1 = new Player(player1Name, GamePiece.RED);
        Player player2 = new Player(player2Name, GamePiece.BLUE);
        
        Game game = Game.builder()
                .board(board)
                .addPlayer(player1)
                .addPlayer(player2)
                .diceStrategy(new StandardDiceStrategy())
                .build();
        
        game.addObserver(new ConsoleGameObserver());
        return game;
    }

    /**
     * Creates a multi-player game.
     */
    public static Game createMultiPlayerGame(List<String> playerNames) {
        if (playerNames.size() < 2 || playerNames.size() > GamePiece.values().length) {
            throw new IllegalArgumentException(
                "Player count must be between 2 and " + GamePiece.values().length);
        }

        Board board = BoardFactory.createStandardBoard();
        Game.Builder gameBuilder = Game.builder().board(board);

        GamePiece[] pieces = GamePiece.values();
        for (int i = 0; i < playerNames.size(); i++) {
            gameBuilder.addPlayer(new Player(playerNames.get(i), pieces[i]));
        }

        Game game = gameBuilder
                .diceStrategy(new StandardDiceStrategy())
                .build();
        
        game.addObserver(new ConsoleGameObserver());
        return game;
    }

    /**
     * Creates a quick game on a small board.
     */
    public static Game createQuickGame(String player1Name, String player2Name) {
        Board board = BoardFactory.createSmallBoard();
        
        Player player1 = new Player(player1Name, GamePiece.RED);
        Player player2 = new Player(player2Name, GamePiece.BLUE);
        
        Game game = Game.builder()
                .board(board)
                .addPlayer(player1)
                .addPlayer(player2)
                .diceStrategy(new StandardDiceStrategy())
                .build();
        
        game.addObserver(new ConsoleGameObserver());
        return game;
    }

    /**
     * Creates a custom game with specified board and dice strategy.
     */
    public static Game createCustomGame(Board board, List<Player> players, 
                                         DiceStrategy diceStrategy, boolean extraTurnOnSix) {
        Game.Builder gameBuilder = Game.builder()
                .board(board)
                .diceStrategy(diceStrategy)
                .extraTurnOnSix(extraTurnOnSix);

        for (Player player : players) {
            gameBuilder.addPlayer(player);
        }

        return gameBuilder.build();
    }

    /**
     * Creates a game with extra turn on rolling 6.
     */
    public static Game createGameWithExtraTurn(String player1Name, String player2Name) {
        Board board = BoardFactory.createStandardBoard();
        
        Player player1 = new Player(player1Name, GamePiece.RED);
        Player player2 = new Player(player2Name, GamePiece.BLUE);
        
        Game game = Game.builder()
                .board(board)
                .addPlayer(player1)
                .addPlayer(player2)
                .diceStrategy(new StandardDiceStrategy())
                .extraTurnOnSix(true)
                .build();
        
        game.addObserver(new ConsoleGameObserver());
        return game;
    }
}



