package pubsub.impl;

import pubsub.PubSubBroker;
import pubsub.interfaces.Publisher;
import pubsub.models.Message;
import pubsub.models.Topic;

import java.util.Objects;
import java.util.UUID;

/**
 * Default implementation of Publisher.
 * Publishes messages through a PubSubBroker.
 *
 * @param <T> The type of message payload
 */
public class DefaultPublisher<T> implements Publisher<T> {
    
    private final String id;
    private final PubSubBroker<T> broker;
    
    public DefaultPublisher(PubSubBroker<T> broker) {
        this(UUID.randomUUID().toString(), broker);
    }
    
    public DefaultPublisher(String id, PubSubBroker<T> broker) {
        this.id = Objects.requireNonNull(id, "Publisher ID cannot be null");
        this.broker = Objects.requireNonNull(broker, "Broker cannot be null");
    }
    
    @Override
    public Message<T> publish(Topic topic, T payload) {
        Message<T> message = Message.<T>builder()
                .topic(topic)
                .payload(payload)
                .publisherId(id)
                .build();
        
        broker.publish(message);
        return message;
    }
    
    @Override
    public Message<T> publish(String topicName, T payload) {
        return publish(new Topic(topicName), payload);
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return "DefaultPublisher{id='" + id + "'}";
    }
}



