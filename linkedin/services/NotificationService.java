package linkedin.services;

import linkedin.models.Notification;
import linkedin.observers.NotificationObserver;
import linkedin.repositories.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing notifications.
 * Implements the Subject part of Observer pattern.
 */
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final List<NotificationObserver> observers;
    
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
        this.observers = new ArrayList<>();
    }
    
    public void addObserver(NotificationObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }
    
    public void notify(Notification notification) {
        for (NotificationObserver observer : observers) {
            observer.onNotification(notification);
        }
    }
    
    public List<Notification> getNotificationsForUser(String userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    public List<Notification> getUnreadNotificationsForUser(String userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }
    
    public int getUnreadCount(String userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
    
    public void markAsRead(String notificationId) {
        notificationRepository.findById(notificationId)
                .ifPresent(Notification::markAsRead);
    }
    
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }
}



