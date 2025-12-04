package carrental.models;

import carrental.enums.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a car rental reservation.
 */
public class Reservation {
    private final String id;
    private final Car car;
    private final Customer customer;
    private LocalDate startDate;
    private LocalDate endDate;
    private ReservationStatus status;
    private BigDecimal totalAmount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Reservation(Builder builder) {
        this.id = builder.id;
        this.car = builder.car;
        this.customer = builder.customer;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.status = builder.status;
        this.totalAmount = builder.totalAmount;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    // Getters
    public String getId() { return id; }
    public Car getCar() { return car; }
    public Customer getCustomer() { return customer; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public ReservationStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Status management
    public synchronized void setStatus(ReservationStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public synchronized void updateDates(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.updatedAt = LocalDateTime.now();
    }

    public synchronized void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        this.updatedAt = LocalDateTime.now();
    }

    public long getDurationInDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public boolean isModifiable() {
        return status.canModify();
    }

    public boolean isCancellable() {
        return status.canCancel();
    }

    public boolean overlaps(LocalDate otherStart, LocalDate otherEnd) {
        return !endDate.isBefore(otherStart) && !startDate.isAfter(otherEnd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Reservation{id='%s', car=%s, customer=%s, dates=%s to %s, status=%s, amount=$%.2f}",
            id, car.getId(), customer.getName(), startDate, endDate, status, totalAmount);
    }

    // Builder Pattern
    public static class Builder {
        private String id;
        private Car car;
        private Customer customer;
        private LocalDate startDate;
        private LocalDate endDate;
        private ReservationStatus status = ReservationStatus.PENDING;
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime updatedAt = LocalDateTime.now();

        public Builder id(String id) { this.id = id; return this; }
        public Builder car(Car car) { this.car = car; return this; }
        public Builder customer(Customer customer) { this.customer = customer; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder status(ReservationStatus status) { this.status = status; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Reservation build() {
            Objects.requireNonNull(id, "Reservation ID cannot be null");
            Objects.requireNonNull(car, "Car cannot be null");
            Objects.requireNonNull(customer, "Customer cannot be null");
            Objects.requireNonNull(startDate, "Start date cannot be null");
            Objects.requireNonNull(endDate, "End date cannot be null");
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
            return new Reservation(this);
        }
    }
}



