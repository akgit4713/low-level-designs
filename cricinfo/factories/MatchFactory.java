package cricinfo.factories;

import cricinfo.enums.MatchFormat;
import cricinfo.models.Match;
import cricinfo.models.Team;
import cricinfo.models.Venue;

import java.time.LocalDateTime;

/**
 * Factory interface for creating matches.
 * Follows Factory pattern to encapsulate match creation logic.
 */
public interface MatchFactory {
    
    /**
     * Create a match with basic parameters.
     */
    Match createMatch(Team team1, Team team2);
    
    /**
     * Create a match with venue.
     */
    Match createMatch(Team team1, Team team2, Venue venue);
    
    /**
     * Create a match with full details.
     */
    Match createMatch(Team team1, Team team2, Venue venue, 
                      LocalDateTime startTime, String seriesName);
    
    /**
     * Get the format this factory creates.
     */
    MatchFormat getFormat();
}



