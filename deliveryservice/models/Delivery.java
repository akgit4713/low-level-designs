package deliveryservice.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Delivery {
    private final String deliveryId;
    private final String driverId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final BigDecimal cost;
    private boolean paid;

    public Delivery(String driverId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal cost) {
        this.deliveryId = UUID.randomUUID().toString();
        this.driverId = driverId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cost = cost;
        this.paid = false;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public String getDriverId() {
        return driverId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public boolean isPaid() {
        return paid;
    }

    /**
     * Marks this delivery as paid.
     * Package-private to be called only by DeliveryService.
     */
    public void markAsPaid() {
        this.paid = true;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "deliveryId='" + deliveryId + '\'' +
                ", driverId='" + driverId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", cost=" + cost +
                ", paid=" + paid +
                '}';
    }
}
