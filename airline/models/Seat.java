package airline.models;

import airline.enums.SeatClass;
import airline.enums.SeatStatus;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a seat on a flight with class, position, and availability.
 * Thread-safe for concurrent booking operations.
 */
public class Seat {
    private final String seatNumber; // e.g., "12A", "1F"
    private final SeatClass seatClass;
    private final int row;
    private final char column;
    private final boolean windowSeat;
    private final boolean aisleSeat;
    private final boolean exitRow;
    private final BigDecimal extraCharge; // Additional charge for premium positions
    private volatile SeatStatus status;
    private volatile String bookedByPassengerId;
    private final ReentrantLock lock = new ReentrantLock();

    private Seat(Builder builder) {
        this.seatNumber = builder.seatNumber;
        this.seatClass = builder.seatClass;
        this.row = builder.row;
        this.column = builder.column;
        this.windowSeat = builder.windowSeat;
        this.aisleSeat = builder.aisleSeat;
        this.exitRow = builder.exitRow;
        this.extraCharge = builder.extraCharge != null ? builder.extraCharge : BigDecimal.ZERO;
        this.status = SeatStatus.AVAILABLE;
        this.bookedByPassengerId = null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public int getRow() {
        return row;
    }

    public char getColumn() {
        return column;
    }

    public boolean isWindowSeat() {
        return windowSeat;
    }

    public boolean isAisleSeat() {
        return aisleSeat;
    }

    public boolean isExitRow() {
        return exitRow;
    }

    public BigDecimal getExtraCharge() {
        return extraCharge;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public String getBookedByPassengerId() {
        return bookedByPassengerId;
    }

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    /**
     * Attempts to book the seat for a passenger.
     * Thread-safe operation.
     */
    public boolean book(String passengerId) {
        lock.lock();
        try {
            if (status != SeatStatus.AVAILABLE) {
                return false;
            }
            this.status = SeatStatus.BOOKED;
            this.bookedByPassengerId = passengerId;
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Releases the seat booking.
     * Thread-safe operation.
     */
    public boolean release() {
        lock.lock();
        try {
            if (status != SeatStatus.BOOKED) {
                return false;
            }
            this.status = SeatStatus.AVAILABLE;
            this.bookedByPassengerId = null;
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Blocks the seat temporarily (e.g., during booking process).
     */
    public boolean block() {
        lock.lock();
        try {
            if (status != SeatStatus.AVAILABLE) {
                return false;
            }
            this.status = SeatStatus.BLOCKED;
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Unblocks a temporarily blocked seat.
     */
    public boolean unblock() {
        lock.lock();
        try {
            if (status != SeatStatus.BLOCKED) {
                return false;
            }
            this.status = SeatStatus.AVAILABLE;
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        String features = "";
        if (windowSeat) features += "W";
        if (aisleSeat) features += "A";
        if (exitRow) features += "E";
        return String.format("Seat[%s | %s | %s%s]",
                seatNumber, seatClass, status, features.isEmpty() ? "" : " | " + features);
    }

    public static class Builder {
        private String seatNumber;
        private SeatClass seatClass;
        private int row;
        private char column;
        private boolean windowSeat;
        private boolean aisleSeat;
        private boolean exitRow;
        private BigDecimal extraCharge;

        public Builder seatNumber(String seatNumber) {
            this.seatNumber = seatNumber;
            return this;
        }

        public Builder seatClass(SeatClass seatClass) {
            this.seatClass = seatClass;
            return this;
        }

        public Builder row(int row) {
            this.row = row;
            return this;
        }

        public Builder column(char column) {
            this.column = column;
            return this;
        }

        public Builder windowSeat(boolean windowSeat) {
            this.windowSeat = windowSeat;
            return this;
        }

        public Builder aisleSeat(boolean aisleSeat) {
            this.aisleSeat = aisleSeat;
            return this;
        }

        public Builder exitRow(boolean exitRow) {
            this.exitRow = exitRow;
            return this;
        }

        public Builder extraCharge(BigDecimal extraCharge) {
            this.extraCharge = extraCharge;
            return this;
        }

        public Seat build() {
            if (seatNumber == null || seatClass == null) {
                throw new IllegalStateException("Seat requires seatNumber and seatClass");
            }
            return new Seat(this);
        }
    }
}



