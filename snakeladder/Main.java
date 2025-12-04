package snakeladder;

import snakeladder.enums.GamePiece;
import snakeladder.factories.BoardFactory;
import snakeladder.factories.GameFactory;
import snakeladder.models.*;
import snakeladder.observers.ConsoleGameObserver;
import snakeladder.observers.StatisticsObserver;
import snakeladder.services.GameManager;
import snakeladder.strategies.*;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point demonstrating various game configurations.
 * Shows usage of different design patterns and extension points.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        printBanner();

        while (true) {
            printMenu();
            int choice = getChoice();

            switch (choice) {
                case 1:
                    demo2PlayerGame();
                    break;
                case 2:
                    demoMultiPlayerGame();
                    break;
                case 3:
                    demoQuickGame();
                    break;
                case 4:
                    demoCustomDiceStrategy();
                    break;
                case 5:
                    demoExtraTurnRule();
                    break;
                case 6:
                    demoGameManager();
                    break;
                case 7:
                    demoBoardConfiguration();
                    break;
                case 8:
                    demoStatisticsObserver();
                    break;
                case 0:
                    System.out.println("\n👋 Thanks for playing! Goodbye!\n");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void printBanner() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║           🐍 SNAKE AND LADDER GAME 🪜                ║");
        System.out.println("║                  LLD Implementation                   ║");
        System.out.println("╚══════════════════════════════════════════════════════╝\n");
    }

    private static void printMenu() {
        System.out.println("\n┌────────────────────────────────────┐");
        System.out.println("│         SELECT DEMO MODE           │");
        System.out.println("├────────────────────────────────────┤");
        System.out.println("│  1. Standard 2-Player Game         │");
        System.out.println("│  2. Multi-Player Game (4 players)  │");
        System.out.println("│  3. Quick Game (Small Board)       │");
        System.out.println("│  4. Custom Dice Strategy           │");
        System.out.println("│  5. Extra Turn on 6 Rule           │");
        System.out.println("│  6. Game Manager (Concurrent)      │");
        System.out.println("│  7. Board Configuration Demo       │");
        System.out.println("│  8. Statistics Observer Demo       │");
        System.out.println("│  0. Exit                           │");
        System.out.println("└────────────────────────────────────┘");
        System.out.print("\nEnter choice: ");
    }

    private static int getChoice() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            return choice;
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }

    /**
     * Demo 1: Standard 2-player game.
     */
    private static void demo2PlayerGame() {
        System.out.println("\n🎮 DEMO: Standard 2-Player Game");
        System.out.println("━".repeat(50));

        Game game = GameFactory.createStandardGame("Alice", "Bob");
        game.getBoard().display();
        
        Player winner = game.playToCompletion();
        System.out.println("\n🏆 Winner: " + winner.getName());
    }

    /**
     * Demo 2: Multi-player game with 4 players.
     */
    private static void demoMultiPlayerGame() {
        System.out.println("\n🎮 DEMO: Multi-Player Game (4 Players)");
        System.out.println("━".repeat(50));

        List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie", "Diana");
        Game game = GameFactory.createMultiPlayerGame(playerNames);
        
        Player winner = game.playToCompletion();
        System.out.println("\n🏆 Winner: " + winner.getName());
    }

    /**
     * Demo 3: Quick game on a smaller board.
     */
    private static void demoQuickGame() {
        System.out.println("\n🎮 DEMO: Quick Game (50-Cell Board)");
        System.out.println("━".repeat(50));

        Game game = GameFactory.createQuickGame("Speedy", "Quick");
        game.getBoard().display();
        
        Player winner = game.playToCompletion();
        System.out.println("\n🏆 Winner: " + winner.getName() + " in " + 
                          game.getTotalMoves() + " moves!");
    }

    /**
     * Demo 4: Custom dice strategy (biased dice).
     */
    private static void demoCustomDiceStrategy() {
        System.out.println("\n🎮 DEMO: Custom Dice Strategies");
        System.out.println("━".repeat(50));

        Board board = BoardFactory.createSmallBoard();

        // Create player with high-biased dice
        System.out.println("\n🎲 Testing HIGH-BIASED Dice:");
        DiceStrategy highBias = new BiasedDiceStrategy(BiasedDiceStrategy.Bias.HIGH, 0.8);
        System.out.println("Strategy: " + highBias.getDescription());

        Game game1 = Game.builder()
                .board(board)
                .addPlayer(new Player("Lucky", GamePiece.RED))
                .addPlayer(new Player("Normal", GamePiece.BLUE))
                .diceStrategy(highBias)
                .build();
        game1.addObserver(new ConsoleGameObserver());
        Player winner1 = game1.playToCompletion();
        System.out.println("Winner with high-bias dice: " + winner1.getName());

        // Create new board for second game
        board = BoardFactory.createSmallBoard();

        // Test with crooked dice
        System.out.println("\n🎲 Testing CROOKED Dice (Even Only):");
        DiceStrategy crooked = new CrookedDiceStrategy(CrookedDiceStrategy.CrookedType.EVEN_ONLY);
        System.out.println("Strategy: " + crooked.getDescription());

        Game game2 = Game.builder()
                .board(board)
                .addPlayer(new Player("EvenSteven", GamePiece.GREEN))
                .addPlayer(new Player("AlsoEven", GamePiece.YELLOW))
                .diceStrategy(crooked)
                .build();
        game2.addObserver(new ConsoleGameObserver());
        Player winner2 = game2.playToCompletion();
        System.out.println("Winner with crooked dice: " + winner2.getName());
    }

    /**
     * Demo 5: Extra turn on rolling 6.
     */
    private static void demoExtraTurnRule() {
        System.out.println("\n🎮 DEMO: Extra Turn on Rolling 6");
        System.out.println("━".repeat(50));

        Game game = GameFactory.createGameWithExtraTurn("Roller", "Hopper");
        System.out.println("Rule: Rolling a 6 gives an extra turn!\n");
        
        Player winner = game.playToCompletion();
        System.out.println("\n🏆 Winner: " + winner.getName());
    }

    /**
     * Demo 6: Game Manager for concurrent games.
     */
    private static void demoGameManager() {
        System.out.println("\n🎮 DEMO: Game Manager (Concurrent Sessions)");
        System.out.println("━".repeat(50));

        // Reset and get manager instance
        GameManager.resetInstance();
        GameManager manager = GameManager.getInstance();
        manager.addGlobalObserver(new ConsoleGameObserver());

        // Create multiple games
        System.out.println("\n📋 Creating multiple game sessions...\n");

        Game game1 = manager.createGame(Arrays.asList("Player1", "Player2"));
        Game game2 = manager.createGame(Arrays.asList("TeamA", "TeamB", "TeamC"));
        Game game3 = manager.createGame(Arrays.asList("Red", "Blue"));

        manager.printGamesSummary();

        // Start and play games
        System.out.println("\n🎮 Playing Game 1...");
        manager.startGame(game1.getId());
        manager.playToCompletion(game1.getId());

        System.out.println("\n🎮 Playing Game 2...");
        manager.startGame(game2.getId());
        manager.playToCompletion(game2.getId());

        // Cancel game 3 without playing
        manager.startGame(game3.getId());
        manager.cancelGame(game3.getId());
        System.out.println("\n❌ Game 3 cancelled.");

        manager.printGamesSummary();

        // Cleanup
        int cleaned = manager.cleanupOldGames(0);
        System.out.println("\n🧹 Cleaned up " + cleaned + " finished games.");
        manager.printGamesSummary();
    }

    /**
     * Demo 7: Board configuration and custom boards.
     */
    private static void demoBoardConfiguration() {
        System.out.println("\n🎮 DEMO: Board Configuration");
        System.out.println("━".repeat(50));

        // Standard board
        System.out.println("\n📋 Standard Board (100 cells):");
        Board standardBoard = BoardFactory.createStandardBoard();
        standardBoard.display();

        // Small board
        System.out.println("\n📋 Small Board (50 cells):");
        Board smallBoard = BoardFactory.createSmallBoard();
        smallBoard.display();

        // Custom board using builder
        System.out.println("\n📋 Custom Board (30 cells):");
        Board customBoard = BoardFactory.customBoard(30)
                .addSnake(28, 10)
                .addSnake(25, 5)
                .addLadder(3, 15)
                .addLadder(12, 28)
                .build();
        customBoard.display();
    }

    /**
     * Demo 8: Statistics observer.
     */
    private static void demoStatisticsObserver() {
        System.out.println("\n🎮 DEMO: Statistics Observer");
        System.out.println("━".repeat(50));

        Board board = BoardFactory.createSmallBoard();
        
        Game game = Game.builder()
                .board(board)
                .addPlayer(new Player("DataCollector", GamePiece.RED))
                .addPlayer(new Player("StatsGatherer", GamePiece.BLUE))
                .diceStrategy(new StandardDiceStrategy())
                .build();

        StatisticsObserver stats = new StatisticsObserver();
        game.addObserver(stats);
        game.addObserver(new ConsoleGameObserver());

        game.playToCompletion();

        // Print detailed statistics
        stats.printStatistics();

        System.out.println("\nDetailed Player Stats:");
        for (Player player : game.getPlayers()) {
            System.out.println("  " + player.getName() + ":");
            System.out.println("    - Moves: " + stats.getMovesByPlayer(player.getId()));
            System.out.println("    - Snake encounters: " + stats.getSnakesByPlayer(player.getId()));
            System.out.println("    - Ladder climbs: " + stats.getLaddersByPlayer(player.getId()));
        }
    }
}



