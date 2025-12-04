package cricinfo.services;

import cricinfo.enums.PlayerRole;
import cricinfo.models.Player;
import cricinfo.models.PlayerStats;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for player-related operations.
 */
public interface PlayerService {
    
    /**
     * Create a new player.
     */
    Player createPlayer(String name, String country);
    
    /**
     * Get player by ID.
     */
    Optional<Player> getPlayer(String playerId);
    
    /**
     * Get player by name.
     */
    Optional<Player> getPlayerByName(String name);
    
    /**
     * Get all players.
     */
    List<Player> getAllPlayers();
    
    /**
     * Get players by country.
     */
    List<Player> getPlayersByCountry(String country);
    
    /**
     * Get players by role.
     */
    List<Player> getPlayersByRole(PlayerRole role);
    
    /**
     * Search players.
     */
    List<Player> searchPlayers(String query);
    
    /**
     * Update player.
     */
    Player updatePlayer(Player player);
    
    /**
     * Get player statistics.
     */
    PlayerStats getPlayerStats(String playerId);
    
    /**
     * Delete player.
     */
    void deletePlayer(String playerId);
}



