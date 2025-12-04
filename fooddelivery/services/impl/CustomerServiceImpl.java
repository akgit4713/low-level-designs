package fooddelivery.services.impl;

import fooddelivery.enums.UserRole;
import fooddelivery.exceptions.UserException;
import fooddelivery.models.Location;
import fooddelivery.models.User;
import fooddelivery.repositories.UserRepository;
import fooddelivery.services.CustomerService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of CustomerService.
 */
public class CustomerServiceImpl implements CustomerService {
    
    private final UserRepository userRepository;
    
    public CustomerServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerCustomer(String name, String email, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException("Email already registered: " + email);
        }
        
        String id = "CUST-" + UUID.randomUUID().toString().substring(0, 8);
        User customer = new User(id, name, email, phone, UserRole.CUSTOMER);
        
        return userRepository.save(customer);
    }

    @Override
    public Optional<User> getCustomerById(String customerId) {
        return userRepository.findById(customerId)
                .filter(u -> u.getRole() == UserRole.CUSTOMER);
    }

    @Override
    public Optional<User> getCustomerByEmail(String email) {
        return userRepository.findByEmail(email)
                .filter(u -> u.getRole() == UserRole.CUSTOMER);
    }

    @Override
    public void updateCustomerProfile(String customerId, String name, String phone) {
        User customer = getCustomerById(customerId)
                .orElseThrow(() -> new UserException("Customer not found: " + customerId));
        
        if (name != null && !name.isBlank()) {
            customer.setName(name);
        }
        if (phone != null && !phone.isBlank()) {
            customer.setPhone(phone);
        }
        
        userRepository.save(customer);
    }

    @Override
    public void addDeliveryAddress(String customerId, Location address) {
        User customer = getCustomerById(customerId)
                .orElseThrow(() -> new UserException("Customer not found: " + customerId));
        
        customer.addAddress(address);
        userRepository.save(customer);
    }

    @Override
    public void setDefaultAddress(String customerId, Location address) {
        User customer = getCustomerById(customerId)
                .orElseThrow(() -> new UserException("Customer not found: " + customerId));
        
        if (!customer.getSavedAddresses().contains(address)) {
            customer.addAddress(address);
        }
        customer.setDefaultAddress(address);
        userRepository.save(customer);
    }

    @Override
    public List<Location> getDeliveryAddresses(String customerId) {
        User customer = getCustomerById(customerId)
                .orElseThrow(() -> new UserException("Customer not found: " + customerId));
        
        return customer.getSavedAddresses();
    }

    @Override
    public void deactivateCustomer(String customerId) {
        User customer = getCustomerById(customerId)
                .orElseThrow(() -> new UserException("Customer not found: " + customerId));
        
        customer.setActive(false);
        userRepository.save(customer);
    }
}



