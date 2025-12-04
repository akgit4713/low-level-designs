package trafficsignal.states;

import trafficsignal.enums.SignalColor;
import trafficsignal.models.TrafficSignal;

/**
 * State Pattern: Represents the RED signal state.
 * Vehicles must stop. Can only transition to GREEN.
 */
public class RedState implements SignalState {
    
    private static final int DEFAULT_DURATION = 30; // seconds

    @Override
    public SignalColor getColor() {
        return SignalColor.RED;
    }

    @Override
    public void onEnter(TrafficSignal signal) {
        // Red signal activated - vehicles must stop
    }

    @Override
    public void onExit(TrafficSignal signal) {
        // Preparing to leave red state
    }

    @Override
    public SignalState getNextState() {
        return new GreenState();
    }

    @Override
    public boolean canTransitionTo(SignalState nextState) {
        // Red can only transition to Green (skipping Yellow on the way up)
        return nextState.getColor() == SignalColor.GREEN;
    }

    @Override
    public int getDefaultDuration() {
        return DEFAULT_DURATION;
    }

    @Override
    public String toString() {
        return "RED";
    }
}



