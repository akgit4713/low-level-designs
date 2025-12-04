package onlineshopping.observers;

import onlineshopping.enums.OrderStatus;
import onlineshopping.models.Order;

/**
 * Observer that sends email notifications for order events
 */
public class EmailNotificationObserver implements OrderObserver {

    @Override
    public void onOrderPlaced(Order order) {
        sendEmail(order.getUserId(), 
            "Order Confirmation - " + order.getId(),
            String.format("Thank you for your order! Order ID: %s, Total: $%s",
                order.getId(), order.getTotalAmount()));
    }

    @Override
    public void onOrderStatusChanged(Order order, OrderStatus from, OrderStatus to) {
        sendEmail(order.getUserId(),
            "Order Update - " + order.getId(),
            String.format("Your order status has changed from %s to %s", from, to));
    }

    @Override
    public void onOrderShipped(Order order, String trackingNumber) {
        sendEmail(order.getUserId(),
            "Your Order Has Shipped! - " + order.getId(),
            String.format("Your order is on its way! Tracking number: %s\nEstimated delivery: %s",
                trackingNumber, order.getEstimatedDelivery()));
    }

    @Override
    public void onOrderDelivered(Order order) {
        sendEmail(order.getUserId(),
            "Order Delivered - " + order.getId(),
            "Your order has been delivered. Thank you for shopping with us!");
    }

    @Override
    public void onOrderCancelled(Order order, String reason) {
        sendEmail(order.getUserId(),
            "Order Cancelled - " + order.getId(),
            String.format("Your order has been cancelled. Reason: %s\nRefund will be processed within 5-7 business days.",
                reason));
    }

    private void sendEmail(String userId, String subject, String body) {
        // In a real implementation, this would send an actual email
        System.out.printf("[EMAIL] To: %s | Subject: %s | Body: %s%n", userId, subject, body);
    }
}



