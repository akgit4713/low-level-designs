package carrental.services;

import carrental.models.Customer;

import java.util.List;

/**
 * Service interface for customer management operations.
 */
public interface CustomerService {
    
    /**
     * Registers a new customer.
     */
    Customer registerCustomer(Customer customer);
    
    /**
     * Gets a customer by their ID.
     */
    Customer getCustomerById(String customerId);
    
    /**
     * Gets a customer by their email.
     */
    Customer getCustomerByEmail(String email);
    
    /**
     * Gets all customers.
     */
    List<Customer> getAllCustomers();
    
    /**
     * Validates if a customer can rent a car.
     * Checks for valid driver's license, etc.
     */
    boolean canCustomerRent(String customerId);
    
    /**
     * Checks if a customer exists with the given email.
     */
    boolean customerExistsByEmail(String email);
    
    /**
     * Removes a customer from the system.
     */
    boolean removeCustomer(String customerId);
}



