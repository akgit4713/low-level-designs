package ridesharing.services.impl;

import ridesharing.models.Location;
import ridesharing.models.Ride;
import ridesharing.repositories.DriverRepository;
import ridesharing.repositories.RideRepository;
import ridesharing.services.FareService;
import ridesharing.services.NotificationService;
import ridesharing.services.TrackingService;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of TrackingService.
 * Manages real-time ride tracking.
 */
public class TrackingServiceImpl implements TrackingService {
    
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final FareService fareService;
    private final NotificationService notificationService;
    
    // Active tracking sessions
    private final Map<String, Location> rideLocations = new ConcurrentHashMap<>();

    public TrackingServiceImpl(RideRepository rideRepository,
                              DriverRepository driverRepository,
                              FareService fareService,
                              NotificationService notificationService) {
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.fareService = fareService;
        this.notificationService = notificationService;
    }

    @Override
    public void updateDriverLocation(String driverId, Location location) {
        driverRepository.updateLocation(driverId, location);
        
        // Update any active rides for this driver
        driverRepository.findById(driverId).ifPresent(driver -> {
            if (driver.getCurrentRideId() != null) {
                rideLocations.put(driver.getCurrentRideId(), location);
                
                rideRepository.findById(driver.getCurrentRideId()).ifPresent(ride -> {
                    ride.setCurrentLocation(location);
                    rideRepository.save(ride);
                    notificationService.notifyLocationUpdated(ride);
                });
            }
        });
    }

    @Override
    public Optional<Location> getRideLocation(String rideId) {
        Location cachedLocation = rideLocations.get(rideId);
        if (cachedLocation != null) {
            return Optional.of(cachedLocation);
        }
        
        return rideRepository.findById(rideId)
                .map(Ride::getCurrentLocation);
    }

    @Override
    public long getETAMinutes(String rideId) {
        return rideRepository.findById(rideId)
                .map(ride -> {
                    Location current = ride.getCurrentLocation();
                    if (current == null) {
                        return ride.getEstimatedDuration().toMinutes();
                    }
                    
                    Location destination = ride.isActive() && ride.getPickupLocation() != null ?
                            (ride.getStatus().ordinal() < 3 ? ride.getPickupLocation() : ride.getDropoffLocation()) :
                            ride.getDropoffLocation();
                    
                    return fareService.estimateTravelTime(current, destination);
                })
                .orElse(0L);
    }

    @Override
    public double getRemainingDistance(String rideId) {
        return rideRepository.findById(rideId)
                .map(ride -> {
                    Location current = ride.getCurrentLocation();
                    if (current == null) {
                        return ride.getEstimatedDistance();
                    }
                    return fareService.calculateDistance(current, ride.getDropoffLocation());
                })
                .orElse(0.0);
    }

    @Override
    public void startTracking(Ride ride) {
        if (ride.getDriverId() != null) {
            driverRepository.findById(ride.getDriverId()).ifPresent(driver -> {
                if (driver.getCurrentLocation() != null) {
                    rideLocations.put(ride.getRideId(), driver.getCurrentLocation());
                }
            });
        }
    }

    @Override
    public void stopTracking(String rideId) {
        rideLocations.remove(rideId);
    }
}



