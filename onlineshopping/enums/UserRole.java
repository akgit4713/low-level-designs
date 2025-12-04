package onlineshopping.enums;

/**
 * User roles in the system
 */
public enum UserRole {
    CUSTOMER,
    SELLER,
    ADMIN;

    public boolean canSellProducts() {
        return this == SELLER || this == ADMIN;
    }

    public boolean canManageSystem() {
        return this == ADMIN;
    }
}



