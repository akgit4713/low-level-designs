package pubsub.models;

import java.util.Objects;

/**
 * Immutable value object representing a named topic/channel.
 * Topics are identified by their unique name.
 */
public final class Topic {
    
    private final String name;
    
    public Topic(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Topic name cannot be null or blank");
        }
        this.name = name.trim();
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return Objects.equals(name, topic.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return "Topic{name='" + name + "'}";
    }
}



