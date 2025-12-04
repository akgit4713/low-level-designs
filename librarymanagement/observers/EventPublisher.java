package librarymanagement.observers;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Thread-safe event publisher that manages observers and dispatches events.
 */
public class EventPublisher {
    
    private final List<LibraryEventObserver> observers = new CopyOnWriteArrayList<>();
    private final Map<LibraryEvent.EventType, List<LibraryEventObserver>> typeSpecificObservers = 
            new EnumMap<>(LibraryEvent.EventType.class);

    public void subscribe(LibraryEventObserver observer) {
        observers.add(observer);
        
        // Register for specific event types if specified
        LibraryEvent.EventType[] interestedTypes = observer.getInterestedEventTypes();
        if (interestedTypes != null && interestedTypes.length > 0) {
            for (LibraryEvent.EventType type : interestedTypes) {
                typeSpecificObservers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>())
                        .add(observer);
            }
        }
    }

    public void unsubscribe(LibraryEventObserver observer) {
        observers.remove(observer);
        typeSpecificObservers.values().forEach(list -> list.remove(observer));
    }

    public void publish(LibraryEvent event) {
        // Notify observers interested in specific event types
        List<LibraryEventObserver> specificObservers = typeSpecificObservers.get(event.getType());
        Set<LibraryEventObserver> notified = new HashSet<>();
        
        if (specificObservers != null) {
            for (LibraryEventObserver observer : specificObservers) {
                observer.onEvent(event);
                notified.add(observer);
            }
        }
        
        // Notify observers interested in all events (those without specific types)
        for (LibraryEventObserver observer : observers) {
            if (!notified.contains(observer)) {
                LibraryEvent.EventType[] types = observer.getInterestedEventTypes();
                if (types == null || types.length == 0) {
                    observer.onEvent(event);
                }
            }
        }
    }

    public int getObserverCount() {
        return observers.size();
    }
}



