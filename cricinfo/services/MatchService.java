package cricinfo.services;

import cricinfo.enums.MatchFormat;
import cricinfo.enums.MatchStatus;
import cricinfo.models.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for match-related operations.
 */
public interface MatchService {
    
    /**
     * Create a new match.
     */
    Match createMatch(Team team1, Team team2, MatchFormat format);
    
    /**
     * Create a match with venue and timing.
     */
    Match createMatch(Team team1, Team team2, MatchFormat format, 
                      Venue venue, LocalDateTime startTime, String seriesName);
    
    /**
     * Get match by ID.
     */
    Optional<Match> getMatch(String matchId);
    
    /**
     * Get all matches.
     */
    List<Match> getAllMatches();
    
    /**
     * Get live matches.
     */
    List<Match> getLiveMatches();
    
    /**
     * Get upcoming matches.
     */
    List<Match> getUpcomingMatches();
    
    /**
     * Get completed matches.
     */
    List<Match> getCompletedMatches();
    
    /**
     * Get matches for a team.
     */
    List<Match> getMatchesByTeam(String teamId);
    
    /**
     * Start a match.
     */
    Match startMatch(String matchId);
    
    /**
     * End a match.
     */
    Match endMatch(String matchId);
    
    /**
     * Search matches.
     */
    List<Match> searchMatches(String query);
    
    /**
     * Get match scorecard.
     */
    Scorecard getScorecard(String matchId);
    
    /**
     * Get match commentary.
     */
    List<Commentary> getCommentary(String matchId);
}



