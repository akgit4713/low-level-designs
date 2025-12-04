package cricinfo.repositories.impl;

import cricinfo.enums.MatchStatus;
import cricinfo.models.Match;
import cricinfo.repositories.MatchRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of MatchRepository.
 * Uses ConcurrentHashMap for thread safety.
 */
public class InMemoryMatchRepository implements MatchRepository {
    
    private final Map<String, Match> matches = new ConcurrentHashMap<>();
    
    @Override
    public Match save(Match match) {
        matches.put(match.getId(), match);
        return match;
    }
    
    @Override
    public Optional<Match> findById(String matchId) {
        return Optional.ofNullable(matches.get(matchId));
    }
    
    @Override
    public List<Match> findAll() {
        return new ArrayList<>(matches.values());
    }
    
    @Override
    public List<Match> findByStatus(MatchStatus status) {
        return matches.values().stream()
                .filter(m -> m.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Match> findLiveMatches() {
        return findByStatus(MatchStatus.LIVE);
    }
    
    @Override
    public List<Match> findUpcomingMatches() {
        return matches.values().stream()
                .filter(Match::isUpcoming)
                .sorted(Comparator.comparing(Match::getStartTime, 
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Match> findCompletedMatches() {
        return matches.values().stream()
                .filter(Match::isCompleted)
                .sorted(Comparator.comparing(Match::getEndTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Match> findByTeamId(String teamId) {
        return matches.values().stream()
                .filter(m -> m.getTeam1().getId().equals(teamId) || 
                            m.getTeam2().getId().equals(teamId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Match> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return matches.values().stream()
                .filter(m -> m.getStartTime() != null)
                .filter(m -> !m.getStartTime().isBefore(start) && 
                            !m.getStartTime().isAfter(end))
                .sorted(Comparator.comparing(Match::getStartTime))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Match> findBySeriesName(String seriesName) {
        return matches.values().stream()
                .filter(m -> seriesName.equalsIgnoreCase(m.getSeriesName()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void delete(String matchId) {
        matches.remove(matchId);
    }
    
    @Override
    public boolean exists(String matchId) {
        return matches.containsKey(matchId);
    }
}



