package restaurant.enums;

/**
 * Categories of menu items
 */
public enum MenuCategory {
    APPETIZER("Appetizers & Starters"),
    MAIN_COURSE("Main Course"),
    DESSERT("Desserts"),
    BEVERAGE("Drinks & Beverages"),
    SIDE("Side Dishes"),
    SPECIAL("Chef's Special");

    private final String displayName;

    MenuCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

