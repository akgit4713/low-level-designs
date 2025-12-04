package restaurant.enums;

/**
 * Type of order - extensible for future delivery options
 */
public enum OrderType {
    DINE_IN("Dine-in at restaurant"),
    TAKEOUT("Customer picks up"),
    DELIVERY("Delivered to customer");

    private final String description;

    OrderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

