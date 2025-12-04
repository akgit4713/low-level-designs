package airline.services.impl;

import airline.enums.AircraftStatus;
import airline.exceptions.AirlineException;
import airline.models.Aircraft;
import airline.models.Flight;
import airline.repositories.AircraftRepository;
import airline.services.AircraftService;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of AircraftService.
 */
public class AircraftServiceImpl implements AircraftService {
    
    private final AircraftRepository aircraftRepository;

    public AircraftServiceImpl(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    @Override
    public Aircraft addAircraft(Aircraft aircraft) {
        if (aircraftRepository.findByRegistrationNumber(aircraft.getRegistrationNumber()) != null) {
            throw new AirlineException("Aircraft with registration " + 
                    aircraft.getRegistrationNumber() + " already exists");
        }
        return aircraftRepository.save(aircraft);
    }

    @Override
    public Optional<Aircraft> getAircraft(String aircraftId) {
        return aircraftRepository.findById(aircraftId);
    }

    @Override
    public Optional<Aircraft> getAircraftByRegistration(String registrationNumber) {
        return Optional.ofNullable(aircraftRepository.findByRegistrationNumber(registrationNumber));
    }

    @Override
    public List<Aircraft> getAvailableAircraft() {
        return aircraftRepository.findAvailable();
    }

    @Override
    public void assignToFlight(String aircraftId, Flight flight) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new AirlineException("Aircraft not found: " + aircraftId));
        
        if (!aircraft.isAvailable()) {
            throw new AirlineException("Aircraft " + aircraft.getRegistrationNumber() + " is not available");
        }
        
        aircraft.setStatus(AircraftStatus.IN_FLIGHT);
        aircraftRepository.save(aircraft);
        
        System.out.println("✓ Aircraft " + aircraft.getRegistrationNumber() + 
                " assigned to flight " + flight.getFlightNumber());
    }

    @Override
    public void releaseFromFlight(String aircraftId) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new AirlineException("Aircraft not found: " + aircraftId));
        
        aircraft.setStatus(AircraftStatus.AVAILABLE);
        aircraftRepository.save(aircraft);
        
        System.out.println("✓ Aircraft " + aircraft.getRegistrationNumber() + " released and available");
    }

    @Override
    public void sendToMaintenance(String aircraftId) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new AirlineException("Aircraft not found: " + aircraftId));
        
        aircraft.setStatus(AircraftStatus.MAINTENANCE);
        aircraftRepository.save(aircraft);
        
        System.out.println("⚠️ Aircraft " + aircraft.getRegistrationNumber() + " sent for maintenance");
    }
}



