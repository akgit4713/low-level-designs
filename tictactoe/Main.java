package tictactoe;

import tictactoe.game.ConsoleGameEventListener;
import tictactoe.game.Game;
import tictactoe.game.GameResult;
import tictactoe.models.Symbol;
import tictactoe.players.BotPlayer;
import tictactoe.players.HumanPlayer;
import tictactoe.players.Player;
import tictactoe.players.PlayerFactory;
import tictactoe.players.botstrategies.SmartBotStrategy;
import tictactoe.strategies.CompositeWinningStrategy;
import tictactoe.strategies.CornersWinningStrategy;
import tictactoe.strategies.WinningStrategy;

import java.util.Scanner;

/**
 * Main entry point demonstrating various game configurations.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║        TIC TAC TOE - LLD DEMO        ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        while (true) {
            System.out.println("Select Game Mode:");
            System.out.println("  1. Human vs Human");
            System.out.println("  2. Human vs Bot (Easy)");
            System.out.println("  3. Human vs Bot (Smart)");
            System.out.println("  4. Bot vs Bot (Demo)");
            System.out.println("  5. Custom Board Size (5x5)");
            System.out.println("  6. Custom Winning Strategy (Corners)");
            System.out.println("  0. Exit");
            System.out.print("\nEnter choice: ");

            int choice;
            try {
                choice = scanner.nextInt();
            } catch (Exception e) {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter a number.\n");
                continue;
            }

            if (choice == 0) {
                System.out.println("\nThanks for playing! Goodbye! 👋\n");
                break;
            }

            GameResult result = null;
            switch (choice) {
                case 1:
                    result = playHumanVsHuman();
                    break;
                case 2:
                    result = playHumanVsBotEasy();
                    break;
                case 3:
                    result = playHumanVsBotSmart();
                    break;
                case 4:
                    result = playBotVsBot();
                    break;
                case 5:
                    result = playCustomBoardSize();
                    break;
                case 6:
                    result = playWithCustomStrategy();
                    break;
                default:
                    System.out.println("Invalid choice. Try again.\n");
            }

            if (result != null) {
                System.out.println("\n" + result);
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                scanner.nextLine();
            }
        }
    }

    /**
     * Human vs Human game.
     */
    private static GameResult playHumanVsHuman() {
        Player player1 = new HumanPlayer("Player 1", Symbol.X);
        Player player2 = new HumanPlayer("Player 2", Symbol.O);

        Game game = new Game.Builder()
                .withBoardSize(3)
                .withPlayers(player1, player2)
                .build();

        game.addGameEventListener(new ConsoleGameEventListener());
        return game.play();
    }

    /**
     * Human vs Easy Bot.
     */
    private static GameResult playHumanVsBotEasy() {
        Player human = new HumanPlayer("Human", Symbol.X);
        Player bot = PlayerFactory.createPlayer(
            PlayerFactory.PlayerType.BOT_EASY, "Easy Bot", Symbol.O);

        Game game = new Game.Builder()
                .withBoardSize(3)
                .withPlayers(human, bot)
                .build();

        game.addGameEventListener(new ConsoleGameEventListener());
        return game.play();
    }

    /**
     * Human vs Smart Bot.
     */
    private static GameResult playHumanVsBotSmart() {
        Player human = new HumanPlayer("Human", Symbol.X);
        Player bot = new BotPlayer("Smart Bot", Symbol.O, 
                                   BotPlayer.DifficultyLevel.HARD, 
                                   new SmartBotStrategy());

        Game game = new Game.Builder()
                .withBoardSize(3)
                .withPlayers(human, bot)
                .build();

        game.addGameEventListener(new ConsoleGameEventListener());
        return game.play();
    }

    /**
     * Bot vs Bot demonstration.
     */
    private static GameResult playBotVsBot() {
        Player bot1 = new BotPlayer("Random Bot", Symbol.X, 
                                    BotPlayer.DifficultyLevel.EASY);
        Player bot2 = new BotPlayer("Smart Bot", Symbol.O, 
                                    BotPlayer.DifficultyLevel.HARD, 
                                    new SmartBotStrategy());

        Game game = new Game.Builder()
                .withBoardSize(3)
                .withPlayers(bot1, bot2)
                .build();

        game.addGameEventListener(new ConsoleGameEventListener());
        return game.play();
    }

    /**
     * Custom board size demonstration.
     */
    private static GameResult playCustomBoardSize() {
        Player bot1 = new BotPlayer("Bot X", Symbol.X, 
                                    BotPlayer.DifficultyLevel.HARD, 
                                    new SmartBotStrategy());
        Player bot2 = new BotPlayer("Bot O", Symbol.O, 
                                    BotPlayer.DifficultyLevel.HARD, 
                                    new SmartBotStrategy());

        Game game = new Game.Builder()
                .withBoardSize(5)  // 5x5 board
                .withPlayers(bot1, bot2)
                .build();

        game.addGameEventListener(new ConsoleGameEventListener());
        System.out.println("\n📏 Playing on a 5x5 board!\n");
        return game.play();
    }

    /**
     * Custom winning strategy demonstration.
     * Win by capturing all 4 corners OR traditional win.
     */
    private static GameResult playWithCustomStrategy() {
        // Create custom strategy: standard rules + corners win
        WinningStrategy customStrategy = new CompositeWinningStrategy(
            CompositeWinningStrategy.createDefaultStrategy(),
            new CornersWinningStrategy()
        );

        Player bot1 = new BotPlayer("Bot X", Symbol.X, 
                                    BotPlayer.DifficultyLevel.HARD, 
                                    new SmartBotStrategy());
        Player bot2 = new BotPlayer("Bot O", Symbol.O, 
                                    BotPlayer.DifficultyLevel.EASY);

        Game game = new Game.Builder()
                .withBoardSize(3)
                .withPlayers(bot1, bot2)
                .withWinningStrategy(customStrategy)
                .build();

        game.addGameEventListener(new ConsoleGameEventListener());
        System.out.println("\n🎯 Custom Strategy: Win by row/column/diagonal OR all 4 corners!\n");
        return game.play();
    }
}

