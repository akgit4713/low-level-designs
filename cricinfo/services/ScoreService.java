package cricinfo.services;

import cricinfo.models.*;

/**
 * Service interface for score-related operations.
 */
public interface ScoreService {
    
    /**
     * Start a new innings.
     */
    Innings startInnings(Match match, Team battingTeam, Team bowlingTeam,
                         Player striker, Player nonStriker);
    
    /**
     * Record a ball being bowled.
     */
    void recordBall(Match match, Ball ball);
    
    /**
     * Send a new batsman.
     */
    void sendNewBatsman(Match match, Player batsman);
    
    /**
     * Change the bowler.
     */
    void changeBowler(Match match, Player bowler);
    
    /**
     * End the current innings.
     */
    void endInnings(Match match);
    
    /**
     * Declare the innings.
     */
    void declareInnings(Match match);
    
    /**
     * Get the current live score string.
     */
    String getLiveScore(Match match);
    
    /**
     * Get required run rate.
     */
    double getRequiredRunRate(Match match);
    
    /**
     * Get projected score.
     */
    int getProjectedScore(Match match);
    
    /**
     * Get win probability for batting team.
     */
    double getWinProbability(Match match);
}



