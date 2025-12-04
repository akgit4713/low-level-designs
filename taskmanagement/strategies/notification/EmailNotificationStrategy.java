package taskmanagement.strategies.notification;

import taskmanagement.models.Reminder;
import taskmanagement.models.Task;
import taskmanagement.models.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Notification strategy that simulates sending emails.
 * In a real implementation, this would integrate with an email service.
 */
public class EmailNotificationStrategy implements NotificationStrategy {
    
    private static final DateTimeFormatter FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final String smtpServer;
    private final String fromAddress;
    
    public EmailNotificationStrategy(String smtpServer, String fromAddress) {
        this.smtpServer = smtpServer;
        this.fromAddress = fromAddress;
    }
    
    public EmailNotificationStrategy() {
        this("smtp.localhost", "noreply@taskmanager.com");
    }

    @Override
    public void notify(User user, Reminder reminder, Task task) {
        String subject = "Task Reminder: " + task.getTitle();
        StringBuilder body = new StringBuilder();
        body.append("Hello ").append(user.getName()).append(",\n\n");
        body.append("This is a reminder for your task:\n\n");
        body.append("Task: ").append(task.getTitle()).append("\n");
        body.append("Priority: ").append(task.getPriority().getDisplayName()).append("\n");
        body.append("Status: ").append(task.getStatus().getDisplayName()).append("\n");
        if (task.getDueDate() != null) {
            body.append("Due Date: ").append(task.getDueDate().format(FORMATTER)).append("\n");
        }
        body.append("\nMessage: ").append(reminder.getMessage()).append("\n");
        body.append("\nBest regards,\nTask Management System");
        
        sendEmail(user.getEmail(), subject, body.toString());
    }

    @Override
    public void notify(User user, String subject, String message) {
        StringBuilder body = new StringBuilder();
        body.append("Hello ").append(user.getName()).append(",\n\n");
        body.append(message).append("\n\n");
        body.append("Best regards,\nTask Management System");
        
        sendEmail(user.getEmail(), subject, body.toString());
    }

    @Override
    public String getType() {
        return "EMAIL";
    }
    
    /**
     * Simulates sending an email. In production, this would use JavaMail or similar.
     */
    private void sendEmail(String to, String subject, String body) {
        // Simulate email sending
        System.out.println("[EMAIL] Sending email via " + smtpServer);
        System.out.println("[EMAIL] From: " + fromAddress);
        System.out.println("[EMAIL] To: " + to);
        System.out.println("[EMAIL] Subject: " + subject);
        System.out.println("[EMAIL] Body length: " + body.length() + " chars");
        System.out.println("[EMAIL] Status: SENT (simulated)");
    }
    
    public String getSmtpServer() {
        return smtpServer;
    }
    
    public String getFromAddress() {
        return fromAddress;
    }
}



