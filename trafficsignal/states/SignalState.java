package trafficsignal.states;

import trafficsignal.enums.SignalColor;
import trafficsignal.models.TrafficSignal;

/**
 * State Pattern: Interface for traffic signal states.
 * Each state encapsulates the behavior for a specific signal color.
 */
public interface SignalState {
    
    /**
     * Gets the color of this state.
     */
    SignalColor getColor();

    /**
     * Handles entry into this state.
     */
    void onEnter(TrafficSignal signal);

    /**
     * Handles exit from this state.
     */
    void onExit(TrafficSignal signal);

    /**
     * Gets the next state in the normal signal cycle.
     */
    SignalState getNextState();

    /**
     * Checks if transition to the given state is allowed.
     */
    boolean canTransitionTo(SignalState nextState);

    /**
     * Gets the default duration for this state in seconds.
     */
    int getDefaultDuration();
}



