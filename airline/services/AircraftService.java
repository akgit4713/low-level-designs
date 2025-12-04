package airline.services;

import airline.models.Aircraft;
import airline.models.Flight;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for aircraft management.
 */
public interface AircraftService {
    
    /**
     * Adds a new aircraft to the fleet.
     */
    Aircraft addAircraft(Aircraft aircraft);
    
    /**
     * Gets an aircraft by ID.
     */
    Optional<Aircraft> getAircraft(String aircraftId);
    
    /**
     * Gets an aircraft by registration number.
     */
    Optional<Aircraft> getAircraftByRegistration(String registrationNumber);
    
    /**
     * Gets all available aircraft.
     */
    List<Aircraft> getAvailableAircraft();
    
    /**
     * Assigns an aircraft to a flight.
     */
    void assignToFlight(String aircraftId, Flight flight);
    
    /**
     * Releases an aircraft after flight completion.
     */
    void releaseFromFlight(String aircraftId);
    
    /**
     * Puts an aircraft into maintenance.
     */
    void sendToMaintenance(String aircraftId);
}



