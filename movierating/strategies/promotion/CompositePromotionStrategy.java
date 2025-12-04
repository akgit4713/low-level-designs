package movierating.strategies.promotion;

import movierating.models.User;
import movierating.models.UserLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite promotion strategy that combines multiple promotion strategies.
 * 
 * Composite Pattern: Treats group of strategies as a single strategy.
 * Open/Closed Principle: New strategies can be added without modifying this class.
 */
public class CompositePromotionStrategy implements LevelPromotionStrategy {
    
    private final List<LevelPromotionStrategy> strategies;
    private final EvaluationMode evaluationMode;
    
    public enum EvaluationMode {
        ALL_MUST_AGREE,      // All strategies must agree for promotion/demotion
        ANY_CAN_PROMOTE,     // Any strategy can trigger promotion
        HIGHEST_LEVEL,       // Use the highest level suggested
        LOWEST_LEVEL         // Use the lowest level suggested (most conservative)
    }
    
    public CompositePromotionStrategy(EvaluationMode mode) {
        this.strategies = new ArrayList<>();
        this.evaluationMode = mode;
    }
    
    public void addStrategy(LevelPromotionStrategy strategy) {
        strategies.add(strategy);
    }
    
    public void removeStrategy(LevelPromotionStrategy strategy) {
        strategies.remove(strategy);
    }
    
    @Override
    public UserLevel evaluateLevel(User user) {
        if (strategies.isEmpty()) {
            return user.getLevel();
        }
        
        return switch (evaluationMode) {
            case ALL_MUST_AGREE -> evaluateAllAgree(user);
            case ANY_CAN_PROMOTE -> evaluateAnyPromote(user);
            case HIGHEST_LEVEL -> evaluateHighest(user);
            case LOWEST_LEVEL -> evaluateLowest(user);
        };
    }
    
    private UserLevel evaluateAllAgree(User user) {
        UserLevel firstLevel = strategies.get(0).evaluateLevel(user);
        for (int i = 1; i < strategies.size(); i++) {
            if (strategies.get(i).evaluateLevel(user) != firstLevel) {
                return user.getLevel(); // No change if strategies disagree
            }
        }
        return firstLevel;
    }
    
    private UserLevel evaluateAnyPromote(User user) {
        UserLevel currentLevel = user.getLevel();
        UserLevel highest = currentLevel;
        
        for (LevelPromotionStrategy strategy : strategies) {
            UserLevel suggested = strategy.evaluateLevel(user);
            if (suggested.ordinal() > highest.ordinal()) {
                highest = suggested;
            }
        }
        
        return highest;
    }
    
    private UserLevel evaluateHighest(User user) {
        UserLevel highest = UserLevel.NOVICE;
        for (LevelPromotionStrategy strategy : strategies) {
            UserLevel suggested = strategy.evaluateLevel(user);
            if (suggested.ordinal() > highest.ordinal()) {
                highest = suggested;
            }
        }
        return highest;
    }
    
    private UserLevel evaluateLowest(User user) {
        UserLevel lowest = UserLevel.MASTER;
        for (LevelPromotionStrategy strategy : strategies) {
            UserLevel suggested = strategy.evaluateLevel(user);
            if (suggested.ordinal() < lowest.ordinal()) {
                lowest = suggested;
            }
        }
        return lowest;
    }
    
    @Override
    public boolean shouldPromote(User user) {
        return switch (evaluationMode) {
            case ALL_MUST_AGREE -> strategies.stream().allMatch(s -> s.shouldPromote(user));
            case ANY_CAN_PROMOTE, HIGHEST_LEVEL -> strategies.stream().anyMatch(s -> s.shouldPromote(user));
            case LOWEST_LEVEL -> strategies.stream().allMatch(s -> s.shouldPromote(user));
        };
    }
    
    @Override
    public boolean shouldDemote(User user) {
        return switch (evaluationMode) {
            case ALL_MUST_AGREE, LOWEST_LEVEL -> strategies.stream().anyMatch(s -> s.shouldDemote(user));
            case ANY_CAN_PROMOTE, HIGHEST_LEVEL -> strategies.stream().allMatch(s -> s.shouldDemote(user));
        };
    }
    
    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder("Composite promotion strategy (");
        sb.append(evaluationMode).append("): [");
        for (int i = 0; i < strategies.size(); i++) {
            sb.append(strategies.get(i).getDescription());
            if (i < strategies.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}


