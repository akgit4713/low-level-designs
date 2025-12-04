package bookmyshow.models;

import bookmyshow.enums.SeatStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a seat's status and pricing for a specific show.
 * This tracks availability, locking, and booking state per show.
 */
public class ShowSeat {
    private final String id;
    private final String showId;
    private final Seat seat;
    private SeatStatus status;
    private BigDecimal price;
    private String lockedByUserId;
    private LocalDateTime lockExpiry;
    private String bookingId;

    public ShowSeat(String showId, Seat seat, BigDecimal price) {
        this.id = UUID.randomUUID().toString();
        this.showId = showId;
        this.seat = seat;
        this.status = SeatStatus.AVAILABLE;
        this.price = price;
    }

    // Getters
    public String getId() { return id; }
    public String getShowId() { return showId; }
    public Seat getSeat() { return seat; }
    public SeatStatus getStatus() { return status; }
    public BigDecimal getPrice() { return price; }
    public String getLockedByUserId() { return lockedByUserId; }
    public LocalDateTime getLockExpiry() { return lockExpiry; }
    public String getBookingId() { return bookingId; }

    // Setters
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setStatus(SeatStatus status) { this.status = status; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    // Lock management
    public boolean isAvailable() {
        if (status == SeatStatus.LOCKED && lockExpiry != null && LocalDateTime.now().isAfter(lockExpiry)) {
            // Lock expired, seat is now available
            unlock();
        }
        return status == SeatStatus.AVAILABLE;
    }

    public boolean lock(String userId, int lockDurationMinutes) {
        if (!isAvailable()) {
            return false;
        }
        this.status = SeatStatus.LOCKED;
        this.lockedByUserId = userId;
        this.lockExpiry = LocalDateTime.now().plusMinutes(lockDurationMinutes);
        return true;
    }

    public void unlock() {
        this.status = SeatStatus.AVAILABLE;
        this.lockedByUserId = null;
        this.lockExpiry = null;
    }

    public void book(String bookingId) {
        this.status = SeatStatus.BOOKED;
        this.bookingId = bookingId;
        this.lockedByUserId = null;
        this.lockExpiry = null;
    }

    public boolean isLockedByUser(String userId) {
        return status == SeatStatus.LOCKED && userId.equals(lockedByUserId) && !isLockExpired();
    }

    public boolean isLockExpired() {
        return lockExpiry != null && LocalDateTime.now().isAfter(lockExpiry);
    }

    @Override
    public String toString() {
        return String.format("ShowSeat{seat=%s, status=%s, price=%s}", 
            seat.getSeatLabel(), status, price);
    }
}



