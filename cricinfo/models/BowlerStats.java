package cricinfo.models;

/**
 * Represents a bowler's bowling figures in an innings.
 */
public class BowlerStats {
    private final Player player;
    private int overs;
    private int ballsInCurrentOver;
    private int maidens;
    private int runsConceded;
    private int wickets;
    private int wides;
    private int noBalls;
    private int dotBalls;

    public BowlerStats(Player player) {
        this.player = player;
    }

    public void addBall(boolean isLegal) {
        if (isLegal) {
            ballsInCurrentOver++;
            if (ballsInCurrentOver == 6) {
                overs++;
                ballsInCurrentOver = 0;
            }
        }
    }

    public void addRuns(int runs) {
        runsConceded += runs;
        if (runs == 0) {
            dotBalls++;
        }
    }

    public void addWicket() {
        wickets++;
    }

    public void addWide() {
        wides++;
        runsConceded++;
    }

    public void addNoBall() {
        noBalls++;
        runsConceded++;
    }

    public void completeMaiden() {
        maidens++;
    }

    public double getEconomyRate() {
        double totalOvers = overs + (ballsInCurrentOver / 6.0);
        return totalOvers > 0 ? runsConceded / totalOvers : 0;
    }

    public String getOversString() {
        if (ballsInCurrentOver == 0) {
            return String.valueOf(overs);
        }
        return overs + "." + ballsInCurrentOver;
    }

    public int getTotalBalls() {
        return overs * 6 + ballsInCurrentOver;
    }

    // Getters
    public Player getPlayer() {
        return player;
    }

    public int getOvers() {
        return overs;
    }

    public int getBallsInCurrentOver() {
        return ballsInCurrentOver;
    }

    public int getMaidens() {
        return maidens;
    }

    public int getRunsConceded() {
        return runsConceded;
    }

    public int getWickets() {
        return wickets;
    }

    public int getWides() {
        return wides;
    }

    public int getNoBalls() {
        return noBalls;
    }

    public int getDotBalls() {
        return dotBalls;
    }

    public String getFigures() {
        return wickets + "/" + runsConceded;
    }

    @Override
    public String toString() {
        return String.format("%s %s-%d-%d-%d (Econ: %.2f)",
                player.getName(),
                getOversString(),
                maidens,
                runsConceded,
                wickets,
                getEconomyRate());
    }
}



