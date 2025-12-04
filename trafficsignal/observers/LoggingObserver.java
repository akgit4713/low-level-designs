package trafficsignal.observers;

import trafficsignal.models.EmergencyVehicle;
import trafficsignal.models.Intersection;
import trafficsignal.models.Road;
import trafficsignal.states.SignalState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Observer Pattern: Logs all signal and emergency events for auditing.
 */
public class LoggingObserver implements SignalObserver, EmergencyObserver {
    
    private final List<LogEntry> logs;
    private final int maxLogSize;

    public LoggingObserver() {
        this(10000);
    }

    public LoggingObserver(int maxLogSize) {
        this.logs = new ArrayList<>();
        this.maxLogSize = maxLogSize;
    }

    @Override
    public void onSignalChange(Road road, SignalState previousState, SignalState newState) {
        addLog(LogEntry.Type.SIGNAL_CHANGE, 
            String.format("Road %s (%s): %s → %s", 
                road.getName(), 
                road.getDirection(), 
                previousState.getColor(), 
                newState.getColor()
            )
        );
    }

    @Override
    public void onCycleComplete(String intersectionId) {
        addLog(LogEntry.Type.CYCLE_COMPLETE, 
            String.format("Intersection %s completed a signal cycle", intersectionId)
        );
    }

    @Override
    public void onEmergencyDetected(Intersection intersection, EmergencyVehicle vehicle) {
        addLog(LogEntry.Type.EMERGENCY_DETECTED,
            String.format("Emergency %s detected at %s from %s",
                vehicle.getType().getDisplayName(),
                intersection.getName(),
                vehicle.getApproachingFrom()
            )
        );
    }

    @Override
    public void onEmergencyCleared(Intersection intersection, EmergencyVehicle vehicle) {
        addLog(LogEntry.Type.EMERGENCY_CLEARED,
            String.format("Emergency %s cleared at %s",
                vehicle.getType().getDisplayName(),
                intersection.getName()
            )
        );
    }

    @Override
    public void onEmergencyOverrideActivated(Intersection intersection) {
        addLog(LogEntry.Type.EMERGENCY_OVERRIDE,
            String.format("Emergency override activated at %s", intersection.getName())
        );
    }

    @Override
    public void onNormalOperationResumed(Intersection intersection) {
        addLog(LogEntry.Type.NORMAL_RESUMED,
            String.format("Normal operation resumed at %s", intersection.getName())
        );
    }

    private synchronized void addLog(LogEntry.Type type, String message) {
        if (logs.size() >= maxLogSize) {
            logs.remove(0); // Remove oldest
        }
        logs.add(new LogEntry(type, message));
    }

    public List<LogEntry> getLogs() {
        return Collections.unmodifiableList(logs);
    }

    public List<LogEntry> getLogsByType(LogEntry.Type type) {
        return logs.stream()
            .filter(log -> log.type() == type)
            .toList();
    }

    public void clearLogs() {
        logs.clear();
    }

    /**
     * Represents a single log entry.
     */
    public record LogEntry(Type type, String message, LocalDateTime timestamp) {
        
        public LogEntry(Type type, String message) {
            this(type, message, LocalDateTime.now());
        }

        public enum Type {
            SIGNAL_CHANGE,
            CYCLE_COMPLETE,
            EMERGENCY_DETECTED,
            EMERGENCY_CLEARED,
            EMERGENCY_OVERRIDE,
            NORMAL_RESUMED
        }
    }
}



