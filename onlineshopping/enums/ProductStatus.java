package onlineshopping.enums;

/**
 * Product availability status
 */
public enum ProductStatus {
    ACTIVE,
    INACTIVE,
    OUT_OF_STOCK,
    DISCONTINUED;

    public boolean isAvailable() {
        return this == ACTIVE;
    }

    public boolean canBePurchased() {
        return this == ACTIVE;
    }
}



