package airline.repositories.impl;

import airline.enums.FlightStatus;
import airline.models.Airport;
import airline.models.Flight;
import airline.repositories.FlightRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of FlightRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryFlightRepository implements FlightRepository {
    
    private final ConcurrentHashMap<String, Flight> flights = new ConcurrentHashMap<>();

    @Override
    public Flight save(Flight flight) {
        flights.put(flight.getFlightNumber(), flight);
        return flight;
    }

    @Override
    public Optional<Flight> findById(String id) {
        return Optional.ofNullable(flights.get(id));
    }

    @Override
    public List<Flight> findAll() {
        return new ArrayList<>(flights.values());
    }

    @Override
    public boolean deleteById(String id) {
        return flights.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return flights.containsKey(id);
    }

    @Override
    public long count() {
        return flights.size();
    }

    @Override
    public List<Flight> findByRouteAndDate(Airport source, Airport destination, LocalDate date) {
        return flights.values().stream()
                .filter(f -> f.getSource().equals(source) &&
                             f.getDestination().equals(destination) &&
                             f.getDepartureTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Flight> findBySource(Airport source) {
        return flights.values().stream()
                .filter(f -> f.getSource().equals(source))
                .collect(Collectors.toList());
    }

    @Override
    public List<Flight> findByDestination(Airport destination) {
        return flights.values().stream()
                .filter(f -> f.getDestination().equals(destination))
                .collect(Collectors.toList());
    }

    @Override
    public List<Flight> findByStatus(FlightStatus status) {
        return flights.values().stream()
                .filter(f -> f.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Flight> findByDate(LocalDate date) {
        return flights.values().stream()
                .filter(f -> f.getDepartureTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public Flight findByFlightNumber(String flightNumber) {
        return flights.get(flightNumber);
    }
}



