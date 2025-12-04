package pubsub.interfaces;

import pubsub.models.Message;
import pubsub.models.Topic;

/**
 * Interface for message publishers.
 * Defines the contract for publishing messages to topics.
 *
 * @param <T> The type of message payload this publisher handles
 */
public interface Publisher<T> {
    
    /**
     * Publishes a message to the specified topic.
     *
     * @param topic The topic to publish to
     * @param payload The message payload
     * @return The published message with generated ID and timestamp
     */
    Message<T> publish(Topic topic, T payload);
    
    /**
     * Publishes a message to the specified topic by name.
     *
     * @param topicName The topic name to publish to
     * @param payload The message payload
     * @return The published message with generated ID and timestamp
     */
    Message<T> publish(String topicName, T payload);
    
    /**
     * Returns the unique identifier for this publisher.
     *
     * @return Publisher ID
     */
    String getId();
}



