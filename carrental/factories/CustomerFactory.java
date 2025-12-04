package carrental.factories;

import carrental.models.Customer;
import carrental.models.DriverLicense;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Factory for creating Customer instances.
 */
public class CustomerFactory {

    /**
     * Creates a new customer with a generated ID.
     */
    public static Customer createCustomer(String name, String email, String phone, 
                                           DriverLicense driverLicense) {
        return new Customer.Builder()
            .id(UUID.randomUUID().toString())
            .name(name)
            .email(email)
            .phone(phone)
            .driverLicense(driverLicense)
            .build();
    }

    /**
     * Creates a customer with a new driver license.
     */
    public static Customer createCustomer(String name, String email, String phone,
                                           String licenseNumber, String licenseState,
                                           LocalDate licenseExpiry) {
        DriverLicense license = new DriverLicense(
            licenseNumber,
            licenseState,
            LocalDate.now().minusYears(2),
            licenseExpiry
        );
        return createCustomer(name, email, phone, license);
    }

    /**
     * Creates a sample customer for testing.
     */
    public static Customer createSampleCustomer(String name, String email) {
        DriverLicense license = new DriverLicense(
            "DL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
            "CA",
            LocalDate.now().minusYears(2),
            LocalDate.now().plusYears(3)
        );
        return createCustomer(name, email, "555-0100", license);
    }
}



