package pubsub.impl;

import pubsub.interfaces.SubscriptionManager;
import pubsub.models.Subscription;
import pubsub.models.Topic;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe implementation of SubscriptionManager using concurrent collections.
 * 
 * Uses ConcurrentHashMap for topic-to-subscriptions mapping.
 * All operations are atomic and thread-safe.
 *
 * @param <T> The type of message payload
 */
public class ConcurrentSubscriptionManager<T> implements SubscriptionManager<T> {
    
    // Topic -> Set of Subscriptions
    private final ConcurrentHashMap<Topic, Set<Subscription<T>>> topicSubscriptions;
    
    // SubscriptionId -> Subscription (for O(1) removal by ID)
    private final ConcurrentHashMap<String, Subscription<T>> subscriptionById;
    
    // SubscriberId -> Set of SubscriptionIds (for removing subscriber from all topics)
    private final ConcurrentHashMap<String, Set<String>> subscriberSubscriptions;
    
    public ConcurrentSubscriptionManager() {
        this.topicSubscriptions = new ConcurrentHashMap<>();
        this.subscriptionById = new ConcurrentHashMap<>();
        this.subscriberSubscriptions = new ConcurrentHashMap<>();
    }
    
    @Override
    public boolean addSubscription(Subscription<T> subscription) {
        String subscriptionId = subscription.getId();
        
        // Atomic putIfAbsent to prevent duplicate subscriptions
        if (subscriptionById.putIfAbsent(subscriptionId, subscription) != null) {
            return false; // Already exists
        }
        
        // Add to topic subscriptions
        topicSubscriptions
                .computeIfAbsent(subscription.getTopic(), k -> ConcurrentHashMap.newKeySet())
                .add(subscription);
        
        // Track subscriber's subscriptions
        subscriberSubscriptions
                .computeIfAbsent(subscription.getSubscriber().getId(), k -> ConcurrentHashMap.newKeySet())
                .add(subscriptionId);
        
        return true;
    }
    
    @Override
    public Optional<Subscription<T>> removeSubscription(String subscriptionId) {
        Subscription<T> subscription = subscriptionById.remove(subscriptionId);
        
        if (subscription == null) {
            return Optional.empty();
        }
        
        // Remove from topic subscriptions
        Set<Subscription<T>> topicSubs = topicSubscriptions.get(subscription.getTopic());
        if (topicSubs != null) {
            topicSubs.remove(subscription);
            // Clean up empty topic sets
            if (topicSubs.isEmpty()) {
                topicSubscriptions.remove(subscription.getTopic(), Collections.emptySet());
            }
        }
        
        // Remove from subscriber tracking
        Set<String> subIds = subscriberSubscriptions.get(subscription.getSubscriber().getId());
        if (subIds != null) {
            subIds.remove(subscriptionId);
            if (subIds.isEmpty()) {
                subscriberSubscriptions.remove(subscription.getSubscriber().getId());
            }
        }
        
        subscription.deactivate();
        return Optional.of(subscription);
    }
    
    @Override
    public Set<Subscription<T>> removeSubscriberFromAll(String subscriberId) {
        Set<String> subscriptionIds = subscriberSubscriptions.remove(subscriberId);
        
        if (subscriptionIds == null || subscriptionIds.isEmpty()) {
            return Collections.emptySet();
        }
        
        Set<Subscription<T>> removed = new HashSet<>();
        for (String subId : new HashSet<>(subscriptionIds)) {
            removeSubscription(subId).ifPresent(removed::add);
        }
        
        return removed;
    }
    
    @Override
    public Set<Subscription<T>> getSubscriptions(Topic topic) {
        Set<Subscription<T>> subs = topicSubscriptions.get(topic);
        if (subs == null || subs.isEmpty()) {
            return Collections.emptySet();
        }
        
        // Return only active subscriptions
        return subs.stream()
                .filter(Subscription::isActive)
                .collect(Collectors.toUnmodifiableSet());
    }
    
    @Override
    public Set<Subscription<T>> getSubscriptionsBySubscriber(String subscriberId) {
        Set<String> subscriptionIds = subscriberSubscriptions.get(subscriberId);
        
        if (subscriptionIds == null || subscriptionIds.isEmpty()) {
            return Collections.emptySet();
        }
        
        return subscriptionIds.stream()
                .map(subscriptionById::get)
                .filter(Objects::nonNull)
                .filter(Subscription::isActive)
                .collect(Collectors.toUnmodifiableSet());
    }
    
    @Override
    public boolean hasSubscribers(Topic topic) {
        Set<Subscription<T>> subs = topicSubscriptions.get(topic);
        return subs != null && subs.stream().anyMatch(Subscription::isActive);
    }
    
    @Override
    public int getSubscriptionCount() {
        return (int) subscriptionById.values().stream()
                .filter(Subscription::isActive)
                .count();
    }
    
    @Override
    public Set<Topic> getActiveTopics() {
        return topicSubscriptions.entrySet().stream()
                .filter(e -> e.getValue().stream().anyMatch(Subscription::isActive))
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }
    
    @Override
    public void clear() {
        subscriptionById.values().forEach(Subscription::deactivate);
        topicSubscriptions.clear();
        subscriptionById.clear();
        subscriberSubscriptions.clear();
    }
}



