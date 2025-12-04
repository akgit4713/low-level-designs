package restaurant.models;

import restaurant.enums.OrderStatus;
import restaurant.enums.OrderType;
import restaurant.exceptions.OrderException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents a customer order
 * Thread-safe for concurrent status updates
 */
public class Order {
    private final String id;
    private final String customerId;
    private final Table table; // null for takeout/delivery
    private final OrderType orderType;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    private volatile OrderStatus status;
    private Staff assignedWaiter;
    private LocalDateTime completedAt;
    private String notes;

    private Order(Builder builder) {
        this.id = builder.id;
        this.customerId = builder.customerId;
        this.table = builder.table;
        this.orderType = builder.orderType;
        this.items = Collections.unmodifiableList(new ArrayList<>(builder.items));
        this.status = OrderStatus.PLACED;
        this.createdAt = LocalDateTime.now();
        this.assignedWaiter = builder.assignedWaiter;
        this.notes = builder.notes;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Optional<Table> getTable() {
        return Optional.ofNullable(table);
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public List<OrderItem> getItems() {
        return items;
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

    public Optional<LocalDateTime> getCompletedAt() {
        return Optional.ofNullable(completedAt);
    }

    public Optional<Staff> getAssignedWaiter() {
        return Optional.ofNullable(assignedWaiter);
    }

    public void assignWaiter(Staff waiter) {
        this.assignedWaiter = waiter;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Calculate total amount for this order
     */
    public BigDecimal calculateSubtotal() {
        return items.stream()
            .map(OrderItem::getItemTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get estimated preparation time in minutes
     */
    public int getEstimatedPrepTime() {
        return items.stream()
            .mapToInt(item -> item.getMenuItem().getPreparationTimeMinutes())
            .max()
            .orElse(0);
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
            
            if (newStatus == OrderStatus.COMPLETED) {
                this.completedAt = LocalDateTime.now();
            }
        } finally {
            lock.writeLock().unlock();
        }
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
        return String.format("Order{id='%s', type=%s, items=%d, status=%s, total=%s}",
            id, orderType, items.size(), status, calculateSubtotal());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String customerId;
        private Table table;
        private OrderType orderType = OrderType.DINE_IN;
        private List<OrderItem> items = new ArrayList<>();
        private Staff assignedWaiter;
        private String notes = "";

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder table(Table table) {
            this.table = table;
            return this;
        }

        public Builder orderType(OrderType orderType) {
            this.orderType = orderType;
            return this;
        }

        public Builder addItem(OrderItem item) {
            this.items.add(item);
            return this;
        }

        public Builder items(List<OrderItem> items) {
            this.items = new ArrayList<>(items);
            return this;
        }

        public Builder assignedWaiter(Staff waiter) {
            this.assignedWaiter = waiter;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Order build() {
            Objects.requireNonNull(id, "Order ID is required");
            Objects.requireNonNull(customerId, "Customer ID is required");
            
            if (items.isEmpty()) {
                throw OrderException.emptyOrder();
            }
            
            if (orderType == OrderType.DINE_IN && table == null) {
                throw new IllegalStateException("Dine-in orders require a table");
            }
            
            return new Order(this);
        }
    }
}

