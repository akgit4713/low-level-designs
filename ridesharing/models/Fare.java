package ridesharing.models;

import ridesharing.enums.RideType;

/**
 * Represents the fare breakdown for a ride.
 * Immutable value object.
 */
public class Fare {
    private final double baseFare;
    private final double distanceFare;
    private final double timeFare;
    private final double surgeMultiplier;
    private final double discount;
    private final RideType rideType;

    private Fare(Builder builder) {
        this.baseFare = builder.baseFare;
        this.distanceFare = builder.distanceFare;
        this.timeFare = builder.timeFare;
        this.surgeMultiplier = builder.surgeMultiplier;
        this.discount = builder.discount;
        this.rideType = builder.rideType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public double getBaseFare() {
        return baseFare;
    }

    public double getDistanceFare() {
        return distanceFare;
    }

    public double getTimeFare() {
        return timeFare;
    }

    public double getSurgeMultiplier() {
        return surgeMultiplier;
    }

    public double getDiscount() {
        return discount;
    }

    public RideType getRideType() {
        return rideType;
    }

    /**
     * Calculate the total fare amount.
     */
    public double getTotalAmount() {
        double subtotal = (baseFare + distanceFare + timeFare) * rideType.getMultiplier();
        double afterSurge = subtotal * surgeMultiplier;
        return Math.max(0, afterSurge - discount);
    }

    /**
     * Get driver's earnings (typically 75-80% of fare).
     */
    public double getDriverEarnings() {
        return getTotalAmount() * 0.75;
    }

    /**
     * Get platform commission.
     */
    public double getPlatformCommission() {
        return getTotalAmount() * 0.25;
    }

    @Override
    public String toString() {
        return String.format(
            "Fare{base=%.2f, distance=%.2f, time=%.2f, surge=%.2fx, discount=%.2f, total=%.2f}",
            baseFare, distanceFare, timeFare, surgeMultiplier, discount, getTotalAmount()
        );
    }

    public static class Builder {
        private double baseFare = 2.50;
        private double distanceFare = 0.0;
        private double timeFare = 0.0;
        private double surgeMultiplier = 1.0;
        private double discount = 0.0;
        private RideType rideType = RideType.REGULAR;

        public Builder baseFare(double baseFare) {
            this.baseFare = baseFare;
            return this;
        }

        public Builder distanceFare(double distanceFare) {
            this.distanceFare = distanceFare;
            return this;
        }

        public Builder timeFare(double timeFare) {
            this.timeFare = timeFare;
            return this;
        }

        public Builder surgeMultiplier(double surgeMultiplier) {
            this.surgeMultiplier = Math.max(1.0, surgeMultiplier);
            return this;
        }

        public Builder discount(double discount) {
            this.discount = Math.max(0, discount);
            return this;
        }

        public Builder rideType(RideType rideType) {
            this.rideType = rideType;
            return this;
        }

        public Fare build() {
            return new Fare(this);
        }
    }
}



