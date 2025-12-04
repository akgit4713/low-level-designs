package cricinfo.services.impl;

import cricinfo.enums.MatchFormat;
import cricinfo.enums.MatchStatus;
import cricinfo.exceptions.InvalidMatchStateException;
import cricinfo.exceptions.MatchNotFoundException;
import cricinfo.factories.MatchFactory;
import cricinfo.factories.MatchFactoryProvider;
import cricinfo.models.*;
import cricinfo.observers.MatchSubject;
import cricinfo.repositories.MatchRepository;
import cricinfo.services.MatchService;
import cricinfo.strategies.search.MatchSearchStrategy;
import cricinfo.strategies.search.SearchStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of MatchService.
 * Uses Factory pattern for match creation and Strategy pattern for search.
 */
public class MatchServiceImpl implements MatchService {
    
    private final MatchRepository matchRepository;
    private final MatchSubject matchSubject;
    private final SearchStrategy<Match> searchStrategy;
    
    public MatchServiceImpl(MatchRepository matchRepository, MatchSubject matchSubject) {
        this.matchRepository = matchRepository;
        this.matchSubject = matchSubject;
        this.searchStrategy = new MatchSearchStrategy();
    }
    
    public MatchServiceImpl(MatchRepository matchRepository, MatchSubject matchSubject,
                           SearchStrategy<Match> searchStrategy) {
        this.matchRepository = matchRepository;
        this.matchSubject = matchSubject;
        this.searchStrategy = searchStrategy;
    }
    
    @Override
    public Match createMatch(Team team1, Team team2, MatchFormat format) {
        MatchFactory factory = MatchFactoryProvider.getFactory(format);
        Match match = factory.createMatch(team1, team2);
        return matchRepository.save(match);
    }
    
    @Override
    public Match createMatch(Team team1, Team team2, MatchFormat format,
                            Venue venue, LocalDateTime startTime, String seriesName) {
        MatchFactory factory = MatchFactoryProvider.getFactory(format);
        Match match = factory.createMatch(team1, team2, venue, startTime, seriesName);
        return matchRepository.save(match);
    }
    
    @Override
    public Optional<Match> getMatch(String matchId) {
        return matchRepository.findById(matchId);
    }
    
    @Override
    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }
    
    @Override
    public List<Match> getLiveMatches() {
        return matchRepository.findLiveMatches();
    }
    
    @Override
    public List<Match> getUpcomingMatches() {
        return matchRepository.findUpcomingMatches();
    }
    
    @Override
    public List<Match> getCompletedMatches() {
        return matchRepository.findCompletedMatches();
    }
    
    @Override
    public List<Match> getMatchesByTeam(String teamId) {
        return matchRepository.findByTeamId(teamId);
    }
    
    @Override
    public Match startMatch(String matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
        
        if (match.getStatus() != MatchStatus.SCHEDULED) {
            throw new InvalidMatchStateException(match.getStatus(), "start match");
        }
        
        match.startMatch();
        matchRepository.save(match);
        matchSubject.notifyMatchStart(match);
        
        return match;
    }
    
    @Override
    public Match endMatch(String matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
        
        if (match.getStatus() != MatchStatus.LIVE) {
            throw new InvalidMatchStateException(match.getStatus(), "end match");
        }
        
        match.endMatch();
        matchRepository.save(match);
        matchSubject.notifyMatchEnd(match);
        
        return match;
    }
    
    @Override
    public List<Match> searchMatches(String query) {
        List<Match> allMatches = matchRepository.findAll();
        return searchStrategy.search(allMatches, query);
    }
    
    @Override
    public Scorecard getScorecard(String matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
        
        Scorecard.Builder builder = new Scorecard.Builder(matchId);
        
        for (Innings innings : match.getInnings()) {
            builder.addInningsScorecard(new Scorecard.InningsScorecard(innings));
        }
        
        if (match.getResultDescription() != null) {
            builder.setMatchResult(match.getResultDescription());
        }
        
        if (match.getManOfTheMatch() != null) {
            builder.setManOfTheMatch(match.getManOfTheMatch().getName());
        }
        
        return builder.build();
    }
    
    @Override
    public List<Commentary> getCommentary(String matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
        
        return match.getCommentaries();
    }
}



