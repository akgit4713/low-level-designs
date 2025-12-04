package fooddelivery.observers;

import fooddelivery.enums.NotificationType;
import fooddelivery.models.Order;
import java.util.Set;

/**
 * Observer that sends notifications to delivery agents.
 */
public class DeliveryAgentNotificationObserver implements OrderObserver {
    
    private final String agentId;
    private String currentOrderId;
    
    private static final Set<NotificationType> RELEVANT_EVENTS = Set.of(
        NotificationType.ORDER_READY,
        NotificationType.DELIVERY_ASSIGNED,
        NotificationType.ORDER_CANCELLED
    );
    
    public DeliveryAgentNotificationObserver(String agentId) {
        this.agentId = agentId;
    }
    
    public void setCurrentOrderId(String orderId) {
        this.currentOrderId = orderId;
    }

    @Override
    public void onOrderEvent(OrderEvent event) {
        Order order = event.getOrder();
        
        // Only notify for relevant events and if assigned to this order
        if (!RELEVANT_EVENTS.contains(event.getType())) {
            return;
        }
        
        // For DELIVERY_ASSIGNED, check if it's for this agent via message
        if (event.getType() == NotificationType.DELIVERY_ASSIGNED) {
            if (event.getMessage().contains(agentId)) {
                currentOrderId = order.getId();
                sendNotification(formatNotification(event));
            }
            return;
        }
        
        // For other events, check if this is the assigned order
        if (currentOrderId != null && currentOrderId.equals(order.getId())) {
            sendNotification(formatNotification(event));
        }
    }
    
    private String formatNotification(OrderEvent event) {
        Order order = event.getOrder();
        
        return switch (event.getType()) {
            case DELIVERY_ASSIGNED -> String.format(
                "📦 New delivery assigned! Order #%s - Pickup from restaurant", 
                order.getId());
            case ORDER_READY -> String.format(
                "✅ Order #%s is ready for pickup at restaurant", order.getId());
            case ORDER_CANCELLED -> String.format(
                "❌ Order #%s has been cancelled. Return to available pool.", 
                order.getId());
            default -> event.getMessage();
        };
    }
    
    private void sendNotification(String notification) {
        // In real system, this would push to delivery agent app
        System.out.println("[DeliveryAgent " + agentId + "] " + notification);
    }

    @Override
    public String getObserverId() {
        return "DeliveryAgentObserver-" + agentId;
    }
}



