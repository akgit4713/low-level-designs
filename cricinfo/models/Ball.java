package cricinfo.models;

import cricinfo.enums.BallType;
import cricinfo.enums.DismissalType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a single delivery/ball in a cricket match.
 */
public class Ball {
    private final String id;
    private final int overNumber;
    private final int ballNumber;
    private final Player bowler;
    private final Player batsman;
    private final Player nonStriker;
    private int runs;
    private int extras;
    private BallType ballType;
    private boolean isWicket;
    private DismissalType dismissalType;
    private Player dismissedPlayer;
    private Player fielder;  // For catches, run outs, stumpings
    private boolean isBoundary;
    private boolean isSix;
    private LocalDateTime timestamp;
    private String commentary;

    public Ball(int overNumber, int ballNumber, Player bowler, Player batsman, Player nonStriker) {
        this.id = UUID.randomUUID().toString();
        this.overNumber = overNumber;
        this.ballNumber = ballNumber;
        this.bowler = bowler;
        this.batsman = batsman;
        this.nonStriker = nonStriker;
        this.ballType = BallType.LEGAL;
        this.timestamp = LocalDateTime.now();
    }

    public int getTotalRuns() {
        return runs + extras;
    }

    public boolean isLegalDelivery() {
        return ballType.incrementsBallCount();
    }

    // Getters
    public String getId() {
        return id;
    }

    public int getOverNumber() {
        return overNumber;
    }

    public int getBallNumber() {
        return ballNumber;
    }

    public Player getBowler() {
        return bowler;
    }

    public Player getBatsman() {
        return batsman;
    }

    public Player getNonStriker() {
        return nonStriker;
    }

    public int getRuns() {
        return runs;
    }

    public int getExtras() {
        return extras;
    }

    public BallType getBallType() {
        return ballType;
    }

    public boolean isWicket() {
        return isWicket;
    }

    public DismissalType getDismissalType() {
        return dismissalType;
    }

    public Player getDismissedPlayer() {
        return dismissedPlayer;
    }

    public Player getFielder() {
        return fielder;
    }

    public boolean isBoundary() {
        return isBoundary;
    }

    public boolean isSix() {
        return isSix;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getCommentary() {
        return commentary;
    }

    // Setters
    public void setRuns(int runs) {
        this.runs = runs;
        if (runs == 4) {
            this.isBoundary = true;
        } else if (runs == 6) {
            this.isSix = true;
            this.isBoundary = true;
        }
    }

    public void setExtras(int extras) {
        this.extras = extras;
    }

    public void setBallType(BallType ballType) {
        this.ballType = ballType;
    }

    public void setWicket(boolean wicket) {
        isWicket = wicket;
    }

    public void setDismissalType(DismissalType dismissalType) {
        this.dismissalType = dismissalType;
    }

    public void setDismissedPlayer(Player dismissedPlayer) {
        this.dismissedPlayer = dismissedPlayer;
    }

    public void setFielder(Player fielder) {
        this.fielder = fielder;
    }

    public void setBoundary(boolean boundary) {
        isBoundary = boundary;
    }

    public void setSix(boolean six) {
        isSix = six;
    }

    public void setCommentary(String commentary) {
        this.commentary = commentary;
    }

    public String getBallNotation() {
        return overNumber + "." + ballNumber;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getBallNotation()).append(" ");
        sb.append(bowler.getName()).append(" to ").append(batsman.getName()).append(": ");
        
        if (isWicket) {
            sb.append("WICKET! ");
        }
        if (ballType != BallType.LEGAL) {
            sb.append(ballType.getDisplayName()).append(" ");
        }
        sb.append(getTotalRuns()).append(" run(s)");
        
        return sb.toString();
    }
}



