package hotelmanagement.models;

import hotelmanagement.enums.RoomStatus;
import hotelmanagement.enums.RoomType;
import hotelmanagement.exceptions.RoomException;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a hotel room with type, capacity, and amenities
 * Thread-safe for concurrent status updates
 */
public class Room {
    private final String id;
    private final String roomNumber;
    private final int floor;
    private final RoomType type;
    private final int capacity;
    private final BigDecimal baseRate;
    private final Set<String> amenities;
    private final ReentrantLock lock = new ReentrantLock();
    
    private volatile RoomStatus status;
    private String description;

    private Room(Builder builder) {
        this.id = builder.id;
        this.roomNumber = builder.roomNumber;
        this.floor = builder.floor;
        this.type = builder.type;
        this.capacity = builder.capacity;
        this.baseRate = builder.baseRate;
        this.amenities = Collections.unmodifiableSet(new HashSet<>(builder.amenities));
        this.status = RoomStatus.AVAILABLE;
        this.description = builder.description;
    }

    public String getId() {
        return id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public RoomType getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public Set<String> getAmenities() {
        return amenities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoomStatus getStatus() {
        lock.lock();
        try {
            return status;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Check if room is available for booking
     */
    public boolean isAvailable() {
        return getStatus() == RoomStatus.AVAILABLE;
    }

    /**
     * Atomically try to reserve this room
     * @return true if reservation was successful
     */
    public boolean tryReserve() {
        lock.lock();
        try {
            if (status == RoomStatus.AVAILABLE) {
                status = RoomStatus.RESERVED;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Mark room as occupied (guest checked in)
     */
    public boolean occupy() {
        lock.lock();
        try {
            if (status == RoomStatus.AVAILABLE || status == RoomStatus.RESERVED) {
                status = RoomStatus.OCCUPIED;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Mark room for cleaning (guest checked out)
     */
    public void markForCleaning() {
        lock.lock();
        try {
            if (status == RoomStatus.OCCUPIED) {
                status = RoomStatus.CLEANING;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Mark room as cleaned and available
     */
    public void markClean() {
        lock.lock();
        try {
            if (status == RoomStatus.CLEANING) {
                status = RoomStatus.AVAILABLE;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Transition room to new status with validation
     */
    public void transitionTo(RoomStatus newStatus) {
        lock.lock();
        try {
            if (!status.canTransitionTo(newStatus)) {
                throw RoomException.invalidStateTransition(roomNumber, status, newStatus);
            }
            this.status = newStatus;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Release reservation (reservation cancelled or guest didn't show)
     */
    public void release() {
        lock.lock();
        try {
            if (status == RoomStatus.RESERVED) {
                status = RoomStatus.AVAILABLE;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Check if room can accommodate the given number of guests
     */
    public boolean canAccommodate(int numberOfGuests) {
        return numberOfGuests <= capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Room{number='%s', floor=%d, type=%s, status=%s, rate=%s}",
            roomNumber, floor, type, status, baseRate);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String roomNumber;
        private int floor = 1;
        private RoomType type = RoomType.SINGLE;
        private int capacity;
        private BigDecimal baseRate;
        private Set<String> amenities = new HashSet<>();
        private String description = "";

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder roomNumber(String roomNumber) {
            this.roomNumber = roomNumber;
            return this;
        }

        public Builder floor(int floor) {
            this.floor = floor;
            return this;
        }

        public Builder type(RoomType type) {
            this.type = type;
            return this;
        }

        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder baseRate(BigDecimal baseRate) {
            this.baseRate = baseRate;
            return this;
        }

        public Builder addAmenity(String amenity) {
            this.amenities.add(amenity);
            return this;
        }

        public Builder amenities(Set<String> amenities) {
            this.amenities = new HashSet<>(amenities);
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Room build() {
            if (id == null || id.isBlank()) {
                id = "ROOM-" + UUID.randomUUID().toString().substring(0, 8);
            }
            Objects.requireNonNull(roomNumber, "Room number is required");
            Objects.requireNonNull(type, "Room type is required");
            
            if (capacity <= 0) {
                capacity = type.getDefaultCapacity();
            }
            
            if (baseRate == null) {
                // Default base rate: $100 * price multiplier
                baseRate = new BigDecimal("100.00").multiply(type.getPriceMultiplier());
            }
            
            return new Room(this);
        }
    }
}



