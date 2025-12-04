package ridesharing.services.impl;

import ridesharing.enums.RideStatus;
import ridesharing.enums.RideType;
import ridesharing.models.Fare;
import ridesharing.models.Location;
import ridesharing.models.RideRequest;
import ridesharing.repositories.RideRepository;
import ridesharing.services.FareService;
import ridesharing.strategies.distance.DistanceCalculationStrategy;
import ridesharing.strategies.pricing.PricingStrategy;
import ridesharing.strategies.pricing.SurgePricingStrategy;

/**
 * Implementation of FareService.
 * Uses configurable pricing and distance strategies.
 */
public class FareServiceImpl implements FareService {
    
    private final DistanceCalculationStrategy distanceStrategy;
    private final PricingStrategy pricingStrategy;
    private final RideRepository rideRepository;

    public FareServiceImpl(DistanceCalculationStrategy distanceStrategy,
                          PricingStrategy pricingStrategy,
                          RideRepository rideRepository) {
        this.distanceStrategy = distanceStrategy;
        this.pricingStrategy = pricingStrategy;
        this.rideRepository = rideRepository;
    }

    @Override
    public Fare calculateEstimatedFare(RideRequest request) {
        double distance = calculateDistance(request.getPickupLocation(), request.getDropoffLocation());
        long duration = estimateTravelTime(request.getPickupLocation(), request.getDropoffLocation());
        
        // Check if surge pricing should apply
        double surgeMultiplier = getSurgeMultiplier(request.getPickupLocation());
        
        if (surgeMultiplier > 1.0) {
            PricingStrategy surgeStrategy = new SurgePricingStrategy(pricingStrategy, surgeMultiplier);
            return surgeStrategy.calculateFare(distance, duration, request.getRideType());
        }
        
        return pricingStrategy.calculateFare(distance, duration, request.getRideType());
    }

    @Override
    public Fare calculateFare(double distanceKm, long durationMinutes, RideType rideType) {
        return pricingStrategy.calculateFare(distanceKm, durationMinutes, rideType);
    }

    @Override
    public Fare calculateFinalFare(double actualDistanceKm, long actualDurationMinutes, RideType rideType) {
        return pricingStrategy.calculateFare(actualDistanceKm, actualDurationMinutes, rideType);
    }

    @Override
    public double getSurgeMultiplier(Location location) {
        // Calculate surge based on demand in the area
        // Simplified: count pending rides vs available drivers
        long pendingRides = rideRepository.findByStatus(RideStatus.REQUESTED).size();
        
        if (pendingRides > 10) return 2.0;
        if (pendingRides > 5) return 1.5;
        if (pendingRides > 2) return 1.25;
        return 1.0;
    }

    @Override
    public double calculateDistance(Location from, Location to) {
        return distanceStrategy.calculateDistance(from, to);
    }

    @Override
    public long estimateTravelTime(Location from, Location to) {
        return distanceStrategy.estimateTravelTimeMinutes(from, to);
    }
}



