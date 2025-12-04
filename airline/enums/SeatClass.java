package airline.enums;

import java.math.BigDecimal;

/**
 * Represents different classes of seats with price multipliers.
 */
public enum SeatClass {
    ECONOMY("Economy Class", new BigDecimal("1.0")),
    PREMIUM_ECONOMY("Premium Economy", new BigDecimal("1.5")),
    BUSINESS("Business Class", new BigDecimal("2.5")),
    FIRST("First Class", new BigDecimal("4.0"));

    private final String displayName;
    private final BigDecimal priceMultiplier;

    SeatClass(String displayName, BigDecimal priceMultiplier) {
        this.displayName = displayName;
        this.priceMultiplier = priceMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getPriceMultiplier() {
        return priceMultiplier;
    }
}



