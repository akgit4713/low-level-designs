package socialnetwork.services.impl;

import socialnetwork.models.Notification;
import socialnetwork.observers.NotificationObserver;
import socialnetwork.repositories.NotificationRepository;
import socialnetwork.services.NotificationService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of NotificationService.
 * Uses Observer pattern to deliver notifications through multiple channels.
 */
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final List<NotificationObserver> observers;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
        this.observers = new CopyOnWriteArrayList<>();
    }

    @Override
    public void sendNotification(Notification notification) {
        // Notify all observers
        for (NotificationObserver observer : observers) {
            try {
                observer.onNotification(notification);
            } catch (Exception e) {
                System.err.println("Failed to send notification via " + 
                                   observer.getChannelName() + ": " + e.getMessage());
            }
        }
    }

    @Override
    public List<Notification> getNotifications(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }

    @Override
    public int getUnreadCount(String userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(String notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    @Override
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    @Override
    public void registerObserver(NotificationObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            System.out.println("Registered notification observer: " + observer.getChannelName());
        }
    }

    @Override
    public void unregisterObserver(NotificationObserver observer) {
        observers.remove(observer);
    }
}



