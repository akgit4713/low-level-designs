package carrental.observers;

import carrental.models.Reservation;

/**
 * Observer that sends email notifications for reservation events.
 * In production, would integrate with an email service.
 */
public class EmailNotificationObserver implements ReservationObserver {

    @Override
    public void onReservationCreated(Reservation reservation) {
        sendEmail(
            reservation.getCustomer().getEmail(),
            "Reservation Created - " + reservation.getId(),
            String.format("Dear %s,\n\nYour reservation for %s %s has been created.\n" +
                "Dates: %s to %s\nTotal: $%.2f\n\nThank you for choosing us!",
                reservation.getCustomer().getName(),
                reservation.getCar().getMake(),
                reservation.getCar().getModel(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getTotalAmount())
        );
    }

    @Override
    public void onReservationConfirmed(Reservation reservation) {
        sendEmail(
            reservation.getCustomer().getEmail(),
            "Reservation Confirmed - " + reservation.getId(),
            String.format("Dear %s,\n\nYour reservation has been confirmed!\n" +
                "Car: %s %s\nPickup: %s\nReturn: %s\n\nWe look forward to seeing you!",
                reservation.getCustomer().getName(),
                reservation.getCar().getMake(),
                reservation.getCar().getModel(),
                reservation.getStartDate(),
                reservation.getEndDate())
        );
    }

    @Override
    public void onReservationModified(Reservation reservation) {
        sendEmail(
            reservation.getCustomer().getEmail(),
            "Reservation Modified - " + reservation.getId(),
            String.format("Dear %s,\n\nYour reservation has been modified.\n" +
                "New dates: %s to %s\nNew total: $%.2f",
                reservation.getCustomer().getName(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getTotalAmount())
        );
    }

    @Override
    public void onReservationCancelled(Reservation reservation) {
        sendEmail(
            reservation.getCustomer().getEmail(),
            "Reservation Cancelled - " + reservation.getId(),
            String.format("Dear %s,\n\nYour reservation has been cancelled.\n" +
                "If you paid by card, a refund will be processed within 5-7 business days.\n\n" +
                "We hope to serve you again soon!",
                reservation.getCustomer().getName())
        );
    }

    @Override
    public void onReservationCompleted(Reservation reservation) {
        sendEmail(
            reservation.getCustomer().getEmail(),
            "Thank You - Rental Completed",
            String.format("Dear %s,\n\nThank you for renting with us!\n" +
                "We hope you enjoyed your %s %s.\n\n" +
                "Please consider leaving us a review!",
                reservation.getCustomer().getName(),
                reservation.getCar().getMake(),
                reservation.getCar().getModel())
        );
    }

    private void sendEmail(String to, String subject, String body) {
        // In production, would integrate with email service
        System.out.println("[EMAIL] To: " + to);
        System.out.println("[EMAIL] Subject: " + subject);
        System.out.println("[EMAIL] Body: " + body);
        System.out.println("---");
    }
}



