package ridesharing.exceptions;

import ridesharing.enums.RideStatus;

public class InvalidRideStateException extends RideSharingException {
    
    public InvalidRideStateException(RideStatus currentStatus, String attemptedAction) {
        super(String.format("Cannot %s when ride is in %s state", attemptedAction, currentStatus));
    }
    
    public InvalidRideStateException(String message) {
        super(message);
    }
}



