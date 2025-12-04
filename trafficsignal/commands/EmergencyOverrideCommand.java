package trafficsignal.commands;

import trafficsignal.enums.Direction;
import trafficsignal.models.EmergencyVehicle;
import trafficsignal.models.Intersection;
import trafficsignal.models.Road;
import trafficsignal.states.GreenState;
import trafficsignal.states.RedState;
import trafficsignal.states.SignalState;

import java.util.HashMap;
import java.util.Map;

/**
 * Command Pattern: Emergency override command that:
 * 1. Sets emergency direction to GREEN
 * 2. Sets all perpendicular directions to RED
 * 3. Stores previous states for undo
 */
public class EmergencyOverrideCommand implements SignalCommand {
    
    private final Intersection intersection;
    private final EmergencyVehicle vehicle;
    private final Map<Direction, SignalState> previousStates;
    private boolean executed;

    public EmergencyOverrideCommand(Intersection intersection, EmergencyVehicle vehicle) {
        this.intersection = intersection;
        this.vehicle = vehicle;
        this.previousStates = new HashMap<>();
        this.executed = false;
    }

    @Override
    public void execute() {
        if (executed) {
            return;
        }

        Direction emergencyDirection = vehicle.getApproachingFrom();
        
        // Store current states and apply emergency override
        for (Road road : intersection.getAllRoads()) {
            previousStates.put(road.getDirection(), road.getSignal().getCurrentState());
            road.getSignal().setEmergencyOverride(true);
            
            if (road.getDirection() == emergencyDirection || 
                road.getDirection() == emergencyDirection.getOpposite()) {
                // Emergency vehicle's road axis gets GREEN
                road.getSignal().forceTransitionTo(new GreenState());
            } else {
                // Perpendicular roads get RED
                road.getSignal().forceTransitionTo(new RedState());
            }
        }

        executed = true;
    }

    @Override
    public void undo() {
        if (!executed) {
            return;
        }

        // Restore previous states
        for (Road road : intersection.getAllRoads()) {
            road.getSignal().setEmergencyOverride(false);
            SignalState previousState = previousStates.get(road.getDirection());
            if (previousState != null) {
                road.getSignal().forceTransitionTo(previousState);
            }
        }

        executed = false;
    }

    @Override
    public String getDescription() {
        return String.format("Emergency override for %s from %s", 
            vehicle.getType().getDisplayName(), 
            vehicle.getApproachingFrom());
    }

    public boolean isExecuted() {
        return executed;
    }
}



