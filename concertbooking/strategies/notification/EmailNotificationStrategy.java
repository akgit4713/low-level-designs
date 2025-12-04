package concertbooking.strategies.notification;

import concertbooking.models.Ticket;
import concertbooking.models.User;

import java.util.List;

/**
 * Email notification strategy
 */
public class EmailNotificationStrategy implements NotificationStrategy {
    
    @Override
    public boolean sendBookingConfirmation(User user, String bookingId, List<Ticket> tickets) {
        System.out.println("\n========== EMAIL NOTIFICATION ==========");
        System.out.println("To: " + user.getEmail());
        System.out.println("Subject: Booking Confirmation - " + bookingId);
        System.out.println("-----------------------------------------");
        System.out.println("Dear " + user.getName() + ",");
        System.out.println("\nYour booking has been confirmed!");
        System.out.println("Booking ID: " + bookingId);
        System.out.println("Number of tickets: " + tickets.size());
        System.out.println("\nYour tickets:");
        tickets.forEach(ticket -> {
            System.out.println("  - " + ticket.getSeatLabel() + " | " + ticket.getSectionName() 
                + " | $" + ticket.getPrice());
        });
        System.out.println("\nPlease show the QR code at the venue entrance.");
        System.out.println("=========================================\n");
        return true;
    }
    
    @Override
    public boolean sendBookingCancellation(User user, String bookingId) {
        System.out.println("\n========== EMAIL NOTIFICATION ==========");
        System.out.println("To: " + user.getEmail());
        System.out.println("Subject: Booking Cancelled - " + bookingId);
        System.out.println("-----------------------------------------");
        System.out.println("Dear " + user.getName() + ",");
        System.out.println("\nYour booking " + bookingId + " has been cancelled.");
        System.out.println("If eligible, a refund will be processed within 5-7 business days.");
        System.out.println("=========================================\n");
        return true;
    }
    
    @Override
    public boolean sendWaitlistNotification(User user, String concertId, int availableSeats) {
        System.out.println("\n========== EMAIL NOTIFICATION ==========");
        System.out.println("To: " + user.getEmail());
        System.out.println("Subject: 🎉 Seats Available - Act Fast!");
        System.out.println("-----------------------------------------");
        System.out.println("Dear " + user.getName() + ",");
        System.out.println("\nGreat news! " + availableSeats + " seat(s) are now available");
        System.out.println("for the concert you've been waiting for!");
        System.out.println("\nConcert ID: " + concertId);
        System.out.println("\n⚠️ Hurry! These seats won't last long.");
        System.out.println("Book now before they're gone!");
        System.out.println("=========================================\n");
        return true;
    }
    
    @Override
    public boolean sendConcertReminder(User user, String concertId, List<Ticket> tickets) {
        System.out.println("\n========== EMAIL NOTIFICATION ==========");
        System.out.println("To: " + user.getEmail());
        System.out.println("Subject: 🎵 Concert Reminder - Tomorrow!");
        System.out.println("-----------------------------------------");
        System.out.println("Dear " + user.getName() + ",");
        System.out.println("\nThis is a friendly reminder about your upcoming concert!");
        System.out.println("Don't forget to bring your tickets and valid ID.");
        System.out.println("\nYour seats: ");
        tickets.forEach(t -> System.out.println("  - " + t.getSeatLabel()));
        System.out.println("=========================================\n");
        return true;
    }
}



