package ridesharing.factories;

import ridesharing.enums.PaymentMethod;
import ridesharing.enums.RideType;
import ridesharing.models.*;
import ridesharing.observers.RideObserver;
import ridesharing.services.*;

import java.util.List;
import java.util.Optional;

/**
 * Main facade for the Ride-Sharing Service.
 * Provides a simplified API for common operations.
 * Follows Facade Pattern.
 */
public class RideSharingService {
    
    private final RideService rideService;
    private final UserService userService;
    private final FareService fareService;
    private final PaymentService paymentService;
    private final TrackingService trackingService;
    private final NotificationService notificationService;

    public RideSharingService(RideService rideService,
                              UserService userService,
                              FareService fareService,
                              PaymentService paymentService,
                              TrackingService trackingService,
                              NotificationService notificationService) {
        this.rideService = rideService;
        this.userService = userService;
        this.fareService = fareService;
        this.paymentService = paymentService;
        this.trackingService = trackingService;
        this.notificationService = notificationService;
    }

    // ============ User Management ============
    
    public Passenger registerPassenger(String name, String email, String phone) {
        return userService.registerPassenger(name, email, phone);
    }

    public Driver registerDriver(String name, String email, String phone, 
                                 Vehicle vehicle, String licenseNumber) {
        return userService.registerDriver(name, email, phone, vehicle, licenseNumber);
    }

    public Optional<Passenger> getPassenger(String passengerId) {
        return userService.getPassenger(passengerId);
    }

    public Optional<Driver> getDriver(String driverId) {
        return userService.getDriver(driverId);
    }

    public void setDriverOnline(String driverId, Location location) {
        userService.updateDriverLocation(driverId, location);
        userService.setDriverOnline(driverId);
    }

    public void setDriverOffline(String driverId) {
        userService.setDriverOffline(driverId);
    }

    // ============ Ride Operations ============
    
    /**
     * Request a new ride.
     */
    public Ride requestRide(String passengerId, Location pickup, Location dropoff, RideType rideType) {
        RideRequest request = RideRequest.builder()
                .passengerId(passengerId)
                .pickupLocation(pickup)
                .dropoffLocation(dropoff)
                .rideType(rideType)
                .build();
        
        return rideService.requestRide(request);
    }

    /**
     * Get fare estimate before requesting ride.
     */
    public Fare getFareEstimate(Location pickup, Location dropoff, RideType rideType) {
        RideRequest request = RideRequest.builder()
                .passengerId("temp")
                .pickupLocation(pickup)
                .dropoffLocation(dropoff)
                .rideType(rideType)
                .build();
        
        return rideService.estimateFare(request);
    }

    /**
     * Accept a ride (driver action).
     */
    public void acceptRide(String rideId, String driverId) {
        rideService.acceptRide(rideId, driverId);
    }

    /**
     * Mark driver as arrived at pickup location.
     */
    public void driverArrived(String rideId) {
        rideService.driverArrived(rideId);
    }

    /**
     * Start the ride.
     */
    public void startRide(String rideId) {
        rideService.startRide(rideId);
        trackingService.startTracking(rideService.getRide(rideId).orElse(null));
    }

    /**
     * Complete the ride.
     */
    public Ride completeRide(String rideId) {
        Ride ride = rideService.completeRide(rideId);
        trackingService.stopTracking(rideId);
        return ride;
    }

    /**
     * Cancel a ride.
     */
    public void cancelRide(String rideId, String reason) {
        rideService.cancelRide(rideId, reason);
        trackingService.stopTracking(rideId);
    }

    /**
     * Get ride details.
     */
    public Optional<Ride> getRide(String rideId) {
        return rideService.getRide(rideId);
    }

    /**
     * Get active rides for a passenger.
     */
    public List<Ride> getActiveRidesForPassenger(String passengerId) {
        return rideService.getActiveRidesForPassenger(passengerId);
    }

    /**
     * Get ride history for a passenger.
     */
    public List<Ride> getRideHistoryForPassenger(String passengerId) {
        return rideService.getRideHistoryForPassenger(passengerId);
    }

    // ============ Tracking ============
    
    public void updateDriverLocation(String driverId, Location location) {
        trackingService.updateDriverLocation(driverId, location);
    }

    public Optional<Location> getRideLocation(String rideId) {
        return trackingService.getRideLocation(rideId);
    }

    public long getETAMinutes(String rideId) {
        return trackingService.getETAMinutes(rideId);
    }

    // ============ Ratings ============
    
    public void rateDriver(String rideId, int rating) {
        rideService.rateDriver(rideId, rating);
    }

    public void ratePassenger(String rideId, int rating) {
        rideService.ratePassenger(rideId, rating);
    }

    // ============ Payments ============
    
    public void addWalletBalance(String passengerId, double amount) {
        paymentService.addWalletBalance(passengerId, amount);
    }

    public double getWalletBalance(String passengerId) {
        return paymentService.getWalletBalance(passengerId);
    }

    // ============ Observers ============
    
    public void registerObserver(RideObserver observer) {
        notificationService.registerObserver(observer);
    }

    public void removeObserver(RideObserver observer) {
        notificationService.removeObserver(observer);
    }
}



