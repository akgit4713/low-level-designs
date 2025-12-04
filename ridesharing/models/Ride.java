package ridesharing.models;

import ridesharing.enums.RideStatus;
import ridesharing.enums.RideType;
import ridesharing.exceptions.InvalidRideStateException;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

/**
 * Core entity representing a ride from request to completion.
 */
public class Ride {
    private final String rideId;
    private final String passengerId;
    private String driverId;
    private final Location pickupLocation;
    private final Location dropoffLocation;
    private final RideType rideType;
    private RideStatus status;
    private Fare fare;
    private Payment payment;
    
    // Timestamps
    private final LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime driverArrivedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    
    // Tracking
    private Location currentLocation;
    private double estimatedDistance; // in kilometers
    private Duration estimatedDuration;
    private double actualDistance;
    private Duration actualDuration;
    
    // Ratings
    private Integer passengerRating;
    private Integer driverRating;
    private String cancellationReason;

    private Ride(Builder builder) {
        this.rideId = UUID.randomUUID().toString();
        this.passengerId = builder.passengerId;
        this.pickupLocation = builder.pickupLocation;
        this.dropoffLocation = builder.dropoffLocation;
        this.rideType = builder.rideType;
        this.status = RideStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
        this.estimatedDistance = builder.estimatedDistance;
        this.estimatedDuration = builder.estimatedDuration;
    }

    public static Builder builder() {
        return new Builder();
    }

    // State transitions with validation
    public void matchDriver(String driverId) {
        validateState(RideStatus.REQUESTED, "match driver");
        this.driverId = driverId;
        this.status = RideStatus.MATCHED;
    }

    public void acceptRide() {
        validateState(RideStatus.MATCHED, "accept ride");
        this.status = RideStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    public void driverArrived() {
        validateState(RideStatus.ACCEPTED, "mark driver arrived");
        this.status = RideStatus.DRIVER_ARRIVED;
        this.driverArrivedAt = LocalDateTime.now();
    }

    public void startRide() {
        if (status != RideStatus.DRIVER_ARRIVED && status != RideStatus.ACCEPTED) {
            throw new InvalidRideStateException(status, "start ride");
        }
        this.status = RideStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void completeRide(double actualDistance, Fare fare) {
        validateState(RideStatus.IN_PROGRESS, "complete ride");
        this.status = RideStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.actualDistance = actualDistance;
        this.actualDuration = Duration.between(startedAt, completedAt);
        this.fare = fare;
    }

    public void cancelRide(String reason) {
        if (status == RideStatus.COMPLETED || status == RideStatus.CANCELLED) {
            throw new InvalidRideStateException(status, "cancel ride");
        }
        this.status = RideStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    private void validateState(RideStatus expectedStatus, String action) {
        if (status != expectedStatus) {
            throw new InvalidRideStateException(status, action);
        }
    }

    // Getters
    public String getRideId() {
        return rideId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public String getDriverId() {
        return driverId;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDropoffLocation() {
        return dropoffLocation;
    }

    public RideType getRideType() {
        return rideType;
    }

    public RideStatus getStatus() {
        return status;
    }

    public Fare getFare() {
        return fare;
    }

    public void setFare(Fare fare) {
        this.fare = fare;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public LocalDateTime getDriverArrivedAt() {
        return driverArrivedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public double getEstimatedDistance() {
        return estimatedDistance;
    }

    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }

    public double getActualDistance() {
        return actualDistance;
    }

    public Duration getActualDuration() {
        return actualDuration;
    }

    public Integer getPassengerRating() {
        return passengerRating;
    }

    public void setPassengerRating(Integer passengerRating) {
        if (passengerRating < 1 || passengerRating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.passengerRating = passengerRating;
    }

    public Integer getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        if (driverRating < 1 || driverRating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.driverRating = driverRating;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public boolean isActive() {
        return status == RideStatus.MATCHED || 
               status == RideStatus.ACCEPTED || 
               status == RideStatus.DRIVER_ARRIVED ||
               status == RideStatus.IN_PROGRESS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ride ride = (Ride) o;
        return Objects.equals(rideId, ride.rideId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rideId);
    }

    @Override
    public String toString() {
        return String.format("Ride{id='%s', type=%s, status=%s, from=%s, to=%s}", 
                rideId, rideType, status, 
                pickupLocation.getAddress(), dropoffLocation.getAddress());
    }

    public static class Builder {
        private String passengerId;
        private Location pickupLocation;
        private Location dropoffLocation;
        private RideType rideType = RideType.REGULAR;
        private double estimatedDistance;
        private Duration estimatedDuration;

        public Builder passengerId(String passengerId) {
            this.passengerId = passengerId;
            return this;
        }

        public Builder pickupLocation(Location pickupLocation) {
            this.pickupLocation = pickupLocation;
            return this;
        }

        public Builder dropoffLocation(Location dropoffLocation) {
            this.dropoffLocation = dropoffLocation;
            return this;
        }

        public Builder rideType(RideType rideType) {
            this.rideType = rideType;
            return this;
        }

        public Builder estimatedDistance(double estimatedDistance) {
            this.estimatedDistance = estimatedDistance;
            return this;
        }

        public Builder estimatedDuration(Duration estimatedDuration) {
            this.estimatedDuration = estimatedDuration;
            return this;
        }

        public Ride build() {
            Objects.requireNonNull(passengerId, "Passenger ID is required");
            Objects.requireNonNull(pickupLocation, "Pickup location is required");
            Objects.requireNonNull(dropoffLocation, "Dropoff location is required");
            return new Ride(this);
        }
    }
}



