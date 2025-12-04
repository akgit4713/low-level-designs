package concertbooking.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a concert venue
 */
public class Venue {
    private final String id;
    private final String name;
    private final String address;
    private final String city;
    private final int totalCapacity;
    private final List<Section> sections;

    private Venue(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.address = builder.address;
        this.city = builder.city;
        this.sections = new ArrayList<>(builder.sections);
        this.totalCapacity = calculateTotalCapacity();
    }

    private int calculateTotalCapacity() {
        return sections.stream()
            .mapToInt(Section::getCapacity)
            .sum();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venue venue = (Venue) o;
        return Objects.equals(id, venue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Venue{id='%s', name='%s', city='%s', capacity=%d}",
            id, name, city, totalCapacity);
    }

    public static class Builder {
        private String id;
        private String name;
        private String address;
        private String city;
        private final List<Section> sections = new ArrayList<>();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder addSection(Section section) {
            this.sections.add(section);
            return this;
        }

        public Builder sections(List<Section> sections) {
            this.sections.clear();
            this.sections.addAll(sections);
            return this;
        }

        public Venue build() {
            Objects.requireNonNull(id, "Venue ID is required");
            Objects.requireNonNull(name, "Venue name is required");
            Objects.requireNonNull(city, "City is required");
            if (sections.isEmpty()) {
                throw new IllegalStateException("Venue must have at least one section");
            }
            return new Venue(this);
        }
    }
}



