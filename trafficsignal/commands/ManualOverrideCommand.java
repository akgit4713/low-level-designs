package trafficsignal.commands;

import trafficsignal.models.Road;
import trafficsignal.states.SignalState;

/**
 * Command Pattern: Manual override command for a specific road signal.
 */
public class ManualOverrideCommand implements SignalCommand {
    
    private final Road road;
    private final SignalState targetState;
    private SignalState previousState;
    private boolean executed;

    public ManualOverrideCommand(Road road, SignalState targetState) {
        this.road = road;
        this.targetState = targetState;
        this.executed = false;
    }

    @Override
    public void execute() {
        if (executed) {
            return;
        }

        previousState = road.getSignal().getCurrentState();
        road.getSignal().setEmergencyOverride(true);
        road.getSignal().forceTransitionTo(targetState);
        executed = true;
    }

    @Override
    public void undo() {
        if (!executed || previousState == null) {
            return;
        }

        road.getSignal().setEmergencyOverride(false);
        road.getSignal().forceTransitionTo(previousState);
        executed = false;
    }

    @Override
    public String getDescription() {
        return String.format("Manual override for %s to %s", 
            road.getName(), 
            targetState.getColor());
    }

    public boolean isExecuted() {
        return executed;
    }
}



