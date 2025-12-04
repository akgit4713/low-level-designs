package ridesharing.services.impl;

import ridesharing.enums.DriverStatus;
import ridesharing.enums.RideStatus;
import ridesharing.exceptions.*;
import ridesharing.models.*;
import ridesharing.repositories.DriverRepository;
import ridesharing.repositories.PassengerRepository;
import ridesharing.repositories.RideRepository;
import ridesharing.services.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of RideService.
 * Coordinates ride lifecycle management.
 */
public class RideServiceImpl implements RideService {
    
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final PassengerRepository passengerRepository;
    private final DriverMatchingService driverMatchingService;
    private final FareService fareService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    public RideServiceImpl(RideRepository rideRepository,
                           DriverRepository driverRepository,
                           PassengerRepository passengerRepository,
                           DriverMatchingService driverMatchingService,
                           FareService fareService,
                           PaymentService paymentService,
                           NotificationService notificationService) {
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.passengerRepository = passengerRepository;
        this.driverMatchingService = driverMatchingService;
        this.fareService = fareService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    @Override
    public Ride requestRide(RideRequest request) {
        // Validate passenger exists
        passengerRepository.findById(request.getPassengerId())
                .orElseThrow(() -> new PassengerNotFoundException(request.getPassengerId()));
        
        // Calculate estimates
        double distance = fareService.calculateDistance(
                request.getPickupLocation(), request.getDropoffLocation());
        long duration = fareService.estimateTravelTime(
                request.getPickupLocation(), request.getDropoffLocation());
        
        // Create ride
        Ride ride = Ride.builder()
                .passengerId(request.getPassengerId())
                .pickupLocation(request.getPickupLocation())
                .dropoffLocation(request.getDropoffLocation())
                .rideType(request.getRideType())
                .estimatedDistance(distance)
                .estimatedDuration(Duration.ofMinutes(duration))
                .build();
        
        // Calculate estimated fare
        Fare estimatedFare = fareService.calculateEstimatedFare(request);
        ride.setFare(estimatedFare);
        
        rideRepository.save(ride);
        notificationService.notifyRideStatusChanged(ride);
        
        // Try to find a driver
        Optional<Driver> matchedDriver = driverMatchingService.findBestDriver(request);
        
        if (matchedDriver.isPresent()) {
            ride.matchDriver(matchedDriver.get().getUserId());
            rideRepository.save(ride);
            notificationService.notifyDriverMatched(ride);
        }
        
        return ride;
    }

    @Override
    public void acceptRide(String rideId, String driverId) {
        Ride ride = getRideOrThrow(rideId);
        Driver driver = getDriverOrThrow(driverId);
        
        if (!driver.isAvailable()) {
            throw new InvalidRideStateException("Driver is not available");
        }
        
        // If not already matched to this driver, match now
        if (ride.getDriverId() == null) {
            ride.matchDriver(driverId);
        } else if (!ride.getDriverId().equals(driverId)) {
            throw new InvalidRideStateException("Ride is already matched to another driver");
        }
        
        ride.acceptRide();
        driver.setStatus(DriverStatus.BUSY);
        driver.setCurrentRideId(rideId);
        
        rideRepository.save(ride);
        driverRepository.save(driver);
        notificationService.notifyRideStatusChanged(ride);
    }

    @Override
    public void driverArrived(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        ride.driverArrived();
        rideRepository.save(ride);
        notificationService.notifyRideStatusChanged(ride);
    }

    @Override
    public void startRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        ride.startRide();
        rideRepository.save(ride);
        notificationService.notifyRideStatusChanged(ride);
    }

    @Override
    public Ride completeRide(String rideId) {
        Ride ride = getRideOrThrow(rideId);
        Driver driver = getDriverOrThrow(ride.getDriverId());
        Passenger passenger = passengerRepository.findById(ride.getPassengerId())
                .orElseThrow(() -> new PassengerNotFoundException(ride.getPassengerId()));
        
        // Calculate final fare based on actual distance/time
        // For simplicity, using estimated values (in production, use actual tracking data)
        Fare finalFare = fareService.calculateFinalFare(
                ride.getEstimatedDistance(),
                ride.getEstimatedDuration().toMinutes(),
                ride.getRideType());
        
        ride.completeRide(ride.getEstimatedDistance(), finalFare);
        
        // Process payment
        Payment payment = paymentService.processPayment(ride, passenger.getPreferredPaymentMethod());
        ride.setPayment(payment);
        
        // Update driver
        driver.setStatus(DriverStatus.AVAILABLE);
        driver.setCurrentRideId(null);
        driver.addEarnings(finalFare.getDriverEarnings());
        driver.addRideToHistory(rideId);
        
        // Update passenger
        passenger.addRideToHistory(rideId);
        
        rideRepository.save(ride);
        driverRepository.save(driver);
        passengerRepository.save(passenger);
        notificationService.notifyRideStatusChanged(ride);
        
        return ride;
    }

    @Override
    public void cancelRide(String rideId, String reason) {
        Ride ride = getRideOrThrow(rideId);
        
        // If driver was assigned, make them available again
        if (ride.getDriverId() != null) {
            driverRepository.findById(ride.getDriverId()).ifPresent(driver -> {
                driver.setStatus(DriverStatus.AVAILABLE);
                driver.setCurrentRideId(null);
                driverRepository.save(driver);
            });
        }
        
        ride.cancelRide(reason);
        rideRepository.save(ride);
        notificationService.notifyRideStatusChanged(ride);
    }

    @Override
    public Optional<Ride> getRide(String rideId) {
        return rideRepository.findById(rideId);
    }

    @Override
    public List<Ride> getActiveRidesForPassenger(String passengerId) {
        return rideRepository.findByPassengerId(passengerId).stream()
                .filter(Ride::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ride> getRideHistoryForPassenger(String passengerId) {
        return rideRepository.findByPassengerId(passengerId).stream()
                .filter(ride -> ride.getStatus() == RideStatus.COMPLETED || 
                               ride.getStatus() == RideStatus.CANCELLED)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Ride> getActiveRideForDriver(String driverId) {
        return rideRepository.findByDriverId(driverId).stream()
                .filter(Ride::isActive)
                .findFirst();
    }

    @Override
    public Fare estimateFare(RideRequest request) {
        return fareService.calculateEstimatedFare(request);
    }

    @Override
    public void rateDriver(String rideId, int rating) {
        Ride ride = getRideOrThrow(rideId);
        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new InvalidRideStateException(ride.getStatus(), "rate driver");
        }
        
        ride.setDriverRating(rating);
        rideRepository.save(ride);
        
        driverRepository.findById(ride.getDriverId())
                .ifPresent(driver -> {
                    driver.addRating(rating);
                    driverRepository.save(driver);
                });
    }

    @Override
    public void ratePassenger(String rideId, int rating) {
        Ride ride = getRideOrThrow(rideId);
        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new InvalidRideStateException(ride.getStatus(), "rate passenger");
        }
        
        ride.setPassengerRating(rating);
        rideRepository.save(ride);
        
        passengerRepository.findById(ride.getPassengerId())
                .ifPresent(passenger -> {
                    passenger.addRating(rating);
                    passengerRepository.save(passenger);
                });
    }

    @Override
    public void updateRideLocation(String rideId, Location location) {
        Ride ride = getRideOrThrow(rideId);
        ride.setCurrentLocation(location);
        rideRepository.save(ride);
        notificationService.notifyLocationUpdated(ride);
    }

    private Ride getRideOrThrow(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(rideId));
    }

    private Driver getDriverOrThrow(String driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
    }
}



