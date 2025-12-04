package cricinfo.models;

/**
 * Represents a team's overall statistics.
 */
public class TeamStats {
    private int matchesPlayed;
    private int matchesWon;
    private int matchesLost;
    private int matchesTied;
    private int matchesDrawn;
    private int matchesNoResult;
    private int totalRunsScored;
    private int totalRunsConceded;
    private int totalWicketsTaken;
    private int totalWicketsLost;

    public TeamStats() {
    }

    public double getWinPercentage() {
        return matchesPlayed > 0 ? (double) matchesWon * 100 / matchesPlayed : 0;
    }

    public double getNetRunRate() {
        // Simplified NRR calculation
        if (matchesPlayed == 0) return 0;
        double runsPerOver = totalRunsScored / (matchesPlayed * 20.0); // Assuming T20 for simplicity
        double runsConcededPerOver = totalRunsConceded / (matchesPlayed * 20.0);
        return runsPerOver - runsConcededPerOver;
    }

    public void recordWin() {
        matchesPlayed++;
        matchesWon++;
    }

    public void recordLoss() {
        matchesPlayed++;
        matchesLost++;
    }

    public void recordTie() {
        matchesPlayed++;
        matchesTied++;
    }

    public void recordDraw() {
        matchesPlayed++;
        matchesDrawn++;
    }

    public void recordNoResult() {
        matchesPlayed++;
        matchesNoResult++;
    }

    // Getters and Setters
    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public int getMatchesWon() {
        return matchesWon;
    }

    public void setMatchesWon(int matchesWon) {
        this.matchesWon = matchesWon;
    }

    public int getMatchesLost() {
        return matchesLost;
    }

    public void setMatchesLost(int matchesLost) {
        this.matchesLost = matchesLost;
    }

    public int getMatchesTied() {
        return matchesTied;
    }

    public void setMatchesTied(int matchesTied) {
        this.matchesTied = matchesTied;
    }

    public int getMatchesDrawn() {
        return matchesDrawn;
    }

    public void setMatchesDrawn(int matchesDrawn) {
        this.matchesDrawn = matchesDrawn;
    }

    public int getMatchesNoResult() {
        return matchesNoResult;
    }

    public void setMatchesNoResult(int matchesNoResult) {
        this.matchesNoResult = matchesNoResult;
    }

    public int getTotalRunsScored() {
        return totalRunsScored;
    }

    public void setTotalRunsScored(int totalRunsScored) {
        this.totalRunsScored = totalRunsScored;
    }

    public int getTotalRunsConceded() {
        return totalRunsConceded;
    }

    public void setTotalRunsConceded(int totalRunsConceded) {
        this.totalRunsConceded = totalRunsConceded;
    }

    public int getTotalWicketsTaken() {
        return totalWicketsTaken;
    }

    public void setTotalWicketsTaken(int totalWicketsTaken) {
        this.totalWicketsTaken = totalWicketsTaken;
    }

    public int getTotalWicketsLost() {
        return totalWicketsLost;
    }

    public void setTotalWicketsLost(int totalWicketsLost) {
        this.totalWicketsLost = totalWicketsLost;
    }

    @Override
    public String toString() {
        return "TeamStats{" +
                "played=" + matchesPlayed +
                ", won=" + matchesWon +
                ", lost=" + matchesLost +
                ", winPct=" + String.format("%.2f", getWinPercentage()) + "%" +
                '}';
    }
}



