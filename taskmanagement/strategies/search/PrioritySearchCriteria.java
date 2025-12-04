package taskmanagement.strategies.search;

import taskmanagement.enums.TaskPriority;
import taskmanagement.models.Task;

import java.util.Objects;

/**
 * Criteria for filtering tasks by priority.
 */
public class PrioritySearchCriteria implements SearchCriteria {
    
    private final TaskPriority priority;
    private final boolean atLeast;

    /**
     * Creates criteria that matches exact priority.
     */
    public PrioritySearchCriteria(TaskPriority priority) {
        this(priority, false);
    }

    /**
     * Creates criteria that matches exact priority or at least the specified priority.
     * @param atLeast if true, matches tasks with priority >= specified priority
     */
    public PrioritySearchCriteria(TaskPriority priority, boolean atLeast) {
        this.priority = Objects.requireNonNull(priority, "Priority cannot be null");
        this.atLeast = atLeast;
    }

    @Override
    public boolean matches(Task task) {
        if (atLeast) {
            return task.getPriority().isAtLeast(priority);
        }
        return task.getPriority() == priority;
    }

    @Override
    public String getDescription() {
        if (atLeast) {
            return "Tasks with priority at least " + priority.getDisplayName();
        }
        return "Tasks with priority " + priority.getDisplayName();
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public boolean isAtLeast() {
        return atLeast;
    }
}



