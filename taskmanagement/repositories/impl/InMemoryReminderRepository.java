package taskmanagement.repositories.impl;

import taskmanagement.models.Reminder;
import taskmanagement.repositories.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of Reminder repository.
 */
public class InMemoryReminderRepository implements Repository<Reminder, String> {
    
    private final Map<String, Reminder> reminders = new ConcurrentHashMap<>();

    @Override
    public Reminder save(Reminder reminder) {
        reminders.put(reminder.getId(), reminder);
        return reminder;
    }

    @Override
    public Optional<Reminder> findById(String id) {
        return Optional.ofNullable(reminders.get(id));
    }

    @Override
    public List<Reminder> findAll() {
        return new ArrayList<>(reminders.values());
    }

    @Override
    public boolean delete(String id) {
        return reminders.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return reminders.containsKey(id);
    }

    @Override
    public long count() {
        return reminders.size();
    }

    @Override
    public void deleteAll() {
        reminders.clear();
    }

    /**
     * Finds all reminders for a task.
     */
    public List<Reminder> findByTaskId(String taskId) {
        return reminders.values().stream()
                .filter(r -> taskId.equals(r.getTaskId()))
                .collect(Collectors.toList());
    }

    /**
     * Finds all reminders for a user.
     */
    public List<Reminder> findByUserId(String userId) {
        return reminders.values().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .collect(Collectors.toList());
    }

    /**
     * Finds all untriggered reminders.
     */
    public List<Reminder> findUntriggered() {
        return reminders.values().stream()
                .filter(r -> !r.isTriggered())
                .collect(Collectors.toList());
    }

    /**
     * Finds all due reminders (time has passed, not yet triggered).
     */
    public List<Reminder> findDueReminders() {
        LocalDateTime now = LocalDateTime.now();
        return reminders.values().stream()
                .filter(r -> !r.isTriggered() && r.getReminderTime().isBefore(now))
                .collect(Collectors.toList());
    }

    /**
     * Finds upcoming reminders within specified minutes.
     */
    public List<Reminder> findUpcomingWithin(int minutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusMinutes(minutes);
        return reminders.values().stream()
                .filter(r -> !r.isTriggered() && 
                            r.getReminderTime().isAfter(now) &&
                            r.getReminderTime().isBefore(threshold))
                .collect(Collectors.toList());
    }

    /**
     * Deletes all reminders for a task.
     */
    public int deleteByTaskId(String taskId) {
        List<String> toDelete = reminders.values().stream()
                .filter(r -> taskId.equals(r.getTaskId()))
                .map(Reminder::getId)
                .collect(Collectors.toList());
        toDelete.forEach(reminders::remove);
        return toDelete.size();
    }
}



