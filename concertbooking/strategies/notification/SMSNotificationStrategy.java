package concertbooking.strategies.notification;

import concertbooking.models.Ticket;
import concertbooking.models.User;

import java.util.List;

/**
 * SMS notification strategy
 */
public class SMSNotificationStrategy implements NotificationStrategy {
    
    @Override
    public boolean sendBookingConfirmation(User user, String bookingId, List<Ticket> tickets) {
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            return false;
        }
        
        System.out.println("\n[SMS to " + user.getPhone() + "]");
        System.out.println("Booking confirmed! ID: " + bookingId 
            + ". " + tickets.size() + " ticket(s) sent to " + user.getEmail());
        return true;
    }
    
    @Override
    public boolean sendBookingCancellation(User user, String bookingId) {
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            return false;
        }
        
        System.out.println("\n[SMS to " + user.getPhone() + "]");
        System.out.println("Booking " + bookingId + " cancelled. Refund will be processed if applicable.");
        return true;
    }
    
    @Override
    public boolean sendWaitlistNotification(User user, String concertId, int availableSeats) {
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            return false;
        }
        
        System.out.println("\n[SMS to " + user.getPhone() + "]");
        System.out.println("🎉 " + availableSeats + " seat(s) available for concert " 
            + concertId + "! Book now before they're gone!");
        return true;
    }
    
    @Override
    public boolean sendConcertReminder(User user, String concertId, List<Ticket> tickets) {
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            return false;
        }
        
        System.out.println("\n[SMS to " + user.getPhone() + "]");
        System.out.println("🎵 Reminder: Your concert is tomorrow! " 
            + tickets.size() + " tickets for seats: " 
            + tickets.stream().map(Ticket::getSeatLabel).reduce((a, b) -> a + ", " + b).orElse(""));
        return true;
    }
}



