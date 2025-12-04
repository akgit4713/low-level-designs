package trafficsignal.observers;

import trafficsignal.models.Road;
import trafficsignal.states.SignalState;

/**
 * Observer Pattern: Interface for signal change notifications.
 */
public interface SignalObserver {
    
    /**
     * Called when a signal changes state.
     */
    void onSignalChange(Road road, SignalState previousState, SignalState newState);

    /**
     * Called when a signal cycle completes for an intersection.
     */
    void onCycleComplete(String intersectionId);
}



