package ridesharing.exceptions;

public class DriverNotFoundException extends RideSharingException {
    
    public DriverNotFoundException(String driverId) {
        super("Driver not found with ID: " + driverId);
    }
}



