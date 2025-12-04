package cricinfo.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a commentary entry for a ball or event in the match.
 */
public class Commentary {
    private final String id;
    private final String matchId;
    private final int inningsNumber;
    private final double overNumber;
    private final LocalDateTime timestamp;
    private final String text;
    private final CommentaryType type;
    private final Ball ball; // Can be null for non-ball events

    public enum CommentaryType {
        BALL_BY_BALL,
        WICKET,
        BOUNDARY,
        MILESTONE,
        END_OF_OVER,
        DRINKS_BREAK,
        INNINGS_BREAK,
        MATCH_EVENT,
        REVIEW
    }

    public Commentary(String matchId, int inningsNumber, double overNumber, 
                      String text, CommentaryType type, Ball ball) {
        this.id = UUID.randomUUID().toString();
        this.matchId = matchId;
        this.inningsNumber = inningsNumber;
        this.overNumber = overNumber;
        this.timestamp = LocalDateTime.now();
        this.text = text;
        this.type = type;
        this.ball = ball;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getMatchId() {
        return matchId;
    }

    public int getInningsNumber() {
        return inningsNumber;
    }

    public double getOverNumber() {
        return overNumber;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

    public CommentaryType getType() {
        return type;
    }

    public Ball getBall() {
        return ball;
    }

    public String getFormattedCommentary() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%.1f", overNumber)).append(" ");
        
        if (type == CommentaryType.WICKET) {
            sb.append("[WICKET] ");
        } else if (type == CommentaryType.BOUNDARY) {
            sb.append("[FOUR/SIX] ");
        }
        
        sb.append(text);
        return sb.toString();
    }

    @Override
    public String toString() {
        return getFormattedCommentary();
    }
}



