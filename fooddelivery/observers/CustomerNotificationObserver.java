package fooddelivery.observers;

import fooddelivery.enums.NotificationType;
import fooddelivery.models.Order;

/**
 * Observer that sends notifications to customers.
 */
public class CustomerNotificationObserver implements OrderObserver {
    
    private final String customerId;
    
    public CustomerNotificationObserver(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public void onOrderEvent(OrderEvent event) {
        Order order = event.getOrder();
        
        // Only notify if this is the customer's order
        if (!order.getCustomerId().equals(customerId)) {
            return;
        }
        
        String notification = formatNotification(event);
        sendNotification(notification);
    }
    
    private String formatNotification(OrderEvent event) {
        NotificationType type = event.getType();
        Order order = event.getOrder();
        
        return switch (type) {
            case ORDER_PLACED -> String.format(
                "🛒 Order #%s placed successfully! Total: ₹%s", 
                order.getId(), order.getTotalAmount());
            case ORDER_CONFIRMED -> String.format(
                "✅ Order #%s confirmed by restaurant", order.getId());
            case ORDER_PREPARING -> String.format(
                "👨‍🍳 Your order #%s is being prepared", order.getId());
            case ORDER_READY -> String.format(
                "🍽️ Order #%s is ready for pickup", order.getId());
            case ORDER_PICKED_UP -> String.format(
                "🚴 Order #%s picked up! On the way to you", order.getId());
            case ORDER_DELIVERED -> String.format(
                "🎉 Order #%s delivered! Enjoy your meal!", order.getId());
            case ORDER_CANCELLED -> String.format(
                "❌ Order #%s has been cancelled. %s", 
                order.getId(), order.getCancellationReason());
            case PAYMENT_SUCCESS -> String.format(
                "💳 Payment successful for order #%s", order.getId());
            case PAYMENT_FAILED -> String.format(
                "⚠️ Payment failed for order #%s. Please retry.", order.getId());
            default -> event.getMessage();
        };
    }
    
    private void sendNotification(String notification) {
        // In real system, this would use push notifications, SMS, email, etc.
        System.out.println("[Customer " + customerId + "] " + notification);
    }

    @Override
    public String getObserverId() {
        return "CustomerObserver-" + customerId;
    }
}



