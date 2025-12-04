package airline.models;

import airline.enums.BookingStatus;
import airline.exceptions.BookingException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents a flight booking with passengers, seats, and payment.
 * Thread-safe for concurrent operations.
 */
public class Booking {
    private final String id;
    private final String pnr; // Passenger Name Record (6-char code)
    private final Flight flight;
    private final List<BookingPassenger> passengers;
    private final LocalDateTime createdAt;
    private volatile BookingStatus status;
    private volatile BigDecimal totalAmount;
    private volatile Payment payment;
    private volatile LocalDateTime confirmedAt;
    private volatile LocalDateTime cancelledAt;
    private volatile String cancellationReason;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private Booking(Builder builder) {
        this.id = builder.id;
        this.pnr = generatePNR();
        this.flight = builder.flight;
        this.passengers = Collections.synchronizedList(new ArrayList<>(builder.passengers));
        this.createdAt = LocalDateTime.now();
        this.status = BookingStatus.PENDING;
        this.totalAmount = builder.totalAmount;
    }

    public static Builder builder() {
        return new Builder();
    }

    private String generatePNR() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder pnr = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            pnr.append(chars.charAt(random.nextInt(chars.length())));
        }
        return pnr.toString();
    }

    public String getId() {
        return id;
    }

    public String getPnr() {
        return pnr;
    }

    public Flight getFlight() {
        return flight;
    }

    public List<BookingPassenger> getPassengers() {
        return new ArrayList<>(passengers);
    }

    public int getPassengerCount() {
        return passengers.size();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Payment getPayment() {
        return payment;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    /**
     * Updates booking status with validation.
     */
    public void setStatus(BookingStatus newStatus) {
        lock.writeLock().lock();
        try {
            if (!this.status.canTransitionTo(newStatus)) {
                throw new BookingException("Invalid status transition from " + this.status + " to " + newStatus);
            }
            this.status = newStatus;
            
            if (newStatus == BookingStatus.CONFIRMED) {
                this.confirmedAt = LocalDateTime.now();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Cancels the booking with a reason.
     */
    public void cancel(String reason) {
        lock.writeLock().lock();
        try {
            if (status == BookingStatus.COMPLETED || status == BookingStatus.REFUNDED) {
                throw new BookingException("Cannot cancel a " + status + " booking");
            }
            this.status = BookingStatus.CANCELLED;
            this.cancelledAt = LocalDateTime.now();
            this.cancellationReason = reason;
            
            // Release seats
            for (BookingPassenger bp : passengers) {
                if (bp.getSeatNumber() != null) {
                    flight.releaseSeat(bp.getSeatNumber());
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Adds a passenger to the booking.
     */
    public void addPassenger(Passenger passenger, String seatNumber) {
        lock.writeLock().lock();
        try {
            if (status != BookingStatus.PENDING) {
                throw new BookingException("Cannot add passenger to " + status + " booking");
            }
            BookingPassenger bp = new BookingPassenger(passenger, seatNumber);
            passengers.add(bp);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean isConfirmed() {
        return status == BookingStatus.CONFIRMED;
    }

    public boolean isCancelled() {
        return status == BookingStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return String.format("Booking[%s | PNR: %s | %s | %d passengers | $%.2f | %s]",
                id, pnr, flight.getFlightNumber(), passengers.size(), totalAmount, status);
    }

    /**
     * Represents a passenger in a booking with their seat assignment.
     */
    public static class BookingPassenger {
        private final Passenger passenger;
        private String seatNumber;
        private boolean checkedIn;

        public BookingPassenger(Passenger passenger, String seatNumber) {
            this.passenger = passenger;
            this.seatNumber = seatNumber;
            this.checkedIn = false;
        }

        public Passenger getPassenger() {
            return passenger;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public void setSeatNumber(String seatNumber) {
            this.seatNumber = seatNumber;
        }

        public boolean isCheckedIn() {
            return checkedIn;
        }

        public void checkIn() {
            this.checkedIn = true;
        }

        @Override
        public String toString() {
            return String.format("%s - Seat: %s%s",
                    passenger.getFullName(), seatNumber,
                    checkedIn ? " (Checked In)" : "");
        }
    }

    public static class Builder {
        private String id;
        private Flight flight;
        private List<BookingPassenger> passengers = new ArrayList<>();
        private BigDecimal totalAmount = BigDecimal.ZERO;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder flight(Flight flight) {
            this.flight = flight;
            return this;
        }

        public Builder addPassenger(Passenger passenger, String seatNumber) {
            this.passengers.add(new BookingPassenger(passenger, seatNumber));
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Booking build() {
            if (id == null || flight == null || passengers.isEmpty()) {
                throw new IllegalStateException("Booking requires id, flight, and at least one passenger");
            }
            return new Booking(this);
        }
    }
}



