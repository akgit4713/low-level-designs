package hotelmanagement.observers;

import hotelmanagement.models.Reservation;

/**
 * Observer that sends email notifications for reservation events
 * In a real implementation, this would integrate with an email service
 */
public class EmailNotificationObserver implements ReservationObserver {
    
    @Override
    public void onReservationCreated(Reservation reservation) {
        sendEmail(
            reservation.getGuest().getEmail(),
            "Reservation Request Received",
            String.format(
                "Dear %s,\n\n" +
                "We have received your reservation request for Room %s (%s).\n" +
                "Check-in: %s\n" +
                "Check-out: %s\n" +
                "Number of guests: %d\n\n" +
                "Reservation ID: %s\n\n" +
                "We will confirm your reservation shortly.\n\n" +
                "Best regards,\nHotel Management",
                reservation.getGuest().getName(),
                reservation.getRoom().getRoomNumber(),
                reservation.getRoom().getType(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getNumberOfGuests(),
                reservation.getId()
            )
        );
    }
    
    @Override
    public void onReservationConfirmed(Reservation reservation) {
        sendEmail(
            reservation.getGuest().getEmail(),
            "Reservation Confirmed - " + reservation.getId(),
            String.format(
                "Dear %s,\n\n" +
                "Your reservation has been confirmed!\n\n" +
                "Confirmation Number: %s\n" +
                "Room: %s (%s)\n" +
                "Check-in: %s (after 3:00 PM)\n" +
                "Check-out: %s (before 11:00 AM)\n" +
                "Rate: $%s per night\n\n" +
                "We look forward to welcoming you!\n\n" +
                "Best regards,\nHotel Management",
                reservation.getGuest().getName(),
                reservation.getId(),
                reservation.getRoom().getRoomNumber(),
                reservation.getRoom().getType(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getRoomRatePerNight()
            )
        );
    }
    
    @Override
    public void onReservationCancelled(Reservation reservation) {
        sendEmail(
            reservation.getGuest().getEmail(),
            "Reservation Cancelled - " + reservation.getId(),
            String.format(
                "Dear %s,\n\n" +
                "Your reservation (ID: %s) has been cancelled.\n\n" +
                "If you did not request this cancellation or have any questions, " +
                "please contact our front desk.\n\n" +
                "We hope to welcome you in the future.\n\n" +
                "Best regards,\nHotel Management",
                reservation.getGuest().getName(),
                reservation.getId()
            )
        );
    }
    
    @Override
    public void onCheckIn(Reservation reservation) {
        sendEmail(
            reservation.getGuest().getEmail(),
            "Welcome to Our Hotel!",
            String.format(
                "Dear %s,\n\n" +
                "Welcome! You have successfully checked in.\n\n" +
                "Room: %s on Floor %d\n" +
                "WiFi Password: HOTEL2024\n\n" +
                "Enjoy your stay!\n\n" +
                "Best regards,\nHotel Management",
                reservation.getGuest().getName(),
                reservation.getRoom().getRoomNumber(),
                reservation.getRoom().getFloor()
            )
        );
    }
    
    @Override
    public void onCheckOut(Reservation reservation) {
        sendEmail(
            reservation.getGuest().getEmail(),
            "Thank You for Staying With Us!",
            String.format(
                "Dear %s,\n\n" +
                "Thank you for staying with us!\n\n" +
                "We hope you had a pleasant stay. Your final bill will be sent separately.\n\n" +
                "We look forward to welcoming you again!\n\n" +
                "Best regards,\nHotel Management",
                reservation.getGuest().getName()
            )
        );
    }
    
    @Override
    public void onNoShow(Reservation reservation) {
        sendEmail(
            reservation.getGuest().getEmail(),
            "Missed Reservation - " + reservation.getId(),
            String.format(
                "Dear %s,\n\n" +
                "We noticed you did not check in for your reservation (ID: %s) " +
                "scheduled for %s.\n\n" +
                "If you still wish to stay with us, please contact our front desk " +
                "to make a new reservation.\n\n" +
                "Best regards,\nHotel Management",
                reservation.getGuest().getName(),
                reservation.getId(),
                reservation.getCheckInDate()
            )
        );
    }
    
    /**
     * Simulates sending an email
     * In production, this would integrate with an email service (SMTP, SendGrid, etc.)
     */
    private void sendEmail(String to, String subject, String body) {
        System.out.println("\n📧 EMAIL NOTIFICATION");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("---");
        System.out.println(body);
        System.out.println("---\n");
    }
}



