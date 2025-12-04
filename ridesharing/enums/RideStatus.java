package ridesharing.enums;

public enum RideStatus {
    REQUESTED,      // Passenger has requested a ride
    MATCHING,       // System is finding a driver
    MATCHED,        // Driver has been matched
    ACCEPTED,       // Driver has accepted the ride
    DRIVER_ARRIVED, // Driver has arrived at pickup
    IN_PROGRESS,    // Ride is ongoing
    COMPLETED,      // Ride completed successfully
    CANCELLED       // Ride was cancelled
}



