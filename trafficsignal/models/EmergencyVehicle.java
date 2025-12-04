package trafficsignal.models;

import trafficsignal.enums.Direction;
import trafficsignal.enums.EmergencyType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an emergency vehicle approaching the intersection.
 */
public class EmergencyVehicle {
    
    private final String id;
    private final EmergencyType type;
    private final Direction approachingFrom;
    private final LocalDateTime detectedAt;
    private boolean isCleared;

    public EmergencyVehicle(EmergencyType type, Direction approachingFrom) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.approachingFrom = approachingFrom;
        this.detectedAt = LocalDateTime.now();
        this.isCleared = false;
    }

    public String getId() {
        return id;
    }

    public EmergencyType getType() {
        return type;
    }

    public Direction getApproachingFrom() {
        return approachingFrom;
    }

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public boolean isCleared() {
        return isCleared;
    }

    public void markCleared() {
        this.isCleared = true;
    }

    public int getPriority() {
        return type.getPriorityLevel();
    }

    @Override
    public String toString() {
        return String.format("EmergencyVehicle[type=%s, from=%s, cleared=%s]", 
            type.getDisplayName(), approachingFrom, isCleared);
    }
}



