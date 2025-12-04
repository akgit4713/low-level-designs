package trafficsignal.observers;

import trafficsignal.models.EmergencyVehicle;
import trafficsignal.models.Intersection;

/**
 * Observer Pattern: Interface for emergency event notifications.
 */
public interface EmergencyObserver {
    
    /**
     * Called when an emergency vehicle is detected.
     */
    void onEmergencyDetected(Intersection intersection, EmergencyVehicle vehicle);

    /**
     * Called when an emergency vehicle clears the intersection.
     */
    void onEmergencyCleared(Intersection intersection, EmergencyVehicle vehicle);

    /**
     * Called when emergency override is activated.
     */
    void onEmergencyOverrideActivated(Intersection intersection);

    /**
     * Called when normal operation resumes after emergency.
     */
    void onNormalOperationResumed(Intersection intersection);
}



