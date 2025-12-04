package hotelmanagement.enums;

import java.math.BigDecimal;

/**
 * Enum representing different types of hotel rooms with their base multipliers
 */
public enum RoomType {
    SINGLE(1, new BigDecimal("1.0"), "Single Room - 1 bed"),
    DOUBLE(2, new BigDecimal("1.5"), "Double Room - 2 beds or 1 queen"),
    DELUXE(2, new BigDecimal("2.0"), "Deluxe Room - Premium amenities"),
    SUITE(4, new BigDecimal("3.0"), "Suite - Living area + bedroom");

    private final int defaultCapacity;
    private final BigDecimal priceMultiplier;
    private final String description;

    RoomType(int defaultCapacity, BigDecimal priceMultiplier, String description) {
        this.defaultCapacity = defaultCapacity;
        this.priceMultiplier = priceMultiplier;
        this.description = description;
    }

    public int getDefaultCapacity() {
        return defaultCapacity;
    }

    public BigDecimal getPriceMultiplier() {
        return priceMultiplier;
    }

    public String getDescription() {
        return description;
    }
}



