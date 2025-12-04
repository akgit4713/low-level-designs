package airline.repositories;

import airline.models.Passenger;

import java.util.Optional;

/**
 * Repository interface for Passenger entities.
 */
public interface PassengerRepository extends Repository<Passenger, String> {
    
    /**
     * Finds a passenger by email.
     */
    Optional<Passenger> findByEmail(String email);
    
    /**
     * Finds a passenger by passport number.
     */
    Optional<Passenger> findByPassportNumber(String passportNumber);
}



