package airline.services;

import airline.enums.CrewRole;
import airline.models.Crew;
import airline.models.Flight;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for crew management.
 */
public interface CrewService {
    
    /**
     * Adds a new crew member.
     */
    Crew addCrewMember(Crew crew);
    
    /**
     * Gets a crew member by ID.
     */
    Optional<Crew> getCrewMember(String crewId);
    
    /**
     * Gets available crew members by role.
     */
    List<Crew> getAvailableCrewByRole(CrewRole role);
    
    /**
     * Gets crew members certified for an aircraft model.
     */
    List<Crew> getCrewByCertification(String aircraftModel);
    
    /**
     * Assigns crew to a flight.
     */
    void assignToFlight(String crewId, Flight flight);
    
    /**
     * Releases crew after flight completion.
     */
    void releaseFromFlight(String crewId);
    
    /**
     * Validates if a flight has minimum required crew.
     */
    boolean validateCrewAssignment(Flight flight);
}



