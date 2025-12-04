package cricinfo.factories;

import cricinfo.enums.MatchFormat;
import cricinfo.models.Match;
import cricinfo.models.Team;
import cricinfo.models.Venue;

import java.time.LocalDateTime;

/**
 * Factory for creating T20 format matches.
 */
public class T20MatchFactory implements MatchFactory {
    
    @Override
    public Match createMatch(Team team1, Team team2) {
        Match match = new Match(team1, team2, MatchFormat.T20);
        return match;
    }
    
    @Override
    public Match createMatch(Team team1, Team team2, Venue venue) {
        Match match = createMatch(team1, team2);
        match.setVenue(venue);
        return match;
    }
    
    @Override
    public Match createMatch(Team team1, Team team2, Venue venue, 
                             LocalDateTime startTime, String seriesName) {
        Match match = createMatch(team1, team2, venue);
        match.setStartTime(startTime);
        match.setSeriesName(seriesName);
        return match;
    }
    
    @Override
    public MatchFormat getFormat() {
        return MatchFormat.T20;
    }
}



