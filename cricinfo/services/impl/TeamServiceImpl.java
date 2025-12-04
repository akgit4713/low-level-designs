package cricinfo.services.impl;

import cricinfo.exceptions.PlayerNotFoundException;
import cricinfo.exceptions.TeamNotFoundException;
import cricinfo.models.Player;
import cricinfo.models.Team;
import cricinfo.repositories.TeamRepository;
import cricinfo.services.TeamService;
import cricinfo.strategies.search.SearchStrategy;
import cricinfo.strategies.search.TeamSearchStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of TeamService.
 */
public class TeamServiceImpl implements TeamService {
    
    private final TeamRepository teamRepository;
    private final SearchStrategy<Team> searchStrategy;
    
    public TeamServiceImpl(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
        this.searchStrategy = new TeamSearchStrategy();
    }
    
    public TeamServiceImpl(TeamRepository teamRepository, SearchStrategy<Team> searchStrategy) {
        this.teamRepository = teamRepository;
        this.searchStrategy = searchStrategy;
    }
    
    @Override
    public Team createTeam(String name, String country) {
        Team team = new Team(name, country);
        return teamRepository.save(team);
    }
    
    @Override
    public Optional<Team> getTeam(String teamId) {
        return teamRepository.findById(teamId);
    }
    
    @Override
    public Optional<Team> getTeamByName(String name) {
        return teamRepository.findByName(name);
    }
    
    @Override
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    
    @Override
    public Team addPlayerToTeam(String teamId, Player player) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
        
        team.addPlayer(player);
        return teamRepository.save(team);
    }
    
    @Override
    public Team removePlayerFromTeam(String teamId, String playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
        
        Player player = team.getPlayerById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        
        team.removePlayer(player);
        return teamRepository.save(team);
    }
    
    @Override
    public Team setTeamCaptain(String teamId, String playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
        
        Player player = team.getPlayerById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        
        team.setCaptain(player);
        return teamRepository.save(team);
    }
    
    @Override
    public List<Team> searchTeams(String query) {
        List<Team> allTeams = teamRepository.findAll();
        return searchStrategy.search(allTeams, query);
    }
    
    @Override
    public Team updateTeam(Team team) {
        if (!teamRepository.exists(team.getId())) {
            throw new TeamNotFoundException(team.getId());
        }
        return teamRepository.save(team);
    }
    
    @Override
    public void deleteTeam(String teamId) {
        if (!teamRepository.exists(teamId)) {
            throw new TeamNotFoundException(teamId);
        }
        teamRepository.delete(teamId);
    }
}



