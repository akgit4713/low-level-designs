package taskmanagement.services.impl;

import taskmanagement.exceptions.ReminderException;
import taskmanagement.exceptions.TaskException;
import taskmanagement.exceptions.UserException;
import taskmanagement.models.Reminder;
import taskmanagement.models.Task;
import taskmanagement.models.User;
import taskmanagement.repositories.impl.InMemoryReminderRepository;
import taskmanagement.repositories.TaskRepository;
import taskmanagement.services.ReminderService;
import taskmanagement.services.UserService;
import taskmanagement.strategies.notification.NotificationStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ReminderService.
 */
public class ReminderServiceImpl implements ReminderService {
    
    private final InMemoryReminderRepository reminderRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final NotificationStrategy notificationStrategy;

    public ReminderServiceImpl(InMemoryReminderRepository reminderRepository,
                               TaskRepository taskRepository,
                               UserService userService,
                               NotificationStrategy notificationStrategy) {
        this.reminderRepository = reminderRepository;
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.notificationStrategy = notificationStrategy;
    }

    @Override
    public Reminder createReminder(String taskId, String userId, LocalDateTime reminderTime, String message) {
        // Validate task exists
        if (!taskRepository.existsById(taskId)) {
            throw ReminderException.invalidTask(taskId);
        }
        
        // Validate user exists
        if (!userService.userExists(userId)) {
            throw UserException.notFound(userId);
        }
        
        // Validate reminder time is in the future
        if (reminderTime.isBefore(LocalDateTime.now())) {
            throw ReminderException.pastTime();
        }
        
        String id = "REM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Reminder reminder = new Reminder(id, taskId, userId, reminderTime, message);
        return reminderRepository.save(reminder);
    }

    @Override
    public Optional<Reminder> getReminderById(String reminderId) {
        return reminderRepository.findById(reminderId);
    }

    @Override
    public List<Reminder> getRemindersForTask(String taskId) {
        return reminderRepository.findByTaskId(taskId);
    }

    @Override
    public List<Reminder> getRemindersForUser(String userId) {
        return reminderRepository.findByUserId(userId);
    }

    @Override
    public List<Reminder> getPendingReminders() {
        return reminderRepository.findUntriggered();
    }

    @Override
    public List<Reminder> getUpcomingReminders(int withinMinutes) {
        return reminderRepository.findUpcomingWithin(withinMinutes);
    }

    @Override
    public Reminder updateReminderTime(String reminderId, LocalDateTime newTime) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> ReminderException.notFound(reminderId));
        
        if (reminder.isTriggered()) {
            throw ReminderException.alreadyTriggered(reminderId);
        }
        
        if (newTime.isBefore(LocalDateTime.now())) {
            throw ReminderException.pastTime();
        }
        
        reminder.setReminderTime(newTime);
        return reminderRepository.save(reminder);
    }

    @Override
    public Reminder updateReminderMessage(String reminderId, String newMessage) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> ReminderException.notFound(reminderId));
        
        reminder.setMessage(newMessage);
        return reminderRepository.save(reminder);
    }

    @Override
    public boolean deleteReminder(String reminderId) {
        if (!reminderRepository.existsById(reminderId)) {
            throw ReminderException.notFound(reminderId);
        }
        return reminderRepository.delete(reminderId);
    }

    @Override
    public int deleteRemindersForTask(String taskId) {
        return reminderRepository.deleteByTaskId(taskId);
    }

    @Override
    public List<Reminder> checkAndTriggerReminders() {
        List<Reminder> dueReminders = reminderRepository.findDueReminders();
        List<Reminder> triggeredReminders = new ArrayList<>();
        
        for (Reminder reminder : dueReminders) {
            try {
                Optional<Task> taskOpt = taskRepository.findById(reminder.getTaskId());
                Optional<User> userOpt = userService.getUserById(reminder.getUserId());
                
                if (taskOpt.isPresent() && userOpt.isPresent()) {
                    Task task = taskOpt.get();
                    User user = userOpt.get();
                    
                    // Skip reminder if task is already completed or cancelled
                    if (task.getStatus().isTerminal()) {
                        reminder.markTriggered();
                        reminderRepository.save(reminder);
                        continue;
                    }
                    
                    // Send notification
                    notificationStrategy.notify(user, reminder, task);
                    
                    // Mark as triggered
                    reminder.markTriggered();
                    reminderRepository.save(reminder);
                    triggeredReminders.add(reminder);
                }
            } catch (Exception e) {
                System.err.println("Error triggering reminder " + reminder.getId() + ": " + e.getMessage());
            }
        }
        
        return triggeredReminders;
    }
}



