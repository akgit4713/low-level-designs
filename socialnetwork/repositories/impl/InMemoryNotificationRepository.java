package socialnetwork.repositories.impl;

import socialnetwork.models.Notification;
import socialnetwork.repositories.NotificationRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of NotificationRepository.
 */
public class InMemoryNotificationRepository implements NotificationRepository {
    
    private final Map<String, Notification> notifications = new ConcurrentHashMap<>();

    @Override
    public Notification save(Notification notification) {
        notifications.put(notification.getId(), notification);
        return notification;
    }

    @Override
    public Optional<Notification> findById(String id) {
        return Optional.ofNullable(notifications.get(id));
    }

    @Override
    public List<Notification> findByUserId(String userId) {
        return notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId))
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findUnreadByUserId(String userId) {
        return notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public int countUnreadByUserId(String userId) {
        return (int) notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .count();
    }

    @Override
    public void markAsRead(String id) {
        Notification notification = notifications.get(id);
        if (notification != null) {
            notification.markAsRead();
        }
    }

    @Override
    public void markAllAsReadForUser(String userId) {
        notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .forEach(Notification::markAsRead);
    }

    @Override
    public void delete(String id) {
        notifications.remove(id);
    }
}



