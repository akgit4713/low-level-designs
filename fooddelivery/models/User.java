package fooddelivery.models;

import fooddelivery.enums.UserRole;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Base user entity representing customers in the system.
 */
public class User {
    private final String id;
    private String name;
    private String email;
    private String phone;
    private final UserRole role;
    private final List<Location> savedAddresses;
    private Location defaultAddress;
    private final LocalDateTime createdAt;
    private boolean active;

    public User(String id, String name, String email, String phone, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.savedAddresses = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.active = true;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public List<Location> getSavedAddresses() {
        return new ArrayList<>(savedAddresses);
    }

    public void addAddress(Location address) {
        savedAddresses.add(address);
        if (defaultAddress == null) {
            defaultAddress = address;
        }
    }

    public Location getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(Location defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', role=%s}", id, name, role);
    }
}



