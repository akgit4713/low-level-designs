package tictactoe.strategies;

import tictactoe.models.Board;
import tictactoe.models.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Composite Pattern: Combines multiple winning strategies.
 * Allows combining different win conditions flexibly.
 * Open/Closed: New strategies can be added to the composite without modification.
 */
public class CompositeWinningStrategy implements WinningStrategy {
    
    private final List<WinningStrategy> strategies;

    public CompositeWinningStrategy() {
        this.strategies = new ArrayList<>();
    }

    public CompositeWinningStrategy(WinningStrategy... strategies) {
        this.strategies = new ArrayList<>(Arrays.asList(strategies));
    }

    public CompositeWinningStrategy(List<WinningStrategy> strategies) {
        this.strategies = new ArrayList<>(strategies);
    }

    public void addStrategy(WinningStrategy strategy) {
        strategies.add(strategy);
    }

    public void removeStrategy(WinningStrategy strategy) {
        strategies.remove(strategy);
    }

    @Override
    public boolean checkWin(Board board, Symbol symbol) {
        for (WinningStrategy strategy : strategies) {
            if (strategy.checkWin(board, symbol)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getStrategyName() {
        return "CompositeStrategy[" + strategies.size() + " strategies]";
    }

    /**
     * Factory method to create default Tic Tac Toe winning strategy.
     */
    public static CompositeWinningStrategy createDefaultStrategy() {
        return new CompositeWinningStrategy(
            new RowWinningStrategy(),
            new ColumnWinningStrategy(),
            new DiagonalWinningStrategy()
        );
    }
}

