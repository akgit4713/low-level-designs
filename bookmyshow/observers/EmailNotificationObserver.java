package bookmyshow.observers;

import bookmyshow.models.Booking;
import bookmyshow.models.User;
import bookmyshow.repositories.UserRepository;

/**
 * Observer that sends email notifications for booking events.
 */
public class EmailNotificationObserver implements BookingObserver {
    
    private final UserRepository userRepository;

    public EmailNotificationObserver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onBookingConfirmed(Booking booking) {
        User user = userRepository.findById(booking.getUserId()).orElse(null);
        if (user != null) {
            sendEmail(user.getEmail(), 
                "Booking Confirmed - " + booking.getId(),
                String.format("Your booking has been confirmed!\n\nBooking ID: %s\nSeats: %d\nAmount: ₹%s\n\nEnjoy your movie!",
                    booking.getId(), 
                    booking.getNumberOfSeats(),
                    booking.getTotalAmount()));
        }
    }

    @Override
    public void onBookingCancelled(Booking booking) {
        User user = userRepository.findById(booking.getUserId()).orElse(null);
        if (user != null) {
            sendEmail(user.getEmail(), 
                "Booking Cancelled - " + booking.getId(),
                String.format("Your booking has been cancelled.\n\nBooking ID: %s\n\nRefund (if applicable) will be processed within 5-7 business days.",
                    booking.getId()));
        }
    }

    @Override
    public void onBookingExpired(Booking booking) {
        User user = userRepository.findById(booking.getUserId()).orElse(null);
        if (user != null) {
            sendEmail(user.getEmail(), 
                "Booking Expired - " + booking.getId(),
                String.format("Your booking has expired due to payment timeout.\n\nBooking ID: %s\n\nPlease try booking again.",
                    booking.getId()));
        }
    }

    private void sendEmail(String to, String subject, String body) {
        // Simulate email sending
        System.out.println("\n📧 EMAIL NOTIFICATION");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("---");
    }
}



