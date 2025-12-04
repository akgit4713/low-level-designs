package ridesharing.models;

import ridesharing.enums.VehicleType;

import java.util.Objects;

/**
 * Represents a driver's vehicle.
 */
public class Vehicle {
    private final String vehicleId;
    private final String licensePlate;
    private final String make;
    private final String model;
    private final String color;
    private final int year;
    private final VehicleType vehicleType;

    private Vehicle(Builder builder) {
        this.vehicleId = builder.vehicleId;
        this.licensePlate = builder.licensePlate;
        this.make = builder.make;
        this.model = builder.model;
        this.color = builder.color;
        this.year = builder.year;
        this.vehicleType = builder.vehicleType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getColor() {
        return color;
    }

    public int getYear() {
        return year;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public int getCapacity() {
        return vehicleType.getCapacity();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(vehicleId, vehicle.vehicleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleId);
    }

    @Override
    public String toString() {
        return String.format("%d %s %s (%s) - %s", year, make, model, color, licensePlate);
    }

    public static class Builder {
        private String vehicleId;
        private String licensePlate;
        private String make;
        private String model;
        private String color;
        private int year;
        private VehicleType vehicleType = VehicleType.SEDAN;

        public Builder vehicleId(String vehicleId) {
            this.vehicleId = vehicleId;
            return this;
        }

        public Builder licensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
            return this;
        }

        public Builder make(String make) {
            this.make = make;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder year(int year) {
            this.year = year;
            return this;
        }

        public Builder vehicleType(VehicleType vehicleType) {
            this.vehicleType = vehicleType;
            return this;
        }

        public Vehicle build() {
            Objects.requireNonNull(vehicleId, "Vehicle ID is required");
            Objects.requireNonNull(licensePlate, "License plate is required");
            return new Vehicle(this);
        }
    }
}



