package fooddelivery.observers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Central publisher for order events.
 * Implements Subject in Observer pattern with async notification support.
 */
public class OrderEventPublisher implements OrderSubject {
    
    private final List<OrderObserver> observers;
    private final ExecutorService executorService;
    private final boolean asyncNotifications;
    
    public OrderEventPublisher() {
        this(false); // Sync by default for simplicity in demo
    }
    
    public OrderEventPublisher(boolean asyncNotifications) {
        this.observers = new CopyOnWriteArrayList<>();
        this.asyncNotifications = asyncNotifications;
        this.executorService = asyncNotifications ? 
            Executors.newFixedThreadPool(4) : null;
    }

    @Override
    public void registerObserver(OrderObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("[EventPublisher] Registered: " + observer.getObserverId());
        }
    }

    @Override
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
        System.out.println("[EventPublisher] Removed: " + observer.getObserverId());
    }

    @Override
    public void notifyObservers(OrderEvent event) {
        System.out.println("[EventPublisher] Broadcasting: " + event.getType() + 
                          " for order " + event.getOrder().getId());
        
        for (OrderObserver observer : observers) {
            if (asyncNotifications && executorService != null) {
                executorService.submit(() -> safeNotify(observer, event));
            } else {
                safeNotify(observer, event);
            }
        }
    }
    
    private void safeNotify(OrderObserver observer, OrderEvent event) {
        try {
            observer.onOrderEvent(event);
        } catch (Exception e) {
            System.err.println("[EventPublisher] Error notifying " + 
                observer.getObserverId() + ": " + e.getMessage());
        }
    }
    
    /**
     * Shutdown the executor service (call on application shutdown).
     */
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
    
    /**
     * Get count of registered observers.
     */
    public int getObserverCount() {
        return observers.size();
    }
}



