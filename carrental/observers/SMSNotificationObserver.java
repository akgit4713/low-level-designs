package carrental.observers;

import carrental.models.Reservation;

/**
 * Observer that sends SMS notifications for reservation events.
 * In production, would integrate with an SMS gateway.
 */
public class SMSNotificationObserver implements ReservationObserver {

    @Override
    public void onReservationCreated(Reservation reservation) {
        sendSMS(
            reservation.getCustomer().getPhone(),
            String.format("Reservation %s created for %s to %s. Total: $%.2f",
                reservation.getId().substring(0, 8),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getTotalAmount())
        );
    }

    @Override
    public void onReservationConfirmed(Reservation reservation) {
        sendSMS(
            reservation.getCustomer().getPhone(),
            String.format("Reservation CONFIRMED! Pick up your %s %s on %s",
                reservation.getCar().getMake(),
                reservation.getCar().getModel(),
                reservation.getStartDate())
        );
    }

    @Override
    public void onReservationModified(Reservation reservation) {
        sendSMS(
            reservation.getCustomer().getPhone(),
            String.format("Reservation updated. New dates: %s to %s",
                reservation.getStartDate(),
                reservation.getEndDate())
        );
    }

    @Override
    public void onReservationCancelled(Reservation reservation) {
        sendSMS(
            reservation.getCustomer().getPhone(),
            "Your reservation has been cancelled. Refund processing if applicable."
        );
    }

    @Override
    public void onReservationCompleted(Reservation reservation) {
        sendSMS(
            reservation.getCustomer().getPhone(),
            "Thanks for renting with us! Hope to see you again soon."
        );
    }

    private void sendSMS(String phoneNumber, String message) {
        // In production, would integrate with SMS gateway
        System.out.println("[SMS] To: " + phoneNumber);
        System.out.println("[SMS] Message: " + message);
        System.out.println("---");
    }
}



