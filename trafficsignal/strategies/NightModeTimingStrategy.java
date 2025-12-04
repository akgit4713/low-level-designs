package trafficsignal.strategies;

import trafficsignal.enums.SignalColor;
import trafficsignal.models.Road;

/**
 * Strategy Pattern: Night mode timing with shorter cycles.
 */
public class NightModeTimingStrategy implements TimingStrategy {
    
    private static final int RED_DURATION = 20;
    private static final int YELLOW_DURATION = 3;
    private static final int GREEN_DURATION = 30;

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
        // Night mode uses fixed durations regardless of traffic
        return getDuration(color);
    }

    @Override
    public String getStrategyName() {
        return "Night Mode";
    }

    @Override
    public String getDescription() {
        return "Shorter cycles for low nighttime traffic";
    }
}



