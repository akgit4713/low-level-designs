package taskmanagement.strategies.search;

import taskmanagement.models.Task;

/**
 * Strategy interface for searching/filtering tasks.
 */
public interface SearchCriteria {
    
    /**
     * Checks if the given task matches this criteria.
     */
    boolean matches(Task task);
    
    /**
     * Returns a description of this criteria.
     */
    String getDescription();
}



