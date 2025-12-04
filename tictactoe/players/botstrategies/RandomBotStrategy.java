package tictactoe.players.botstrategies;

import tictactoe.models.Board;
import tictactoe.models.Cell;
import tictactoe.models.Move;
import tictactoe.players.Player;

import java.util.List;
import java.util.Random;

/**
 * Simple random move selection strategy.
 * Suitable for easy difficulty bots.
 */
public class RandomBotStrategy implements BotPlayingStrategy {
    
    private final Random random;

    public RandomBotStrategy() {
        this.random = new Random();
    }

    @Override
    public Move decideMove(Board board, Player player) {
        List<Cell> emptyCells = board.getEmptyCells();
        
        if (emptyCells.isEmpty()) {
            throw new IllegalStateException("No empty cells available!");
        }
        
        Cell chosenCell = emptyCells.get(random.nextInt(emptyCells.size()));
        return new Move(chosenCell.getRow(), chosenCell.getCol(), player);
    }
}

