package bookmyshow.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a screen/auditorium in a theater.
 */
public class Screen {
    private final String id;
    private final String theaterId;
    private String name;
    private int totalSeats;
    private final List<Seat> seats;
    private boolean isActive;

    public Screen(String theaterId, String name) {
        this.id = UUID.randomUUID().toString();
        this.theaterId = theaterId;
        this.name = name;
        this.seats = new ArrayList<>();
        this.totalSeats = 0;
        this.isActive = true;
    }

    // Getters
    public String getId() { return id; }
    public String getTheaterId() { return theaterId; }
    public String getName() { return name; }
    public int getTotalSeats() { return totalSeats; }
    public List<Seat> getSeats() { return Collections.unmodifiableList(seats); }
    public boolean isActive() { return isActive; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setActive(boolean active) { this.isActive = active; }

    // Seat management
    public void addSeat(Seat seat) {
        seats.add(seat);
        totalSeats = seats.size();
    }

    public void addSeats(List<Seat> newSeats) {
        seats.addAll(newSeats);
        totalSeats = seats.size();
    }

    public Seat getSeatById(String seatId) {
        return seats.stream()
            .filter(s -> s.getId().equals(seatId))
            .findFirst()
            .orElse(null);
    }

    public List<Seat> getSeatsByRow(String rowLabel) {
        return seats.stream()
            .filter(s -> s.getRowLabel().equals(rowLabel))
            .toList();
    }

    @Override
    public String toString() {
        return String.format("Screen{id='%s', name='%s', totalSeats=%d}", id, name, totalSeats);
    }
}



