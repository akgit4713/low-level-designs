package fooddelivery.models;

import fooddelivery.enums.CuisineType;
import fooddelivery.enums.MenuItemStatus;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a single item in a restaurant's menu.
 */
public class MenuItem {
    private final String id;
    private final String restaurantId;
    private String name;
    private String description;
    private BigDecimal price;
    private CuisineType cuisineType;
    private MenuItemStatus status;
    private boolean vegetarian;
    private int preparationTimeMinutes;
    private String imageUrl;

    public MenuItem(String id, String restaurantId, String name, String description, 
                    BigDecimal price, CuisineType cuisineType) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.cuisineType = cuisineType;
        this.status = MenuItemStatus.AVAILABLE;
        this.vegetarian = false;
        this.preparationTimeMinutes = 15;
    }

    public String getId() {
        return id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public CuisineType getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(CuisineType cuisineType) {
        this.cuisineType = cuisineType;
    }

    public MenuItemStatus getStatus() {
        return status;
    }

    public void setStatus(MenuItemStatus status) {
        this.status = status;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    public int getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public void setPreparationTimeMinutes(int preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAvailable() {
        return status == MenuItemStatus.AVAILABLE;
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
        return String.format("MenuItem{id='%s', name='%s', price=%s, status=%s}", 
            id, name, price, status);
    }
}



