package pubsub;

import pubsub.impl.AsyncMessageDispatcher;
import pubsub.impl.ConcurrentSubscriptionManager;
import pubsub.impl.TopicRegistry;
import pubsub.interfaces.MessageDispatcher;
import pubsub.interfaces.Subscriber;
import pubsub.interfaces.SubscriptionManager;
import pubsub.models.Message;
import pubsub.models.Subscription;
import pubsub.models.Topic;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Central facade for the Pub-Sub system.
 * Coordinates publishing, subscribing, and message delivery.
 * 
 * Design Patterns:
 * - Facade: Simplifies complex subsystem interactions
 * - Mediator: Decouples publishers from subscribers
 * - Dependency Injection: All dependencies are injected
 * 
 * Thread-safe: All operations are safe for concurrent access.
 *
 * @param <T> The type of message payload handled by this broker
 */
public class PubSubBroker<T> {
    
    private final TopicRegistry topicRegistry;
    private final SubscriptionManager<T> subscriptionManager;
    private final MessageDispatcher<T> messageDispatcher;
    private final AtomicLong messagesPublished;
    private final AtomicLong messagesDelivered;
    
    /**
     * Creates a broker with default components (async dispatcher).
     */
    public PubSubBroker() {
        this(
                new TopicRegistry(),
                new ConcurrentSubscriptionManager<>(),
                new AsyncMessageDispatcher<>()
        );
    }
    
    /**
     * Creates a broker with custom components (Dependency Injection).
     * Follows DIP: Depends on abstractions, not concrete implementations.
     *
     * @param topicRegistry Registry for managing topics
     * @param subscriptionManager Manager for subscriptions
     * @param messageDispatcher Strategy for message delivery
     */
    public PubSubBroker(
            TopicRegistry topicRegistry,
            SubscriptionManager<T> subscriptionManager,
            MessageDispatcher<T> messageDispatcher
    ) {
        this.topicRegistry = Objects.requireNonNull(topicRegistry, "TopicRegistry cannot be null");
        this.subscriptionManager = Objects.requireNonNull(subscriptionManager, "SubscriptionManager cannot be null");
        this.messageDispatcher = Objects.requireNonNull(messageDispatcher, "MessageDispatcher cannot be null");
        this.messagesPublished = new AtomicLong(0);
        this.messagesDelivered = new AtomicLong(0);
    }
    
    // ==================== Topic Management ====================
    
    /**
     * Creates a new topic or returns existing one.
     *
     * @param topicName The topic name
     * @return The Topic instance
     */
    public Topic createTopic(String topicName) {
        return topicRegistry.getOrCreate(topicName);
    }
    
    /**
     * Gets all registered topics.
     *
     * @return Set of all topics
     */
    public Set<Topic> getTopics() {
        return topicRegistry.getAllTopics();
    }
    
    /**
     * Checks if a topic exists.
     *
     * @param topicName The topic name
     * @return true if topic exists
     */
    public boolean topicExists(String topicName) {
        return topicRegistry.exists(topicName);
    }
    
    // ==================== Subscription Management ====================
    
    /**
     * Subscribes to a topic.
     *
     * @param topicName The topic name to subscribe to
     * @param subscriber The subscriber
     * @return The created subscription
     */
    public Subscription<T> subscribe(String topicName, Subscriber<T> subscriber) {
        Topic topic = topicRegistry.getOrCreate(topicName);
        return subscribe(topic, subscriber);
    }
    
    /**
     * Subscribes to a topic.
     *
     * @param topic The topic to subscribe to
     * @param subscriber The subscriber
     * @return The created subscription
     */
    public Subscription<T> subscribe(Topic topic, Subscriber<T> subscriber) {
        Objects.requireNonNull(topic, "Topic cannot be null");
        Objects.requireNonNull(subscriber, "Subscriber cannot be null");
        
        // Ensure topic is registered
        topicRegistry.getOrCreate(topic.getName());
        
        Subscription<T> subscription = new Subscription<>(topic, subscriber);
        subscriptionManager.addSubscription(subscription);
        
        return subscription;
    }
    
    /**
     * Unsubscribes using a subscription ID.
     *
     * @param subscriptionId The subscription ID
     * @return true if unsubscribed successfully
     */
    public boolean unsubscribe(String subscriptionId) {
        return subscriptionManager.removeSubscription(subscriptionId).isPresent();
    }
    
    /**
     * Unsubscribes a subscription.
     *
     * @param subscription The subscription to remove
     * @return true if unsubscribed successfully
     */
    public boolean unsubscribe(Subscription<T> subscription) {
        return unsubscribe(subscription.getId());
    }
    
    /**
     * Removes all subscriptions for a subscriber.
     *
     * @param subscriberId The subscriber ID
     * @return Number of subscriptions removed
     */
    public int unsubscribeAll(String subscriberId) {
        return subscriptionManager.removeSubscriberFromAll(subscriberId).size();
    }
    
    /**
     * Gets subscriptions for a topic.
     *
     * @param topicName The topic name
     * @return Set of subscriptions
     */
    public Set<Subscription<T>> getSubscriptions(String topicName) {
        return topicRegistry.get(topicName)
                .map(subscriptionManager::getSubscriptions)
                .orElse(Set.of());
    }
    
    /**
     * Gets the count of subscribers for a topic.
     *
     * @param topicName The topic name
     * @return Number of subscribers
     */
    public int getSubscriberCount(String topicName) {
        return getSubscriptions(topicName).size();
    }
    
    // ==================== Publishing ====================
    
    /**
     * Publishes a message to its topic.
     * The message is dispatched asynchronously to all subscribers.
     *
     * @param message The message to publish
     */
    public void publish(Message<T> message) {
        Objects.requireNonNull(message, "Message cannot be null");
        
        Topic topic = message.getTopic();
        
        // Ensure topic is registered
        topicRegistry.getOrCreate(topic.getName());
        
        Set<Subscription<T>> subscriptions = subscriptionManager.getSubscriptions(topic);
        
        if (!subscriptions.isEmpty()) {
            messageDispatcher.dispatch(message, subscriptions);
            messagesDelivered.addAndGet(subscriptions.size());
        }
        
        messagesPublished.incrementAndGet();
    }
    
    /**
     * Publishes a payload to a topic (convenience method).
     *
     * @param topicName The topic name
     * @param payload The payload to publish
     * @return The created message
     */
    public Message<T> publish(String topicName, T payload) {
        Topic topic = topicRegistry.getOrCreate(topicName);
        return publish(topic, payload);
    }
    
    /**
     * Publishes a payload to a topic (convenience method).
     *
     * @param topic The topic
     * @param payload The payload to publish
     * @return The created message
     */
    public Message<T> publish(Topic topic, T payload) {
        Message<T> message = Message.<T>builder()
                .topic(topic)
                .payload(payload)
                .build();
        
        publish(message);
        return message;
    }
    
    // ==================== Statistics & Management ====================
    
    /**
     * Gets the total number of messages published.
     *
     * @return Messages published count
     */
    public long getMessagesPublishedCount() {
        return messagesPublished.get();
    }
    
    /**
     * Gets the total number of message deliveries (msg * subscribers).
     *
     * @return Messages delivered count
     */
    public long getMessagesDeliveredCount() {
        return messagesDelivered.get();
    }
    
    /**
     * Gets the total subscription count.
     *
     * @return Total subscriptions
     */
    public int getTotalSubscriptionCount() {
        return subscriptionManager.getSubscriptionCount();
    }
    
    /**
     * Gets topics that have active subscribers.
     *
     * @return Set of active topics
     */
    public Set<Topic> getActiveTopics() {
        return subscriptionManager.getActiveTopics();
    }
    
    /**
     * Shuts down the broker gracefully.
     * Completes pending message deliveries before shutting down.
     */
    public void shutdown() {
        messageDispatcher.shutdown();
    }
    
    /**
     * Shuts down the broker immediately.
     * Pending messages may not be delivered.
     */
    public void shutdownNow() {
        messageDispatcher.shutdownNow();
    }
    
    /**
     * Checks if the broker is running.
     *
     * @return true if broker can accept new messages
     */
    public boolean isRunning() {
        return messageDispatcher.isRunning();
    }
    
    /**
     * Clears all subscriptions (useful for testing).
     */
    public void clearSubscriptions() {
        subscriptionManager.clear();
    }
}



