package carrental.exceptions;

/**
 * Thrown when a customer cannot be found in the system.
 */
public class CustomerNotFoundException extends CarRentalException {
    
    public CustomerNotFoundException(String customerId) {
        super("Customer not found with ID: " + customerId);
    }
}



