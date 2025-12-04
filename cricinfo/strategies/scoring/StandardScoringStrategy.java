package cricinfo.strategies.scoring;

import cricinfo.models.Innings;
import cricinfo.models.Match;

/**
 * Standard scoring calculation strategy.
 */
public class StandardScoringStrategy implements ScoringStrategy {
    
    @Override
    public double calculateRequiredRunRate(Match match) {
        if (match.getInnings().size() < 2) {
            return 0;
        }
        
        Innings firstInnings = match.getInnings().get(0);
        Innings secondInnings = match.getInnings().get(1);
        
        int target = firstInnings.getTotalRuns() + 1;
        int runsRequired = target - secondInnings.getTotalRuns();
        
        double oversRemaining = match.getFormat().getOversPerInnings() - 
                secondInnings.getOversCompleted();
        
        return oversRemaining > 0 ? runsRequired / oversRemaining : Double.POSITIVE_INFINITY;
    }
    
    @Override
    public int calculateProjectedScore(Innings innings, int totalOvers) {
        double currentRunRate = innings.getRunRate();
        double oversRemaining = totalOvers - innings.getOversCompleted();
        
        return innings.getTotalRuns() + (int) (currentRunRate * oversRemaining);
    }
    
    @Override
    public double calculateWinProbability(Match match, boolean forBattingTeam) {
        if (match.getInnings().size() < 2) {
            return 0.5; // Equal probability before chase begins
        }
        
        Innings chasingInnings = match.getInnings().get(1);
        Innings firstInnings = match.getInnings().get(0);
        
        int target = firstInnings.getTotalRuns() + 1;
        int runsRequired = target - chasingInnings.getTotalRuns();
        int wicketsRemaining = 10 - chasingInnings.getWickets();
        double oversRemaining = match.getFormat().getOversPerInnings() - 
                chasingInnings.getOversCompleted();
        
        // Simple probability calculation based on required rate vs current rate
        double requiredRate = oversRemaining > 0 ? runsRequired / oversRemaining : Double.MAX_VALUE;
        double currentRate = chasingInnings.getRunRate();
        
        // Factor in wickets
        double wicketFactor = wicketsRemaining / 10.0;
        
        // Simple probability model
        double rateRatio = currentRate / Math.max(requiredRate, 0.1);
        double probability = Math.min(1.0, Math.max(0.0, 
                (rateRatio * 0.6 + wicketFactor * 0.4)));
        
        return forBattingTeam ? probability : 1 - probability;
    }
    
    @Override
    public String getStrategyName() {
        return "StandardScoringStrategy";
    }
}



