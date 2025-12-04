package fooddelivery.observers;

import fooddelivery.enums.NotificationType;
import fooddelivery.models.Order;
import java.time.LocalDateTime;

/**
 * Event object representing an order-related event.
 */
public class OrderEvent {
    private final NotificationType type;
    private final Order order;
    private final String message;
    private final LocalDateTime timestamp;

    public OrderEvent(NotificationType type, Order order, String message) {
        this.type = type;
        this.order = order;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public NotificationType getType() {
        return type;
    }

    public Order getOrder() {
        return order;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("OrderEvent{type=%s, orderId=%s, message='%s'}", 
            type, order.getId(), message);
    }
}



