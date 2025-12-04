package restaurant.models;

import restaurant.enums.MenuCategory;
import java.math.BigDecimal;
import java.util.*;

/**
 * Represents a menu item available for ordering
 */
public class MenuItem {
    private final String id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final MenuCategory category;
    private final List<IngredientRequirement> ingredients;
    private final int preparationTimeMinutes;
    private final boolean vegetarian;
    private boolean available;

    private MenuItem(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.category = builder.category;
        this.ingredients = Collections.unmodifiableList(new ArrayList<>(builder.ingredients));
        this.preparationTimeMinutes = builder.preparationTimeMinutes;
        this.vegetarian = builder.vegetarian;
        this.available = builder.available;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public MenuCategory getCategory() {
        return category;
    }

    public List<IngredientRequirement> getIngredients() {
        return ingredients;
    }

    public int getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MenuItem menuItem = (MenuItem) o;
        return Objects.equals(id, menuItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("MenuItem{id='%s', name='%s', price=%s, category=%s, available=%s}",
            id, name, price, category, available);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String description = "";
        private BigDecimal price;
        private MenuCategory category;
        private List<IngredientRequirement> ingredients = new ArrayList<>();
        private int preparationTimeMinutes = 15;
        private boolean vegetarian = false;
        private boolean available = true;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder category(MenuCategory category) {
            this.category = category;
            return this;
        }

        public Builder addIngredient(IngredientRequirement ingredient) {
            this.ingredients.add(ingredient);
            return this;
        }

        public Builder ingredients(List<IngredientRequirement> ingredients) {
            this.ingredients = new ArrayList<>(ingredients);
            return this;
        }

        public Builder preparationTimeMinutes(int minutes) {
            this.preparationTimeMinutes = minutes;
            return this;
        }

        public Builder vegetarian(boolean vegetarian) {
            this.vegetarian = vegetarian;
            return this;
        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        public MenuItem build() {
            Objects.requireNonNull(id, "MenuItem ID is required");
            Objects.requireNonNull(name, "MenuItem name is required");
            Objects.requireNonNull(price, "MenuItem price is required");
            Objects.requireNonNull(category, "MenuItem category is required");
            
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            
            return new MenuItem(this);
        }
    }
}

