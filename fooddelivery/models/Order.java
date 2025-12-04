package fooddelivery.models;

import fooddelivery.enums.OrderStatus;
import fooddelivery.enums.PaymentMethod;
import fooddelivery.exceptions.OrderException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Core order entity representing a customer's food order.
 */
public class Order {
    private final String id;
    private final String customerId;
    private final String restaurantId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private Location deliveryAddress;
    private PaymentMethod paymentMethod;
    private String paymentId;
    private String deliveryId;
    
    // Pricing
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    
    // Timestamps
    private final LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime preparingAt;
    private LocalDateTime readyAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    
    // Additional info
    private String specialInstructions;
    private String cancellationReason;
    private double customerRating;
    private String customerReview;

    public Order(String id, String customerId, String restaurantId, List<OrderItem> items, 
                 Location deliveryAddress) {
        this.id = id;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.items = new ArrayList<>(items);
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.PLACED;
        this.createdAt = LocalDateTime.now();
        this.discountAmount = BigDecimal.ZERO;
        calculatePricing();
    }

    private void calculatePricing() {
        this.subtotal = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.taxAmount = subtotal.multiply(new BigDecimal("0.05")); // 5% tax
        this.deliveryFee = new BigDecimal("40.00"); // Default delivery fee
        this.totalAmount = subtotal.add(taxAmount).add(deliveryFee).subtract(discountAmount);
    }

    // Getters
    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getRestaurantId() { return restaurantId; }
    public List<OrderItem> getItems() { return new ArrayList<>(items); }
    public OrderStatus getStatus() { return status; }
    public Location getDeliveryAddress() { return deliveryAddress; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public String getPaymentId() { return paymentId; }
    public String getDeliveryId() { return deliveryId; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDeliveryFee() { return deliveryFee; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public LocalDateTime getPreparingAt() { return preparingAt; }
    public LocalDateTime getReadyAt() { return readyAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public String getSpecialInstructions() { return specialInstructions; }
    public String getCancellationReason() { return cancellationReason; }
    public double getCustomerRating() { return customerRating; }
    public String getCustomerReview() { return customerReview; }

    // Setters
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void setDeliveryId(String deliveryId) { this.deliveryId = deliveryId; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
        recalculateTotal();
    }

    public void applyDiscount(BigDecimal discount) {
        this.discountAmount = discount;
        recalculateTotal();
    }

    private void recalculateTotal() {
        this.totalAmount = subtotal.add(taxAmount).add(deliveryFee).subtract(discountAmount);
    }

    // Status Transitions
    public void confirm() {
        validateTransition(OrderStatus.CONFIRMED);
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void startPreparing() {
        validateTransition(OrderStatus.PREPARING);
        this.status = OrderStatus.PREPARING;
        this.preparingAt = LocalDateTime.now();
    }

    public void markReady() {
        validateTransition(OrderStatus.READY_FOR_PICKUP);
        this.status = OrderStatus.READY_FOR_PICKUP;
        this.readyAt = LocalDateTime.now();
    }

    public void markOutForDelivery() {
        validateTransition(OrderStatus.OUT_FOR_DELIVERY);
        this.status = OrderStatus.OUT_FOR_DELIVERY;
    }

    public void markDelivered() {
        validateTransition(OrderStatus.DELIVERED);
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public void cancel(String reason) {
        if (status == OrderStatus.DELIVERED) {
            throw new OrderException("Cannot cancel delivered order");
        }
        if (status == OrderStatus.OUT_FOR_DELIVERY) {
            throw new OrderException("Cannot cancel order that is out for delivery");
        }
        this.status = OrderStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledAt = LocalDateTime.now();
    }

    public void addRating(double rating, String review) {
        if (status != OrderStatus.DELIVERED) {
            throw new OrderException("Can only rate delivered orders");
        }
        this.customerRating = rating;
        this.customerReview = review;
    }

    private void validateTransition(OrderStatus newStatus) {
        boolean valid = switch (newStatus) {
            case CONFIRMED -> status == OrderStatus.PLACED;
            case PREPARING -> status == OrderStatus.CONFIRMED;
            case READY_FOR_PICKUP -> status == OrderStatus.PREPARING;
            case OUT_FOR_DELIVERY -> status == OrderStatus.READY_FOR_PICKUP;
            case DELIVERED -> status == OrderStatus.OUT_FOR_DELIVERY;
            default -> false;
        };
        
        if (!valid) {
            throw new OrderException(
                String.format("Invalid status transition from %s to %s", status, newStatus));
        }
    }

    public boolean canBeCancelled() {
        return status != OrderStatus.DELIVERED && 
               status != OrderStatus.OUT_FOR_DELIVERY && 
               status != OrderStatus.CANCELLED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Order{id='%s', status=%s, total=%s, items=%d}", 
            id, status, totalAmount, items.size());
    }
}



