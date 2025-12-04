package fooddelivery.models;

import fooddelivery.enums.AgentStatus;
import fooddelivery.enums.UserRole;
import java.util.Objects;

/**
 * Delivery agent with location tracking and availability status.
 */
public class DeliveryAgent extends User {
    private Location currentLocation;
    private AgentStatus status;
    private String vehicleNumber;
    private double rating;
    private int totalDeliveries;
    private String currentOrderId;

    public DeliveryAgent(String id, String name, String email, String phone, String vehicleNumber) {
        super(id, name, email, phone, UserRole.DELIVERY_AGENT);
        this.vehicleNumber = vehicleNumber;
        this.status = AgentStatus.OFFLINE;
        this.rating = 5.0;
        this.totalDeliveries = 0;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void updateLocation(Location location) {
        this.currentLocation = location;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public double getRating() {
        return rating;
    }

    public void updateRating(double newRating) {
        this.rating = ((this.rating * totalDeliveries) + newRating) / (totalDeliveries + 1);
    }

    public int getTotalDeliveries() {
        return totalDeliveries;
    }

    public void incrementDeliveryCount() {
        this.totalDeliveries++;
    }

    public String getCurrentOrderId() {
        return currentOrderId;
    }

    public void assignOrder(String orderId) {
        this.currentOrderId = orderId;
        this.status = AgentStatus.BUSY;
    }

    public void completeDelivery() {
        this.currentOrderId = null;
        this.status = AgentStatus.AVAILABLE;
        incrementDeliveryCount();
    }

    public boolean isAvailable() {
        return status == AgentStatus.AVAILABLE && currentOrderId == null;
    }

    @Override
    public String toString() {
        return String.format("DeliveryAgent{id='%s', name='%s', status=%s, rating=%.1f}", 
            getId(), getName(), status, rating);
    }
}



