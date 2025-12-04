package restaurant.enums;

/**
 * Staff roles in the restaurant
 */
public enum StaffRole {
    MANAGER("Restaurant Manager", 1.5),
    CHEF("Kitchen Chef", 1.3),
    SOUS_CHEF("Assistant Chef", 1.2),
    WAITER("Server/Waiter", 1.0),
    HOST("Front Desk Host", 1.0),
    CASHIER("Cashier", 1.0),
    BUSSER("Table Cleaner", 0.9);

    private final String title;
    private final double payMultiplier;

    StaffRole(String title, double payMultiplier) {
        this.title = title;
        this.payMultiplier = payMultiplier;
    }

    public String getTitle() {
        return title;
    }

    public double getPayMultiplier() {
        return payMultiplier;
    }
}

