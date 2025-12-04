package airline.services;

import airline.enums.FlightStatus;
import airline.models.Airport;
import airline.models.Flight;
import airline.models.FlightSearchResult;
import airline.observers.FlightObserver;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for flight management operations.
 */
public interface FlightService {
    
    /**
     * Adds a new flight.
     */
    Flight addFlight(Flight flight);
    
    /**
     * Gets a flight by flight number.
     */
    Optional<Flight> getFlight(String flightNumber);
    
    /**
     * Searches for flights by route and date.
     */
    List<FlightSearchResult> searchFlights(Airport source, Airport destination, LocalDate date);
    
    /**
     * Gets all flights for a date.
     */
    List<Flight> getFlightsByDate(LocalDate date);
    
    /**
     * Updates flight status.
     */
    void updateFlightStatus(String flightNumber, FlightStatus status);
    
    /**
     * Delays a flight.
     */
    void delayFlight(String flightNumber, String reason);
    
    /**
     * Cancels a flight.
     */
    void cancelFlight(String flightNumber, String reason);
    
    /**
     * Adds a flight observer.
     */
    void addObserver(FlightObserver observer);
    
    /**
     * Removes a flight observer.
     */
    void removeObserver(FlightObserver observer);
}



