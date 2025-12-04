package airline.factories;

import airline.enums.SeatClass;
import airline.models.*;
import airline.strategies.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating tickets from confirmed bookings.
 */
public class TicketFactory {
    
    private final PricingStrategy pricingStrategy;

    public TicketFactory(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    /**
     * Creates tickets for all passengers in a confirmed booking.
     */
    public List<Ticket> createTickets(Booking booking) {
        if (!booking.isConfirmed()) {
            throw new IllegalStateException("Cannot create tickets for unconfirmed booking");
        }

        List<Ticket> tickets = new ArrayList<>();
        Flight flight = booking.getFlight();

        for (Booking.BookingPassenger bp : booking.getPassengers()) {
            Seat seat = flight.getSeat(bp.getSeatNumber()).orElse(null);
            SeatClass seatClass = seat != null ? seat.getSeatClass() : SeatClass.ECONOMY;
            
            BigDecimal fare = pricingStrategy.calculatePrice(flight, seatClass);
            if (seat != null) {
                fare = fare.add(seat.getExtraCharge());
            }

            Ticket ticket = new Ticket(booking, bp.getPassenger(), bp.getSeatNumber(), seatClass, fare);
            tickets.add(ticket);
        }

        return tickets;
    }

    /**
     * Creates a single ticket for a passenger.
     */
    public Ticket createTicket(Booking booking, Passenger passenger, String seatNumber) {
        Flight flight = booking.getFlight();
        Seat seat = flight.getSeat(seatNumber).orElse(null);
        SeatClass seatClass = seat != null ? seat.getSeatClass() : SeatClass.ECONOMY;
        
        BigDecimal fare = pricingStrategy.calculatePrice(flight, seatClass);
        if (seat != null) {
            fare = fare.add(seat.getExtraCharge());
        }

        return new Ticket(booking, passenger, seatNumber, seatClass, fare);
    }
}



