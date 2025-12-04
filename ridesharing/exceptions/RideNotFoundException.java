package ridesharing.exceptions;

public class RideNotFoundException extends RideSharingException {
    
    public RideNotFoundException(String rideId) {
        super("Ride not found with ID: " + rideId);
    }
}



