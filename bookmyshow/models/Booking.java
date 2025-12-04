package bookmyshow.models;

import bookmyshow.enums.BookingStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a ticket booking made by a user.
 */
public class Booking {
    private final String id;
    private final String userId;
    private final String showId;
    private final List<String> seatIds;
    private BookingStatus status;
    private BigDecimal totalAmount;
    private String paymentId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;

    private static final int BOOKING_EXPIRY_MINUTES = 10;

    public Booking(String userId, String showId, List<String> seatIds, BigDecimal totalAmount) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.showId = showId;
        this.seatIds = new ArrayList<>(seatIds);
        this.totalAmount = totalAmount;
        this.status = BookingStatus.INITIATED;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = createdAt.plusMinutes(BOOKING_EXPIRY_MINUTES);
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getShowId() { return showId; }
    public List<String> getSeatIds() { return Collections.unmodifiableList(seatIds); }
    public BookingStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getPaymentId() { return paymentId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }

    // Setters
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    // Status transitions
    public void markPending() {
        this.status = BookingStatus.PENDING;
    }

    public void confirm() {
        this.status = BookingStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

    public void expire() {
        this.status = BookingStatus.EXPIRED;
    }

    public void refund() {
        this.status = BookingStatus.REFUNDED;
    }

    // Utility methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt) && 
               status != BookingStatus.CONFIRMED && 
               status != BookingStatus.CANCELLED &&
               status != BookingStatus.REFUNDED;
    }

    public int getNumberOfSeats() {
        return seatIds.size();
    }

    public boolean canBeCancelled() {
        return status == BookingStatus.CONFIRMED;
    }

    public boolean canBeConfirmed() {
        return status == BookingStatus.PENDING && !isExpired();
    }

    @Override
    public String toString() {
        return String.format("Booking{id='%s', showId='%s', seats=%d, status=%s, amount=%s}", 
            id, showId, seatIds.size(), status, totalAmount);
    }
}



