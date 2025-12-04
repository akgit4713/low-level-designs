package cricinfo.models;

import cricinfo.enums.MatchFormat;
import cricinfo.enums.MatchResult;
import cricinfo.enums.MatchStatus;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a cricket match.
 */
public class Match {
    private final String id;
    private String title;
    private String seriesName;
    private MatchFormat format;
    private MatchStatus status;
    private Team team1;
    private Team team2;
    private Venue venue;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Team tossWinner;
    private String tossDecision;
    private final List<Innings> innings;
    private MatchResult result;
    private Team winner;
    private String resultDescription;
    private Player manOfTheMatch;
    private final List<Commentary> commentaries;

    public Match(Team team1, Team team2, MatchFormat format) {
        this.id = UUID.randomUUID().toString();
        this.team1 = team1;
        this.team2 = team2;
        this.format = format;
        this.status = MatchStatus.SCHEDULED;
        this.innings = new ArrayList<>();
        this.commentaries = new ArrayList<>();
        this.title = team1.getName() + " vs " + team2.getName();
    }

    public Match(String id, Team team1, Team team2, MatchFormat format) {
        this.id = id;
        this.team1 = team1;
        this.team2 = team2;
        this.format = format;
        this.status = MatchStatus.SCHEDULED;
        this.innings = new ArrayList<>();
        this.commentaries = new ArrayList<>();
        this.title = team1.getName() + " vs " + team2.getName();
    }

    public void startMatch() {
        this.status = MatchStatus.LIVE;
        this.startTime = LocalDateTime.now();
    }

    public void endMatch() {
        this.status = MatchStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }

    public Innings startNewInnings(Team battingTeam, Team bowlingTeam) {
        Innings newInnings = new Innings(innings.size() + 1, battingTeam, bowlingTeam);
        innings.add(newInnings);
        return newInnings;
    }

    public Innings getCurrentInnings() {
        return innings.isEmpty() ? null : innings.get(innings.size() - 1);
    }

    public void addCommentary(Commentary commentary) {
        commentaries.add(0, commentary); // Add to front for latest first
    }

    public void setToss(Team tossWinner, String decision) {
        this.tossWinner = tossWinner;
        this.tossDecision = decision;
    }

    public void setResult(MatchResult result, Team winner, String description) {
        this.result = result;
        this.winner = winner;
        this.resultDescription = description;
    }

    public String getLiveScore() {
        if (innings.isEmpty()) {
            return "Match not started";
        }
        Innings current = getCurrentInnings();
        StringBuilder sb = new StringBuilder();
        sb.append(current.getBattingTeam().getName()).append(": ");
        sb.append(current.getScoreWithOvers());
        
        if (current.getTarget() > 0) {
            int required = current.getTarget() - current.getTotalRuns();
            int ballsRemaining = (format.getOversPerInnings() * 6) - 
                    (current.getOvers().size() * 6 + 
                     (current.getCurrentOver() != null ? current.getCurrentOver().getLegalBallCount() : 0));
            sb.append(String.format(" | Need %d runs from %d balls", required, ballsRemaining));
        }
        
        return sb.toString();
    }

    public boolean isLive() {
        return status == MatchStatus.LIVE;
    }

    public boolean isCompleted() {
        return status == MatchStatus.COMPLETED;
    }

    public boolean isUpcoming() {
        return status == MatchStatus.SCHEDULED;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public MatchFormat getFormat() {
        return format;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public Team getTeam1() {
        return team1;
    }

    public Team getTeam2() {
        return team2;
    }

    public Venue getVenue() {
        return venue;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Team getTossWinner() {
        return tossWinner;
    }

    public String getTossDecision() {
        return tossDecision;
    }

    public List<Innings> getInnings() {
        return Collections.unmodifiableList(innings);
    }

    public MatchResult getResult() {
        return result;
    }

    public Team getWinner() {
        return winner;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public Player getManOfTheMatch() {
        return manOfTheMatch;
    }

    public List<Commentary> getCommentaries() {
        return Collections.unmodifiableList(commentaries);
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public void setFormat(MatchFormat format) {
        this.format = format;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setManOfTheMatch(Player manOfTheMatch) {
        this.manOfTheMatch = manOfTheMatch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        if (venue != null) {
            sb.append(" at ").append(venue.getName());
        }
        sb.append(" | ").append(format.getDisplayName());
        sb.append(" | ").append(status.getDescription());
        if (isLive()) {
            sb.append("\n").append(getLiveScore());
        }
        if (resultDescription != null) {
            sb.append("\nResult: ").append(resultDescription);
        }
        return sb.toString();
    }
}



