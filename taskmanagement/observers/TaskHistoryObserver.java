package taskmanagement.observers;

import taskmanagement.enums.HistoryAction;
import taskmanagement.enums.TaskStatus;
import taskmanagement.models.Task;
import taskmanagement.models.User;
import taskmanagement.services.TaskHistoryService;

/**
 * Observer that records task changes to history.
 */
public class TaskHistoryObserver implements TaskObserver {
    
    private final TaskHistoryService historyService;

    public TaskHistoryObserver(TaskHistoryService historyService) {
        this.historyService = historyService;
    }

    @Override
    public void onTaskCreated(Task task, User creator) {
        historyService.recordHistory(
                task.getId(),
                HistoryAction.CREATED,
                null,
                null,
                taskToString(task),
                creator != null ? creator.getId() : null
        );
    }

    @Override
    public void onTaskUpdated(Task task, String fieldName, String previousValue, String newValue, User updatedBy) {
        historyService.recordHistory(
                task.getId(),
                HistoryAction.UPDATED,
                fieldName,
                previousValue,
                newValue,
                updatedBy != null ? updatedBy.getId() : null
        );
    }

    @Override
    public void onTaskDeleted(Task task, User deletedBy) {
        historyService.recordHistory(
                task.getId(),
                HistoryAction.DELETED,
                null,
                taskToString(task),
                null,
                deletedBy != null ? deletedBy.getId() : null
        );
    }

    @Override
    public void onTaskAssigned(Task task, User previousAssignee, User newAssignee, User assignedBy) {
        String prevValue = previousAssignee != null ? previousAssignee.getUsername() : "(unassigned)";
        String newValue = newAssignee != null ? newAssignee.getUsername() : "(unassigned)";
        
        HistoryAction action = newAssignee != null ? HistoryAction.ASSIGNED : HistoryAction.UNASSIGNED;
        
        historyService.recordHistory(
                task.getId(),
                action,
                "assignedTo",
                prevValue,
                newValue,
                assignedBy != null ? assignedBy.getId() : null
        );
    }

    @Override
    public void onTaskStatusChanged(Task task, TaskStatus previousStatus, TaskStatus newStatus, User changedBy) {
        HistoryAction action;
        if (newStatus == TaskStatus.COMPLETED) {
            action = HistoryAction.COMPLETED;
        } else if (newStatus == TaskStatus.CANCELLED) {
            action = HistoryAction.CANCELLED;
        } else {
            action = HistoryAction.STATUS_CHANGED;
        }
        
        historyService.recordHistory(
                task.getId(),
                action,
                "status",
                previousStatus.getDisplayName(),
                newStatus.getDisplayName(),
                changedBy != null ? changedBy.getId() : null
        );
    }

    @Override
    public void onTaskCompleted(Task task, User completedBy) {
        // Already handled by onTaskStatusChanged
    }
    
    private String taskToString(Task task) {
        return String.format("Task[title=%s, priority=%s, status=%s]",
                task.getTitle(),
                task.getPriority().getDisplayName(),
                task.getStatus().getDisplayName());
    }
}



