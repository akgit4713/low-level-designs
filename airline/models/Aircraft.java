package airline.models;

import airline.enums.AircraftStatus;
import airline.enums.SeatClass;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents an aircraft with its specifications and seat configuration.
 * Thread-safe for concurrent access.
 */
public class Aircraft {
    private final String id;
    private final String registrationNumber;
    private final String model;
    private final String manufacturer;
    private final Map<SeatClass, Integer> seatConfiguration;
    private volatile AircraftStatus status;
    private final ReentrantLock lock = new ReentrantLock();

    private Aircraft(Builder builder) {
        this.id = builder.id;
        this.registrationNumber = builder.registrationNumber;
        this.model = builder.model;
        this.manufacturer = builder.manufacturer;
        this.seatConfiguration = new EnumMap<>(builder.seatConfiguration);
        this.status = AircraftStatus.AVAILABLE;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getModel() {
        return model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public int getSeatCount(SeatClass seatClass) {
        return seatConfiguration.getOrDefault(seatClass, 0);
    }

    public int getTotalSeats() {
        return seatConfiguration.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Map<SeatClass, Integer> getSeatConfiguration() {
        return new EnumMap<>(seatConfiguration);
    }

    public AircraftStatus getStatus() {
        return status;
    }

    public boolean setStatus(AircraftStatus newStatus) {
        lock.lock();
        try {
            this.status = newStatus;
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean isAvailable() {
        return status == AircraftStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return String.format("Aircraft[%s - %s %s | Seats: %d | Status: %s]",
                registrationNumber, manufacturer, model, getTotalSeats(), status);
    }

    public static class Builder {
        private String id;
        private String registrationNumber;
        private String model;
        private String manufacturer;
        private final Map<SeatClass, Integer> seatConfiguration = new EnumMap<>(SeatClass.class);

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder registrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder manufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public Builder addSeats(SeatClass seatClass, int count) {
            this.seatConfiguration.put(seatClass, count);
            return this;
        }

        public Aircraft build() {
            if (id == null || registrationNumber == null || model == null) {
                throw new IllegalStateException("Aircraft requires id, registrationNumber, and model");
            }
            return new Aircraft(this);
        }
    }
}



