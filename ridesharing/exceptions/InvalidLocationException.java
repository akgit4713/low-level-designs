package ridesharing.exceptions;

public class InvalidLocationException extends RideSharingException {
    
    public InvalidLocationException(String message) {
        super(message);
    }
    
    public InvalidLocationException(double latitude, double longitude) {
        super(String.format("Invalid location coordinates: (%.6f, %.6f)", latitude, longitude));
    }
}



