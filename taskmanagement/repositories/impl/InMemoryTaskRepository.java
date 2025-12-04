package taskmanagement.repositories.impl;

import taskmanagement.enums.TaskPriority;
import taskmanagement.enums.TaskStatus;
import taskmanagement.models.Task;
import taskmanagement.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of TaskRepository.
 */
public class InMemoryTaskRepository implements TaskRepository {
    
    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public boolean delete(String id) {
        return tasks.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return tasks.containsKey(id);
    }

    @Override
    public long count() {
        return tasks.size();
    }

    @Override
    public void deleteAll() {
        tasks.clear();
    }

    @Override
    public List<Task> findByCreatedBy(String userId) {
        return tasks.values().stream()
                .filter(task -> userId.equals(task.getCreatedBy()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByAssignedTo(String userId) {
        return tasks.values().stream()
                .filter(task -> userId.equals(task.getAssignedTo()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return tasks.values().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByPriority(TaskPriority priority) {
        return tasks.values().stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByDueDateBefore(LocalDateTime date) {
        return tasks.values().stream()
                .filter(task -> task.getDueDate() != null && task.getDueDate().isBefore(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByDueDateBetween(LocalDateTime start, LocalDateTime end) {
        return tasks.values().stream()
                .filter(task -> {
                    LocalDateTime dueDate = task.getDueDate();
                    return dueDate != null && 
                           (dueDate.isEqual(start) || dueDate.isAfter(start)) &&
                           (dueDate.isEqual(end) || dueDate.isBefore(end));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        return tasks.values().stream()
                .filter(task -> task.getDueDate() != null && 
                               task.getDueDate().isBefore(now) &&
                               task.getStatus().isActive())
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findActiveTasks() {
        return tasks.values().stream()
                .filter(task -> task.getStatus().isActive())
                .collect(Collectors.toList());
    }
}



