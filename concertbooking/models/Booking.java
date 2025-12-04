package concertbooking.models;

import concertbooking.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a booking for concert tickets
 */
public class Booking {
    private final String id;
    private final String concertId;
    private final String userId;
    private final List<String> seatIds;
    private final BigDecimal totalAmount;
    private final LocalDateTime createdAt;
    private volatile LocalDateTime expiresAt;
    private volatile BookingStatus status;
    private volatile String paymentId;
    private volatile LocalDateTime confirmedAt;
    private volatile LocalDateTime cancelledAt;

    private Booking(Builder builder) {
        this.id = builder.id;
        this.concertId = builder.concertId;
        this.userId = builder.userId;
        this.seatIds = new ArrayList<>(builder.seatIds);
        this.totalAmount = builder.totalAmount;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = builder.expiresAt;
        this.status = BookingStatus.PENDING;
    }

    public String getId() { return id; }
    public String getConcertId() { return concertId; }
    public String getUserId() { return userId; }
    public List<String> getSeatIds() { return Collections.unmodifiableList(seatIds); }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public BookingStatus getStatus() { return status; }
    public String getPaymentId() { return paymentId; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }

    public int getSeatCount() {
        return seatIds.size();
    }

    public boolean isExpired() {
        return status == BookingStatus.PENDING 
            && expiresAt != null 
            && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isPending() {
        return status == BookingStatus.PENDING;
    }

    public boolean isConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }

    public boolean isCancelled() {
        return status == BookingStatus.CANCELLED;
    }

    public void confirm(String paymentId) {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException("Cannot confirm booking in status: " + status);
        }
        this.status = BookingStatus.CONFIRMED;
        this.paymentId = paymentId;
        this.confirmedAt = LocalDateTime.now();
        this.expiresAt = null;
    }

    public void cancel() {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot cancel booking in status: " + status);
        }
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void markExpired() {
        if (status == BookingStatus.PENDING) {
            this.status = BookingStatus.EXPIRED;
        }
    }

    public void markRefunded() {
        if (status == BookingStatus.CONFIRMED) {
            this.status = BookingStatus.REFUNDED;
            this.cancelledAt = LocalDateTime.now();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Booking{id='%s', concertId='%s', userId='%s', seats=%d, amount=%s, status=%s}",
            id, concertId, userId, seatIds.size(), totalAmount, status);
    }

    public static class Builder {
        private String id;
        private String concertId;
        private String userId;
        private List<String> seatIds = new ArrayList<>();
        private BigDecimal totalAmount;
        private LocalDateTime expiresAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder concertId(String concertId) { this.concertId = concertId; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder seatIds(List<String> seatIds) { this.seatIds = new ArrayList<>(seatIds); return this; }
        public Builder addSeatId(String seatId) { this.seatIds.add(seatId); return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }

        public Booking build() {
            Objects.requireNonNull(id, "Booking ID is required");
            Objects.requireNonNull(concertId, "Concert ID is required");
            Objects.requireNonNull(userId, "User ID is required");
            Objects.requireNonNull(totalAmount, "Total amount is required");
            if (seatIds.isEmpty()) {
                throw new IllegalStateException("At least one seat is required");
            }
            return new Booking(this);
        }
    }
}



