package cricinfo.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an over in a cricket match containing multiple balls.
 */
public class Over {
    private final int overNumber;
    private final Player bowler;
    private final List<Ball> balls;
    private boolean completed;

    public Over(int overNumber, Player bowler) {
        this.overNumber = overNumber;
        this.bowler = bowler;
        this.balls = new ArrayList<>();
        this.completed = false;
    }

    public void addBall(Ball ball) {
        balls.add(ball);
        if (getLegalBallCount() >= 6) {
            completed = true;
        }
    }

    public int getLegalBallCount() {
        return (int) balls.stream()
                .filter(Ball::isLegalDelivery)
                .count();
    }

    public int getTotalRuns() {
        return balls.stream()
                .mapToInt(Ball::getTotalRuns)
                .sum();
    }

    public int getWickets() {
        return (int) balls.stream()
                .filter(Ball::isWicket)
                .count();
    }

    public boolean isMaiden() {
        return completed && getTotalRuns() == 0 && getWickets() == 0;
    }

    public int getBoundaries() {
        return (int) balls.stream()
                .filter(b -> b.isBoundary() && !b.isSix())
                .count();
    }

    public int getSixes() {
        return (int) balls.stream()
                .filter(Ball::isSix)
                .count();
    }

    // Getters
    public int getOverNumber() {
        return overNumber;
    }

    public Player getBowler() {
        return bowler;
    }

    public List<Ball> getBalls() {
        return Collections.unmodifiableList(balls);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getOverSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Over ").append(overNumber).append(": ");
        for (Ball ball : balls) {
            if (!ball.isLegalDelivery()) {
                sb.append("(").append(ball.getBallType().name().charAt(0)).append(")");
            }
            if (ball.isWicket()) {
                sb.append("W");
            } else {
                sb.append(ball.getTotalRuns());
            }
            sb.append(" ");
        }
        sb.append("| ").append(getTotalRuns()).append(" runs");
        if (isMaiden()) {
            sb.append(" (Maiden)");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getOverSummary();
    }
}



