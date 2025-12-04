package taskmanagement.repositories.impl;

import taskmanagement.enums.HistoryAction;
import taskmanagement.models.TaskHistory;
import taskmanagement.repositories.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of TaskHistory repository.
 */
public class InMemoryHistoryRepository implements Repository<TaskHistory, String> {
    
    private final Map<String, TaskHistory> histories = new ConcurrentHashMap<>();

    @Override
    public TaskHistory save(TaskHistory history) {
        histories.put(history.getId(), history);
        return history;
    }

    @Override
    public Optional<TaskHistory> findById(String id) {
        return Optional.ofNullable(histories.get(id));
    }

    @Override
    public List<TaskHistory> findAll() {
        return new ArrayList<>(histories.values());
    }

    @Override
    public boolean delete(String id) {
        return histories.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return histories.containsKey(id);
    }

    @Override
    public long count() {
        return histories.size();
    }

    @Override
    public void deleteAll() {
        histories.clear();
    }

    /**
     * Finds all history entries for a task, ordered by timestamp descending.
     */
    public List<TaskHistory> findByTaskId(String taskId) {
        return histories.values().stream()
                .filter(h -> taskId.equals(h.getTaskId()))
                .sorted(Comparator.comparing(TaskHistory::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Finds all history entries by a user, ordered by timestamp descending.
     */
    public List<TaskHistory> findByChangedBy(String userId) {
        return histories.values().stream()
                .filter(h -> userId.equals(h.getChangedBy()))
                .sorted(Comparator.comparing(TaskHistory::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Finds all history entries for a specific action.
     */
    public List<TaskHistory> findByAction(HistoryAction action) {
        return histories.values().stream()
                .filter(h -> h.getAction() == action)
                .sorted(Comparator.comparing(TaskHistory::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Finds recent history entries, limited to the specified count.
     */
    public List<TaskHistory> findRecent(int limit) {
        return histories.values().stream()
                .sorted(Comparator.comparing(TaskHistory::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Finds history entries within a time range.
     */
    public List<TaskHistory> findByTimestampBetween(LocalDateTime start, LocalDateTime end) {
        return histories.values().stream()
                .filter(h -> {
                    LocalDateTime ts = h.getTimestamp();
                    return (ts.isEqual(start) || ts.isAfter(start)) &&
                           (ts.isEqual(end) || ts.isBefore(end));
                })
                .sorted(Comparator.comparing(TaskHistory::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Deletes all history for a task.
     */
    public int deleteByTaskId(String taskId) {
        List<String> toDelete = histories.values().stream()
                .filter(h -> taskId.equals(h.getTaskId()))
                .map(TaskHistory::getId)
                .collect(Collectors.toList());
        toDelete.forEach(histories::remove);
        return toDelete.size();
    }
}



