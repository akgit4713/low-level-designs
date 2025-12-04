package onlineshopping.models;

import onlineshopping.enums.ShippingMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents shipment details for an order
 */
public class Shipment {
    private final String id;
    private final String orderId;
    private final Address destination;
    private final ShippingMethod method;
    private final BigDecimal cost;
    private final LocalDateTime createdAt;
    private final List<TrackingEvent> trackingHistory;
    
    private String trackingNumber;
    private String carrier;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime actualDelivery;

    public Shipment(String id, String orderId, Address destination, 
                    ShippingMethod method, BigDecimal cost) {
        this.id = Objects.requireNonNull(id, "Shipment ID is required");
        this.orderId = Objects.requireNonNull(orderId, "Order ID is required");
        this.destination = Objects.requireNonNull(destination, "Destination is required");
        this.method = Objects.requireNonNull(method, "Shipping method is required");
        this.cost = Objects.requireNonNull(cost, "Cost is required");
        this.createdAt = LocalDateTime.now();
        this.trackingHistory = new ArrayList<>();
        
        // Calculate estimated delivery based on shipping method
        this.estimatedDelivery = createdAt.plusDays(method.getMaxDays());
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public Address getDestination() {
        return destination;
    }

    public ShippingMethod getMethod() {
        return method;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
        addTrackingEvent("Tracking number assigned: " + trackingNumber, "Warehouse");
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public LocalDateTime getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public LocalDateTime getActualDelivery() {
        return actualDelivery;
    }

    /**
     * Mark shipment as delivered
     */
    public void markDelivered() {
        this.actualDelivery = LocalDateTime.now();
        addTrackingEvent("Package delivered", destination.getCity());
    }

    public List<TrackingEvent> getTrackingHistory() {
        return Collections.unmodifiableList(trackingHistory);
    }

    /**
     * Add a tracking event
     */
    public void addTrackingEvent(String description, String location) {
        trackingHistory.add(new TrackingEvent(description, location));
    }

    /**
     * Get the latest tracking status
     */
    public String getLatestStatus() {
        if (trackingHistory.isEmpty()) {
            return "Preparing for shipment";
        }
        return trackingHistory.get(trackingHistory.size() - 1).getDescription();
    }

    /**
     * Check if shipment is delivered
     */
    public boolean isDelivered() {
        return actualDelivery != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shipment shipment = (Shipment) o;
        return Objects.equals(id, shipment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Shipment{id='%s', tracking='%s', method=%s, status='%s'}", 
            id, trackingNumber, method, getLatestStatus());
    }

    /**
     * Inner class for tracking events
     */
    public static class TrackingEvent {
        private final String description;
        private final String location;
        private final LocalDateTime timestamp;

        public TrackingEvent(String description, String location) {
            this.description = description;
            this.location = location;
            this.timestamp = LocalDateTime.now();
        }

        public String getDescription() {
            return description;
        }

        public String getLocation() {
            return location;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s - %s", timestamp, location, description);
        }
    }
}



