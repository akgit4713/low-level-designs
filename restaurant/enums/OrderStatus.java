package restaurant.enums;

/**
 * Represents the lifecycle states of an order.
 * State transitions: PLACED -> PREPARING -> READY -> SERVED -> BILLED -> PAID -> COMPLETED
 */
public enum OrderStatus {
    PLACED("Order has been placed"),
    PREPARING("Order is being prepared in kitchen"),
    READY("Order is ready for serving"),
    SERVED("Order has been served to customer"),
    BILLED("Bill has been generated"),
    PAID("Payment has been completed"),
    COMPLETED("Order is complete"),
    CANCELLED("Order has been cancelled");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PLACED -> newStatus == PREPARING || newStatus == CANCELLED;
            case PREPARING -> newStatus == READY || newStatus == CANCELLED;
            case READY -> newStatus == SERVED;
            case SERVED -> newStatus == BILLED;
            case BILLED -> newStatus == PAID;
            case PAID -> newStatus == COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }
}

