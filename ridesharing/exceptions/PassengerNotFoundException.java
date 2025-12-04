package ridesharing.exceptions;

public class PassengerNotFoundException extends RideSharingException {
    
    public PassengerNotFoundException(String passengerId) {
        super("Passenger not found with ID: " + passengerId);
    }
}



