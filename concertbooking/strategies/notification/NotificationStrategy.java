package concertbooking.strategies.notification;

import concertbooking.models.Ticket;
import concertbooking.models.User;

import java.util.List;

/**
 * Strategy interface for sending notifications
 */
public interface NotificationStrategy {
    
    /**
     * Send booking confirmation notification
     */
    boolean sendBookingConfirmation(User user, String bookingId, List<Ticket> tickets);
    
    /**
     * Send booking cancellation notification
     */
    boolean sendBookingCancellation(User user, String bookingId);
    
    /**
     * Send waitlist notification when seats become available
     */
    boolean sendWaitlistNotification(User user, String concertId, int availableSeats);
    
    /**
     * Send reminder notification before concert
     */
    boolean sendConcertReminder(User user, String concertId, List<Ticket> tickets);
}



