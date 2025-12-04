package parkinglot.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a parking ticket issued when a vehicle enters the parking lot.
 * Contains information about the vehicle, spot, gates, and timing for billing.
 */
public class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot parkingSpot;
    private final Level level;
    private final EntryGate entryGate;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public ParkingTicket(Vehicle vehicle, ParkingSpot parkingSpot, Level level) {
        this(vehicle, parkingSpot, level, null);
    }

    public ParkingTicket(Vehicle vehicle, ParkingSpot parkingSpot, Level level, EntryGate entryGate) {
        this.ticketId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.level = level;
        this.entryGate = entryGate;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public Level getLevel() {
        return level;
    }

    public EntryGate getEntryGate() {
        return entryGate;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    /**
     * Calculates the parking duration.
     * @return Duration parked, or duration until now if still parked
     */
    public Duration calculateDuration() {
        LocalDateTime endTime = exitTime != null ? exitTime : LocalDateTime.now();
        return Duration.between(entryTime, endTime);
    }

    /**
     * Calculates parking fee based on duration.
     * Base rates: Motorcycle = $1/hr, Car = $2/hr, Truck = $3/hr
     * 
     * @deprecated Use PricingStrategy instead for flexible fee calculation
     */
    @Deprecated
    public double calculateFee() {
        long hours = Math.max(1, calculateDuration().toHours() + 1); // Minimum 1 hour
        double ratePerHour = switch (vehicle.getType()) {
            case MOTORCYCLE -> 1.0;
            case CAR -> 2.0;
            case TRUCK -> 3.0;
        };
        return hours * ratePerHour;
    }

    @Override
    public String toString() {
        String gateInfo = entryGate != null ? " via " + entryGate.getGateId() : "";
        return String.format("Ticket[%s | %s | Level %d, Spot %d%s | Entry: %s]",
            ticketId,
            vehicle,
            level.getFloorNumber(),
            parkingSpot.getSpotNumber(),
            gateInfo,
            entryTime.toString());
    }
}
