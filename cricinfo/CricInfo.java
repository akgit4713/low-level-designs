package cricinfo;

import cricinfo.enums.MatchFormat;
import cricinfo.enums.PlayerRole;
import cricinfo.models.*;
import cricinfo.observers.LiveScoreNotifier;
import cricinfo.observers.MatchObserver;
import cricinfo.repositories.MatchRepository;
import cricinfo.repositories.PlayerRepository;
import cricinfo.repositories.TeamRepository;
import cricinfo.repositories.impl.InMemoryMatchRepository;
import cricinfo.repositories.impl.InMemoryPlayerRepository;
import cricinfo.repositories.impl.InMemoryTeamRepository;
import cricinfo.services.*;
import cricinfo.services.impl.MatchServiceImpl;
import cricinfo.services.impl.PlayerServiceImpl;
import cricinfo.services.impl.ScoreServiceImpl;
import cricinfo.services.impl.TeamServiceImpl;
import cricinfo.strategies.scoring.ScoringStrategy;
import cricinfo.strategies.scoring.StandardScoringStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Main facade for the CricInfo system.
 * Provides a unified interface for all cricket information operations.
 * Implements Singleton pattern for system-wide access.
 */
public class CricInfo {
    
    private static volatile CricInfo instance;
    
    private final MatchService matchService;
    private final ScoreService scoreService;
    private final TeamService teamService;
    private final PlayerService playerService;
    private final LiveScoreNotifier scoreNotifier;
    
    private CricInfo(MatchRepository matchRepository,
                    TeamRepository teamRepository,
                    PlayerRepository playerRepository,
                    ScoringStrategy scoringStrategy) {
        this.scoreNotifier = new LiveScoreNotifier();
        this.matchService = new MatchServiceImpl(matchRepository, scoreNotifier);
        this.scoreService = new ScoreServiceImpl(scoreNotifier, scoringStrategy);
        this.teamService = new TeamServiceImpl(teamRepository);
        this.playerService = new PlayerServiceImpl(playerRepository);
    }
    
    /**
     * Get the singleton instance with default repositories.
     */
    public static CricInfo getInstance() {
        if (instance == null) {
            synchronized (CricInfo.class) {
                if (instance == null) {
                    instance = new CricInfo(
                            new InMemoryMatchRepository(),
                            new InMemoryTeamRepository(),
                            new InMemoryPlayerRepository(),
                            new StandardScoringStrategy()
                    );
                }
            }
        }
        return instance;
    }
    
    /**
     * Create a new instance with custom dependencies (for testing).
     */
    public static CricInfo createInstance(MatchRepository matchRepository,
                                          TeamRepository teamRepository,
                                          PlayerRepository playerRepository,
                                          ScoringStrategy scoringStrategy) {
        return new CricInfo(matchRepository, teamRepository, playerRepository, scoringStrategy);
    }
    
    /**
     * Reset singleton instance (for testing).
     */
    public static void resetInstance() {
        synchronized (CricInfo.class) {
            instance = null;
        }
    }
    
    // ==================== Match Operations ====================
    
    public Match createMatch(Team team1, Team team2, MatchFormat format) {
        return matchService.createMatch(team1, team2, format);
    }
    
    public Match createMatch(Team team1, Team team2, MatchFormat format,
                            Venue venue, LocalDateTime startTime, String seriesName) {
        return matchService.createMatch(team1, team2, format, venue, startTime, seriesName);
    }
    
    public Optional<Match> getMatch(String matchId) {
        return matchService.getMatch(matchId);
    }
    
    public List<Match> getAllMatches() {
        return matchService.getAllMatches();
    }
    
    public List<Match> getLiveMatches() {
        return matchService.getLiveMatches();
    }
    
    public List<Match> getUpcomingMatches() {
        return matchService.getUpcomingMatches();
    }
    
    public List<Match> getCompletedMatches() {
        return matchService.getCompletedMatches();
    }
    
    public Match startMatch(String matchId) {
        return matchService.startMatch(matchId);
    }
    
    public Match endMatch(String matchId) {
        return matchService.endMatch(matchId);
    }
    
    public List<Match> searchMatches(String query) {
        return matchService.searchMatches(query);
    }
    
    public Scorecard getScorecard(String matchId) {
        return matchService.getScorecard(matchId);
    }
    
    public List<Commentary> getCommentary(String matchId) {
        return matchService.getCommentary(matchId);
    }
    
    // ==================== Score Operations ====================
    
    public Innings startInnings(Match match, Team battingTeam, Team bowlingTeam,
                                Player striker, Player nonStriker) {
        return scoreService.startInnings(match, battingTeam, bowlingTeam, striker, nonStriker);
    }
    
    public void recordBall(Match match, Ball ball) {
        scoreService.recordBall(match, ball);
    }
    
    public void sendNewBatsman(Match match, Player batsman) {
        scoreService.sendNewBatsman(match, batsman);
    }
    
    public void changeBowler(Match match, Player bowler) {
        scoreService.changeBowler(match, bowler);
    }
    
    public void endInnings(Match match) {
        scoreService.endInnings(match);
    }
    
    public String getLiveScore(Match match) {
        return scoreService.getLiveScore(match);
    }
    
    public double getRequiredRunRate(Match match) {
        return scoreService.getRequiredRunRate(match);
    }
    
    public int getProjectedScore(Match match) {
        return scoreService.getProjectedScore(match);
    }
    
    public double getWinProbability(Match match) {
        return scoreService.getWinProbability(match);
    }
    
    // ==================== Team Operations ====================
    
    public Team createTeam(String name, String country) {
        return teamService.createTeam(name, country);
    }
    
    public Optional<Team> getTeam(String teamId) {
        return teamService.getTeam(teamId);
    }
    
    public Optional<Team> getTeamByName(String name) {
        return teamService.getTeamByName(name);
    }
    
    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }
    
    public Team addPlayerToTeam(String teamId, Player player) {
        return teamService.addPlayerToTeam(teamId, player);
    }
    
    public List<Team> searchTeams(String query) {
        return teamService.searchTeams(query);
    }
    
    // ==================== Player Operations ====================
    
    public Player createPlayer(String name, String country) {
        return playerService.createPlayer(name, country);
    }
    
    public Optional<Player> getPlayer(String playerId) {
        return playerService.getPlayer(playerId);
    }
    
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }
    
    public List<Player> getPlayersByCountry(String country) {
        return playerService.getPlayersByCountry(country);
    }
    
    public List<Player> getPlayersByRole(PlayerRole role) {
        return playerService.getPlayersByRole(role);
    }
    
    public List<Player> searchPlayers(String query) {
        return playerService.searchPlayers(query);
    }
    
    public PlayerStats getPlayerStats(String playerId) {
        return playerService.getPlayerStats(playerId);
    }
    
    // ==================== Observer Operations ====================
    
    public void subscribeToLiveScores(MatchObserver observer) {
        scoreNotifier.registerObserver(observer);
    }
    
    public void unsubscribeFromLiveScores(MatchObserver observer) {
        scoreNotifier.removeObserver(observer);
    }
    
    public int getSubscriberCount() {
        return scoreNotifier.getObserverCount();
    }
    
    // ==================== Internal Access (for services) ====================
    
    public MatchService getMatchService() {
        return matchService;
    }
    
    public ScoreService getScoreService() {
        return scoreService;
    }
    
    public TeamService getTeamService() {
        return teamService;
    }
    
    public PlayerService getPlayerService() {
        return playerService;
    }
}



