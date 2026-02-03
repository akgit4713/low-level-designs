package deliveryservice.exceptions;

public class DriverAlreadyExistsException extends RuntimeException {
    
    public DriverAlreadyExistsException(String driverId) {
        super("Driver already exists: " + driverId);
    }
}
