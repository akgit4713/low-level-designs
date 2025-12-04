package carrental.repositories.impl;

import carrental.models.Customer;
import carrental.repositories.CustomerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of CustomerRepository.
 */
public class InMemoryCustomerRepository implements CustomerRepository {
    
    private final Map<String, Customer> customers = new ConcurrentHashMap<>();

    @Override
    public Customer save(Customer customer) {
        customers.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public Optional<Customer> findById(String id) {
        return Optional.ofNullable(customers.get(id));
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public boolean deleteById(String id) {
        return customers.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return customers.containsKey(id);
    }

    @Override
    public long count() {
        return customers.size();
    }

    @Override
    public Customer findByEmail(String email) {
        return customers.values().stream()
            .filter(customer -> customer.getEmail().equalsIgnoreCase(email))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Customer findByPhone(String phone) {
        return customers.values().stream()
            .filter(customer -> customer.getPhone().equals(phone))
            .findFirst()
            .orElse(null);
    }

    @Override
    public Customer findByDriverLicenseNumber(String licenseNumber) {
        return customers.values().stream()
            .filter(customer -> customer.getDriverLicense() != null && 
                    customer.getDriverLicense().getLicenseNumber().equals(licenseNumber))
            .findFirst()
            .orElse(null);
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email) != null;
    }
}



