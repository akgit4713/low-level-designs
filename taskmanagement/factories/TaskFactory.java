package taskmanagement.factories;

import taskmanagement.enums.TaskPriority;
import taskmanagement.models.Task;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Factory for creating Task objects.
 */
public class TaskFactory {
    
    private TaskFactory() {
        // Utility class
    }
    
    /**
     * Creates a basic task with minimal information.
     */
    public static Task createTask(String title, String createdBy) {
        return Task.builder()
                .id(generateId())
                .title(title)
                .createdBy(createdBy)
                .build();
    }
    
    /**
     * Creates a task with title, description, and priority.
     */
    public static Task createTask(String title, String description, 
                                  TaskPriority priority, String createdBy) {
        return Task.builder()
                .id(generateId())
                .title(title)
                .description(description)
                .priority(priority)
                .createdBy(createdBy)
                .build();
    }
    
    /**
     * Creates a complete task with all details.
     */
    public static Task createTask(String title, String description, 
                                  TaskPriority priority, LocalDateTime dueDate,
                                  String createdBy, String assignedTo) {
        return Task.builder()
                .id(generateId())
                .title(title)
                .description(description)
                .priority(priority)
                .dueDate(dueDate)
                .createdBy(createdBy)
                .assignedTo(assignedTo)
                .build();
    }
    
    /**
     * Creates a high-priority task due soon.
     */
    public static Task createUrgentTask(String title, String description,
                                        String createdBy, int dueInHours) {
        return Task.builder()
                .id(generateId())
                .title(title)
                .description(description)
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDateTime.now().plusHours(dueInHours))
                .createdBy(createdBy)
                .build();
    }
    
    /**
     * Creates a critical task due immediately.
     */
    public static Task createCriticalTask(String title, String description,
                                          String createdBy, String assignedTo) {
        return Task.builder()
                .id(generateId())
                .title(title)
                .description(description)
                .priority(TaskPriority.CRITICAL)
                .dueDate(LocalDateTime.now().plusHours(4))
                .createdBy(createdBy)
                .assignedTo(assignedTo)
                .build();
    }
    
    private static String generateId() {
        return "TASK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}



