package snakeladder.strategies;

/**
 * Strategy interface for dice rolling behavior.
 * Follows Strategy Pattern - allows different dice implementations.
 * Follows DIP - Game depends on abstraction, not concrete dice.
 */
public interface DiceStrategy {
    
    /**
     * Rolls the dice and returns the value.
     * @return the dice roll value
     */
    int roll();

    /**
     * Gets the minimum possible value from this dice.
     */
    int getMinValue();

    /**
     * Gets the maximum possible value from this dice.
     */
    int getMaxValue();

    /**
     * Gets the number of dice being used.
     */
    default int getDiceCount() {
        return 1;
    }

    /**
     * Gets a description of this dice strategy.
     */
    String getDescription();
}



