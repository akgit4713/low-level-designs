package pubsub.subscribers;

import pubsub.interfaces.Subscriber;
import pubsub.models.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A subscriber that collects all received messages.
 * Useful for testing and batch processing.
 * Thread-safe: Uses CopyOnWriteArrayList for concurrent access.
 *
 * @param <T> The type of message payload
 */
public class CollectingSubscriber<T> implements Subscriber<T> {
    
    private final String id;
    private final List<Message<T>> messages;
    
    public CollectingSubscriber() {
        this.id = UUID.randomUUID().toString();
        this.messages = new CopyOnWriteArrayList<>();
    }
    
    @Override
    public void onMessage(Message<T> message) {
        messages.add(message);
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    /**
     * Gets all collected messages.
     *
     * @return Unmodifiable list of messages
     */
    public List<Message<T>> getMessages() {
        return Collections.unmodifiableList(new ArrayList<>(messages));
    }
    
    /**
     * Gets the count of received messages.
     *
     * @return Message count
     */
    public int getMessageCount() {
        return messages.size();
    }
    
    /**
     * Gets the last received message.
     *
     * @return Last message or null if none received
     */
    public Message<T> getLastMessage() {
        if (messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }
    
    /**
     * Clears all collected messages.
     */
    public void clear() {
        messages.clear();
    }
    
    /**
     * Checks if any messages were received.
     *
     * @return true if at least one message was received
     */
    public boolean hasMessages() {
        return !messages.isEmpty();
    }
    
    @Override
    public String toString() {
        return "CollectingSubscriber{id='" + id + "', messageCount=" + messages.size() + "}";
    }
}



