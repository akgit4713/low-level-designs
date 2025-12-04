package trafficsignal.states;

import trafficsignal.enums.SignalColor;
import trafficsignal.models.TrafficSignal;

/**
 * State Pattern: Represents the GREEN signal state.
 * Vehicles may proceed. Can only transition to YELLOW.
 */
public class GreenState implements SignalState {
    
    private static final int DEFAULT_DURATION = 45; // seconds

    @Override
    public SignalColor getColor() {
        return SignalColor.GREEN;
    }

    @Override
    public void onEnter(TrafficSignal signal) {
        // Green signal activated - vehicles may proceed
    }

    @Override
    public void onExit(TrafficSignal signal) {
        // Preparing to leave green state
    }

    @Override
    public SignalState getNextState() {
        return new YellowState();
    }

    @Override
    public boolean canTransitionTo(SignalState nextState) {
        // Green can only transition to Yellow
        return nextState.getColor() == SignalColor.YELLOW;
    }

    @Override
    public int getDefaultDuration() {
        return DEFAULT_DURATION;
    }

    @Override
    public String toString() {
        return "GREEN";
    }
}



