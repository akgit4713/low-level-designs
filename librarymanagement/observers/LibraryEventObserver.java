package librarymanagement.observers;

/**
 * Observer interface for library events.
 * Implementations can react to various library events (e.g., notifications, logging).
 */
public interface LibraryEventObserver {
    
    /**
     * Called when a library event occurs.
     * 
     * @param event The event that occurred
     */
    void onEvent(LibraryEvent event);
    
    /**
     * Returns the types of events this observer is interested in.
     * Return null or empty array to receive all events.
     */
    default LibraryEvent.EventType[] getInterestedEventTypes() {
        return null; // Subscribe to all events by default
    }
}



