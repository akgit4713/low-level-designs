package cricinfo.strategies.scoring;

import cricinfo.models.Innings;
import cricinfo.models.Match;

/**
 * Strategy interface for calculating various scoring metrics.
 */
public interface ScoringStrategy {
    
    /**
     * Calculate the required run rate.
     */
    double calculateRequiredRunRate(Match match);
    
    /**
     * Calculate the projected score based on current run rate.
     */
    int calculateProjectedScore(Innings innings, int totalOvers);
    
    /**
     * Calculate win probability.
     */
    double calculateWinProbability(Match match, boolean forBattingTeam);
    
    /**
     * Get the strategy name.
     */
    String getStrategyName();
}



