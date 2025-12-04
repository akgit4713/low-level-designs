package taskmanagement.services.impl;

import taskmanagement.enums.HistoryAction;
import taskmanagement.models.TaskHistory;
import taskmanagement.repositories.impl.InMemoryHistoryRepository;
import taskmanagement.services.TaskHistoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of TaskHistoryService.
 */
public class TaskHistoryServiceImpl implements TaskHistoryService {
    
    private final InMemoryHistoryRepository historyRepository;

    public TaskHistoryServiceImpl(InMemoryHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public TaskHistory recordHistory(String taskId, HistoryAction action, String fieldName,
                                     String previousValue, String newValue, String changedBy) {
        String id = "HIST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        TaskHistory history = TaskHistory.builder()
                .id(id)
                .taskId(taskId)
                .action(action)
                .fieldName(fieldName)
                .previousValue(previousValue)
                .newValue(newValue)
                .changedBy(changedBy)
                .build();
        return historyRepository.save(history);
    }

    @Override
    public List<TaskHistory> getHistoryForTask(String taskId) {
        return historyRepository.findByTaskId(taskId);
    }

    @Override
    public List<TaskHistory> getHistoryByUser(String userId) {
        return historyRepository.findByChangedBy(userId);
    }

    @Override
    public List<TaskHistory> getHistoryByAction(HistoryAction action) {
        return historyRepository.findByAction(action);
    }

    @Override
    public List<TaskHistory> getRecentHistory(int limit) {
        return historyRepository.findRecent(limit);
    }

    @Override
    public List<TaskHistory> getHistoryBetween(LocalDateTime start, LocalDateTime end) {
        return historyRepository.findByTimestampBetween(start, end);
    }

    @Override
    public int deleteHistoryForTask(String taskId) {
        return historyRepository.deleteByTaskId(taskId);
    }
}



