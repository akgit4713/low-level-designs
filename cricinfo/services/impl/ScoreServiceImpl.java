package cricinfo.services.impl;

import cricinfo.enums.InningsStatus;
import cricinfo.exceptions.InvalidMatchStateException;
import cricinfo.exceptions.InvalidScoreUpdateException;
import cricinfo.models.*;
import cricinfo.observers.MatchSubject;
import cricinfo.services.ScoreService;
import cricinfo.strategies.scoring.ScoringStrategy;
import cricinfo.strategies.scoring.StandardScoringStrategy;

/**
 * Implementation of ScoreService.
 * Uses Strategy pattern for scoring calculations.
 */
public class ScoreServiceImpl implements ScoreService {
    
    private final MatchSubject matchSubject;
    private final ScoringStrategy scoringStrategy;
    
    public ScoreServiceImpl(MatchSubject matchSubject) {
        this.matchSubject = matchSubject;
        this.scoringStrategy = new StandardScoringStrategy();
    }
    
    public ScoreServiceImpl(MatchSubject matchSubject, ScoringStrategy scoringStrategy) {
        this.matchSubject = matchSubject;
        this.scoringStrategy = scoringStrategy;
    }
    
    @Override
    public Innings startInnings(Match match, Team battingTeam, Team bowlingTeam,
                                Player striker, Player nonStriker) {
        if (!match.isLive()) {
            throw new InvalidMatchStateException("Match is not live");
        }
        
        Innings innings = match.startNewInnings(battingTeam, bowlingTeam);
        innings.startInnings(striker, nonStriker);
        
        // Set target for second innings
        if (match.getInnings().size() > 1) {
            Innings firstInnings = match.getInnings().get(0);
            innings.setTarget(firstInnings.getTotalRuns() + 1);
        }
        
        matchSubject.notifyScoreUpdate(match, match.getLiveScore());
        
        return innings;
    }
    
    @Override
    public void recordBall(Match match, Ball ball) {
        if (!match.isLive()) {
            throw new InvalidMatchStateException("Match is not live");
        }
        
        Innings currentInnings = match.getCurrentInnings();
        if (currentInnings == null) {
            throw new InvalidScoreUpdateException("No active innings");
        }
        
        currentInnings.addBall(ball);
        
        // Generate commentary
        Commentary commentary = generateCommentary(match, ball, currentInnings);
        match.addCommentary(commentary);
        
        // Notify observers
        matchSubject.notifyBallBowled(match, ball);
        
        if (ball.isWicket()) {
            matchSubject.notifyWicket(match, ball);
        }
        
        matchSubject.notifyScoreUpdate(match, match.getLiveScore());
        
        // Check if innings ended
        if (currentInnings.getStatus() == InningsStatus.ALL_OUT ||
            (currentInnings.getTarget() > 0 && 
             currentInnings.getTotalRuns() >= currentInnings.getTarget())) {
            endInnings(match);
        }
    }
    
    @Override
    public void sendNewBatsman(Match match, Player batsman) {
        Innings currentInnings = match.getCurrentInnings();
        if (currentInnings == null) {
            throw new InvalidScoreUpdateException("No active innings");
        }
        
        currentInnings.sendNewBatsman(batsman);
    }
    
    @Override
    public void changeBowler(Match match, Player bowler) {
        Innings currentInnings = match.getCurrentInnings();
        if (currentInnings == null) {
            throw new InvalidScoreUpdateException("No active innings");
        }
        
        currentInnings.startNewOver(bowler);
    }
    
    @Override
    public void endInnings(Match match) {
        Innings currentInnings = match.getCurrentInnings();
        if (currentInnings == null) {
            throw new InvalidScoreUpdateException("No active innings");
        }
        
        currentInnings.endInnings();
        matchSubject.notifyInningsEnd(match, currentInnings.getInningsNumber());
    }
    
    @Override
    public void declareInnings(Match match) {
        Innings currentInnings = match.getCurrentInnings();
        if (currentInnings == null) {
            throw new InvalidScoreUpdateException("No active innings");
        }
        
        currentInnings.declareInnings();
        matchSubject.notifyInningsEnd(match, currentInnings.getInningsNumber());
    }
    
    @Override
    public String getLiveScore(Match match) {
        return match.getLiveScore();
    }
    
    @Override
    public double getRequiredRunRate(Match match) {
        return scoringStrategy.calculateRequiredRunRate(match);
    }
    
    @Override
    public int getProjectedScore(Match match) {
        Innings currentInnings = match.getCurrentInnings();
        if (currentInnings == null) {
            return 0;
        }
        return scoringStrategy.calculateProjectedScore(currentInnings, 
                match.getFormat().getOversPerInnings());
    }
    
    @Override
    public double getWinProbability(Match match) {
        return scoringStrategy.calculateWinProbability(match, true);
    }
    
    private Commentary generateCommentary(Match match, Ball ball, Innings innings) {
        StringBuilder text = new StringBuilder();
        text.append(ball.getBowler().getName()).append(" to ").append(ball.getBatsman().getName());
        text.append(", ");
        
        if (ball.isWicket()) {
            text.append("OUT! ").append(ball.getDismissalType().getDisplayName());
            return new Commentary(match.getId(), innings.getInningsNumber(),
                    innings.getOversCompleted(), text.toString(),
                    Commentary.CommentaryType.WICKET, ball);
        }
        
        if (ball.isSix()) {
            text.append("SIX! ").append(ball.getRuns()).append(" runs");
            return new Commentary(match.getId(), innings.getInningsNumber(),
                    innings.getOversCompleted(), text.toString(),
                    Commentary.CommentaryType.BOUNDARY, ball);
        }
        
        if (ball.isBoundary()) {
            text.append("FOUR! ").append(ball.getRuns()).append(" runs");
            return new Commentary(match.getId(), innings.getInningsNumber(),
                    innings.getOversCompleted(), text.toString(),
                    Commentary.CommentaryType.BOUNDARY, ball);
        }
        
        text.append(ball.getTotalRuns()).append(" run(s)");
        return new Commentary(match.getId(), innings.getInningsNumber(),
                innings.getOversCompleted(), text.toString(),
                Commentary.CommentaryType.BALL_BY_BALL, ball);
    }
}



