package restaurant.models;

import restaurant.enums.TableStatus;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a table in the restaurant
 * Thread-safe for concurrent reservation/status updates
 */
public class Table {
    private final String id;
    private final int tableNumber;
    private final int capacity;
    private final String location; // indoor, outdoor, private room, etc.
    private volatile TableStatus status;
    private final ReentrantLock lock = new ReentrantLock();

    public Table(String id, int tableNumber, int capacity, String location) {
        this.id = Objects.requireNonNull(id, "Table ID cannot be null");
        if (tableNumber <= 0) {
            throw new IllegalArgumentException("Table number must be positive");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.location = location != null ? location : "Indoor";
        this.status = TableStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getLocation() {
        return location;
    }

    public TableStatus getStatus() {
        return status;
    }

    /**
     * Attempts to reserve the table if it's available
     * @return true if reservation successful, false otherwise
     */
    public boolean tryReserve() {
        lock.lock();
        try {
            if (status == TableStatus.AVAILABLE) {
                status = TableStatus.RESERVED;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Attempts to occupy the table
     * @return true if occupation successful, false otherwise
     */
    public boolean tryOccupy() {
        lock.lock();
        try {
            if (status == TableStatus.AVAILABLE || status == TableStatus.RESERVED) {
                status = TableStatus.OCCUPIED;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Marks table for cleaning after guests leave
     */
    public void markForCleaning() {
        lock.lock();
        try {
            status = TableStatus.CLEANING;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Marks table as available after cleaning
     */
    public void markAvailable() {
        lock.lock();
        try {
            status = TableStatus.AVAILABLE;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Releases a reservation
     */
    public void releaseReservation() {
        lock.lock();
        try {
            if (status == TableStatus.RESERVED) {
                status = TableStatus.AVAILABLE;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean canAccommodate(int partySize) {
        return partySize <= capacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(id, table.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Table{id='%s', number=%d, capacity=%d, location='%s', status=%s}",
            id, tableNumber, capacity, location, status);
    }
}

