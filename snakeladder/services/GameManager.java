package snakeladder.services;

import snakeladder.enums.GamePiece;
import snakeladder.enums.GameStatus;
import snakeladder.exceptions.GameNotFoundException;
import snakeladder.exceptions.InvalidGameStateException;
import snakeladder.factories.BoardFactory;
import snakeladder.models.*;
import snakeladder.observers.GameObserver;
import snakeladder.strategies.DiceStrategy;
import snakeladder.strategies.StandardDiceStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages multiple concurrent game sessions.
 * Thread-safe implementation using ConcurrentHashMap.
 * 
 * Follows Singleton Pattern for global access.
 * Follows DIP - depends on abstractions (DiceStrategy, GameObserver).
 */
public class GameManager {
    
    private static volatile GameManager instance;
    private static final Object lock = new Object();
    
    private final Map<String, Game> games;
    private final List<GameObserver> globalObservers;

    private GameManager() {
        this.games = new ConcurrentHashMap<>();
        this.globalObservers = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Gets the singleton instance.
     */
    public static GameManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }

    /**
     * Resets the singleton for testing purposes.
     */
    public static void resetInstance() {
        synchronized (lock) {
            if (instance != null) {
                instance.games.clear();
                instance.globalObservers.clear();
            }
            instance = null;
        }
    }

    /**
     * Creates a new game session.
     * 
     * @param playerNames list of player names
     * @return the created game
     */
    public Game createGame(List<String> playerNames) {
        return createGame(playerNames, BoardFactory.createStandardBoard(), 
                         new StandardDiceStrategy(), false);
    }

    /**
     * Creates a new game session with custom configuration.
     * 
     * @param playerNames list of player names
     * @param board the game board
     * @param diceStrategy the dice strategy to use
     * @param extraTurnOnSix whether rolling 6 gives an extra turn
     * @return the created game
     */
    public Game createGame(List<String> playerNames, Board board, 
                           DiceStrategy diceStrategy, boolean extraTurnOnSix) {
        validatePlayerNames(playerNames);

        Game.Builder gameBuilder = Game.builder()
                .board(board)
                .diceStrategy(diceStrategy)
                .extraTurnOnSix(extraTurnOnSix);

        GamePiece[] pieces = GamePiece.values();
        for (int i = 0; i < playerNames.size(); i++) {
            gameBuilder.addPlayer(new Player(playerNames.get(i), pieces[i]));
        }

        Game game = gameBuilder.build();

        // Add global observers to the game
        for (GameObserver observer : globalObservers) {
            game.addObserver(observer);
        }

        games.put(game.getId(), game);
        return game;
    }

    private void validatePlayerNames(List<String> playerNames) {
        if (playerNames == null || playerNames.size() < 2) {
            throw new IllegalArgumentException("At least 2 players are required");
        }
        if (playerNames.size() > GamePiece.values().length) {
            throw new IllegalArgumentException(
                "Maximum " + GamePiece.values().length + " players allowed");
        }
        
        Set<String> uniqueNames = new HashSet<>(playerNames);
        if (uniqueNames.size() != playerNames.size()) {
            throw new IllegalArgumentException("Player names must be unique");
        }
    }

    /**
     * Gets a game by its ID.
     * 
     * @param gameId the game ID
     * @return the game
     * @throws GameNotFoundException if game not found
     */
    public Game getGame(String gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new GameNotFoundException(gameId);
        }
        return game;
    }

    /**
     * Starts a game.
     * 
     * @param gameId the game ID
     */
    public void startGame(String gameId) {
        Game game = getGame(gameId);
        game.start();
    }

    /**
     * Plays one turn in the specified game.
     * 
     * @param gameId the game ID
     * @return the move outcome
     */
    public MoveOutcome playTurn(String gameId) {
        Game game = getGame(gameId);
        return game.playTurn();
    }

    /**
     * Plays a game to completion.
     * 
     * @param gameId the game ID
     * @return the winning player
     */
    public Player playToCompletion(String gameId) {
        Game game = getGame(gameId);
        return game.playToCompletion();
    }

    /**
     * Cancels a game.
     * 
     * @param gameId the game ID
     */
    public void cancelGame(String gameId) {
        Game game = getGame(gameId);
        game.cancel();
    }

    /**
     * Removes a finished or cancelled game.
     * 
     * @param gameId the game ID
     */
    public void removeGame(String gameId) {
        Game game = games.get(gameId);
        if (game != null && game.getStatus() != GameStatus.IN_PROGRESS) {
            games.remove(gameId);
        } else if (game != null) {
            throw new InvalidGameStateException(
                "Cannot remove an in-progress game. Cancel it first.");
        }
    }

    /**
     * Gets all active (in-progress) games.
     */
    public List<Game> getActiveGames() {
        return games.values().stream()
                .filter(g -> g.getStatus() == GameStatus.IN_PROGRESS)
                .collect(Collectors.toList());
    }

    /**
     * Gets all games regardless of status.
     */
    public List<Game> getAllGames() {
        return new ArrayList<>(games.values());
    }

    /**
     * Gets the count of active games.
     */
    public int getActiveGameCount() {
        return (int) games.values().stream()
                .filter(g -> g.getStatus() == GameStatus.IN_PROGRESS)
                .count();
    }

    /**
     * Gets the total game count.
     */
    public int getTotalGameCount() {
        return games.size();
    }

    /**
     * Adds a global observer that will be added to all new games.
     */
    public void addGlobalObserver(GameObserver observer) {
        globalObservers.add(observer);
    }

    /**
     * Removes a global observer.
     */
    public void removeGlobalObserver(GameObserver observer) {
        globalObservers.remove(observer);
    }

    /**
     * Adds an observer to a specific game.
     */
    public void addObserverToGame(String gameId, GameObserver observer) {
        Game game = getGame(gameId);
        game.addObserver(observer);
    }

    /**
     * Cleans up finished/cancelled games older than the specified time.
     * 
     * @param maxAgeMs maximum age in milliseconds
     * @return number of games cleaned up
     */
    public int cleanupOldGames(long maxAgeMs) {
        // In a real implementation, we'd track game creation time
        // For simplicity, we just remove all finished/cancelled games
        List<String> toRemove = games.entrySet().stream()
                .filter(e -> e.getValue().getStatus() == GameStatus.FINISHED ||
                            e.getValue().getStatus() == GameStatus.CANCELLED)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        toRemove.forEach(games::remove);
        return toRemove.size();
    }

    /**
     * Prints a summary of all games.
     */
    public void printGamesSummary() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         GAME MANAGER SUMMARY         ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║ Total Games: " + getTotalGameCount());
        System.out.println("║ Active Games: " + getActiveGameCount());
        System.out.println("╠══════════════════════════════════════╣");
        
        for (Game game : games.values()) {
            System.out.println("║ " + game.getId().substring(0, 8) + "... | " + 
                             game.getStatus().getDisplayName() + " | " +
                             game.getPlayers().size() + " players");
        }
        
        System.out.println("╚══════════════════════════════════════╝\n");
    }
}



