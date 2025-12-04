package carrental.models;

import carrental.enums.CarStatus;
import carrental.enums.CarType;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a car in the rental system.
 * Immutable value object with status being the only mutable field.
 */
public class Car {
    private final String id;
    private final String make;
    private final String model;
    private final int year;
    private final String licensePlate;
    private final CarType carType;
    private final BigDecimal basePricePerDay;
    private CarStatus status;
    private final String color;
    private final int seatCapacity;

    private Car(Builder builder) {
        this.id = builder.id;
        this.make = builder.make;
        this.model = builder.model;
        this.year = builder.year;
        this.licensePlate = builder.licensePlate;
        this.carType = builder.carType;
        this.basePricePerDay = builder.basePricePerDay;
        this.status = builder.status;
        this.color = builder.color;
        this.seatCapacity = builder.seatCapacity;
    }

    // Getters
    public String getId() { return id; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public String getLicensePlate() { return licensePlate; }
    public CarType getCarType() { return carType; }
    public BigDecimal getBasePricePerDay() { return basePricePerDay; }
    public CarStatus getStatus() { return status; }
    public String getColor() { return color; }
    public int getSeatCapacity() { return seatCapacity; }

    // Status is the only mutable field
    public synchronized void setStatus(CarStatus status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return status == CarStatus.AVAILABLE;
    }

    /**
     * Gets the effective price per day considering car type multiplier.
     */
    public BigDecimal getEffectivePricePerDay() {
        return basePricePerDay.multiply(BigDecimal.valueOf(carType.getPriceMultiplier()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(id, car.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Car{id='%s', %d %s %s, type=%s, price=$%.2f/day, status=%s}",
            id, year, make, model, carType.getDisplayName(), getEffectivePricePerDay(), status);
    }

    // Builder Pattern
    public static class Builder {
        private String id;
        private String make;
        private String model;
        private int year;
        private String licensePlate;
        private CarType carType = CarType.SEDAN;
        private BigDecimal basePricePerDay = BigDecimal.valueOf(50);
        private CarStatus status = CarStatus.AVAILABLE;
        private String color = "White";
        private int seatCapacity = 5;

        public Builder id(String id) { this.id = id; return this; }
        public Builder make(String make) { this.make = make; return this; }
        public Builder model(String model) { this.model = model; return this; }
        public Builder year(int year) { this.year = year; return this; }
        public Builder licensePlate(String licensePlate) { this.licensePlate = licensePlate; return this; }
        public Builder carType(CarType carType) { this.carType = carType; return this; }
        public Builder basePricePerDay(BigDecimal basePricePerDay) { this.basePricePerDay = basePricePerDay; return this; }
        public Builder status(CarStatus status) { this.status = status; return this; }
        public Builder color(String color) { this.color = color; return this; }
        public Builder seatCapacity(int seatCapacity) { this.seatCapacity = seatCapacity; return this; }

        public Car build() {
            Objects.requireNonNull(id, "Car ID cannot be null");
            Objects.requireNonNull(make, "Car make cannot be null");
            Objects.requireNonNull(model, "Car model cannot be null");
            Objects.requireNonNull(licensePlate, "License plate cannot be null");
            if (year < 1900 || year > 2030) {
                throw new IllegalArgumentException("Invalid year: " + year);
            }
            return new Car(this);
        }
    }
}



