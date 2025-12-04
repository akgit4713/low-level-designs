package hotelmanagement.models;

import hotelmanagement.enums.ReservationStatus;
import hotelmanagement.exceptions.ReservationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents a room reservation with check-in/out dates and guest information
 * Thread-safe for concurrent status updates
 */
public class Reservation {
    private final String id;
    private final Guest guest;
    private final Room room;
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final int numberOfGuests;
    private final LocalDateTime createdAt;
    private final List<ServiceCharge> serviceCharges;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    private volatile ReservationStatus status;
    private LocalDateTime actualCheckInTime;
    private LocalDateTime actualCheckOutTime;
    private String specialRequests;
    private BigDecimal roomRatePerNight;

    private Reservation(Builder builder) {
        this.id = builder.id;
        this.guest = builder.guest;
        this.room = builder.room;
        this.checkInDate = builder.checkInDate;
        this.checkOutDate = builder.checkOutDate;
        this.numberOfGuests = builder.numberOfGuests;
        this.specialRequests = builder.specialRequests;
        this.roomRatePerNight = builder.roomRatePerNight;
        this.status = ReservationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.serviceCharges = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public Guest getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public ReservationStatus getStatus() {
        lock.readLock().lock();
        try {
            return status;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<LocalDateTime> getActualCheckInTime() {
        return Optional.ofNullable(actualCheckInTime);
    }

    public Optional<LocalDateTime> getActualCheckOutTime() {
        return Optional.ofNullable(actualCheckOutTime);
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public BigDecimal getRoomRatePerNight() {
        return roomRatePerNight;
    }

    public void setRoomRatePerNight(BigDecimal rate) {
        this.roomRatePerNight = rate;
    }

    /**
     * Calculate number of nights for this reservation
     */
    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    /**
     * Calculate total room charges (rate * nights)
     */
    public BigDecimal calculateRoomCharges() {
        return roomRatePerNight.multiply(BigDecimal.valueOf(getNumberOfNights()));
    }

    /**
     * Get all service charges
     */
    public List<ServiceCharge> getServiceCharges() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(serviceCharges));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Add a service charge to this reservation
     */
    public void addServiceCharge(ServiceCharge charge) {
        lock.writeLock().lock();
        try {
            serviceCharges.add(charge);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Calculate total service charges
     */
    public BigDecimal calculateServiceCharges() {
        lock.readLock().lock();
        try {
            return serviceCharges.stream()
                .map(ServiceCharge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Confirm the reservation
     */
    public void confirm() {
        transitionTo(ReservationStatus.CONFIRMED);
    }

    /**
     * Check in the guest
     */
    public void checkIn() {
        lock.writeLock().lock();
        try {
            if (!status.canTransitionTo(ReservationStatus.CHECKED_IN)) {
                throw ReservationException.invalidStateTransition(id, status, ReservationStatus.CHECKED_IN);
            }
            this.status = ReservationStatus.CHECKED_IN;
            this.actualCheckInTime = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Check out the guest
     */
    public void checkOut() {
        lock.writeLock().lock();
        try {
            if (!status.canTransitionTo(ReservationStatus.CHECKED_OUT)) {
                throw ReservationException.invalidStateTransition(id, status, ReservationStatus.CHECKED_OUT);
            }
            this.status = ReservationStatus.CHECKED_OUT;
            this.actualCheckOutTime = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Cancel the reservation
     */
    public void cancel() {
        transitionTo(ReservationStatus.CANCELLED);
    }

    /**
     * Mark as no-show
     */
    public void markNoShow() {
        transitionTo(ReservationStatus.NO_SHOW);
    }

    /**
     * Transition reservation to new status with validation
     */
    public void transitionTo(ReservationStatus newStatus) {
        lock.writeLock().lock();
        try {
            if (!status.canTransitionTo(newStatus)) {
                throw ReservationException.invalidStateTransition(id, status, newStatus);
            }
            this.status = newStatus;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Check if reservation is active (guest is currently staying)
     */
    public boolean isActive() {
        return getStatus().isActive();
    }

    /**
     * Check if reservation is in a terminal state
     */
    public boolean isTerminal() {
        return getStatus().isTerminal();
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
        return String.format(
            "Reservation{id='%s', guest='%s', room='%s', dates=%s to %s, status=%s}",
            id, guest.getName(), room.getRoomNumber(), checkInDate, checkOutDate, status
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Guest guest;
        private Room room;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private int numberOfGuests = 1;
        private String specialRequests = "";
        private BigDecimal roomRatePerNight;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder guest(Guest guest) {
            this.guest = guest;
            return this;
        }

        public Builder room(Room room) {
            this.room = room;
            return this;
        }

        public Builder checkInDate(LocalDate checkInDate) {
            this.checkInDate = checkInDate;
            return this;
        }

        public Builder checkOutDate(LocalDate checkOutDate) {
            this.checkOutDate = checkOutDate;
            return this;
        }

        public Builder numberOfGuests(int numberOfGuests) {
            this.numberOfGuests = numberOfGuests;
            return this;
        }

        public Builder specialRequests(String specialRequests) {
            this.specialRequests = specialRequests;
            return this;
        }

        public Builder roomRatePerNight(BigDecimal rate) {
            this.roomRatePerNight = rate;
            return this;
        }

        public Reservation build() {
            if (id == null || id.isBlank()) {
                id = "RES-" + UUID.randomUUID().toString().substring(0, 8);
            }
            Objects.requireNonNull(guest, "Guest is required");
            Objects.requireNonNull(room, "Room is required");
            Objects.requireNonNull(checkInDate, "Check-in date is required");
            Objects.requireNonNull(checkOutDate, "Check-out date is required");
            
            if (!checkOutDate.isAfter(checkInDate)) {
                throw ReservationException.invalidDateRange();
            }
            
            if (numberOfGuests > room.getCapacity()) {
                throw new IllegalStateException(
                    "Number of guests exceeds room capacity: " + numberOfGuests + " > " + room.getCapacity()
                );
            }
            
            if (roomRatePerNight == null) {
                roomRatePerNight = room.getBaseRate();
            }
            
            return new Reservation(this);
        }
    }
}



