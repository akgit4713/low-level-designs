package fooddelivery.observers;

/**
 * Observer interface for order events.
 * Implements Observer Pattern for real-time notifications.
 */
public interface OrderObserver {
    
    /**
     * Called when an order event occurs.
     * @param event The order event
     */
    void onOrderEvent(OrderEvent event);
    
    /**
     * Get the observer identifier for debugging/logging.
     */
    String getObserverId();
}



