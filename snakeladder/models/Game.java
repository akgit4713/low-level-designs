package snakeladder.models;

import snakeladder.enums.GameStatus;
import snakeladder.enums.MoveResult;
import snakeladder.exceptions.InvalidGameStateException;
import snakeladder.observers.GameObserver;
import snakeladder.strategies.DiceStrategy;
import snakeladder.strategies.StandardDiceStrategy;

import java.util.*;

/**
 * Represents a Snake and Ladder game session.
 * Orchestrates gameplay and manages game state.
 * 
 * Design Patterns Used:
 * - Builder Pattern: For flexible game construction
 * - Observer Pattern: For game event notifications
 * - Strategy Pattern: For dice rolling behavior
 */
public class Game {
    
    private final String id;
    private final Board board;
    private final List<Player> players;
    private final DiceStrategy diceStrategy;
    private final List<GameObserver> observers;
    private final boolean extraTurnOnSix;  // Rule: extra turn when rolling 6
    
    private GameStatus status;
    private int currentPlayerIndex;
    private Player winner;
    private int totalMoves;
    private final List<MoveOutcome> moveHistory;

    private Game(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.board = builder.board;
        this.players = new ArrayList<>(builder.players);
        this.diceStrategy = builder.diceStrategy;
        this.observers = new ArrayList<>();
        this.extraTurnOnSix = builder.extraTurnOnSix;
        
        this.status = GameStatus.NOT_STARTED;
        this.currentPlayerIndex = 0;
        this.winner = null;
        this.totalMoves = 0;
        this.moveHistory = new ArrayList<>();
    }

    /**
     * Starts the game.
     */
    public void start() {
        if (status != GameStatus.NOT_STARTED) {
            throw new InvalidGameStateException("Game has already been started");
        }
        if (players.size() < 2) {
            throw new InvalidGameStateException("At least 2 players are required");
        }
        
        status = GameStatus.IN_PROGRESS;
        currentPlayerIndex = 0;
        
        // Reset all players
        for (Player player : players) {
            player.reset();
        }
        
        notifyGameStart();
    }

    /**
     * Gets the current player whose turn it is.
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Plays one turn for the current player.
     * Rolls the dice and moves the player.
     * 
     * @return the outcome of the move
     */
    public MoveOutcome playTurn() {
        if (status != GameStatus.IN_PROGRESS) {
            throw new InvalidGameStateException("Game is not in progress. Status: " + status);
        }

        Player currentPlayer = getCurrentPlayer();
        int diceValue = diceStrategy.roll();
        
        MoveOutcome outcome = executeMove(currentPlayer, diceValue);
        moveHistory.add(outcome);
        totalMoves++;

        notifyPlayerMove(outcome);

        // Handle special moves (snake/ladder)
        if (outcome.hasEncounteredElement()) {
            if (outcome.getEncounteredElement() instanceof Snake) {
                notifySnakeEncounter(outcome);
            } else if (outcome.getEncounteredElement() instanceof Ladder) {
                notifyLadderClimb(outcome);
            }
        }

        // Check for winner
        if (outcome.isWinningMove()) {
            winner = currentPlayer;
            currentPlayer.setWon(true);
            status = GameStatus.FINISHED;
            notifyPlayerWin(currentPlayer);
            notifyGameEnd();
        } else if (outcome.getResult() != MoveResult.NO_MOVE) {
            // Move to next player (unless rolled 6 with extra turn rule)
            boolean getsExtraTurn = extraTurnOnSix && diceValue == 6;
            if (!getsExtraTurn) {
                advanceToNextPlayer();
            }
        } else {
            // No move made, still advance turn
            advanceToNextPlayer();
        }

        return outcome;
    }

    /**
     * Executes a move for a player with the given dice value.
     */
    private MoveOutcome executeMove(Player player, int diceValue) {
        int fromPosition = player.getPosition();
        int toPosition = fromPosition + diceValue;
        
        MoveOutcome.Builder builder = MoveOutcome.builder()
                .player(player)
                .diceValue(diceValue)
                .fromPosition(fromPosition);

        // Check if move exceeds board
        if (toPosition > board.getSize()) {
            builder.toPosition(fromPosition)
                   .finalPosition(fromPosition)
                   .result(MoveResult.NO_MOVE);
            return builder.build();
        }

        // Check for snake or ladder
        Optional<BoardElement> element = board.getElementAt(toPosition);
        int finalPosition = board.getFinalPosition(toPosition);
        player.setPosition(finalPosition);

        builder.toPosition(toPosition)
               .finalPosition(finalPosition);

        if (element.isPresent()) {
            builder.encounteredElement(element.get());
            if (element.get() instanceof Snake) {
                builder.result(MoveResult.SNAKE_BITE);
            } else {
                builder.result(MoveResult.LADDER_CLIMB);
            }
        } else if (finalPosition == board.getSize()) {
            builder.result(MoveResult.WON);
        } else {
            builder.result(MoveResult.NORMAL);
        }

        return builder.build();
    }

    /**
     * Advances to the next player's turn.
     */
    private void advanceToNextPlayer() {
        Player current = getCurrentPlayer();
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        Player next = getCurrentPlayer();
        notifyTurnChange(current, next);
    }

    /**
     * Plays the game automatically until a winner is determined.
     * 
     * @return the winning player
     */
    public Player playToCompletion() {
        if (status == GameStatus.NOT_STARTED) {
            start();
        }
        
        while (status == GameStatus.IN_PROGRESS) {
            playTurn();
        }
        
        return winner;
    }

    /**
     * Cancels the game.
     */
    public void cancel() {
        if (status == GameStatus.FINISHED) {
            throw new InvalidGameStateException("Cannot cancel a finished game");
        }
        status = GameStatus.CANCELLED;
        notifyGameEnd();
    }

    // Observer management
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    // Notification methods
    private void notifyGameStart() {
        observers.forEach(o -> o.onGameStart(this));
    }

    private void notifyPlayerMove(MoveOutcome outcome) {
        observers.forEach(o -> o.onPlayerMove(outcome));
    }

    private void notifySnakeEncounter(MoveOutcome outcome) {
        observers.forEach(o -> o.onSnakeEncounter(outcome));
    }

    private void notifyLadderClimb(MoveOutcome outcome) {
        observers.forEach(o -> o.onLadderClimb(outcome));
    }

    private void notifyPlayerWin(Player player) {
        observers.forEach(o -> o.onPlayerWin(player, this));
    }

    private void notifyGameEnd() {
        observers.forEach(o -> o.onGameEnd(this));
    }

    private void notifyTurnChange(Player current, Player next) {
        observers.forEach(o -> o.onTurnChange(current, next));
    }

    // Getters
    public String getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public DiceStrategy getDiceStrategy() {
        return diceStrategy;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Player getWinner() {
        return winner;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public List<MoveOutcome> getMoveHistory() {
        return Collections.unmodifiableList(moveHistory);
    }

    public boolean isExtraTurnOnSix() {
        return extraTurnOnSix;
    }

    @Override
    public String toString() {
        return "Game{id='" + id + "', status=" + status + 
               ", players=" + players.size() + ", moves=" + totalMoves + "}";
    }

    // Builder Pattern
    public static class Builder {
        private String id;
        private Board board;
        private List<Player> players = new ArrayList<>();
        private DiceStrategy diceStrategy = new StandardDiceStrategy();
        private boolean extraTurnOnSix = false;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder board(Board board) {
            this.board = board;
            return this;
        }

        public Builder addPlayer(Player player) {
            this.players.add(player);
            return this;
        }

        public Builder players(List<Player> players) {
            this.players = new ArrayList<>(players);
            return this;
        }

        public Builder diceStrategy(DiceStrategy diceStrategy) {
            this.diceStrategy = diceStrategy;
            return this;
        }

        public Builder extraTurnOnSix(boolean extraTurnOnSix) {
            this.extraTurnOnSix = extraTurnOnSix;
            return this;
        }

        public Game build() {
            if (board == null) {
                throw new IllegalStateException("Board is required");
            }
            if (players.isEmpty()) {
                throw new IllegalStateException("At least one player is required");
            }
            return new Game(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}



