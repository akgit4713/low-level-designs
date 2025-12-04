package snakeladder.strategies;

import java.util.Random;

/**
 * Strategy for rolling multiple dice.
 * For example, rolling 2 dice gives values 2-12.
 */
public class MultipleDiceStrategy implements DiceStrategy {
    
    private static final int SIDES_PER_DIE = 6;
    
    private final int diceCount;
    private final Random random;

    public MultipleDiceStrategy(int diceCount) {
        if (diceCount < 1) {
            throw new IllegalArgumentException("Dice count must be at least 1");
        }
        this.diceCount = diceCount;
        this.random = new Random();
    }

    public MultipleDiceStrategy(int diceCount, long seed) {
        if (diceCount < 1) {
            throw new IllegalArgumentException("Dice count must be at least 1");
        }
        this.diceCount = diceCount;
        this.random = new Random(seed);
    }

    @Override
    public int roll() {
        int total = 0;
        for (int i = 0; i < diceCount; i++) {
            total += random.nextInt(SIDES_PER_DIE) + 1;
        }
        return total;
    }

    @Override
    public int getMinValue() {
        return diceCount;  // All dice show 1
    }

    @Override
    public int getMaxValue() {
        return diceCount * SIDES_PER_DIE;  // All dice show 6
    }

    @Override
    public int getDiceCount() {
        return diceCount;
    }

    @Override
    public String getDescription() {
        return diceCount + " dice (range: " + getMinValue() + "-" + getMaxValue() + ")";
    }
}



