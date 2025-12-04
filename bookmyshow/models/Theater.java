package bookmyshow.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a theater/cinema complex.
 */
public class Theater {
    private final String id;
    private String name;
    private String address;
    private String cityId;
    private String pincode;
    private final List<Screen> screens;
    private boolean isActive;

    public Theater(String name, String address, String cityId, String pincode) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.cityId = cityId;
        this.pincode = pincode;
        this.screens = new ArrayList<>();
        this.isActive = true;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCityId() { return cityId; }
    public String getPincode() { return pincode; }
    public List<Screen> getScreens() { return Collections.unmodifiableList(screens); }
    public boolean isActive() { return isActive; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setCityId(String cityId) { this.cityId = cityId; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public void setActive(boolean active) { this.isActive = active; }

    // Screen management
    public void addScreen(Screen screen) {
        screens.add(screen);
    }

    public void removeScreen(String screenId) {
        screens.removeIf(s -> s.getId().equals(screenId));
    }

    public Screen getScreenById(String screenId) {
        return screens.stream()
            .filter(s -> s.getId().equals(screenId))
            .findFirst()
            .orElse(null);
    }

    public int getTotalScreens() {
        return screens.size();
    }

    public int getTotalSeats() {
        return screens.stream()
            .mapToInt(Screen::getTotalSeats)
            .sum();
    }

    @Override
    public String toString() {
        return String.format("Theater{id='%s', name='%s', city='%s', screens=%d}", 
            id, name, cityId, screens.size());
    }
}



