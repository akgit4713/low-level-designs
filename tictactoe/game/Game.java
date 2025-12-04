package tictactoe.game;

import tictactoe.models.Board;
import tictactoe.models.Move;
import tictactoe.models.Symbol;
import tictactoe.players.Player;
import tictactoe.strategies.CompositeWinningStrategy;
import tictactoe.strategies.WinningStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Main game orchestrator class.
 * Single Responsibility: Orchestrating game flow.
 * Uses Dependency Injection for strategies and players (DIP).
 */
public class Game {
    
    private final Board board;
    private final List<Player> players;
    private final WinningStrategy winningStrategy;
    private final List<Move> moveHistory;
    private final List<GameEventListener> eventListeners;
    
    private int currentPlayerIndex;
    private GameState gameState;
    private Player winner;
    private long gameStartTime;

    // Private constructor - use Builder to create instances
    private Game(Board board, List<Player> players, WinningStrategy winningStrategy) {
        this.board = board;
        this.players = new ArrayList<>(players);
        this.winningStrategy = winningStrategy;
        this.moveHistory = new ArrayList<>();
        this.eventListeners = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.gameState = GameState.NOT_STARTED;
    }

    public void addGameEventListener(GameEventListener listener) {
        eventListeners.add(listener);
    }

    public void removeGameEventListener(GameEventListener listener) {
        eventListeners.remove(listener);
    }

    /**
     * Starts and runs the game until completion.
     */
    public GameResult play() {
        gameState = GameState.IN_PROGRESS;
        gameStartTime = System.currentTimeMillis();
        
        notifyGameStart();
        
        while (!gameState.isGameOver()) {
            board.display();
            
            Player currentPlayer = getCurrentPlayer();
            notifyTurnChange(currentPlayer);
            
            Move move = currentPlayer.makeMove(board);
            executeMove(move);
            
            checkGameStatus(currentPlayer);
            
            if (!gameState.isGameOver()) {
                nextTurn();
            }
        }
        
        board.display();
        
        GameResult result = createGameResult();
        notifyGameEnd(result);
        
        return result;
    }

    /**
     * Execute a single move (for programmatic control).
     */
    public boolean executeMove(Move move) {
        if (gameState == GameState.NOT_STARTED) {
            gameState = GameState.IN_PROGRESS;
            gameStartTime = System.currentTimeMillis();
        }
        
        if (gameState.isGameOver()) {
            return false;
        }
        
        Player player = move.getPlayer();
        int row = move.getRow();
        int col = move.getCol();
        
        if (!board.placeSymbol(row, col, player.getSymbol())) {
            notifyInvalidMove(player, row, col);
            return false;
        }
        
        moveHistory.add(move);
        notifyMoveMade(move);
        
        return true;
    }

    private void checkGameStatus(Player currentPlayer) {
        if (winningStrategy.checkWin(board, currentPlayer.getSymbol())) {
            gameState = GameState.WIN;
            winner = currentPlayer;
        } else if (board.isFull()) {
            gameState = GameState.DRAW;
        }
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Board getBoard() {
        return board;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Player getWinner() {
        return winner;
    }

    public List<Move> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    private GameResult createGameResult() {
        long duration = System.currentTimeMillis() - gameStartTime;
        return new GameResult(gameState, winner, moveHistory.size(), duration);
    }

    // Event notification methods
    private void notifyGameStart() {
        for (GameEventListener listener : eventListeners) {
            listener.onGameStart(this);
        }
    }

    private void notifyMoveMade(Move move) {
        for (GameEventListener listener : eventListeners) {
            listener.onMoveMade(move);
        }
    }

    private void notifyTurnChange(Player player) {
        for (GameEventListener listener : eventListeners) {
            listener.onTurnChange(player);
        }
    }

    private void notifyGameEnd(GameResult result) {
        for (GameEventListener listener : eventListeners) {
            listener.onGameEnd(result);
        }
    }

    private void notifyInvalidMove(Player player, int row, int col) {
        for (GameEventListener listener : eventListeners) {
            listener.onInvalidMove(player, row, col);
        }
    }

    // ==================== BUILDER PATTERN ====================
    
    /**
     * Builder Pattern for flexible game construction.
     * Allows step-by-step configuration of game parameters.
     */
    public static class Builder {
        private int boardSize = 3;
        private List<Player> players = new ArrayList<>();
        private WinningStrategy winningStrategy;

        public Builder() {
            // Default strategy
            this.winningStrategy = CompositeWinningStrategy.createDefaultStrategy();
        }

        public Builder withBoardSize(int size) {
            if (size < 3) {
                throw new IllegalArgumentException("Board size must be at least 3");
            }
            this.boardSize = size;
            return this;
        }

        public Builder addPlayer(Player player) {
            if (players.size() >= 2) {
                throw new IllegalStateException("Cannot add more than 2 players");
            }
            // Validate unique symbols
            for (Player existingPlayer : players) {
                if (existingPlayer.getSymbol() == player.getSymbol()) {
                    throw new IllegalArgumentException("Players must have different symbols");
                }
            }
            this.players.add(player);
            return this;
        }

        public Builder withPlayers(Player player1, Player player2) {
            this.players.clear();
            return addPlayer(player1).addPlayer(player2);
        }

        public Builder withWinningStrategy(WinningStrategy strategy) {
            this.winningStrategy = strategy;
            return this;
        }

        public Game build() {
            validate();
            Board board = new Board(boardSize);
            return new Game(board, players, winningStrategy);
        }

        private void validate() {
            if (players.size() != 2) {
                throw new IllegalStateException("Exactly 2 players are required");
            }
            if (winningStrategy == null) {
                throw new IllegalStateException("Winning strategy is required");
            }
        }
    }
}

