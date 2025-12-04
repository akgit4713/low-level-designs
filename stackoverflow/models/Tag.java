package stackoverflow.models;

import java.util.Objects;

/**
 * Represents a tag/category for questions.
 */
public class Tag {
    private final String id;
    private final String name;

    public Tag(String name) {
        this.id = name.toLowerCase().replace(" ", "-");
        this.name = name.toLowerCase();
    }

    public String getId() { return id; }
    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return name.equals(tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "[" + name + "]";
    }
}



