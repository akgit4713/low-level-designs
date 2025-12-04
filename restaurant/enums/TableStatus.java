package restaurant.enums;

/**
 * Current status of a restaurant table
 */
public enum TableStatus {
    AVAILABLE("Table is available for seating"),
    OCCUPIED("Table is currently occupied"),
    RESERVED("Table is reserved for upcoming booking"),
    CLEANING("Table is being cleaned");

    private final String description;

    TableStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

