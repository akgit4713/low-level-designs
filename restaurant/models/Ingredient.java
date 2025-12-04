package restaurant.models;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a raw ingredient used in menu items
 */
public class Ingredient {
    private final String id;
    private final String name;
    private final String unit; // kg, liters, pieces, etc.
    private final BigDecimal costPerUnit;

    public Ingredient(String id, String name, String unit, BigDecimal costPerUnit) {
        this.id = Objects.requireNonNull(id, "Ingredient ID cannot be null");
        this.name = Objects.requireNonNull(name, "Ingredient name cannot be null");
        this.unit = Objects.requireNonNull(unit, "Unit cannot be null");
        this.costPerUnit = Objects.requireNonNull(costPerUnit, "Cost per unit cannot be null");
        
        if (costPerUnit.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Cost per unit cannot be negative");
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getCostPerUnit() {
        return costPerUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Ingredient{id='%s', name='%s', unit='%s', costPerUnit=%s}", 
            id, name, unit, costPerUnit);
    }
}

