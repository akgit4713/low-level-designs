package restaurant.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a table reservation
 */
public class Reservation {
    private final String id;
    private final String customerName;
    private final String customerPhone;
    private final Table table;
    private final LocalDateTime reservationTime;
    private final int partySize;
    private final int durationMinutes;
    private final String specialRequests;
    
    private volatile ReservationStatus status;

    public enum ReservationStatus {
        CONFIRMED,
        CHECKED_IN,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }

    public Reservation(String id, String customerName, String customerPhone,
                       Table table, LocalDateTime reservationTime, int partySize,
                       int durationMinutes, String specialRequests) {
        this.id = Objects.requireNonNull(id, "Reservation ID cannot be null");
        this.customerName = Objects.requireNonNull(customerName, "Customer name cannot be null");
        this.customerPhone = customerPhone;
        this.table = Objects.requireNonNull(table, "Table cannot be null");
        this.reservationTime = Objects.requireNonNull(reservationTime, "Reservation time cannot be null");
        
        if (partySize <= 0) {
            throw new IllegalArgumentException("Party size must be positive");
        }
        this.partySize = partySize;
        this.durationMinutes = durationMinutes > 0 ? durationMinutes : 90; // default 90 minutes
        this.specialRequests = specialRequests != null ? specialRequests : "";
        this.status = ReservationStatus.CONFIRMED;
    }

    public String getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public Table getTable() {
        return table;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public LocalDateTime getEndTime() {
        return reservationTime.plusMinutes(durationMinutes);
    }

    public int getPartySize() {
        return partySize;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void checkIn() {
        if (status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Can only check in confirmed reservations");
        }
        this.status = ReservationStatus.CHECKED_IN;
        table.tryOccupy();
    }

    public void complete() {
        this.status = ReservationStatus.COMPLETED;
        table.markForCleaning();
    }

    public void cancel() {
        if (status == ReservationStatus.CONFIRMED) {
            this.status = ReservationStatus.CANCELLED;
            table.releaseReservation();
        }
    }

    public void markNoShow() {
        if (status == ReservationStatus.CONFIRMED) {
            this.status = ReservationStatus.NO_SHOW;
            table.releaseReservation();
        }
    }

    /**
     * Check if this reservation overlaps with a given time period
     */
    public boolean overlaps(LocalDateTime start, LocalDateTime end) {
        return reservationTime.isBefore(end) && getEndTime().isAfter(start);
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
        return String.format("Reservation{id='%s', customer='%s', table=%d, time=%s, party=%d, status=%s}",
            id, customerName, table.getTableNumber(), reservationTime, partySize, status);
    }
}

