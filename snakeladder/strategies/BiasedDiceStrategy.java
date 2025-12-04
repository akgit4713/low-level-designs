package snakeladder.strategies;

import java.util.Random;

/**
 * A biased dice that tends to give higher or lower values.
 * Useful for testing or game variants.
 */
public class BiasedDiceStrategy implements DiceStrategy {
    
    public enum Bias {
        HIGH,   // Tends toward higher values
        LOW     // Tends toward lower values
    }
    
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 6;
    
    private final Bias bias;
    private final double biasFactor;  // 0.0 to 1.0, higher = more bias
    private final Random random;

    public BiasedDiceStrategy(Bias bias) {
        this(bias, 0.7);  // Default bias factor
    }

    public BiasedDiceStrategy(Bias bias, double biasFactor) {
        this.bias = bias;
        this.biasFactor = Math.max(0.0, Math.min(1.0, biasFactor));
        this.random = new Random();
    }

    public BiasedDiceStrategy(Bias bias, double biasFactor, long seed) {
        this.bias = bias;
        this.biasFactor = Math.max(0.0, Math.min(1.0, biasFactor));
        this.random = new Random(seed);
    }

    @Override
    public int roll() {
        // Roll twice and pick based on bias
        int roll1 = random.nextInt(MAX_VALUE) + MIN_VALUE;
        int roll2 = random.nextInt(MAX_VALUE) + MIN_VALUE;
        
        if (random.nextDouble() < biasFactor) {
            // Apply bias
            return bias == Bias.HIGH ? Math.max(roll1, roll2) : Math.min(roll1, roll2);
        }
        
        // No bias applied, return first roll
        return roll1;
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
        return "Biased dice (" + bias + ", factor: " + String.format("%.1f", biasFactor) + ")";
    }
}



