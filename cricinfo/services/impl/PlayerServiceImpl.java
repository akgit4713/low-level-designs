package cricinfo.services.impl;

import cricinfo.enums.PlayerRole;
import cricinfo.exceptions.PlayerNotFoundException;
import cricinfo.models.Player;
import cricinfo.models.PlayerStats;
import cricinfo.repositories.PlayerRepository;
import cricinfo.services.PlayerService;
import cricinfo.strategies.search.PlayerSearchStrategy;
import cricinfo.strategies.search.SearchStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of PlayerService.
 */
public class PlayerServiceImpl implements PlayerService {
    
    private final PlayerRepository playerRepository;
    private final SearchStrategy<Player> searchStrategy;
    
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
        this.searchStrategy = new PlayerSearchStrategy();
    }
    
    public PlayerServiceImpl(PlayerRepository playerRepository, 
                            SearchStrategy<Player> searchStrategy) {
        this.playerRepository = playerRepository;
        this.searchStrategy = searchStrategy;
    }
    
    @Override
    public Player createPlayer(String name, String country) {
        Player player = new Player(name, country);
        return playerRepository.save(player);
    }
    
    @Override
    public Optional<Player> getPlayer(String playerId) {
        return playerRepository.findById(playerId);
    }
    
    @Override
    public Optional<Player> getPlayerByName(String name) {
        return playerRepository.findByName(name);
    }
    
    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }
    
    @Override
    public List<Player> getPlayersByCountry(String country) {
        return playerRepository.findByCountry(country);
    }
    
    @Override
    public List<Player> getPlayersByRole(PlayerRole role) {
        return playerRepository.findByRole(role);
    }
    
    @Override
    public List<Player> searchPlayers(String query) {
        List<Player> allPlayers = playerRepository.findAll();
        return searchStrategy.search(allPlayers, query);
    }
    
    @Override
    public Player updatePlayer(Player player) {
        if (!playerRepository.exists(player.getId())) {
            throw new PlayerNotFoundException(player.getId());
        }
        return playerRepository.save(player);
    }
    
    @Override
    public PlayerStats getPlayerStats(String playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        
        return player.getStats();
    }
    
    @Override
    public void deletePlayer(String playerId) {
        if (!playerRepository.exists(playerId)) {
            throw new PlayerNotFoundException(playerId);
        }
        playerRepository.delete(playerId);
    }
}



