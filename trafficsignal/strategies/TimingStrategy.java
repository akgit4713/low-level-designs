package trafficsignal.strategies;

import trafficsignal.enums.SignalColor;
import trafficsignal.models.Road;

/**
 * Strategy Pattern: Interface for signal timing strategies.
 * Allows different timing configurations based on conditions.
 */
public interface TimingStrategy {
    
    /**
     * Gets the duration in seconds for a given signal color.
     */
    int getDuration(SignalColor color);

    /**
     * Gets the adjusted duration based on road conditions.
     */
    int getAdjustedDuration(SignalColor color, Road road);

    /**
     * Gets the name of this timing strategy.
     */
    String getStrategyName();

    /**
     * Gets the description of when this strategy should be used.
     */
    String getDescription();
}



