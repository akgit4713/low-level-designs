package digitalwallet.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported currencies in the digital wallet system.
 * Each currency has a code, symbol, and decimal places for precision.
 */
public enum Currency {
    USD("US Dollar", "$", 2),
    EUR("Euro", "€", 2),
    GBP("British Pound", "£", 2),
    INR("Indian Rupee", "₹", 2),
    JPY("Japanese Yen", "¥", 0),
    CAD("Canadian Dollar", "C$", 2),
    AUD("Australian Dollar", "A$", 2),
    CHF("Swiss Franc", "CHF", 2),
    CNY("Chinese Yuan", "¥", 2),
    SGD("Singapore Dollar", "S$", 2);

    private final String displayName;
    private final String symbol;
    private final int decimalPlaces;

    private static final Map<String, Currency> BY_CODE = new HashMap<>();

    static {
        for (Currency currency : values()) {
            BY_CODE.put(currency.name(), currency);
        }
    }

    Currency(String displayName, String symbol, int decimalPlaces) {
        this.displayName = displayName;
        this.symbol = symbol;
        this.decimalPlaces = decimalPlaces;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getDecimalPlaces() {
        return decimalPlaces;
    }

    /**
     * Get currency by code (case-insensitive)
     */
    public static Currency fromCode(String code) {
        Currency currency = BY_CODE.get(code.toUpperCase());
        if (currency == null) {
            throw new IllegalArgumentException("Unknown currency code: " + code);
        }
        return currency;
    }

    /**
     * Format an amount with currency symbol
     */
    public String format(java.math.BigDecimal amount) {
        return String.format("%s%,."+decimalPlaces+"f", symbol, amount);
    }
}



