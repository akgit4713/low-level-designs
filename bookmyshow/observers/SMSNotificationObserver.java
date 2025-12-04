package bookmyshow.observers;

import bookmyshow.models.Booking;
import bookmyshow.models.User;
import bookmyshow.repositories.UserRepository;

/**
 * Observer that sends SMS notifications for booking events.
 */
public class SMSNotificationObserver implements BookingObserver {
    
    private final UserRepository userRepository;

    public SMSNotificationObserver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onBookingConfirmed(Booking booking) {
        User user = userRepository.findById(booking.getUserId()).orElse(null);
        if (user != null && user.getPhone() != null) {
            sendSMS(user.getPhone(), 
                String.format("BookMyShow: Booking %s confirmed! %d seat(s) booked. Amount: Rs.%s. Enjoy your movie!",
                    booking.getId().substring(0, 8), 
                    booking.getNumberOfSeats(),
                    booking.getTotalAmount()));
        }
    }

    @Override
    public void onBookingCancelled(Booking booking) {
        User user = userRepository.findById(booking.getUserId()).orElse(null);
        if (user != null && user.getPhone() != null) {
            sendSMS(user.getPhone(), 
                String.format("BookMyShow: Booking %s cancelled. Refund will be processed soon.",
                    booking.getId().substring(0, 8)));
        }
    }

    @Override
    public void onBookingExpired(Booking booking) {
        User user = userRepository.findById(booking.getUserId()).orElse(null);
        if (user != null && user.getPhone() != null) {
            sendSMS(user.getPhone(), 
                String.format("BookMyShow: Booking %s expired. Please try again.",
                    booking.getId().substring(0, 8)));
        }
    }

    private void sendSMS(String phone, String message) {
        // Simulate SMS sending
        System.out.println("\n📱 SMS NOTIFICATION");
        System.out.println("To: " + phone);
        System.out.println("Message: " + message);
        System.out.println("---");
    }
}



