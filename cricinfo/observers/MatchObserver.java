package cricinfo.observers;

import cricinfo.models.Ball;
import cricinfo.models.Match;

/**
 * Observer interface for match events.
 * Implements Observer pattern for live score updates.
 */
public interface MatchObserver {
    
    /**
     * Called when the match starts.
     */
    void onMatchStart(Match match);
    
    /**
     * Called when a ball is bowled.
     */
    void onBallBowled(Match match, Ball ball);
    
    /**
     * Called when a wicket falls.
     */
    void onWicket(Match match, Ball ball);
    
    /**
     * Called when an innings ends.
     */
    void onInningsEnd(Match match, int inningsNumber);
    
    /**
     * Called when the match ends.
     */
    void onMatchEnd(Match match);
    
    /**
     * Called when there's a score update.
     */
    void onScoreUpdate(Match match, String score);
}



