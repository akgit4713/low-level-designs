package carrental.services.impl;

import carrental.exceptions.CustomerNotFoundException;
import carrental.models.Customer;
import carrental.repositories.CustomerRepository;
import carrental.services.CustomerService;

import java.util.List;

/**
 * Implementation of CustomerService.
 */
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer registerCustomer(Customer customer) {
        // Check if customer already exists
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Customer with email " + customer.getEmail() + " already exists");
        }
        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomerById(String customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    @Override
    public Customer getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email);
        if (customer == null) {
            throw new CustomerNotFoundException("email: " + email);
        }
        return customer;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public boolean canCustomerRent(String customerId) {
        Customer customer = getCustomerById(customerId);
        
        // Must have a valid driver's license
        if (!customer.hasValidLicense()) {
            return false;
        }
        
        // Could add additional checks here:
        // - Credit check
        // - Minimum age requirement
        // - Outstanding payments
        
        return true;
    }

    @Override
    public boolean customerExistsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    @Override
    public boolean removeCustomer(String customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }
        return customerRepository.deleteById(customerId);
    }
}



