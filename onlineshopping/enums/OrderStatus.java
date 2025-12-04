package onlineshopping.enums;

/**
 * Represents the lifecycle status of an order
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PROCESSING,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    RETURNED;

    /**
     * Check if transition to new status is valid
     */
    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED -> newStatus == PROCESSING || newStatus == CANCELLED;
            case PROCESSING -> newStatus == SHIPPED || newStatus == CANCELLED;
            case SHIPPED -> newStatus == OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> newStatus == DELIVERED;
            case DELIVERED -> newStatus == RETURNED;
            case CANCELLED, RETURNED -> false;
        };
    }

    /**
     * Check if order is in a terminal state
     */
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED || this == RETURNED;
    }

    /**
     * Check if order can be cancelled
     */
    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED || this == PROCESSING;
    }
}



