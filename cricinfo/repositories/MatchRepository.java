package cricinfo.repositories;

import cricinfo.enums.MatchStatus;
import cricinfo.models.Match;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Match entity operations.
 * Follows Repository pattern for data access abstraction.
 */
public interface MatchRepository {
    
    /**
     * Save or update a match.
     */
    Match save(Match match);
    
    /**
     * Find match by ID.
     */
    Optional<Match> findById(String matchId);
    
    /**
     * Find all matches.
     */
    List<Match> findAll();
    
    /**
     * Find matches by status.
     */
    List<Match> findByStatus(MatchStatus status);
    
    /**
     * Find live matches.
     */
    List<Match> findLiveMatches();
    
    /**
     * Find upcoming matches.
     */
    List<Match> findUpcomingMatches();
    
    /**
     * Find completed matches.
     */
    List<Match> findCompletedMatches();
    
    /**
     * Find matches by team ID.
     */
    List<Match> findByTeamId(String teamId);
    
    /**
     * Find matches in a date range.
     */
    List<Match> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find matches by series name.
     */
    List<Match> findBySeriesName(String seriesName);
    
    /**
     * Delete a match.
     */
    void delete(String matchId);
    
    /**
     * Check if match exists.
     */
    boolean exists(String matchId);
}



