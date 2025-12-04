package pubsub.impl;

import pubsub.models.Topic;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Registry for managing Topic instances.
 * Ensures single Topic instance per topic name (flyweight pattern).
 * Thread-safe using ConcurrentHashMap.
 */
public class TopicRegistry {
    
    private final ConcurrentHashMap<String, Topic> topics;
    
    public TopicRegistry() {
        this.topics = new ConcurrentHashMap<>();
    }
    
    /**
     * Gets or creates a topic by name.
     *
     * @param topicName The topic name
     * @return The Topic instance
     */
    public Topic getOrCreate(String topicName) {
        return topics.computeIfAbsent(topicName, Topic::new);
    }
    
    /**
     * Gets a topic by name if it exists.
     *
     * @param topicName The topic name
     * @return Optional containing the topic if found
     */
    public Optional<Topic> get(String topicName) {
        return Optional.ofNullable(topics.get(topicName));
    }
    
    /**
     * Checks if a topic exists in the registry.
     *
     * @param topicName The topic name
     * @return true if the topic exists
     */
    public boolean exists(String topicName) {
        return topics.containsKey(topicName);
    }
    
    /**
     * Removes a topic from the registry.
     *
     * @param topicName The topic name to remove
     * @return The removed topic if it existed
     */
    public Optional<Topic> remove(String topicName) {
        return Optional.ofNullable(topics.remove(topicName));
    }
    
    /**
     * Gets all registered topics.
     *
     * @return Unmodifiable set of all topics
     */
    public Set<Topic> getAllTopics() {
        return topics.values().stream()
                .collect(Collectors.toUnmodifiableSet());
    }
    
    /**
     * Gets all registered topic names.
     *
     * @return Unmodifiable set of topic names
     */
    public Set<String> getAllTopicNames() {
        return topics.keySet().stream()
                .collect(Collectors.toUnmodifiableSet());
    }
    
    /**
     * Gets the count of registered topics.
     *
     * @return Number of topics
     */
    public int size() {
        return topics.size();
    }
    
    /**
     * Clears all topics from the registry.
     */
    public void clear() {
        topics.clear();
    }
}



