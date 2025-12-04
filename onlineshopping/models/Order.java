package onlineshopping.models;

import onlineshopping.enums.OrderStatus;
import onlineshopping.enums.PaymentMethod;
import onlineshopping.enums.ShippingMethod;
import onlineshopping.exceptions.OrderException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents a customer order
 * Thread-safe for concurrent status updates
 * Uses Builder pattern for construction
 */
public class Order {
    private final String id;
    private final String userId;
    private final List<OrderItem> items;
    private final Address shippingAddress;
    private final Address billingAddress;
    private final ShippingMethod shippingMethod;
    private final PaymentMethod paymentMethod;
    private final LocalDateTime createdAt;
    private final BigDecimal subtotal;
    private final BigDecimal shippingCost;
    private final BigDecimal tax;
    private final BigDecimal totalAmount;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private volatile OrderStatus status;
    private String paymentId;
    private String trackingNumber;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private String cancellationReason;
    private String notes;

    private Order(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.items = Collections.unmodifiableList(new ArrayList<>(builder.items));
        this.shippingAddress = builder.shippingAddress;
        this.billingAddress = builder.billingAddress != null ? builder.billingAddress : builder.shippingAddress;
        this.shippingMethod = builder.shippingMethod;
        this.paymentMethod = builder.paymentMethod;
        this.subtotal = builder.subtotal;
        this.shippingCost = builder.shippingCost;
        this.tax = builder.tax;
        this.totalAmount = subtotal.add(shippingCost).add(tax);
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.notes = builder.notes;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public ShippingMethod getShippingMethod() {
        return shippingMethod;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public OrderStatus getStatus() {
        lock.readLock().lock();
        try {
            return status;
        } finally {
            lock.readLock().unlock();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Optional<String> getPaymentId() {
        return Optional.ofNullable(paymentId);
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Optional<String> getTrackingNumber() {
        return Optional.ofNullable(trackingNumber);
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Optional<LocalDateTime> getShippedAt() {
        return Optional.ofNullable(shippedAt);
    }

    public Optional<LocalDateTime> getDeliveredAt() {
        return Optional.ofNullable(deliveredAt);
    }

    public Optional<String> getCancellationReason() {
        return Optional.ofNullable(cancellationReason);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Transition order to new status with validation
     */
    public void transitionTo(OrderStatus newStatus) {
        lock.writeLock().lock();
        try {
            if (!status.canTransitionTo(newStatus)) {
                throw OrderException.invalidStateTransition(id, status.name(), newStatus.name());
            }
            this.status = newStatus;
            
            if (newStatus == OrderStatus.SHIPPED) {
                this.shippedAt = LocalDateTime.now();
            } else if (newStatus == OrderStatus.DELIVERED) {
                this.deliveredAt = LocalDateTime.now();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Cancel the order with a reason
     */
    public void cancel(String reason) {
        lock.writeLock().lock();
        try {
            if (!status.isCancellable()) {
                throw OrderException.cannotCancel(id, status.name());
            }
            this.status = OrderStatus.CANCELLED;
            this.cancellationReason = reason;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Get total item count
     */
    public int getTotalItemCount() {
        return items.stream()
            .mapToInt(OrderItem::getQuantity)
            .sum();
    }

    /**
     * Check if order can be cancelled
     */
    public boolean isCancellable() {
        lock.readLock().lock();
        try {
            return status.isCancellable();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get estimated delivery date based on shipping method
     */
    public LocalDateTime getEstimatedDelivery() {
        return createdAt.plusDays(shippingMethod.getMaxDays());
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
        return String.format("Order{id='%s', items=%d, total=%s, status=%s}", 
            id, items.size(), totalAmount, status);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String userId;
        private List<OrderItem> items = new ArrayList<>();
        private Address shippingAddress;
        private Address billingAddress;
        private ShippingMethod shippingMethod = ShippingMethod.STANDARD;
        private PaymentMethod paymentMethod;
        private BigDecimal subtotal = BigDecimal.ZERO;
        private BigDecimal shippingCost = BigDecimal.ZERO;
        private BigDecimal tax = BigDecimal.ZERO;
        private String notes = "";

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder items(List<OrderItem> items) {
            this.items = new ArrayList<>(items);
            return this;
        }

        public Builder addItem(OrderItem item) {
            this.items.add(item);
            return this;
        }

        public Builder shippingAddress(Address address) {
            this.shippingAddress = address;
            return this;
        }

        public Builder billingAddress(Address address) {
            this.billingAddress = address;
            return this;
        }

        public Builder shippingMethod(ShippingMethod method) {
            this.shippingMethod = method;
            return this;
        }

        public Builder paymentMethod(PaymentMethod method) {
            this.paymentMethod = method;
            return this;
        }

        public Builder subtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
            return this;
        }

        public Builder shippingCost(BigDecimal shippingCost) {
            this.shippingCost = shippingCost;
            return this;
        }

        public Builder tax(BigDecimal tax) {
            this.tax = tax;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Order build() {
            Objects.requireNonNull(id, "Order ID is required");
            Objects.requireNonNull(userId, "User ID is required");
            Objects.requireNonNull(shippingAddress, "Shipping address is required");
            Objects.requireNonNull(paymentMethod, "Payment method is required");
            
            if (items.isEmpty()) {
                throw OrderException.emptyOrder();
            }
            
            // Calculate subtotal from items if not set
            if (subtotal.equals(BigDecimal.ZERO)) {
                subtotal = items.stream()
                    .map(OrderItem::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            
            return new Order(this);
        }
    }
}



