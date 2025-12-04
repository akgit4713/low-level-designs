package fooddelivery.models;

import fooddelivery.enums.CuisineType;
import fooddelivery.enums.RestaurantStatus;
import java.time.LocalTime;
import java.util.*;

/**
 * Restaurant entity with menu management and availability status.
 */
public class Restaurant {
    private final String id;
    private String name;
    private String ownerId;
    private Location location;
    private RestaurantStatus status;
    private final Set<CuisineType> cuisineTypes;
    private final Map<String, MenuItem> menu;
    private double rating;
    private int totalRatings;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private int avgDeliveryTimeMinutes;
    private double minimumOrderAmount;
    private boolean acceptingOrders;

    public Restaurant(String id, String name, String ownerId, Location location) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.location = location;
        this.status = RestaurantStatus.CLOSED;
        this.cuisineTypes = new HashSet<>();
        this.menu = new HashMap<>();
        this.rating = 0.0;
        this.totalRatings = 0;
        this.openingTime = LocalTime.of(9, 0);
        this.closingTime = LocalTime.of(23, 0);
        this.avgDeliveryTimeMinutes = 30;
        this.minimumOrderAmount = 0;
        this.acceptingOrders = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public RestaurantStatus getStatus() {
        return status;
    }

    public void setStatus(RestaurantStatus status) {
        this.status = status;
    }

    public Set<CuisineType> getCuisineTypes() {
        return new HashSet<>(cuisineTypes);
    }

    public void addCuisineType(CuisineType type) {
        cuisineTypes.add(type);
    }

    // Menu Management
    public void addMenuItem(MenuItem item) {
        menu.put(item.getId(), item);
        cuisineTypes.add(item.getCuisineType());
    }

    public void removeMenuItem(String itemId) {
        menu.remove(itemId);
    }

    public MenuItem getMenuItem(String itemId) {
        return menu.get(itemId);
    }

    public List<MenuItem> getMenu() {
        return new ArrayList<>(menu.values());
    }

    public List<MenuItem> getAvailableItems() {
        return menu.values().stream()
                .filter(MenuItem::isAvailable)
                .toList();
    }

    // Rating Management
    public double getRating() {
        return rating;
    }

    public void addRating(double newRating) {
        this.rating = ((this.rating * totalRatings) + newRating) / (totalRatings + 1);
        this.totalRatings++;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    // Operating Hours
    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public boolean isOpenNow() {
        if (status != RestaurantStatus.OPEN) return false;
        LocalTime now = LocalTime.now();
        return !now.isBefore(openingTime) && !now.isAfter(closingTime);
    }

    public int getAvgDeliveryTimeMinutes() {
        return avgDeliveryTimeMinutes;
    }

    public void setAvgDeliveryTimeMinutes(int avgDeliveryTimeMinutes) {
        this.avgDeliveryTimeMinutes = avgDeliveryTimeMinutes;
    }

    public double getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(double minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public boolean isAcceptingOrders() {
        return acceptingOrders && status == RestaurantStatus.OPEN;
    }

    public void setAcceptingOrders(boolean acceptingOrders) {
        this.acceptingOrders = acceptingOrders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Restaurant that = (Restaurant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Restaurant{id='%s', name='%s', status=%s, rating=%.1f}", 
            id, name, status, rating);
    }
}



