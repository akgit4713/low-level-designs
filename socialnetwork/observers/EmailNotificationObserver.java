package socialnetwork.observers;

import socialnetwork.models.Notification;
import socialnetwork.models.User;
import socialnetwork.repositories.UserRepository;

/**
 * Observer that sends email notifications.
 * In production, this would integrate with an email service.
 */
public class EmailNotificationObserver implements NotificationObserver {
    
    private final UserRepository userRepository;

    public EmailNotificationObserver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onNotification(Notification notification) {
        userRepository.findById(notification.getUserId()).ifPresent(user -> {
            sendEmail(user, notification);
        });
    }

    private void sendEmail(User user, Notification notification) {
        // In production, this would use an email service (SendGrid, SES, etc.)
        System.out.println("[EMAIL] Sending to " + user.getEmail() + ": " + notification.getMessage());
    }

    @Override
    public String getChannelName() {
        return "Email";
    }
}



