package hotelmanagement.enums;

import java.math.BigDecimal;

/**
 * Enum representing additional hotel services that can be charged
 */
public enum ServiceType {
    ROOM_SERVICE("Room Service", new BigDecimal("0.00")),      // Variable price
    LAUNDRY("Laundry Service", new BigDecimal("25.00")),
    SPA("Spa Treatment", new BigDecimal("100.00")),
    MINIBAR("Mini Bar", new BigDecimal("0.00")),               // Variable price
    PARKING("Parking", new BigDecimal("20.00")),
    AIRPORT_TRANSFER("Airport Transfer", new BigDecimal("75.00")),
    BREAKFAST("Breakfast Buffet", new BigDecimal("35.00")),
    GYM("Gym Access", new BigDecimal("15.00")),
    POOL("Pool Access", new BigDecimal("10.00")),
    EXTRA_BED("Extra Bed", new BigDecimal("50.00"));

    private final String displayName;
    private final BigDecimal defaultRate;

    ServiceType(String displayName, BigDecimal defaultRate) {
        this.displayName = displayName;
        this.defaultRate = defaultRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BigDecimal getDefaultRate() {
        return defaultRate;
    }

    /**
     * Check if this service has a fixed rate
     */
    public boolean hasFixedRate() {
        return defaultRate.compareTo(BigDecimal.ZERO) > 0;
    }
}



