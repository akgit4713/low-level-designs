package hotelmanagement.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a hotel guest with personal information and loyalty status
 */
public class Guest {
    private final String id;
    private final String name;
    private final String email;
    private final String phone;
    private final String idType;      // Passport, Driver's License, etc.
    private final String idNumber;
    private final LocalDateTime registeredAt;
    
    private Address address;
    private int loyaltyPoints;
    private int totalStays;
    private String specialRequests;

    private Guest(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.phone = builder.phone;
        this.idType = builder.idType;
        this.idNumber = builder.idNumber;
        this.address = builder.address;
        this.loyaltyPoints = builder.loyaltyPoints;
        this.totalStays = builder.totalStays;
        this.specialRequests = builder.specialRequests;
        this.registeredAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getIdType() {
        return idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public int getTotalStays() {
        return totalStays;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    /**
     * Add loyalty points for the guest
     */
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }

    /**
     * Increment stay count
     */
    public void incrementStayCount() {
        this.totalStays++;
    }

    /**
     * Use loyalty points (for discounts, etc.)
     * @return true if points were successfully used
     */
    public boolean useLoyaltyPoints(int points) {
        if (points <= loyaltyPoints) {
            loyaltyPoints -= points;
            return true;
        }
        return false;
    }

    /**
     * Get loyalty tier based on total stays
     */
    public String getLoyaltyTier() {
        if (totalStays >= 50) return "Platinum";
        if (totalStays >= 25) return "Gold";
        if (totalStays >= 10) return "Silver";
        if (totalStays >= 5) return "Bronze";
        return "Member";
    }

    /**
     * Get discount percentage based on loyalty tier
     */
    public int getLoyaltyDiscountPercent() {
        return switch (getLoyaltyTier()) {
            case "Platinum" -> 15;
            case "Gold" -> 10;
            case "Silver" -> 7;
            case "Bronze" -> 5;
            default -> 0;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guest guest = (Guest) o;
        return Objects.equals(id, guest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Guest{id='%s', name='%s', email='%s', tier='%s', stays=%d}",
            id, name, email, getLoyaltyTier(), totalStays);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String name;
        private String email;
        private String phone;
        private String idType;
        private String idNumber;
        private Address address;
        private int loyaltyPoints = 0;
        private int totalStays = 0;
        private String specialRequests = "";

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder idType(String idType) {
            this.idType = idType;
            return this;
        }

        public Builder idNumber(String idNumber) {
            this.idNumber = idNumber;
            return this;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public Builder loyaltyPoints(int loyaltyPoints) {
            this.loyaltyPoints = loyaltyPoints;
            return this;
        }

        public Builder totalStays(int totalStays) {
            this.totalStays = totalStays;
            return this;
        }

        public Builder specialRequests(String specialRequests) {
            this.specialRequests = specialRequests;
            return this;
        }

        public Guest build() {
            if (id == null || id.isBlank()) {
                id = "GUEST-" + UUID.randomUUID().toString().substring(0, 8);
            }
            Objects.requireNonNull(name, "Guest name is required");
            Objects.requireNonNull(email, "Guest email is required");
            Objects.requireNonNull(phone, "Guest phone is required");
            
            return new Guest(this);
        }
    }
}



