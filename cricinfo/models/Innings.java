package cricinfo.models;

import cricinfo.enums.InningsStatus;

import java.util.*;

/**
 * Represents an innings in a cricket match.
 */
public class Innings {
    private final String id;
    private final int inningsNumber;
    private final Team battingTeam;
    private final Team bowlingTeam;
    private int totalRuns;
    private int wickets;
    private int extras;
    private int wides;
    private int noBalls;
    private int byes;
    private int legByes;
    private int penaltyRuns;
    private final List<Over> overs;
    private final Map<String, BatsmanScore> batsmanScores;
    private final Map<String, BowlerStats> bowlerStats;
    private Player striker;
    private Player nonStriker;
    private Player currentBowler;
    private InningsStatus status;
    private int target; // For second innings
    private final List<String> fallOfWickets;

    public Innings(int inningsNumber, Team battingTeam, Team bowlingTeam) {
        this.id = UUID.randomUUID().toString();
        this.inningsNumber = inningsNumber;
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;
        this.overs = new ArrayList<>();
        this.batsmanScores = new LinkedHashMap<>();
        this.bowlerStats = new HashMap<>();
        this.status = InningsStatus.NOT_STARTED;
        this.fallOfWickets = new ArrayList<>();
    }

    public void startInnings(Player striker, Player nonStriker) {
        this.striker = striker;
        this.nonStriker = nonStriker;
        this.status = InningsStatus.IN_PROGRESS;
        
        batsmanScores.put(striker.getId(), new BatsmanScore(striker, 1));
        batsmanScores.put(nonStriker.getId(), new BatsmanScore(nonStriker, 2));
    }

    public void addBall(Ball ball) {
        if (overs.isEmpty() || getCurrentOver().isCompleted()) {
            startNewOver(ball.getBowler());
        }
        getCurrentOver().addBall(ball);
        
        // Update batsman score
        BatsmanScore strikerScore = batsmanScores.get(striker.getId());
        if (ball.isLegalDelivery()) {
            strikerScore.addBallFaced();
        }
        strikerScore.addRuns(ball.getRuns());
        
        // Update total runs
        totalRuns += ball.getTotalRuns();
        extras += ball.getExtras();
        
        // Update bowler stats
        BowlerStats bowler = bowlerStats.get(ball.getBowler().getId());
        bowler.addBall(ball.isLegalDelivery());
        bowler.addRuns(ball.getTotalRuns());
        
        // Handle wicket
        if (ball.isWicket()) {
            wickets++;
            strikerScore.setDismissed(ball.getDismissalType(), ball.getBowler(), ball.getFielder());
            if (ball.getDismissalType().isCreditedToBowler()) {
                bowler.addWicket();
            }
            fallOfWickets.add(String.format("%d-%d (%s, %.1f ov)",
                    wickets, totalRuns, striker.getName(), getOversCompleted()));
            
            if (wickets >= 10) {
                status = InningsStatus.ALL_OUT;
            }
        }
        
        // Rotate strike if odd runs
        if (ball.getRuns() % 2 == 1) {
            rotateStrike();
        }
    }

    public void startNewOver(Player bowler) {
        Over newOver = new Over(overs.size() + 1, bowler);
        overs.add(newOver);
        currentBowler = bowler;
        
        if (!bowlerStats.containsKey(bowler.getId())) {
            bowlerStats.put(bowler.getId(), new BowlerStats(bowler));
        }
        
        // Rotate strike at end of over
        if (overs.size() > 1) {
            rotateStrike();
        }
    }

    public void sendNewBatsman(Player newBatsman) {
        striker = newBatsman;
        int position = batsmanScores.size() + 1;
        batsmanScores.put(newBatsman.getId(), new BatsmanScore(newBatsman, position));
    }

    public void rotateStrike() {
        Player temp = striker;
        striker = nonStriker;
        nonStriker = temp;
    }

    public void declareInnings() {
        status = InningsStatus.DECLARED;
    }

    public void endInnings() {
        if (status == InningsStatus.IN_PROGRESS) {
            status = InningsStatus.COMPLETED;
        }
    }

    public Over getCurrentOver() {
        return overs.isEmpty() ? null : overs.get(overs.size() - 1);
    }

    public double getOversCompleted() {
        if (overs.isEmpty()) return 0;
        Over currentOver = getCurrentOver();
        int completedOvers = (int) overs.stream().filter(Over::isCompleted).count();
        int ballsInCurrent = currentOver.isCompleted() ? 0 : currentOver.getLegalBallCount();
        return completedOvers + (ballsInCurrent / 10.0);
    }

    public double getRunRate() {
        double oversCompleted = getOversCompleted();
        return oversCompleted > 0 ? totalRuns / oversCompleted : 0;
    }

    public double getRequiredRunRate() {
        if (target <= 0) return 0;
        int runsRequired = target - totalRuns;
        // Assuming limited overs match with max overs
        double oversRemaining = 20 - getOversCompleted(); // Simplified for T20
        return oversRemaining > 0 ? runsRequired / oversRemaining : 0;
    }

    public String getScore() {
        return totalRuns + "/" + wickets;
    }

    public String getScoreWithOvers() {
        return String.format("%d/%d (%.1f ov)", totalRuns, wickets, getOversCompleted());
    }

    // Getters
    public String getId() {
        return id;
    }

    public int getInningsNumber() {
        return inningsNumber;
    }

    public Team getBattingTeam() {
        return battingTeam;
    }

    public Team getBowlingTeam() {
        return bowlingTeam;
    }

    public int getTotalRuns() {
        return totalRuns;
    }

    public int getWickets() {
        return wickets;
    }

    public int getExtras() {
        return extras;
    }

    public List<Over> getOvers() {
        return Collections.unmodifiableList(overs);
    }

    public Map<String, BatsmanScore> getBatsmanScores() {
        return Collections.unmodifiableMap(batsmanScores);
    }

    public Map<String, BowlerStats> getBowlerStats() {
        return Collections.unmodifiableMap(bowlerStats);
    }

    public Player getStriker() {
        return striker;
    }

    public Player getNonStriker() {
        return nonStriker;
    }

    public Player getCurrentBowler() {
        return currentBowler;
    }

    public InningsStatus getStatus() {
        return status;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public List<String> getFallOfWickets() {
        return Collections.unmodifiableList(fallOfWickets);
    }

    public void setStatus(InningsStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("%s: %s | RR: %.2f",
                battingTeam.getName(),
                getScoreWithOvers(),
                getRunRate());
    }
}



