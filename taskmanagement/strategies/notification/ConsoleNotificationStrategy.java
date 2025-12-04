package taskmanagement.strategies.notification;

import taskmanagement.models.Reminder;
import taskmanagement.models.Task;
import taskmanagement.models.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Notification strategy that prints notifications to the console.
 * Useful for development and testing.
 */
public class ConsoleNotificationStrategy implements NotificationStrategy {
    
    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SEPARATOR = "═".repeat(60);

    @Override
    public void notify(User user, Reminder reminder, Task task) {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("🔔 TASK REMINDER NOTIFICATION");
        System.out.println(SEPARATOR);
        System.out.println("To: " + user.getName() + " (" + user.getEmail() + ")");
        System.out.println("Time: " + LocalDateTime.now().format(FORMATTER));
        System.out.println("─".repeat(60));
        System.out.println("Task: " + task.getTitle());
        System.out.println("Priority: " + task.getPriority().getDisplayName());
        System.out.println("Status: " + task.getStatus().getDisplayName());
        if (task.getDueDate() != null) {
            System.out.println("Due Date: " + task.getDueDate().format(FORMATTER));
        }
        System.out.println("─".repeat(60));
        System.out.println("Message: " + reminder.getMessage());
        System.out.println(SEPARATOR);
        System.out.println();
    }

    @Override
    public void notify(User user, String subject, String message) {
        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("📬 NOTIFICATION");
        System.out.println(SEPARATOR);
        System.out.println("To: " + user.getName() + " (" + user.getEmail() + ")");
        System.out.println("Time: " + LocalDateTime.now().format(FORMATTER));
        System.out.println("─".repeat(60));
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println(SEPARATOR);
        System.out.println();
    }

    @Override
    public String getType() {
        return "CONSOLE";
    }
}



