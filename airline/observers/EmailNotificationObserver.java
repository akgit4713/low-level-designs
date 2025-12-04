package airline.observers;

import airline.enums.FlightStatus;
import airline.models.Booking;
import airline.models.Flight;

/**
 * Observer that sends email notifications for flight and booking events.
 */
public class EmailNotificationObserver implements FlightObserver, BookingObserver {

    @Override
    public void onFlightStatusChanged(Flight flight, FlightStatus oldStatus, FlightStatus newStatus) {
        System.out.println("📧 [EMAIL] Flight " + flight.getFlightNumber() + 
                " status changed from " + oldStatus + " to " + newStatus);
    }

    @Override
    public void onFlightDelayed(Flight flight, String reason) {
        System.out.println("📧 [EMAIL] Flight " + flight.getFlightNumber() + 
                " is delayed. Reason: " + reason);
    }

    @Override
    public void onFlightCancelled(Flight flight, String reason) {
        System.out.println("📧 [EMAIL] Flight " + flight.getFlightNumber() + 
                " has been CANCELLED. Reason: " + reason);
    }

    @Override
    public void onBookingCreated(Booking booking) {
        System.out.println("📧 [EMAIL] Booking " + booking.getPnr() + 
                " created for flight " + booking.getFlight().getFlightNumber());
    }

    @Override
    public void onBookingConfirmed(Booking booking) {
        System.out.println("📧 [EMAIL] Booking CONFIRMED! PNR: " + booking.getPnr() + 
                " for flight " + booking.getFlight().getFlightNumber());
    }

    @Override
    public void onBookingCancelled(Booking booking) {
        System.out.println("📧 [EMAIL] Booking " + booking.getPnr() + " has been CANCELLED.");
    }
}



