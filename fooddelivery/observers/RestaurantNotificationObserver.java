package fooddelivery.observers;

import fooddelivery.enums.NotificationType;
import fooddelivery.models.Order;
import java.util.Set;

/**
 * Observer that sends notifications to restaurants.
 */
public class RestaurantNotificationObserver implements OrderObserver {
    
    private final String restaurantId;
    private static final Set<NotificationType> RELEVANT_EVENTS = Set.of(
        NotificationType.ORDER_PLACED,
        NotificationType.ORDER_CANCELLED,
        NotificationType.PAYMENT_SUCCESS,
        NotificationType.PAYMENT_FAILED
    );
    
    public RestaurantNotificationObserver(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    @Override
    public void onOrderEvent(OrderEvent event) {
        Order order = event.getOrder();
        
        // Only notify if this is the restaurant's order and event is relevant
        if (!order.getRestaurantId().equals(restaurantId)) {
            return;
        }
        
        if (!RELEVANT_EVENTS.contains(event.getType())) {
            return;
        }
        
        String notification = formatNotification(event);
        sendNotification(notification);
    }
    
    private String formatNotification(OrderEvent event) {
        Order order = event.getOrder();
        
        return switch (event.getType()) {
            case ORDER_PLACED -> String.format(
                "🔔 NEW ORDER #%s - %d items - ₹%s", 
                order.getId(), order.getItems().size(), order.getSubtotal());
            case ORDER_CANCELLED -> String.format(
                "❌ Order #%s cancelled: %s", 
                order.getId(), order.getCancellationReason());
            case PAYMENT_SUCCESS -> String.format(
                "💰 Payment confirmed for order #%s", order.getId());
            case PAYMENT_FAILED -> String.format(
                "⚠️ Payment failed for order #%s - please wait for retry", order.getId());
            default -> event.getMessage();
        };
    }
    
    private void sendNotification(String notification) {
        // In real system, this would push to restaurant dashboard/app
        System.out.println("[Restaurant " + restaurantId + "] " + notification);
    }

    @Override
    public String getObserverId() {
        return "RestaurantObserver-" + restaurantId;
    }
}



