package concertbooking.services;

import concertbooking.enums.NotificationType;
import concertbooking.models.Ticket;
import concertbooking.models.User;
import concertbooking.strategies.notification.NotificationStrategy;

import java.util.List;

/**
 * Service interface for sending notifications
 */
public interface NotificationService {
    
    /**
     * Send booking confirmation
     */
    void sendBookingConfirmation(User user, String bookingId, List<Ticket> tickets);
    
    /**
     * Send booking cancellation notification
     */
    void sendBookingCancellation(User user, String bookingId);
    
    /**
     * Send waitlist notification
     */
    void sendWaitlistNotification(User user, String concertId, int availableSeats);
    
    /**
     * Send concert reminder
     */
    void sendConcertReminder(User user, String concertId, List<Ticket> tickets);
    
    /**
     * Register notification strategy
     */
    void registerNotificationStrategy(NotificationType type, NotificationStrategy strategy);
}



