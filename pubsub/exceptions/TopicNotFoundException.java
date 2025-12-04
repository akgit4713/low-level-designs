package pubsub.exceptions;

import pubsub.models.Topic;

/**
 * Exception thrown when an operation references a non-existent topic.
 */
public class TopicNotFoundException extends PubSubException {
    
    private final String topicName;
    
    public TopicNotFoundException(String topicName) {
        super("Topic not found: " + topicName);
        this.topicName = topicName;
    }
    
    public TopicNotFoundException(Topic topic) {
        this(topic.getName());
    }
    
    public String getTopicName() {
        return topicName;
    }
}



