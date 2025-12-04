package carrental.repositories;

import carrental.models.Customer;

/**
 * Repository interface for Customer entities.
 */
public interface CustomerRepository extends Repository<Customer, String> {
    
    /**
     * Finds a customer by email address.
     */
    Customer findByEmail(String email);
    
    /**
     * Finds a customer by phone number.
     */
    Customer findByPhone(String phone);
    
    /**
     * Finds a customer by driver license number.
     */
    Customer findByDriverLicenseNumber(String licenseNumber);
    
    /**
     * Checks if a customer with the given email exists.
     */
    boolean existsByEmail(String email);
}



