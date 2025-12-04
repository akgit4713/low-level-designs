package ridesharing.exceptions;

public class NoDriverAvailableException extends RideSharingException {
    
    public NoDriverAvailableException(String message) {
        super(message);
    }
    
    public NoDriverAvailableException() {
        super("No drivers available in your area");
    }
}



