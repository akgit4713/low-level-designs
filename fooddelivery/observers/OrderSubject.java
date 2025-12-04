package fooddelivery.observers;

/**
 * Subject interface for order event publishing.
 */
public interface OrderSubject {
    
    /**
     * Register an observer to receive order events.
     */
    void registerObserver(OrderObserver observer);
    
    /**
     * Remove an observer from receiving events.
     */
    void removeObserver(OrderObserver observer);
    
    /**
     * Notify all registered observers of an event.
     */
    void notifyObservers(OrderEvent event);
}



