package cricinfo.repositories;

import cricinfo.models.Team;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Team entity operations.
 */
public interface TeamRepository {
    
    /**
     * Save or update a team.
     */
    Team save(Team team);
    
    /**
     * Find team by ID.
     */
    Optional<Team> findById(String teamId);
    
    /**
     * Find team by name.
     */
    Optional<Team> findByName(String name);
    
    /**
     * Find all teams.
     */
    List<Team> findAll();
    
    /**
     * Find teams by country.
     */
    List<Team> findByCountry(String country);
    
    /**
     * Search teams by name pattern.
     */
    List<Team> searchByName(String namePattern);
    
    /**
     * Delete a team.
     */
    void delete(String teamId);
    
    /**
     * Check if team exists.
     */
    boolean exists(String teamId);
}



