package concertbooking.models;

import concertbooking.enums.SeatStatus;
import concertbooking.enums.SectionType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a seat in a concert venue
 * Thread-safe for concurrent booking operations
 */
public class Seat {
    private final String id;
    private final String sectionId;
    private final SectionType sectionType;
    private final int rowNumber;
    private final int seatNumber;
    private final String seatLabel; // e.g., "A-12", "VIP-3"
    
    private volatile SeatStatus status;
    private volatile String heldByUserId;
    private volatile LocalDateTime holdExpiresAt;
    private volatile String bookedByUserId;
    
    private final ReentrantLock lock = new ReentrantLock();

    public Seat(String id, String sectionId, SectionType sectionType, 
                int rowNumber, int seatNumber) {
        this.id = Objects.requireNonNull(id, "Seat ID cannot be null");
        this.sectionId = Objects.requireNonNull(sectionId, "Section ID cannot be null");
        this.sectionType = Objects.requireNonNull(sectionType, "Section type cannot be null");
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.seatLabel = generateSeatLabel(sectionType, rowNumber, seatNumber);
        this.status = SeatStatus.AVAILABLE;
    }

    private String generateSeatLabel(SectionType type, int row, int seat) {
        char rowChar = (char) ('A' + row - 1);
        return String.format("%s-%c%d", type.name().substring(0, 3), rowChar, seat);
    }

    public String getId() {
        return id;
    }

    public String getSectionId() {
        return sectionId;
    }

    public SectionType getSectionType() {
        return sectionType;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public String getSeatLabel() {
        return seatLabel;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public String getHeldByUserId() {
        return heldByUserId;
    }

    public LocalDateTime getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public String getBookedByUserId() {
        return bookedByUserId;
    }

    /**
     * Attempts to hold the seat for a user
     * @param userId User attempting to hold the seat
     * @param holdDurationMinutes Duration of the hold
     * @return true if hold was successful
     */
    public boolean tryHold(String userId, int holdDurationMinutes) {
        lock.lock();
        try {
            // Check if hold has expired
            if (status == SeatStatus.HELD && holdExpiresAt != null 
                && LocalDateTime.now().isAfter(holdExpiresAt)) {
                releaseHoldInternal();
            }
            
            if (status == SeatStatus.AVAILABLE) {
                status = SeatStatus.HELD;
                heldByUserId = userId;
                holdExpiresAt = LocalDateTime.now().plusMinutes(holdDurationMinutes);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Confirms the booking for the user who held the seat
     * @param userId User confirming the booking
     * @return true if booking was successful
     */
    public boolean confirmBooking(String userId) {
        lock.lock();
        try {
            if (status == SeatStatus.HELD && userId.equals(heldByUserId)) {
                // Check if hold is still valid
                if (holdExpiresAt != null && LocalDateTime.now().isAfter(holdExpiresAt)) {
                    releaseHoldInternal();
                    return false;
                }
                
                status = SeatStatus.BOOKED;
                bookedByUserId = userId;
                heldByUserId = null;
                holdExpiresAt = null;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Releases a hold on the seat
     * @param userId User releasing the hold
     * @return true if release was successful
     */
    public boolean releaseHold(String userId) {
        lock.lock();
        try {
            if (status == SeatStatus.HELD && userId.equals(heldByUserId)) {
                releaseHoldInternal();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Releases a booking (for cancellation/refund)
     * @param userId User releasing the booking
     * @return true if release was successful
     */
    public boolean releaseBooking(String userId) {
        lock.lock();
        try {
            if (status == SeatStatus.BOOKED && userId.equals(bookedByUserId)) {
                status = SeatStatus.AVAILABLE;
                bookedByUserId = null;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Force releases an expired hold
     * @return true if a hold was released
     */
    public boolean releaseExpiredHold() {
        lock.lock();
        try {
            if (status == SeatStatus.HELD && holdExpiresAt != null 
                && LocalDateTime.now().isAfter(holdExpiresAt)) {
                releaseHoldInternal();
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void releaseHoldInternal() {
        status = SeatStatus.AVAILABLE;
        heldByUserId = null;
        holdExpiresAt = null;
    }

    public boolean isAvailable() {
        lock.lock();
        try {
            // Auto-release expired holds
            if (status == SeatStatus.HELD && holdExpiresAt != null 
                && LocalDateTime.now().isAfter(holdExpiresAt)) {
                releaseHoldInternal();
            }
            return status == SeatStatus.AVAILABLE;
        } finally {
            lock.unlock();
        }
    }

    public boolean isHeldBy(String userId) {
        return status == SeatStatus.HELD && userId.equals(heldByUserId);
    }

    public boolean isBookedBy(String userId) {
        return status == SeatStatus.BOOKED && userId.equals(bookedByUserId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return Objects.equals(id, seat.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Seat{id='%s', label='%s', section='%s', status=%s}",
            id, seatLabel, sectionType, status);
    }
}



