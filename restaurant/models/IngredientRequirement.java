package restaurant.models;

import java.util.Objects;

/**
 * Represents the quantity of an ingredient required for a menu item
 */
public class IngredientRequirement {
    private final Ingredient ingredient;
    private final double quantity;

    public IngredientRequirement(Ingredient ingredient, double quantity) {
        this.ingredient = Objects.requireNonNull(ingredient, "Ingredient cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return String.format("%.2f %s of %s", quantity, ingredient.getUnit(), ingredient.getName());
    }
}

