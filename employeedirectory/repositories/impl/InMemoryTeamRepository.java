package employeedirectory.repositories.impl;

import employeedirectory.enums.Department;
import employeedirectory.models.Team;
import employeedirectory.repositories.TeamRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of TeamRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryTeamRepository implements TeamRepository {
    
    private final Map<String, Team> teams = new ConcurrentHashMap<>();

    @Override
    public Team save(Team team) {
        teams.put(team.getTeamId(), team);
        return team;
    }

    @Override
    public Optional<Team> findById(String teamId) {
        return Optional.ofNullable(teams.get(teamId));
    }

    @Override
    public List<Team> findByName(String name) {
        String lowerName = name.toLowerCase();
        return teams.values().stream()
                .filter(t -> t.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Team> findByDepartment(Department department) {
        return teams.values().stream()
                .filter(t -> t.getDepartment() == department)
                .collect(Collectors.toList());
    }

    @Override
    public List<Team> findByEmployee(String employeeId) {
        return teams.values().stream()
                .filter(t -> t.hasMember(employeeId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Team> findAll() {
        return new ArrayList<>(teams.values());
    }

    @Override
    public boolean deleteById(String teamId) {
        return teams.remove(teamId) != null;
    }

    @Override
    public boolean existsById(String teamId) {
        return teams.containsKey(teamId);
    }

    @Override
    public long count() {
        return teams.size();
    }
}
