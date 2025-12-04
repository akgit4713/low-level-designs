package trafficsignal.exceptions;

import trafficsignal.enums.SignalColor;

/**
 * Exception thrown when an invalid signal state transition is attempted.
 */
public class InvalidStateTransitionException extends TrafficSignalException {
    
    private final SignalColor fromState;
    private final SignalColor toState;

    public InvalidStateTransitionException(SignalColor fromState, SignalColor toState) {
        super(String.format("Invalid state transition from %s to %s", fromState, toState));
        this.fromState = fromState;
        this.toState = toState;
    }

    public SignalColor getFromState() {
        return fromState;
    }

    public SignalColor getToState() {
        return toState;
    }
}



