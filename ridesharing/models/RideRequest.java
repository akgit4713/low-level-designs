package ridesharing.models;

import ridesharing.enums.PaymentMethod;
import ridesharing.enums.RideType;

import java.util.Objects;

/**
 * Represents a ride request from a passenger before a ride is created.
 * Used as a DTO/Value Object for ride creation.
 */
public class RideRequest {
    private final String passengerId;
    private final Location pickupLocation;
    private final Location dropoffLocation;
    private final RideType rideType;
    private final PaymentMethod paymentMethod;
    private final int numberOfPassengers;

    private RideRequest(Builder builder) {
        this.passengerId = builder.passengerId;
        this.pickupLocation = builder.pickupLocation;
        this.dropoffLocation = builder.dropoffLocation;
        this.rideType = builder.rideType;
        this.paymentMethod = builder.paymentMethod;
        this.numberOfPassengers = builder.numberOfPassengers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getPassengerId() {
        return passengerId;
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    @Override
    public String toString() {
        return String.format("RideRequest{passenger='%s', type=%s, from=%s, to=%s}", 
                passengerId, rideType, pickupLocation, dropoffLocation);
    }

    public static class Builder {
        private String passengerId;
        private Location pickupLocation;
        private Location dropoffLocation;
        private RideType rideType = RideType.REGULAR;
        private PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        private int numberOfPassengers = 1;

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

        public Builder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder numberOfPassengers(int numberOfPassengers) {
            this.numberOfPassengers = numberOfPassengers;
            return this;
        }

        public RideRequest build() {
            Objects.requireNonNull(passengerId, "Passenger ID is required");
            Objects.requireNonNull(pickupLocation, "Pickup location is required");
            Objects.requireNonNull(dropoffLocation, "Dropoff location is required");
            return new RideRequest(this);
        }
    }
}



