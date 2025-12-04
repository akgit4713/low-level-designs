package ridesharing.observers;

import ridesharing.models.Ride;
import ridesharing.enums.RideStatus;

/**
 * Observer that sends notifications to drivers about ride updates.
 */
public class DriverNotificationObserver implements RideObserver {
    
    @Override
    public void onRideStatusChanged(Ride ride) {
        if (ride.getDriverId() == null) {
            return;
        }
        
        String driverId = ride.getDriverId();
        String message = buildStatusMessage(ride);
        if (message != null) {
            sendNotification(driverId, "Ride Update", message);
        }
    }

    @Override
    public void onLocationUpdated(Ride ride) {
        // Drivers don't need location updates for their own rides
    }

    @Override
    public void onDriverMatched(Ride ride) {
        if (ride.getDriverId() != null) {
            String message = String.format("New ride request! Pickup: %s, Dropoff: %s",
                    ride.getPickupLocation().getAddress(),
                    ride.getDropoffLocation().getAddress());
            sendNotification(ride.getDriverId(), "New Ride Request", message);
        }
    }

    private String buildStatusMessage(Ride ride) {
        switch (ride.getStatus()) {
            case COMPLETED:
                double earnings = ride.getFare() != null ? ride.getFare().getDriverEarnings() : 0;
                return String.format("Ride completed! You earned: $%.2f", earnings);
            case CANCELLED:
                return "The ride has been cancelled.";
            default:
                return null;
        }
    }

    private void sendNotification(String userId, String title, String message) {
        // In production: integrate with push notification service
        System.out.println(String.format("[DRIVER NOTIFICATION - %s] %s: %s", userId, title, message));
    }
}



