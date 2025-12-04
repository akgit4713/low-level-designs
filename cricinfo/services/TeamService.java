package cricinfo.services;

import cricinfo.models.Player;
import cricinfo.models.Team;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for team-related operations.
 */
public interface TeamService {
    
    /**
     * Create a new team.
     */
    Team createTeam(String name, String country);
    
    /**
     * Get team by ID.
     */
    Optional<Team> getTeam(String teamId);
    
    /**
     * Get team by name.
     */
    Optional<Team> getTeamByName(String name);
    
    /**
     * Get all teams.
     */
    List<Team> getAllTeams();
    
    /**
     * Add player to team.
     */
    Team addPlayerToTeam(String teamId, Player player);
    
    /**
     * Remove player from team.
     */
    Team removePlayerFromTeam(String teamId, String playerId);
    
    /**
     * Set team captain.
     */
    Team setTeamCaptain(String teamId, String playerId);
    
    /**
     * Search teams.
     */
    List<Team> searchTeams(String query);
    
    /**
     * Update team.
     */
    Team updateTeam(Team team);
    
    /**
     * Delete team.
     */
    void deleteTeam(String teamId);
}



