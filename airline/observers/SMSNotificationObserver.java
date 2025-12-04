package airline.observers;

import airline.enums.FlightStatus;
import airline.models.Booking;
import airline.models.Flight;

/**
 * Observer that sends SMS notifications for flight and booking events.
 */
public class SMSNotificationObserver implements FlightObserver, BookingObserver {

    @Override
    public void onFlightStatusChanged(Flight flight, FlightStatus oldStatus, FlightStatus newStatus) {
        System.out.println("📱 [SMS] Flight " + flight.getFlightNumber() + ": " + newStatus);
    }

    @Override
    public void onFlightDelayed(Flight flight, String reason) {
        System.out.println("📱 [SMS] Alert! Flight " + flight.getFlightNumber() + " delayed.");
    }

    @Override
    public void onFlightCancelled(Flight flight, String reason) {
        System.out.println("📱 [SMS] URGENT! Flight " + flight.getFlightNumber() + " cancelled.");
    }

    @Override
    public void onBookingCreated(Booking booking) {
        // SMS not sent for creation, only confirmation
    }

    @Override
    public void onBookingConfirmed(Booking booking) {
        System.out.println("📱 [SMS] Booking confirmed! PNR: " + booking.getPnr());
    }

    @Override
    public void onBookingCancelled(Booking booking) {
        System.out.println("📱 [SMS] Your booking " + booking.getPnr() + " is cancelled.");
    }
}



