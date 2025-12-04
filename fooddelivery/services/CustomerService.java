package fooddelivery.services;

import fooddelivery.models.Location;
import fooddelivery.models.User;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for customer operations.
 */
public interface CustomerService {
    User registerCustomer(String name, String email, String phone);
    Optional<User> getCustomerById(String customerId);
    Optional<User> getCustomerByEmail(String email);
    void updateCustomerProfile(String customerId, String name, String phone);
    void addDeliveryAddress(String customerId, Location address);
    void setDefaultAddress(String customerId, Location address);
    List<Location> getDeliveryAddresses(String customerId);
    void deactivateCustomer(String customerId);
}



