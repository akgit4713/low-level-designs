package pubsub.interfaces;

import pubsub.models.Subscription;
import pubsub.models.Topic;

import java.util.Optional;
import java.util.Set;

/**
 * Interface for managing topic subscriptions.
 * Implementations must be thread-safe.
 * 
 * Following SRP: Only handles subscription CRUD operations.
 * Following DIP: PubSubBroker depends on this abstraction.
 *
 * @param <T> The type of message payload for subscriptions
 */
public interface SubscriptionManager<T> {
    
    /**
     * Adds a subscription for a topic.
     *
     * @param subscription The subscription to add
     * @return true if added successfully, false if already exists
     */
    boolean addSubscription(Subscription<T> subscription);
    
    /**
     * Removes a subscription by ID.
     *
     * @param subscriptionId The subscription ID to remove
     * @return The removed subscription, if found
     */
    Optional<Subscription<T>> removeSubscription(String subscriptionId);
    
    /**
     * Removes all subscriptions for a specific subscriber across all topics.
     *
     * @param subscriberId The subscriber ID
     * @return Set of removed subscriptions
     */
    Set<Subscription<T>> removeSubscriberFromAll(String subscriberId);
    
    /**
     * Gets all active subscriptions for a topic.
     *
     * @param topic The topic to get subscriptions for
     * @return Set of active subscriptions (never null, may be empty)
     */
    Set<Subscription<T>> getSubscriptions(Topic topic);
    
    /**
     * Gets all subscriptions for a specific subscriber.
     *
     * @param subscriberId The subscriber ID
     * @return Set of subscriptions for this subscriber
     */
    Set<Subscription<T>> getSubscriptionsBySubscriber(String subscriberId);
    
    /**
     * Checks if a topic has any subscribers.
     *
     * @param topic The topic to check
     * @return true if topic has at least one active subscriber
     */
    boolean hasSubscribers(Topic topic);
    
    /**
     * Gets the total count of active subscriptions.
     *
     * @return Total subscription count
     */
    int getSubscriptionCount();
    
    /**
     * Gets all topics that have subscribers.
     *
     * @return Set of topics with active subscriptions
     */
    Set<Topic> getActiveTopics();
    
    /**
     * Clears all subscriptions.
     */
    void clear();
}



