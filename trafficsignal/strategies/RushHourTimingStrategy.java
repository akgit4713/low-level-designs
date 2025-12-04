package trafficsignal.strategies;

import trafficsignal.enums.SignalColor;
import trafficsignal.models.Road;

/**
 * Strategy Pattern: Rush hour timing with longer green durations.
 */
public class RushHourTimingStrategy implements TimingStrategy {
    
    private static final int RED_DURATION = 25;
    private static final int YELLOW_DURATION = 4;
    private static final int GREEN_DURATION = 60;

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
        
        // During rush hour, give more weight to traffic density
        if (color == SignalColor.GREEN) {
            return (int) (baseDuration * multiplier * 1.2);
        }
        return baseDuration;
    }

    @Override
    public String getStrategyName() {
        return "Rush Hour";
    }

    @Override
    public String getDescription() {
        return "Extended green durations for peak traffic hours";
    }
}



