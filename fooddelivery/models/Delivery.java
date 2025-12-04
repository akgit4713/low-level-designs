package fooddelivery.models;

import fooddelivery.enums.DeliveryStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Delivery information tracking for an order.
 */
public class Delivery {
    private final String id;
    private final String orderId;
    private String agentId;
    private DeliveryStatus status;
    private Location pickupLocation;
    private Location dropLocation;
    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private double estimatedDistanceKm;
    private int estimatedTimeMinutes;
    private final List<LocationUpdate> trackingHistory;

    public Delivery(String id, String orderId, Location pickupLocation, Location dropLocation) {
        this.id = id;
        this.orderId = orderId;
        this.pickupLocation = pickupLocation;
        this.dropLocation = dropLocation;
        this.status = DeliveryStatus.PENDING_ASSIGNMENT;
        this.trackingHistory = new ArrayList<>();
        this.estimatedDistanceKm = pickupLocation.distanceTo(dropLocation);
        this.estimatedTimeMinutes = calculateEstimatedTime();
    }

    private int calculateEstimatedTime() {
        // Assuming average speed of 25 km/h in city traffic
        return (int) Math.ceil((estimatedDistanceKm / 25.0) * 60) + 10; // +10 min buffer
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void assignAgent(String agentId) {
        this.agentId = agentId;
        this.status = DeliveryStatus.ASSIGNED;
        this.assignedAt = LocalDateTime.now();
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
        if (status == DeliveryStatus.PICKED_UP) {
            this.pickedUpAt = LocalDateTime.now();
        } else if (status == DeliveryStatus.DELIVERED) {
            this.deliveredAt = LocalDateTime.now();
        }
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDropLocation() {
        return dropLocation;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getPickedUpAt() {
        return pickedUpAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public double getEstimatedDistanceKm() {
        return estimatedDistanceKm;
    }

    public int getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void addLocationUpdate(Location location) {
        trackingHistory.add(new LocationUpdate(location, LocalDateTime.now()));
    }

    public List<LocationUpdate> getTrackingHistory() {
        return new ArrayList<>(trackingHistory);
    }

    public Location getCurrentLocation() {
        if (trackingHistory.isEmpty()) {
            return pickupLocation;
        }
        return trackingHistory.get(trackingHistory.size() - 1).getLocation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Delivery delivery = (Delivery) o;
        return Objects.equals(id, delivery.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Delivery{id='%s', orderId='%s', status=%s, agent='%s'}", 
            id, orderId, status, agentId);
    }

    /**
     * Inner class for tracking location updates.
     */
    public static class LocationUpdate {
        private final Location location;
        private final LocalDateTime timestamp;

        public LocationUpdate(Location location, LocalDateTime timestamp) {
            this.location = location;
            this.timestamp = timestamp;
        }

        public Location getLocation() {
            return location;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}



