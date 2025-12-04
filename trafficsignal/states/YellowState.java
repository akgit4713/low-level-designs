package trafficsignal.states;

import trafficsignal.enums.SignalColor;
import trafficsignal.models.TrafficSignal;

/**
 * State Pattern: Represents the YELLOW signal state.
 * Warning signal - prepare to stop. Can only transition to RED.
 */
public class YellowState implements SignalState {
    
    private static final int DEFAULT_DURATION = 5; // seconds

    @Override
    public SignalColor getColor() {
        return SignalColor.YELLOW;
    }

    @Override
    public void onEnter(TrafficSignal signal) {
        // Yellow signal activated - vehicles should prepare to stop
    }

    @Override
    public void onExit(TrafficSignal signal) {
        // Preparing to leave yellow state
    }

    @Override
    public SignalState getNextState() {
        return new RedState();
    }

    @Override
    public boolean canTransitionTo(SignalState nextState) {
        // Yellow can only transition to Red
        return nextState.getColor() == SignalColor.RED;
    }

    @Override
    public int getDefaultDuration() {
        return DEFAULT_DURATION;
    }

    @Override
    public String toString() {
        return "YELLOW";
    }
}



