package carrental.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a customer in the car rental system.
 */
public class Customer {
    private final String id;
    private final String name;
    private final String email;
    private final String phone;
    private final DriverLicense driverLicense;
    private final LocalDate registrationDate;

    private Customer(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.phone = builder.phone;
        this.driverLicense = builder.driverLicense;
        this.registrationDate = builder.registrationDate;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public DriverLicense getDriverLicense() { return driverLicense; }
    public LocalDate getRegistrationDate() { return registrationDate; }

    public boolean hasValidLicense() {
        return driverLicense != null && driverLicense.isValid();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Customer{id='%s', name='%s', email='%s', phone='%s'}", 
            id, name, email, phone);
    }

    // Builder Pattern
    public static class Builder {
        private String id;
        private String name;
        private String email;
        private String phone;
        private DriverLicense driverLicense;
        private LocalDate registrationDate = LocalDate.now();

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder driverLicense(DriverLicense driverLicense) { this.driverLicense = driverLicense; return this; }
        public Builder registrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; return this; }

        public Customer build() {
            Objects.requireNonNull(id, "Customer ID cannot be null");
            Objects.requireNonNull(name, "Customer name cannot be null");
            Objects.requireNonNull(email, "Customer email cannot be null");
            Objects.requireNonNull(phone, "Customer phone cannot be null");
            return new Customer(this);
        }
    }
}



