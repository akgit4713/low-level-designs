package concertbooking.models;

import concertbooking.enums.SectionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a section within a venue (e.g., VIP, General, Balcony)
 */
public class Section {
    private final String id;
    private final String name;
    private final SectionType type;
    private final int rows;
    private final int seatsPerRow;
    private final List<Seat> seats;

    private Section(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.rows = builder.rows;
        this.seatsPerRow = builder.seatsPerRow;
        this.seats = new ArrayList<>(builder.seats);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SectionType getType() {
        return type;
    }

    public int getRows() {
        return rows;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public int getCapacity() {
        return seats.size();
    }

    public List<Seat> getSeats() {
        return Collections.unmodifiableList(seats);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Section{id='%s', name='%s', type=%s, capacity=%d}",
            id, name, type, getCapacity());
    }

    public static class Builder {
        private String id;
        private String name;
        private SectionType type = SectionType.GENERAL;
        private int rows;
        private int seatsPerRow;
        private final List<Seat> seats = new ArrayList<>();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(SectionType type) {
            this.type = type;
            return this;
        }

        public Builder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public Builder seatsPerRow(int seatsPerRow) {
            this.seatsPerRow = seatsPerRow;
            return this;
        }

        public Builder addSeat(Seat seat) {
            this.seats.add(seat);
            return this;
        }

        public Builder seats(List<Seat> seats) {
            this.seats.clear();
            this.seats.addAll(seats);
            return this;
        }

        public Section build() {
            Objects.requireNonNull(id, "Section ID is required");
            Objects.requireNonNull(name, "Section name is required");
            if (seats.isEmpty()) {
                throw new IllegalStateException("Section must have at least one seat");
            }
            return new Section(this);
        }
    }
}



