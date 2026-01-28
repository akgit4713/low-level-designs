package packagelocker.models;

import packagelocker.enums.CompartmentSize;

import java.util.Objects;

/**
 * Represents a physical compartment in the locker.
 */
public class Compartment {
    
    private final String id;
    private final int number;
    private final CompartmentSize size;
    private boolean occupied;

    public Compartment(String id, int number, CompartmentSize size) {
        this.id = Objects.requireNonNull(id, "Compartment ID cannot be null");
        this.number = number;
        this.size = Objects.requireNonNull(size, "Compartment size cannot be null");
        this.occupied = false;
    }

    public String getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public CompartmentSize getSize() {
        return size;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public boolean isAvailable() {
        return !occupied;
    }

    public void occupy() {
        if (occupied) {
            throw new IllegalStateException("Compartment " + number + " is already occupied");
        }
        this.occupied = true;
    }

    public void release() {
        if (!occupied) {
            throw new IllegalStateException("Compartment " + number + " is not occupied");
        }
        this.occupied = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compartment that = (Compartment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Compartment{number=%d, size=%s, occupied=%s}", 
                number, size.getDisplayName(), occupied);
    }
}
