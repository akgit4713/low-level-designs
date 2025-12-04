package cricinfo.models;

/**
 * Represents a player's career statistics.
 */
public class PlayerStats {
    // Batting stats
    private int matchesPlayed;
    private int inningsPlayed;
    private int totalRuns;
    private int highestScore;
    private int centuries;
    private int halfCenturies;
    private int fours;
    private int sixes;
    private int ballsFaced;
    private int notOuts;

    // Bowling stats
    private int wicketsTaken;
    private int runsConceded;
    private int ballsBowled;
    private int maidens;
    private int fiveWicketHauls;
    private int tenWicketHauls;
    private int bestBowlingWickets;
    private int bestBowlingRuns;

    // Fielding stats
    private int catches;
    private int stumpings;
    private int runOuts;

    public PlayerStats() {
    }

    // Batting calculations
    public double getBattingAverage() {
        int dismissals = inningsPlayed - notOuts;
        return dismissals > 0 ? (double) totalRuns / dismissals : totalRuns;
    }

    public double getStrikeRate() {
        return ballsFaced > 0 ? (double) totalRuns * 100 / ballsFaced : 0;
    }

    // Bowling calculations
    public double getBowlingAverage() {
        return wicketsTaken > 0 ? (double) runsConceded / wicketsTaken : 0;
    }

    public double getEconomyRate() {
        double overs = ballsBowled / 6.0;
        return overs > 0 ? runsConceded / overs : 0;
    }

    public double getBowlingStrikeRate() {
        return wicketsTaken > 0 ? (double) ballsBowled / wicketsTaken : 0;
    }

    // Getters and Setters
    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public int getInningsPlayed() {
        return inningsPlayed;
    }

    public void setInningsPlayed(int inningsPlayed) {
        this.inningsPlayed = inningsPlayed;
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(int totalRuns) {
        this.totalRuns = totalRuns;
    }

    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public int getCenturies() {
        return centuries;
    }

    public void setCenturies(int centuries) {
        this.centuries = centuries;
    }

    public int getHalfCenturies() {
        return halfCenturies;
    }

    public void setHalfCenturies(int halfCenturies) {
        this.halfCenturies = halfCenturies;
    }

    public int getFours() {
        return fours;
    }

    public void setFours(int fours) {
        this.fours = fours;
    }

    public int getSixes() {
        return sixes;
    }

    public void setSixes(int sixes) {
        this.sixes = sixes;
    }

    public int getBallsFaced() {
        return ballsFaced;
    }

    public void setBallsFaced(int ballsFaced) {
        this.ballsFaced = ballsFaced;
    }

    public int getNotOuts() {
        return notOuts;
    }

    public void setNotOuts(int notOuts) {
        this.notOuts = notOuts;
    }

    public int getWicketsTaken() {
        return wicketsTaken;
    }

    public void setWicketsTaken(int wicketsTaken) {
        this.wicketsTaken = wicketsTaken;
    }

    public int getRunsConceded() {
        return runsConceded;
    }

    public void setRunsConceded(int runsConceded) {
        this.runsConceded = runsConceded;
    }

    public int getBallsBowled() {
        return ballsBowled;
    }

    public void setBallsBowled(int ballsBowled) {
        this.ballsBowled = ballsBowled;
    }

    public int getMaidens() {
        return maidens;
    }

    public void setMaidens(int maidens) {
        this.maidens = maidens;
    }

    public int getFiveWicketHauls() {
        return fiveWicketHauls;
    }

    public void setFiveWicketHauls(int fiveWicketHauls) {
        this.fiveWicketHauls = fiveWicketHauls;
    }

    public int getTenWicketHauls() {
        return tenWicketHauls;
    }

    public void setTenWicketHauls(int tenWicketHauls) {
        this.tenWicketHauls = tenWicketHauls;
    }

    public int getBestBowlingWickets() {
        return bestBowlingWickets;
    }

    public void setBestBowlingWickets(int bestBowlingWickets) {
        this.bestBowlingWickets = bestBowlingWickets;
    }

    public int getBestBowlingRuns() {
        return bestBowlingRuns;
    }

    public void setBestBowlingRuns(int bestBowlingRuns) {
        this.bestBowlingRuns = bestBowlingRuns;
    }

    public int getCatches() {
        return catches;
    }

    public void setCatches(int catches) {
        this.catches = catches;
    }

    public int getStumpings() {
        return stumpings;
    }

    public void setStumpings(int stumpings) {
        this.stumpings = stumpings;
    }

    public int getRunOuts() {
        return runOuts;
    }

    public void setRunOuts(int runOuts) {
        this.runOuts = runOuts;
    }

    public void addRuns(int runs) {
        this.totalRuns += runs;
        if (runs > this.highestScore) {
            this.highestScore = runs;
        }
    }

    public void addWicket() {
        this.wicketsTaken++;
    }

    @Override
    public String toString() {
        return "PlayerStats{" +
                "matchesPlayed=" + matchesPlayed +
                ", totalRuns=" + totalRuns +
                ", battingAvg=" + String.format("%.2f", getBattingAverage()) +
                ", wickets=" + wicketsTaken +
                ", bowlingAvg=" + String.format("%.2f", getBowlingAverage()) +
                '}';
    }
}



