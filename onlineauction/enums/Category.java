package onlineauction.enums;

/**
 * Predefined categories for auction items.
 * Extensible by adding new enum values.
 */
public enum Category {
    ELECTRONICS("Electronics & Gadgets"),
    FASHION("Fashion & Apparel"),
    HOME_GARDEN("Home & Garden"),
    SPORTS("Sports & Outdoors"),
    COLLECTIBLES("Collectibles & Antiques"),
    VEHICLES("Vehicles & Parts"),
    ART("Art & Crafts"),
    BOOKS("Books & Media"),
    JEWELRY("Jewelry & Watches"),
    TOYS("Toys & Games"),
    OTHER("Other");
    
    private final String displayName;
    
    Category(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Find category by display name (case-insensitive)
     */
    public static Category fromDisplayName(String displayName) {
        for (Category category : values()) {
            if (category.displayName.equalsIgnoreCase(displayName)) {
                return category;
            }
        }
        return OTHER;
    }
}



