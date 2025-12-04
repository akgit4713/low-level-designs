package onlineshopping.models;

import onlineshopping.enums.UserRole;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a user (customer, seller, or admin)
 * Uses Builder pattern for construction
 */
public class User {
    private final String id;
    private final String email;
    private final String passwordHash;
    private final String name;
    private final UserRole role;
    private final LocalDateTime createdAt;
    private final List<Address> addresses;
    private String phoneNumber;
    private boolean isActive;

    private User(Builder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.name = builder.name;
        this.role = builder.role;
        this.phoneNumber = builder.phoneNumber;
        this.addresses = new ArrayList<>(builder.addresses);
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getName() {
        return name;
    }

    public UserRole getRole() {
        return role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Address> getAddresses() {
        return Collections.unmodifiableList(addresses);
    }

    public void addAddress(Address address) {
        addresses.add(address);
    }

    public void removeAddress(String addressId) {
        addresses.removeIf(a -> a.getId().equals(addressId));
    }

    public Optional<Address> getDefaultAddress() {
        return addresses.stream()
            .filter(Address::isDefault)
            .findFirst()
            .or(() -> addresses.isEmpty() ? Optional.empty() : Optional.of(addresses.get(0)));
    }

    public Optional<Address> getAddressById(String addressId) {
        return addresses.stream()
            .filter(a -> a.getId().equals(addressId))
            .findFirst();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean canSell() {
        return role.canSellProducts();
    }

    public boolean isAdmin() {
        return role.canManageSystem();
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
        return String.format("User{id='%s', name='%s', email='%s', role=%s}", id, name, email, role);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String email;
        private String passwordHash;
        private String name;
        private UserRole role = UserRole.CUSTOMER;
        private String phoneNumber;
        private List<Address> addresses = new ArrayList<>();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder addresses(List<Address> addresses) {
            this.addresses = new ArrayList<>(addresses);
            return this;
        }

        public Builder addAddress(Address address) {
            this.addresses.add(address);
            return this;
        }

        public User build() {
            Objects.requireNonNull(id, "User ID is required");
            Objects.requireNonNull(email, "Email is required");
            Objects.requireNonNull(name, "Name is required");
            return new User(this);
        }
    }
}



