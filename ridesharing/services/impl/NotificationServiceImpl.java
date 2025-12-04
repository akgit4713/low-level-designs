package ridesharing.services.impl;

import ridesharing.models.Ride;
import ridesharing.observers.RideObserver;
import ridesharing.services.NotificationService;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of NotificationService.
 * Manages observers and distributes notifications.
 */
public class NotificationServiceImpl implements NotificationService {
    
    private final List<RideObserver> observers = new CopyOnWriteArrayList<>();

    @Override
    public void registerObserver(RideObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(RideObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyRideStatusChanged(Ride ride) {
        for (RideObserver observer : observers) {
            try {
                observer.onRideStatusChanged(ride);
            } catch (Exception e) {
                // Log and continue - don't let one observer failure affect others
                System.err.println("Observer failed: " + e.getMessage());
            }
        }
    }

    @Override
    public void notifyLocationUpdated(Ride ride) {
        for (RideObserver observer : observers) {
            try {
                observer.onLocationUpdated(ride);
            } catch (Exception e) {
                System.err.println("Observer failed: " + e.getMessage());
            }
        }
    }

    @Override
    public void notifyDriverMatched(Ride ride) {
        for (RideObserver observer : observers) {
            try {
                observer.onDriverMatched(ride);
            } catch (Exception e) {
                System.err.println("Observer failed: " + e.getMessage());
            }
        }
    }
}



