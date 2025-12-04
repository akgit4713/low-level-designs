package taskmanagement.strategies.search;

import taskmanagement.enums.TaskStatus;
import taskmanagement.models.Task;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Criteria for filtering tasks by status.
 */
public class StatusSearchCriteria implements SearchCriteria {
    
    private final Set<TaskStatus> statuses;

    /**
     * Creates criteria that matches a single status.
     */
    public StatusSearchCriteria(TaskStatus status) {
        this.statuses = EnumSet.of(Objects.requireNonNull(status, "Status cannot be null"));
    }

    /**
     * Creates criteria that matches any of the specified statuses.
     */
    public StatusSearchCriteria(TaskStatus... statuses) {
        if (statuses == null || statuses.length == 0) {
            throw new IllegalArgumentException("At least one status is required");
        }
        this.statuses = EnumSet.copyOf(Arrays.asList(statuses));
    }

    /**
     * Creates criteria that matches active tasks (pending or in progress).
     */
    public static StatusSearchCriteria active() {
        return new StatusSearchCriteria(TaskStatus.PENDING, TaskStatus.IN_PROGRESS);
    }

    /**
     * Creates criteria that matches terminal tasks (completed or cancelled).
     */
    public static StatusSearchCriteria terminal() {
        return new StatusSearchCriteria(TaskStatus.COMPLETED, TaskStatus.CANCELLED);
    }

    @Override
    public boolean matches(Task task) {
        return statuses.contains(task.getStatus());
    }

    @Override
    public String getDescription() {
        String statusNames = statuses.stream()
                .map(TaskStatus::getDisplayName)
                .collect(Collectors.joining(", "));
        return "Tasks with status: " + statusNames;
    }

    public Set<TaskStatus> getStatuses() {
        return EnumSet.copyOf(statuses);
    }
}



