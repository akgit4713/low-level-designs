package snakeladder.strategies;

import java.util.Random;

/**
 * A crooked dice that only shows odd or even numbers.
 * Interesting game variant for testing.
 */
public class CrookedDiceStrategy implements DiceStrategy {
    
    public enum CrookedType {
        ODD_ONLY,   // Only shows 1, 3, 5
        EVEN_ONLY   // Only shows 2, 4, 6
    }
    
    private final CrookedType type;
    private final int[] possibleValues;
    private final Random random;

    public CrookedDiceStrategy(CrookedType type) {
        this.type = type;
        this.possibleValues = type == CrookedType.ODD_ONLY 
            ? new int[]{1, 3, 5} 
            : new int[]{2, 4, 6};
        this.random = new Random();
    }

    public CrookedDiceStrategy(CrookedType type, long seed) {
        this.type = type;
        this.possibleValues = type == CrookedType.ODD_ONLY 
            ? new int[]{1, 3, 5} 
            : new int[]{2, 4, 6};
        this.random = new Random(seed);
    }

    @Override
    public int roll() {
        return possibleValues[random.nextInt(possibleValues.length)];
    }

    @Override
    public int getMinValue() {
        return possibleValues[0];
    }

    @Override
    public int getMaxValue() {
        return possibleValues[possibleValues.length - 1];
    }

    @Override
    public String getDescription() {
        return "Crooked dice (" + type + ": " + java.util.Arrays.toString(possibleValues) + ")";
    }
}



