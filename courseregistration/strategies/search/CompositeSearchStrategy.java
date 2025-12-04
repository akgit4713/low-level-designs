package courseregistration.strategies.search;

import courseregistration.models.Course;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Composite search strategy that combines multiple strategies with AND/OR logic.
 */
public class CompositeSearchStrategy implements CourseSearchStrategy {
    
    public enum LogicalOperator {
        AND, OR
    }
    
    private final List<CourseSearchStrategy> strategies;
    private final LogicalOperator operator;
    
    private CompositeSearchStrategy(List<CourseSearchStrategy> strategies, LogicalOperator operator) {
        this.strategies = new ArrayList<>(strategies);
        this.operator = operator;
    }
    
    public static CompositeSearchStrategy and(CourseSearchStrategy... strategies) {
        return new CompositeSearchStrategy(Arrays.asList(strategies), LogicalOperator.AND);
    }
    
    public static CompositeSearchStrategy or(CourseSearchStrategy... strategies) {
        return new CompositeSearchStrategy(Arrays.asList(strategies), LogicalOperator.OR);
    }
    
    public CompositeSearchStrategy addStrategy(CourseSearchStrategy strategy) {
        this.strategies.add(strategy);
        return this;
    }
    
    @Override
    public boolean matches(Course course) {
        if (strategies.isEmpty()) {
            return true;
        }
        
        return switch (operator) {
            case AND -> strategies.stream().allMatch(s -> s.matches(course));
            case OR -> strategies.stream().anyMatch(s -> s.matches(course));
        };
    }
    
    @Override
    public String getDescription() {
        if (strategies.isEmpty()) {
            return "No criteria";
        }
        
        String joinWord = operator == LogicalOperator.AND ? " AND " : " OR ";
        return "(" + strategies.stream()
                .map(CourseSearchStrategy::getDescription)
                .collect(Collectors.joining(joinWord)) + ")";
    }
}



