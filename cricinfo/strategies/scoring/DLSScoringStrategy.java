package cricinfo.strategies.scoring;

import cricinfo.models.Innings;
import cricinfo.models.Match;

/**
 * Duckworth-Lewis-Stern (DLS) scoring strategy for rain-affected matches.
 * This is a simplified implementation for demonstration purposes.
 */
public class DLSScoringStrategy implements ScoringStrategy {
    
    // Simplified DLS resource table (percentage of resources remaining)
    // Indexed by [overs remaining][wickets lost]
    private static final double[][] RESOURCE_TABLE = {
        // 0 wickets, 1 wicket, ..., 9 wickets (for 20 overs remaining)
        {100.0, 96.5, 91.6, 84.3, 73.5, 59.5, 44.6, 30.2, 17.9, 8.3}, // 20 overs
        {95.0, 91.8, 87.2, 80.3, 70.1, 56.8, 42.6, 28.9, 17.1, 7.9},  // 18 overs
        {87.5, 84.7, 80.4, 74.1, 64.7, 52.4, 39.4, 26.7, 15.8, 7.3},  // 15 overs
        {77.5, 75.1, 71.3, 65.7, 57.4, 46.5, 34.9, 23.7, 14.0, 6.5},  // 12 overs
        {65.0, 62.9, 59.7, 55.1, 48.1, 39.0, 29.2, 19.8, 11.7, 5.5},  // 10 overs
        {50.0, 48.4, 46.0, 42.4, 37.0, 30.0, 22.5, 15.3, 9.0, 4.2},   // 7 overs
        {32.5, 31.5, 29.9, 27.5, 24.0, 19.5, 14.6, 9.9, 5.9, 2.7},    // 5 overs
        {15.0, 14.5, 13.8, 12.7, 11.1, 9.0, 6.7, 4.6, 2.7, 1.3},      // 2 overs
        {5.0, 4.8, 4.6, 4.2, 3.7, 3.0, 2.2, 1.5, 0.9, 0.4}            // 1 over
    };
    
    @Override
    public double calculateRequiredRunRate(Match match) {
        // Calculate DLS par score first
        int dlsTarget = calculateDLSTarget(match);
        
        if (match.getInnings().size() < 2) {
            return 0;
        }
        
        Innings chasingInnings = match.getInnings().get(1);
        int runsRequired = dlsTarget - chasingInnings.getTotalRuns();
        double oversRemaining = match.getFormat().getOversPerInnings() - 
                chasingInnings.getOversCompleted();
        
        return oversRemaining > 0 ? runsRequired / oversRemaining : Double.POSITIVE_INFINITY;
    }
    
    @Override
    public int calculateProjectedScore(Innings innings, int totalOvers) {
        double resourcesUsed = getResourcePercentage(totalOvers, 0) - 
                getResourcePercentage(totalOvers - innings.getOversCompleted(), 
                        innings.getWickets());
        
        double resourcesRemaining = 100 - resourcesUsed;
        
        if (resourcesUsed <= 0) {
            return innings.getTotalRuns();
        }
        
        double runsPerResource = innings.getTotalRuns() / resourcesUsed;
        return innings.getTotalRuns() + (int) (runsPerResource * resourcesRemaining);
    }
    
    @Override
    public double calculateWinProbability(Match match, boolean forBattingTeam) {
        if (match.getInnings().size() < 2) {
            return 0.5;
        }
        
        int dlsTarget = calculateDLSTarget(match);
        Innings chasingInnings = match.getInnings().get(1);
        
        int runsRequired = dlsTarget - chasingInnings.getTotalRuns();
        double resourcesRemaining = getResourcePercentage(
                match.getFormat().getOversPerInnings() - chasingInnings.getOversCompleted(),
                chasingInnings.getWickets());
        
        // Simple probability based on runs required vs resources
        double expectedRuns = (resourcesRemaining / 100.0) * 
                match.getInnings().get(0).getTotalRuns();
        
        double probability = Math.min(1.0, Math.max(0.0, 
                expectedRuns / Math.max(runsRequired, 1)));
        
        return forBattingTeam ? probability : 1 - probability;
    }
    
    /**
     * Calculate DLS target for the chasing team.
     */
    public int calculateDLSTarget(Match match) {
        if (match.getInnings().isEmpty()) {
            return 0;
        }
        
        Innings firstInnings = match.getInnings().get(0);
        int firstInningsScore = firstInnings.getTotalRuns();
        int totalOvers = match.getFormat().getOversPerInnings();
        
        // Get resources for first innings
        double team1Resources = getResourcePercentage(totalOvers, 0);
        
        // Get resources for second innings (assuming full overs available)
        double team2Resources = getResourcePercentage(totalOvers, 0);
        
        // Calculate par score
        double parScore = firstInningsScore * (team2Resources / team1Resources);
        
        return (int) Math.ceil(parScore) + 1; // Target is par + 1
    }
    
    private double getResourcePercentage(double oversRemaining, int wicketsLost) {
        if (oversRemaining <= 0 || wicketsLost >= 10) {
            return 0;
        }
        
        // Map overs to table index
        int oversIndex;
        if (oversRemaining >= 20) oversIndex = 0;
        else if (oversRemaining >= 18) oversIndex = 1;
        else if (oversRemaining >= 15) oversIndex = 2;
        else if (oversRemaining >= 12) oversIndex = 3;
        else if (oversRemaining >= 10) oversIndex = 4;
        else if (oversRemaining >= 7) oversIndex = 5;
        else if (oversRemaining >= 5) oversIndex = 6;
        else if (oversRemaining >= 2) oversIndex = 7;
        else oversIndex = 8;
        
        int wicketsIndex = Math.min(wicketsLost, 9);
        
        return RESOURCE_TABLE[oversIndex][wicketsIndex];
    }
    
    @Override
    public String getStrategyName() {
        return "DLSScoringStrategy";
    }
}



