package cricinfo.observers;

import cricinfo.models.Ball;
import cricinfo.models.Match;

/**
 * Console-based observer that prints match events to console.
 * Useful for debugging and demonstration.
 */
public class ConsoleScoreObserver implements MatchObserver {
    
    private final String observerName;
    
    public ConsoleScoreObserver(String observerName) {
        this.observerName = observerName;
    }
    
    @Override
    public void onMatchStart(Match match) {
        System.out.println("[" + observerName + "] 🏏 MATCH STARTED: " + match.getTitle());
    }
    
    @Override
    public void onBallBowled(Match match, Ball ball) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(observerName).append("] ");
        sb.append(ball.getBallNotation()).append(" | ");
        sb.append(ball.getBowler().getName()).append(" to ").append(ball.getBatsman().getName());
        sb.append(" - ").append(ball.getTotalRuns()).append(" run(s)");
        
        if (ball.isSix()) {
            sb.append(" 🎯 SIX!");
        } else if (ball.isBoundary()) {
            sb.append(" 🎯 FOUR!");
        }
        
        System.out.println(sb);
    }
    
    @Override
    public void onWicket(Match match, Ball ball) {
        System.out.println("[" + observerName + "] ⚡ WICKET! " + 
                ball.getDismissedPlayer().getName() + " is OUT! " + 
                ball.getDismissalType().getDisplayName());
        System.out.println("[" + observerName + "] Score: " + match.getLiveScore());
    }
    
    @Override
    public void onInningsEnd(Match match, int inningsNumber) {
        System.out.println("[" + observerName + "] 📋 INNINGS " + inningsNumber + " ENDED");
        System.out.println("[" + observerName + "] Score: " + match.getLiveScore());
    }
    
    @Override
    public void onMatchEnd(Match match) {
        System.out.println("[" + observerName + "] 🏆 MATCH ENDED");
        if (match.getResultDescription() != null) {
            System.out.println("[" + observerName + "] Result: " + match.getResultDescription());
        }
    }
    
    @Override
    public void onScoreUpdate(Match match, String score) {
        System.out.println("[" + observerName + "] 📊 Score Update: " + score);
    }
}



