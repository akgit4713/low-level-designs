package cricinfo.observers;

import cricinfo.models.Ball;
import cricinfo.models.Match;

/**
 * Subject interface for match events.
 * Part of Observer pattern implementation.
 */
public interface MatchSubject {
    
    /**
     * Register an observer.
     */
    void registerObserver(MatchObserver observer);
    
    /**
     * Remove an observer.
     */
    void removeObserver(MatchObserver observer);
    
    /**
     * Notify all observers of match start.
     */
    void notifyMatchStart(Match match);
    
    /**
     * Notify all observers of a ball being bowled.
     */
    void notifyBallBowled(Match match, Ball ball);
    
    /**
     * Notify all observers of a wicket.
     */
    void notifyWicket(Match match, Ball ball);
    
    /**
     * Notify all observers of innings end.
     */
    void notifyInningsEnd(Match match, int inningsNumber);
    
    /**
     * Notify all observers of match end.
     */
    void notifyMatchEnd(Match match);
    
    /**
     * Notify all observers of score update.
     */
    void notifyScoreUpdate(Match match, String score);
}



