package tictactoe.players;

import tictactoe.models.Board;
import tictactoe.models.Cell;
import tictactoe.models.Move;
import tictactoe.models.Symbol;
import tictactoe.players.botstrategies.BotPlayingStrategy;
import tictactoe.players.botstrategies.RandomBotStrategy;

import java.util.List;

/**
 * Bot player with configurable playing strategy.
 * Uses Strategy Pattern for different bot behaviors.
 * Follows Open/Closed - new bot strategies can be added without modification.
 */
public class BotPlayer extends Player {
    
    private final BotPlayingStrategy playingStrategy;
    private final DifficultyLevel difficultyLevel;

    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }

    public BotPlayer(String name, Symbol symbol) {
        this(name, symbol, DifficultyLevel.EASY, new RandomBotStrategy());
    }

    public BotPlayer(String name, Symbol symbol, DifficultyLevel level) {
        this(name, symbol, level, new RandomBotStrategy());
    }

    public BotPlayer(String name, Symbol symbol, DifficultyLevel level, BotPlayingStrategy strategy) {
        super(name, symbol);
        this.difficultyLevel = level;
        this.playingStrategy = strategy;
    }

    @Override
    public Move makeMove(Board board) {
        System.out.println(name + " (" + symbol + ") is thinking...");
        
        // Simulate thinking time
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Move move = playingStrategy.decideMove(board, this);
        System.out.println(name + " plays at (" + move.getRow() + ", " + move.getCol() + ")");
        
        return move;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public BotPlayingStrategy getPlayingStrategy() {
        return playingStrategy;
    }
}

