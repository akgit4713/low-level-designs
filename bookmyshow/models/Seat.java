package bookmyshow.models;

import bookmyshow.enums.SeatType;
import java.util.UUID;

/**
 * Represents a physical seat in a theater screen.
 * This is the template/layout - actual availability is tracked in ShowSeat.
 */
public class Seat {
    private final String id;
    private final String screenId;
    private final String rowLabel;      // e.g., "A", "B", "C"
    private final int seatNumber;       // e.g., 1, 2, 3
    private final SeatType seatType;

    public Seat(String screenId, String rowLabel, int seatNumber, SeatType seatType) {
        this.id = UUID.randomUUID().toString();
        this.screenId = screenId;
        this.rowLabel = rowLabel;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
    }

    // Getters
    public String getId() { return id; }
    public String getScreenId() { return screenId; }
    public String getRowLabel() { return rowLabel; }
    public int getSeatNumber() { return seatNumber; }
    public SeatType getSeatType() { return seatType; }

    public String getSeatLabel() {
        return rowLabel + seatNumber;
    }

    @Override
    public String toString() {
        return String.format("Seat{%s, type=%s}", getSeatLabel(), seatType.getDisplayName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return id.equals(seat.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}



