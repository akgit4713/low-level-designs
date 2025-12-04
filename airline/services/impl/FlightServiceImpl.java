package airline.services.impl;

import airline.enums.FlightStatus;
import airline.enums.SeatClass;
import airline.exceptions.FlightException;
import airline.models.Airport;
import airline.models.Flight;
import airline.models.FlightSearchResult;
import airline.observers.FlightObserver;
import airline.repositories.FlightRepository;
import airline.services.FlightService;
import airline.strategies.pricing.PricingStrategy;
import airline.strategies.search.FlightSearchStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Implementation of FlightService.
 */
public class FlightServiceImpl implements FlightService {
    
    private final FlightRepository flightRepository;
    private final PricingStrategy pricingStrategy;
    private FlightSearchStrategy searchStrategy;
    private final List<FlightObserver> observers = new ArrayList<>();

    public FlightServiceImpl(FlightRepository flightRepository, PricingStrategy pricingStrategy) {
        this.flightRepository = flightRepository;
        this.pricingStrategy = pricingStrategy;
    }

    public void setSearchStrategy(FlightSearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    @Override
    public Flight addFlight(Flight flight) {
        if (flightRepository.existsById(flight.getFlightNumber())) {
            throw new FlightException("Flight " + flight.getFlightNumber() + " already exists");
        }
        return flightRepository.save(flight);
    }

    @Override
    public Optional<Flight> getFlight(String flightNumber) {
        return flightRepository.findById(flightNumber);
    }

    @Override
    public List<FlightSearchResult> searchFlights(Airport source, Airport destination, LocalDate date) {
        List<Flight> flights = flightRepository.findByRouteAndDate(source, destination, date);
        
        List<FlightSearchResult> results = new ArrayList<>();
        for (Flight flight : flights) {
            if (flight.getStatus() != FlightStatus.CANCELLED) {
                results.add(createSearchResult(flight));
            }
        }
        
        // Apply search strategy if set
        if (searchStrategy != null) {
            results = searchStrategy.sortFlights(results);
        }
        
        return results;
    }

    private FlightSearchResult createSearchResult(Flight flight) {
        Map<SeatClass, Integer> availability = new EnumMap<>(SeatClass.class);
        Map<SeatClass, BigDecimal> prices = new EnumMap<>(SeatClass.class);
        
        for (SeatClass seatClass : SeatClass.values()) {
            availability.put(seatClass, flight.getAvailableSeatCount(seatClass));
            prices.put(seatClass, pricingStrategy.calculatePrice(flight, seatClass));
        }
        
        return new FlightSearchResult(flight, availability, prices);
    }

    @Override
    public List<Flight> getFlightsByDate(LocalDate date) {
        return flightRepository.findByDate(date);
    }

    @Override
    public void updateFlightStatus(String flightNumber, FlightStatus status) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber);
        if (flight == null) {
            throw new FlightException("Flight not found: " + flightNumber);
        }
        
        FlightStatus oldStatus = flight.getStatus();
        flight.setStatus(status);
        
        notifyStatusChanged(flight, oldStatus, status);
    }

    @Override
    public void delayFlight(String flightNumber, String reason) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber);
        if (flight == null) {
            throw new FlightException("Flight not found: " + flightNumber);
        }
        
        FlightStatus oldStatus = flight.getStatus();
        flight.setStatus(FlightStatus.DELAYED);
        
        notifyStatusChanged(flight, oldStatus, FlightStatus.DELAYED);
        notifyFlightDelayed(flight, reason);
    }

    @Override
    public void cancelFlight(String flightNumber, String reason) {
        Flight flight = flightRepository.findByFlightNumber(flightNumber);
        if (flight == null) {
            throw new FlightException("Flight not found: " + flightNumber);
        }
        
        FlightStatus oldStatus = flight.getStatus();
        flight.setStatus(FlightStatus.CANCELLED);
        
        notifyStatusChanged(flight, oldStatus, FlightStatus.CANCELLED);
        notifyFlightCancelled(flight, reason);
    }

    @Override
    public void addObserver(FlightObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(FlightObserver observer) {
        observers.remove(observer);
    }

    private void notifyStatusChanged(Flight flight, FlightStatus oldStatus, FlightStatus newStatus) {
        for (FlightObserver observer : observers) {
            observer.onFlightStatusChanged(flight, oldStatus, newStatus);
        }
    }

    private void notifyFlightDelayed(Flight flight, String reason) {
        for (FlightObserver observer : observers) {
            observer.onFlightDelayed(flight, reason);
        }
    }

    private void notifyFlightCancelled(Flight flight, String reason) {
        for (FlightObserver observer : observers) {
            observer.onFlightCancelled(flight, reason);
        }
    }
}



