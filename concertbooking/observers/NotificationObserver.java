package concertbooking.observers;

import concertbooking.models.Booking;
import concertbooking.models.Ticket;
import concertbooking.models.User;

import java.util.List;

/**
 * Observer that sends notifications on booking events
 */
public class NotificationObserver implements BookingObserver {
    
    @Override
    public void onBookingCreated(Booking booking) {
        System.out.println("[NOTIFICATION] Booking created: " + booking.getId() 
            + " - Please complete payment within " + 
            java.time.Duration.between(java.time.LocalDateTime.now(), booking.getExpiresAt()).toMinutes() 
            + " minutes");
    }
    
    @Override
    public void onBookingConfirmed(Booking booking) {
        System.out.println("[NOTIFICATION] Booking confirmed: " + booking.getId() 
            + " - Your tickets have been sent to your email");
    }
    
    @Override
    public void onBookingCancelled(Booking booking) {
        System.out.println("[NOTIFICATION] Booking cancelled: " + booking.getId() 
            + " - Your seats have been released");
    }
    
    @Override
    public void onBookingExpired(Booking booking) {
        System.out.println("[NOTIFICATION] Booking expired: " + booking.getId() 
            + " - Your hold has expired. Please try again.");
    }
    
    /**
     * Send ticket confirmation
     */
    public void sendTicketConfirmation(User user, List<Ticket> tickets) {
        System.out.println("\n[EMAIL] Sending " + tickets.size() + " tickets to " + user.getEmail());
        tickets.forEach(ticket -> System.out.println(ticket));
    }
}



