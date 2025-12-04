package linkedin.observers;

import linkedin.models.Notification;
import linkedin.models.User;
import linkedin.repositories.UserRepository;

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
        userRepository.findById(notification.getUserId())
                .ifPresent(user -> sendEmail(user, notification));
    }
    
    private void sendEmail(User user, Notification notification) {
        // In production, this would send actual emails
        System.out.println("[EMAIL] Sending to " + user.getEmail() + 
                          ": " + notification.getContent());
    }
    
    @Override
    public String getChannelName() {
        return "EMAIL";
    }
}



