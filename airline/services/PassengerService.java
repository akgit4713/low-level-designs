package airline.services;

import airline.models.Baggage;
import airline.models.Passenger;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for passenger management.
 */
public interface PassengerService {
    
    /**
     * Registers a new passenger.
     */
    Passenger registerPassenger(Passenger passenger);
    
    /**
     * Gets a passenger by ID.
     */
    Optional<Passenger> getPassenger(String passengerId);
    
    /**
     * Gets a passenger by email.
     */
    Optional<Passenger> getPassengerByEmail(String email);
    
    /**
     * Updates passenger details.
     */
    Passenger updatePassenger(Passenger passenger);
    
    /**
     * Adds baggage for a passenger on a booking.
     */
    void addBaggage(String passengerId, Baggage baggage);
    
    /**
     * Gets all baggage for a passenger.
     */
    List<Baggage> getBaggage(String passengerId);
}



