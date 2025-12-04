package trafficsignal.strategies;

import trafficsignal.enums.SignalColor;
import trafficsignal.enums.TrafficDensity;
import trafficsignal.models.Road;

/**
 * Strategy Pattern: Adaptive timing that dynamically adjusts based on real-time traffic.
 */
public class AdaptiveTimingStrategy implements TimingStrategy {
    
    private static final int BASE_RED_DURATION = 25;
    private static final int BASE_YELLOW_DURATION = 4;
    private static final int BASE_GREEN_DURATION = 40;

    @Override
    public int getDuration(SignalColor color) {
        return switch (color) {
            case RED -> BASE_RED_DURATION;
            case YELLOW -> BASE_YELLOW_DURATION;
            case GREEN -> BASE_GREEN_DURATION;
        };
    }

    @Override
    public int getAdjustedDuration(SignalColor color, Road road) {
        int baseDuration = getDuration(color);
        TrafficDensity density = road.getCurrentDensity();
        
        return switch (color) {
            case GREEN -> calculateGreenDuration(baseDuration, density);
            case RED -> calculateRedDuration(baseDuration, density);
            case YELLOW -> baseDuration; // Yellow remains constant for safety
        };
    }

    private int calculateGreenDuration(int baseDuration, TrafficDensity density) {
        // Higher density = longer green
        double multiplier = density.getTimingMultiplier();
        int adjusted = (int) (baseDuration * multiplier);
        
        // Cap between 20 and 90 seconds
        return Math.max(20, Math.min(90, adjusted));
    }

    private int calculateRedDuration(int baseDuration, TrafficDensity density) {
        // Higher density on current road = shorter red (prioritize clearing)
        double inverseMultiplier = 1.0 / density.getTimingMultiplier();
        int adjusted = (int) (baseDuration * inverseMultiplier);
        
        // Cap between 15 and 45 seconds
        return Math.max(15, Math.min(45, adjusted));
    }

    @Override
    public String getStrategyName() {
        return "Adaptive";
    }

    @Override
    public String getDescription() {
        return "Dynamically adjusts timing based on real-time traffic density";
    }
}



