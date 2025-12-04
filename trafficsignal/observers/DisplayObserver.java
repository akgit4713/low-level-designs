package trafficsignal.observers;

import trafficsignal.models.EmergencyVehicle;
import trafficsignal.models.Intersection;
import trafficsignal.models.Road;
import trafficsignal.states.SignalState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Observer Pattern: Displays signal changes to console (simulates traffic display).
 */
public class DisplayObserver implements SignalObserver, EmergencyObserver {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final String displayId;

    public DisplayObserver(String displayId) {
        this.displayId = displayId;
    }

    @Override
    public void onSignalChange(Road road, SignalState previousState, SignalState newState) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[%s][Display-%s] %s Road: %s → %s (Duration: %ds)%n",
            timestamp,
            displayId,
            road.getDirection(),
            previousState.getColor(),
            newState.getColor(),
            road.getSignal().getCurrentDuration()
        );
    }

    @Override
    public void onCycleComplete(String intersectionId) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[%s][Display-%s] === Cycle Complete for Intersection %s ===%n",
            timestamp, displayId, intersectionId);
    }

    @Override
    public void onEmergencyDetected(Intersection intersection, EmergencyVehicle vehicle) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[%s][Display-%s] ⚠️  EMERGENCY: %s approaching from %s%n",
            timestamp,
            displayId,
            vehicle.getType().getDisplayName(),
            vehicle.getApproachingFrom()
        );
    }

    @Override
    public void onEmergencyCleared(Intersection intersection, EmergencyVehicle vehicle) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[%s][Display-%s] ✓ Emergency Cleared: %s%n",
            timestamp,
            displayId,
            vehicle.getType().getDisplayName()
        );
    }

    @Override
    public void onEmergencyOverrideActivated(Intersection intersection) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[%s][Display-%s] 🚨 EMERGENCY OVERRIDE ACTIVATED at %s%n",
            timestamp, displayId, intersection.getName());
    }

    @Override
    public void onNormalOperationResumed(Intersection intersection) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[%s][Display-%s] ✓ Normal operation resumed at %s%n",
            timestamp, displayId, intersection.getName());
    }
}



