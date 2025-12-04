package restaurant.exceptions;

/**
 * Exception for order-related errors
 */
public class OrderException extends RestaurantException {

    public OrderException(String message) {
        super(message);
    }

    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }

    public static OrderException invalidStateTransition(String orderId, String from, String to) {
        return new OrderException(
            String.format("Invalid order state transition for order %s: %s -> %s", orderId, from, to)
        );
    }

    public static OrderException orderNotFound(String orderId) {
        return new OrderException("Order not found: " + orderId);
    }

    public static OrderException emptyOrder() {
        return new OrderException("Cannot create an order with no items");
    }
}

