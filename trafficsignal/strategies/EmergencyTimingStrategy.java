package trafficsignal.strategies;

import trafficsignal.enums.SignalColor;
import trafficsignal.models.Road;

/**
 * Strategy Pattern: Emergency timing with quick transitions.
 */
public class EmergencyTimingStrategy implements TimingStrategy {
    
    private static final int RED_DURATION = 0; // Immediate
    private static final int YELLOW_DURATION = 2;
    private static final int GREEN_DURATION = 15; // Just enough to clear

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
        // Emergency mode uses fixed minimal durations
        return getDuration(color);
    }

    @Override
    public String getStrategyName() {
        return "Emergency";
    }

    @Override
    public String getDescription() {
        return "Quick transitions for emergency vehicle passage";
    }
}



