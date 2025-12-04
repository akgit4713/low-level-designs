package cricinfo.repositories.impl;

import cricinfo.models.Team;
import cricinfo.repositories.TeamRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of TeamRepository.
 */
public class InMemoryTeamRepository implements TeamRepository {
    
    private final Map<String, Team> teams = new ConcurrentHashMap<>();
    
    @Override
    public Team save(Team team) {
        teams.put(team.getId(), team);
        return team;
    }
    
    @Override
    public Optional<Team> findById(String teamId) {
        return Optional.ofNullable(teams.get(teamId));
    }
    
    @Override
    public Optional<Team> findByName(String name) {
        return teams.values().stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst();
    }
    
    @Override
    public List<Team> findAll() {
        return new ArrayList<>(teams.values());
    }
    
    @Override
    public List<Team> findByCountry(String country) {
        return teams.values().stream()
                .filter(t -> country.equalsIgnoreCase(t.getCountry()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Team> searchByName(String namePattern) {
        String pattern = namePattern.toLowerCase();
        return teams.values().stream()
                .filter(t -> t.getName().toLowerCase().contains(pattern))
                .collect(Collectors.toList());
    }
    
    @Override
    public void delete(String teamId) {
        teams.remove(teamId);
    }
    
    @Override
    public boolean exists(String teamId) {
        return teams.containsKey(teamId);
    }
}



