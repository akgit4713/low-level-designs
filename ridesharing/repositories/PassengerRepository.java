package ridesharing.repositories;

import ridesharing.models.Passenger;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Passenger entity.
 */
public interface PassengerRepository {
    
    Passenger save(Passenger passenger);
    
    Optional<Passenger> findById(String passengerId);
    
    Optional<Passenger> findByEmail(String email);
    
    List<Passenger> findAll();
    
    void delete(String passengerId);
}



