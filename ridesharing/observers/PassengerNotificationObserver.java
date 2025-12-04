package ridesharing.observers;

import ridesharing.models.Ride;
import ridesharing.enums.RideStatus;

/**
 * Observer that sends notifications to passengers about ride updates.
 */
public class PassengerNotificationObserver implements RideObserver {
    
    @Override
    public void onRideStatusChanged(Ride ride) {
        String passengerId = ride.getPassengerId();
        String message = buildStatusMessage(ride);
        sendNotification(passengerId, "Ride Update", message);
    }

    @Override
    public void onLocationUpdated(Ride ride) {
        if (ride.getStatus() == RideStatus.ACCEPTED) {
            String message = String.format("Your driver is on the way. Current location: %s",
                    ride.getCurrentLocation());
            sendNotification(ride.getPassengerId(), "Driver Location", message);
        }
    }

    @Override
    public void onDriverMatched(Ride ride) {
        String message = String.format("Driver found! Your ride will arrive shortly.");
        sendNotification(ride.getPassengerId(), "Driver Matched", message);
    }

    private String buildStatusMessage(Ride ride) {
        switch (ride.getStatus()) {
            case MATCHED:
                return "A driver has been found for your ride!";
            case ACCEPTED:
                return "Your driver is on the way to pick you up.";
            case DRIVER_ARRIVED:
                return "Your driver has arrived at the pickup location!";
            case IN_PROGRESS:
                return "Your ride has started. Enjoy your trip!";
            case COMPLETED:
                return String.format("Ride completed! Total fare: $%.2f", 
                        ride.getFare() != null ? ride.getFare().getTotalAmount() : 0);
            case CANCELLED:
                return "Your ride has been cancelled.";
            default:
                return "Your ride status has been updated.";
        }
    }

    private void sendNotification(String userId, String title, String message) {
        // In production: integrate with push notification service (FCM, APNS)
        System.out.println(String.format("[PASSENGER NOTIFICATION - %s] %s: %s", userId, title, message));
    }
}



