package airline.models;

import airline.enums.BaggageType;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a piece of baggage with type, weight, and tracking info.
 */
public class Baggage {
    private final String id;
    private final String tagNumber;
    private final BaggageType type;
    private final double weight; // in kg
    private final BigDecimal extraCharge;

    public Baggage(BaggageType type, double weight) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.tagNumber = "BAG-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.type = type;
        this.weight = weight;
        this.extraCharge = calculateExtraCharge(type, weight);
    }

    private BigDecimal calculateExtraCharge(BaggageType type, double weight) {
        double limit = type.getDefaultWeightLimit();
        if (weight <= limit) {
            return BigDecimal.ZERO;
        }
        // $10 per extra kg
        double extraWeight = weight - limit;
        return BigDecimal.valueOf(extraWeight * 10);
    }

    public String getId() {
        return id;
    }

    public String getTagNumber() {
        return tagNumber;
    }

    public BaggageType getType() {
        return type;
    }

    public double getWeight() {
        return weight;
    }

    public BigDecimal getExtraCharge() {
        return extraCharge;
    }

    public boolean isOverweight() {
        return weight > type.getDefaultWeightLimit();
    }

    @Override
    public String toString() {
        return String.format("Baggage[%s | %s | %.1f kg%s]",
                tagNumber, type.getDisplayName(), weight,
                isOverweight() ? " (Overweight: +$" + extraCharge + ")" : "");
    }
}



