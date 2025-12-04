package trafficsignal.strategies;

import trafficsignal.enums.SignalColor;
import trafficsignal.models.Road;

/**
 * Strategy Pattern: Normal timing for regular traffic conditions.
 */
public class NormalTimingStrategy implements TimingStrategy {
    
    private static final int RED_DURATION = 30;
    private static final int YELLOW_DURATION = 5;
    private static final int GREEN_DURATION = 45;

    @Override
    public int getDuration(SignalColor color) {
        return switch (color) {
            case RED -> RED_DURATION;
            case YELLOW -> YELLOW_DURATION;
            case GREEN -> GREEN_DURATION;
        };
    }

    @Override
    public int getAdjustedDuration(SignalColor color, Road road) {
        int baseDuration = getDuration(color);
        double multiplier = road.getCurrentDensity().getTimingMultiplier();
        
        // Only adjust green duration based on traffic density
        if (color == SignalColor.GREEN) {
            return (int) (baseDuration * multiplier);
        }
        return baseDuration;
    }

    @Override
    public String getStrategyName() {
        return "Normal";
    }

    @Override
    public String getDescription() {
        return "Standard timing for regular traffic flow";
    }
}



