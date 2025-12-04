package onlineshopping.enums;

/**
 * Available shipping methods with estimated delivery days
 */
public enum ShippingMethod {
    STANDARD("Standard Delivery", 5, 7),
    EXPRESS("Express Delivery", 2, 3),
    SAME_DAY("Same Day Delivery", 0, 1);

    private final String displayName;
    private final int minDays;
    private final int maxDays;

    ShippingMethod(String displayName, int minDays, int maxDays) {
        this.displayName = displayName;
        this.minDays = minDays;
        this.maxDays = maxDays;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinDays() {
        return minDays;
    }

    public int getMaxDays() {
        return maxDays;
    }

    public String getEstimatedDelivery() {
        if (minDays == maxDays) {
            return minDays == 0 ? "Today" : minDays + " day";
        }
        return minDays + "-" + maxDays + " days";
    }
}



