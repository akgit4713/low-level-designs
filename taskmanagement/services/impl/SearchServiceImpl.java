package taskmanagement.services.impl;

import taskmanagement.enums.TaskPriority;
import taskmanagement.enums.TaskStatus;
import taskmanagement.models.Task;
import taskmanagement.repositories.TaskRepository;
import taskmanagement.services.SearchService;
import taskmanagement.strategies.search.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SearchService.
 */
public class SearchServiceImpl implements SearchService {
    
    private final TaskRepository taskRepository;

    public SearchServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> search(SearchCriteria criteria) {
        return taskRepository.findAll().stream()
                .filter(criteria::matches)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> searchByPriority(TaskPriority priority) {
        return search(new PrioritySearchCriteria(priority));
    }

    @Override
    public List<Task> searchByPriorityAtLeast(TaskPriority minPriority) {
        return search(new PrioritySearchCriteria(minPriority, true));
    }

    @Override
    public List<Task> searchByStatus(TaskStatus status) {
        return search(new StatusSearchCriteria(status));
    }

    @Override
    public List<Task> searchByStatuses(TaskStatus... statuses) {
        return search(new StatusSearchCriteria(statuses));
    }

    @Override
    public List<Task> searchByAssignee(String userId) {
        return search(new AssigneeSearchCriteria(userId));
    }

    @Override
    public List<Task> searchUnassigned() {
        return search(AssigneeSearchCriteria.unassigned());
    }

    @Override
    public List<Task> searchByDueDateBefore(LocalDateTime date) {
        return search(DueDateSearchCriteria.before(date));
    }

    @Override
    public List<Task> searchByDueDateBetween(LocalDateTime start, LocalDateTime end) {
        return search(DueDateSearchCriteria.between(start, end));
    }

    @Override
    public List<Task> searchOverdue() {
        return search(DueDateSearchCriteria.overdue());
    }

    @Override
    public List<Task> searchDueToday() {
        return search(DueDateSearchCriteria.today());
    }

    @Override
    public List<Task> searchByTitle(String titleQuery) {
        return search(new TitleSearchCriteria(titleQuery));
    }

    @Override
    public List<Task> searchActiveTasks() {
        return search(StatusSearchCriteria.active());
    }
}



