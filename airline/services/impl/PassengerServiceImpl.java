package airline.services.impl;

import airline.exceptions.PassengerException;
import airline.models.Baggage;
import airline.models.Passenger;
import airline.repositories.PassengerRepository;
import airline.services.PassengerService;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of PassengerService.
 */
public class PassengerServiceImpl implements PassengerService {
    
    private final PassengerRepository passengerRepository;

    public PassengerServiceImpl(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Override
    public Passenger registerPassenger(Passenger passenger) {
        // Check if passenger with same email exists
        if (passengerRepository.findByEmail(passenger.getEmail()).isPresent()) {
            throw new PassengerException("Passenger with email " + passenger.getEmail() + " already exists");
        }
        return passengerRepository.save(passenger);
    }

    @Override
    public Optional<Passenger> getPassenger(String passengerId) {
        return passengerRepository.findById(passengerId);
    }

    @Override
    public Optional<Passenger> getPassengerByEmail(String email) {
        return passengerRepository.findByEmail(email);
    }

    @Override
    public Passenger updatePassenger(Passenger passenger) {
        if (!passengerRepository.existsById(passenger.getId())) {
            throw new PassengerException("Passenger not found: " + passenger.getId());
        }
        return passengerRepository.save(passenger);
    }

    @Override
    public void addBaggage(String passengerId, Baggage baggage) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new PassengerException("Passenger not found: " + passengerId));
        
        passenger.addBaggage(baggage);
        passengerRepository.save(passenger);
    }

    @Override
    public List<Baggage> getBaggage(String passengerId) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new PassengerException("Passenger not found: " + passengerId));
        
        return passenger.getBaggage();
    }
}



