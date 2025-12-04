package airline.models;

import airline.enums.SeatClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an issued ticket for a confirmed booking.
 */
public class Ticket {
    private final String ticketNumber;
    private final Booking booking;
    private final Passenger passenger;
    private final String seatNumber;
    private final SeatClass seatClass;
    private final BigDecimal fare;
    private final LocalDateTime issuedAt;
    private volatile boolean boardingPassIssued;

    public Ticket(Booking booking, Passenger passenger, String seatNumber, 
                  SeatClass seatClass, BigDecimal fare) {
        this.ticketNumber = generateTicketNumber();
        this.booking = booking;
        this.passenger = passenger;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.fare = fare;
        this.issuedAt = LocalDateTime.now();
        this.boardingPassIssued = false;
    }

    private String generateTicketNumber() {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public Booking getBooking() {
        return booking;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public boolean isBoardingPassIssued() {
        return boardingPassIssued;
    }

    public void issueBoardingPass() {
        this.boardingPassIssued = true;
    }

    public Flight getFlight() {
        return booking.getFlight();
    }

    @Override
    public String toString() {
        Flight flight = booking.getFlight();
        return String.format("""
                ╔══════════════════════════════════════════════════════════════╗
                ║                        E-TICKET                               ║
                ╠══════════════════════════════════════════════════════════════╣
                ║  Ticket Number: %-45s ║
                ║  PNR: %-54s ║
                ╠══════════════════════════════════════════════════════════════╣
                ║  Passenger: %-49s ║
                ║  Flight: %-52s ║
                ║  Route: %s → %-46s ║
                ║  Date: %-53s ║
                ║  Departure: %-48s ║
                ║  Arrival: %-50s ║
                ╠══════════════════════════════════════════════════════════════╣
                ║  Class: %-52s ║
                ║  Seat: %-53s ║
                ║  Fare: $%-51.2f ║
                ╚══════════════════════════════════════════════════════════════╝
                """,
                ticketNumber,
                booking.getPnr(),
                passenger.getFullName(),
                flight.getFlightNumber(),
                flight.getSource().getCode(), flight.getDestination().getCode(),
                flight.getDepartureTime().toLocalDate(),
                flight.getDepartureTime().toLocalTime(),
                flight.getArrivalTime().toLocalTime(),
                seatClass.getDisplayName(),
                seatNumber,
                fare);
    }
}



