package cricinfo.repositories.impl;

import cricinfo.enums.PlayerRole;
import cricinfo.models.Player;
import cricinfo.repositories.PlayerRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of PlayerRepository.
 */
public class InMemoryPlayerRepository implements PlayerRepository {
    
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> teamPlayerIndex = new ConcurrentHashMap<>();
    
    @Override
    public Player save(Player player) {
        players.put(player.getId(), player);
        return player;
    }
    
    public void addPlayerToTeam(String playerId, String teamId) {
        teamPlayerIndex.computeIfAbsent(teamId, k -> ConcurrentHashMap.newKeySet())
                .add(playerId);
    }
    
    @Override
    public Optional<Player> findById(String playerId) {
        return Optional.ofNullable(players.get(playerId));
    }
    
    @Override
    public Optional<Player> findByName(String name) {
        return players.values().stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst();
    }
    
    @Override
    public List<Player> findAll() {
        return new ArrayList<>(players.values());
    }
    
    @Override
    public List<Player> findByCountry(String country) {
        return players.values().stream()
                .filter(p -> country.equalsIgnoreCase(p.getCountry()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Player> findByRole(PlayerRole role) {
        return players.values().stream()
                .filter(p -> p.getRole() == role)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Player> findByTeamId(String teamId) {
        Set<String> playerIds = teamPlayerIndex.get(teamId);
        if (playerIds == null) {
            return Collections.emptyList();
        }
        return playerIds.stream()
                .map(players::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Player> searchByName(String namePattern) {
        String pattern = namePattern.toLowerCase();
        return players.values().stream()
                .filter(p -> p.getName().toLowerCase().contains(pattern))
                .collect(Collectors.toList());
    }
    
    @Override
    public void delete(String playerId) {
        players.remove(playerId);
        // Remove from team index
        teamPlayerIndex.values().forEach(set -> set.remove(playerId));
    }
    
    @Override
    public boolean exists(String playerId) {
        return players.containsKey(playerId);
    }
}



