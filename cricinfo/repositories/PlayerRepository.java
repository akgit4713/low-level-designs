package cricinfo.repositories;

import cricinfo.enums.PlayerRole;
import cricinfo.models.Player;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Player entity operations.
 */
public interface PlayerRepository {
    
    /**
     * Save or update a player.
     */
    Player save(Player player);
    
    /**
     * Find player by ID.
     */
    Optional<Player> findById(String playerId);
    
    /**
     * Find player by name.
     */
    Optional<Player> findByName(String name);
    
    /**
     * Find all players.
     */
    List<Player> findAll();
    
    /**
     * Find players by country.
     */
    List<Player> findByCountry(String country);
    
    /**
     * Find players by role.
     */
    List<Player> findByRole(PlayerRole role);
    
    /**
     * Find players by team ID.
     */
    List<Player> findByTeamId(String teamId);
    
    /**
     * Search players by name pattern.
     */
    List<Player> searchByName(String namePattern);
    
    /**
     * Delete a player.
     */
    void delete(String playerId);
    
    /**
     * Check if player exists.
     */
    boolean exists(String playerId);
}



