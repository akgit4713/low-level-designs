package taskmanagement.strategies.search;

import taskmanagement.models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Composite criteria that combines multiple search criteria with AND/OR logic.
 */
public class CompositeSearchCriteria implements SearchCriteria {
    
    public enum Operator {
        AND, OR
    }
    
    private final List<SearchCriteria> criteria;
    private final Operator operator;

    public CompositeSearchCriteria(Operator operator) {
        this.criteria = new ArrayList<>();
        this.operator = operator;
    }

    /**
     * Creates a composite criteria with AND logic.
     */
    public static CompositeSearchCriteria and(SearchCriteria... criteria) {
        CompositeSearchCriteria composite = new CompositeSearchCriteria(Operator.AND);
        for (SearchCriteria c : criteria) {
            composite.addCriteria(c);
        }
        return composite;
    }

    /**
     * Creates a composite criteria with OR logic.
     */
    public static CompositeSearchCriteria or(SearchCriteria... criteria) {
        CompositeSearchCriteria composite = new CompositeSearchCriteria(Operator.OR);
        for (SearchCriteria c : criteria) {
            composite.addCriteria(c);
        }
        return composite;
    }

    /**
     * Adds a criteria to this composite.
     */
    public CompositeSearchCriteria addCriteria(SearchCriteria criterion) {
        if (criterion != null) {
            this.criteria.add(criterion);
        }
        return this;
    }

    /**
     * Removes a criteria from this composite.
     */
    public CompositeSearchCriteria removeCriteria(SearchCriteria criterion) {
        this.criteria.remove(criterion);
        return this;
    }

    /**
     * Clears all criteria.
     */
    public void clear() {
        this.criteria.clear();
    }

    @Override
    public boolean matches(Task task) {
        if (criteria.isEmpty()) {
            return true; // No criteria means match all
        }
        
        return switch (operator) {
            case AND -> criteria.stream().allMatch(c -> c.matches(task));
            case OR -> criteria.stream().anyMatch(c -> c.matches(task));
        };
    }

    @Override
    public String getDescription() {
        if (criteria.isEmpty()) {
            return "All tasks";
        }
        
        String delimiter = operator == Operator.AND ? " AND " : " OR ";
        return criteria.stream()
                .map(SearchCriteria::getDescription)
                .collect(Collectors.joining(delimiter, "(", ")"));
    }

    public List<SearchCriteria> getCriteria() {
        return new ArrayList<>(criteria);
    }

    public Operator getOperator() {
        return operator;
    }

    public int size() {
        return criteria.size();
    }

    public boolean isEmpty() {
        return criteria.isEmpty();
    }
}



