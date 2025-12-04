package concertbooking.services.impl;

import concertbooking.enums.NotificationType;
import concertbooking.models.Ticket;
import concertbooking.models.User;
import concertbooking.services.NotificationService;
import concertbooking.strategies.notification.NotificationStrategy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of NotificationService using Strategy pattern
 */
public class NotificationServiceImpl implements NotificationService {
    
    private final Map<NotificationType, NotificationStrategy> strategies = new ConcurrentHashMap<>();
    
    @Override
    public void sendBookingConfirmation(User user, String bookingId, List<Ticket> tickets) {
        strategies.values().forEach(strategy -> 
            strategy.sendBookingConfirmation(user, bookingId, tickets));
    }
    
    @Override
    public void sendBookingCancellation(User user, String bookingId) {
        strategies.values().forEach(strategy -> 
            strategy.sendBookingCancellation(user, bookingId));
    }
    
    @Override
    public void sendWaitlistNotification(User user, String concertId, int availableSeats) {
        strategies.values().forEach(strategy -> 
            strategy.sendWaitlistNotification(user, concertId, availableSeats));
    }
    
    @Override
    public void sendConcertReminder(User user, String concertId, List<Ticket> tickets) {
        strategies.values().forEach(strategy -> 
            strategy.sendConcertReminder(user, concertId, tickets));
    }
    
    @Override
    public void registerNotificationStrategy(NotificationType type, NotificationStrategy strategy) {
        strategies.put(type, strategy);
        System.out.println("[NOTIFICATION] Registered notification strategy: " + type.getDisplayName());
    }
}



