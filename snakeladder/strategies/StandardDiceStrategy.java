package snakeladder.strategies;

import java.util.Random;

/**
 * Standard single 6-sided dice strategy.
 */
public class StandardDiceStrategy implements DiceStrategy {
    
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 6;
    
    private final Random random;

    public StandardDiceStrategy() {
        this.random = new Random();
    }

    public StandardDiceStrategy(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public int roll() {
        return random.nextInt(MAX_VALUE) + MIN_VALUE;
    }

    @Override
    public int getMinValue() {
        return MIN_VALUE;
    }

    @Override
    public int getMaxValue() {
        return MAX_VALUE;
    }

    @Override
    public String getDescription() {
        return "Standard 6-sided dice (1-6)";
    }
}



