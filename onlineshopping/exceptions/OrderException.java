package onlineshopping.exceptions;

/**
 * Exception for order-related errors
 */
public class OrderException extends ShoppingException {

    public OrderException(String message) {
        super(message);
    }

    public static OrderException notFound(String orderId) {
        return new OrderException("Order not found: " + orderId);
    }

    public static OrderException invalidStateTransition(String orderId, String from, String to) {
        return new OrderException(
            String.format("Invalid order state transition for %s: %s -> %s", orderId, from, to)
        );
    }

    public static OrderException emptyOrder() {
        return new OrderException("Cannot place an order with no items");
    }

    public static OrderException alreadyCancelled(String orderId) {
        return new OrderException("Order already cancelled: " + orderId);
    }

    public static OrderException cannotCancel(String orderId, String status) {
        return new OrderException(
            String.format("Cannot cancel order %s in status: %s", orderId, status)
        );
    }
}



