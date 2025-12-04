package linkedin.repositories.impl;

import linkedin.enums.NotificationType;
import linkedin.models.Notification;
import linkedin.repositories.NotificationRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    public List<Notification> findAll() {
        return new ArrayList<>(notifications.values());
    }
    
    @Override
    public void delete(String id) {
        notifications.remove(id);
    }
    
    @Override
    public boolean existsById(String id) {
        return notifications.containsKey(id);
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
    public List<Notification> findByUserIdAndType(String userId, NotificationType type) {
        return notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId) && n.getType() == type)
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
    public void markAllAsReadForUser(String userId) {
        notifications.values().stream()
                .filter(n -> n.getUserId().equals(userId) && !n.isRead())
                .forEach(Notification::markAsRead);
    }
}



