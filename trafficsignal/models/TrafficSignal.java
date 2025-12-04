package trafficsignal.models;

import trafficsignal.enums.SignalColor;
import trafficsignal.exceptions.InvalidStateTransitionException;
import trafficsignal.states.RedState;
import trafficsignal.states.SignalState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a traffic signal at an intersection.
 * Uses State Pattern for managing signal color transitions.
 */
public class TrafficSignal {
    
    private final String id;
    private final String roadId;
    private SignalState currentState;
    private LocalDateTime lastStateChange;
    private int currentDuration; // Duration in seconds for current state
    private boolean isEmergencyOverride;
    private final List<SignalStateChangeListener> listeners;

    public TrafficSignal(String roadId) {
        this.id = UUID.randomUUID().toString();
        this.roadId = roadId;
        this.currentState = new RedState(); // Default to red
        this.lastStateChange = LocalDateTime.now();
        this.currentDuration = currentState.getDefaultDuration();
        this.isEmergencyOverride = false;
        this.listeners = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getRoadId() {
        return roadId;
    }

    public SignalState getCurrentState() {
        return currentState;
    }

    public SignalColor getCurrentColor() {
        return currentState.getColor();
    }

    public LocalDateTime getLastStateChange() {
        return lastStateChange;
    }

    public int getCurrentDuration() {
        return currentDuration;
    }

    public void setCurrentDuration(int duration) {
        this.currentDuration = duration;
    }

    public boolean isEmergencyOverride() {
        return isEmergencyOverride;
    }

    public void setEmergencyOverride(boolean emergencyOverride) {
        this.isEmergencyOverride = emergencyOverride;
    }

    /**
     * Transitions to the next state in the normal signal cycle.
     */
    public void transitionToNextState() {
        SignalState nextState = currentState.getNextState();
        transitionTo(nextState);
    }

    /**
     * Transitions to a specific state (used for emergency override).
     */
    public void transitionTo(SignalState newState) {
        if (!isEmergencyOverride && !currentState.canTransitionTo(newState)) {
            throw new InvalidStateTransitionException(
                currentState.getColor(), 
                newState.getColor()
            );
        }

        SignalState previousState = this.currentState;
        
        // Exit current state
        currentState.onExit(this);
        
        // Update state
        this.currentState = newState;
        this.lastStateChange = LocalDateTime.now();
        this.currentDuration = newState.getDefaultDuration();
        
        // Enter new state
        newState.onEnter(this);
        
        // Notify listeners
        notifyStateChange(previousState, newState);
    }

    /**
     * Force transition to a state (bypasses normal validation - for emergency use).
     */
    public void forceTransitionTo(SignalState newState) {
        SignalState previousState = this.currentState;
        
        currentState.onExit(this);
        this.currentState = newState;
        this.lastStateChange = LocalDateTime.now();
        this.currentDuration = newState.getDefaultDuration();
        newState.onEnter(this);
        
        notifyStateChange(previousState, newState);
    }

    public void addListener(SignalStateChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SignalStateChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyStateChange(SignalState from, SignalState to) {
        for (SignalStateChangeListener listener : listeners) {
            listener.onStateChange(this, from, to);
        }
    }

    /**
     * Functional interface for state change notifications.
     */
    @FunctionalInterface
    public interface SignalStateChangeListener {
        void onStateChange(TrafficSignal signal, SignalState from, SignalState to);
    }

    @Override
    public String toString() {
        return String.format("TrafficSignal[road=%s, color=%s, duration=%ds]", 
            roadId, currentState.getColor(), currentDuration);
    }
}



