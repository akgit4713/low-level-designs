package airline.services.impl;

import airline.enums.CrewRole;
import airline.exceptions.AirlineException;
import airline.models.Crew;
import airline.models.Flight;
import airline.repositories.CrewRepository;
import airline.services.CrewService;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of CrewService.
 */
public class CrewServiceImpl implements CrewService {
    
    private final CrewRepository crewRepository;

    public CrewServiceImpl(CrewRepository crewRepository) {
        this.crewRepository = crewRepository;
    }

    @Override
    public Crew addCrewMember(Crew crew) {
        if (crewRepository.findByEmployeeId(crew.getEmployeeId()) != null) {
            throw new AirlineException("Crew member with employee ID " + 
                    crew.getEmployeeId() + " already exists");
        }
        return crewRepository.save(crew);
    }

    @Override
    public Optional<Crew> getCrewMember(String crewId) {
        return crewRepository.findById(crewId);
    }

    @Override
    public List<Crew> getAvailableCrewByRole(CrewRole role) {
        return crewRepository.findAvailableByRole(role);
    }

    @Override
    public List<Crew> getCrewByCertification(String aircraftModel) {
        return crewRepository.findByCertification(aircraftModel);
    }

    @Override
    public void assignToFlight(String crewId, Flight flight) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new AirlineException("Crew member not found: " + crewId));
        
        if (!crew.isAvailable()) {
            throw new AirlineException("Crew member " + crew.getFullName() + " is not available");
        }
        
        if (!crew.isLicenseValid()) {
            throw new AirlineException("Crew member " + crew.getFullName() + " has an expired license");
        }
        
        // Check certification for cockpit crew
        if (crew.getRole().isCockpitCrew() && 
            !crew.canOperateAircraft(flight.getAircraft().getModel())) {
            throw new AirlineException("Crew member " + crew.getFullName() + 
                    " is not certified for " + flight.getAircraft().getModel());
        }
        
        crew.setAvailable(false);
        flight.addCrewMember(crew);
        crewRepository.save(crew);
        
        System.out.println("✓ " + crew.getRole().getDisplayName() + " " + crew.getFullName() + 
                " assigned to flight " + flight.getFlightNumber());
    }

    @Override
    public void releaseFromFlight(String crewId) {
        Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new AirlineException("Crew member not found: " + crewId));
        
        crew.setAvailable(true);
        crewRepository.save(crew);
        
        System.out.println("✓ " + crew.getFullName() + " released and available");
    }

    @Override
    public boolean validateCrewAssignment(Flight flight) {
        List<Crew> crewMembers = flight.getCrewMembers();
        
        // Minimum requirements: 1 Pilot, 1 Co-Pilot, 2 Flight Attendants
        long pilots = crewMembers.stream()
                .filter(c -> c.getRole() == CrewRole.PILOT)
                .count();
        long coPilots = crewMembers.stream()
                .filter(c -> c.getRole() == CrewRole.CO_PILOT)
                .count();
        long attendants = crewMembers.stream()
                .filter(c -> c.getRole() == CrewRole.FLIGHT_ATTENDANT || 
                            c.getRole() == CrewRole.PURSER)
                .count();
        
        boolean valid = pilots >= 1 && coPilots >= 1 && attendants >= 2;
        
        if (!valid) {
            System.out.println("⚠️ Crew assignment incomplete for flight " + flight.getFlightNumber());
            System.out.println("   Required: 1 Pilot, 1 Co-Pilot, 2 Cabin Crew");
            System.out.println("   Current: " + pilots + " Pilot(s), " + coPilots + " Co-Pilot(s), " + 
                    attendants + " Cabin Crew");
        }
        
        return valid;
    }
}



