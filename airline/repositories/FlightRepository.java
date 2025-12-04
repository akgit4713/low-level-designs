package airline.repositories;

import airline.enums.FlightStatus;
import airline.models.Airport;
import airline.models.Flight;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Flight entities.
 */
public interface FlightRepository extends Repository<Flight, String> {
    
    /**
     * Finds flights by route and date.
     */
    List<Flight> findByRouteAndDate(Airport source, Airport destination, LocalDate date);
    
    /**
     * Finds flights by source airport.
     */
    List<Flight> findBySource(Airport source);
    
    /**
     * Finds flights by destination airport.
     */
    List<Flight> findByDestination(Airport destination);
    
    /**
     * Finds flights by status.
     */
    List<Flight> findByStatus(FlightStatus status);
    
    /**
     * Finds flights by date.
     */
    List<Flight> findByDate(LocalDate date);
    
    /**
     * Finds a flight by flight number.
     */
    Flight findByFlightNumber(String flightNumber);
}



