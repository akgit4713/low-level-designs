package cricinfo.models;

import cricinfo.enums.DismissalType;

/**
 * Represents a batsman's score in an innings.
 */
public class BatsmanScore {
    private final Player player;
    private int runs;
    private int ballsFaced;
    private int fours;
    private int sixes;
    private boolean isOut;
    private DismissalType dismissalType;
    private Player bowler;
    private Player fielder;
    private int battingPosition;

    public BatsmanScore(Player player, int battingPosition) {
        this.player = player;
        this.battingPosition = battingPosition;
    }

    public void addRuns(int runs) {
        this.runs += runs;
        if (runs == 4) {
            fours++;
        } else if (runs == 6) {
            sixes++;
        }
    }

    public void addBallFaced() {
        ballsFaced++;
    }

    public void setDismissed(DismissalType dismissalType, Player bowler, Player fielder) {
        this.isOut = true;
        this.dismissalType = dismissalType;
        this.bowler = bowler;
        this.fielder = fielder;
    }

    public double getStrikeRate() {
        return ballsFaced > 0 ? (double) runs * 100 / ballsFaced : 0;
    }

    // Getters
    public Player getPlayer() {
        return player;
    }

    public int getRuns() {
        return runs;
    }

    public int getBallsFaced() {
        return ballsFaced;
    }

    public int getFours() {
        return fours;
    }

    public int getSixes() {
        return sixes;
    }

    public boolean isOut() {
        return isOut;
    }

    public DismissalType getDismissalType() {
        return dismissalType;
    }

    public Player getBowler() {
        return bowler;
    }

    public Player getFielder() {
        return fielder;
    }

    public int getBattingPosition() {
        return battingPosition;
    }

    public void setBattingPosition(int battingPosition) {
        this.battingPosition = battingPosition;
    }

    public String getDismissalString() {
        if (!isOut) {
            return "not out";
        }
        
        StringBuilder sb = new StringBuilder();
        switch (dismissalType) {
            case BOWLED:
                sb.append("b ").append(bowler.getName());
                break;
            case CAUGHT:
                sb.append("c ").append(fielder.getName()).append(" b ").append(bowler.getName());
                break;
            case LBW:
                sb.append("lbw b ").append(bowler.getName());
                break;
            case RUN_OUT:
                sb.append("run out (").append(fielder != null ? fielder.getName() : "").append(")");
                break;
            case STUMPED:
                sb.append("st ").append(fielder.getName()).append(" b ").append(bowler.getName());
                break;
            case HIT_WICKET:
                sb.append("hit wicket b ").append(bowler.getName());
                break;
            default:
                sb.append(dismissalType.getDisplayName());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%s %s %d (%d) [4s: %d, 6s: %d] SR: %.2f",
                player.getName(),
                getDismissalString(),
                runs,
                ballsFaced,
                fours,
                sixes,
                getStrikeRate());
    }
}



