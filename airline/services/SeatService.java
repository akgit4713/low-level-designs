package airline.services;

import airline.enums.SeatClass;
import airline.models.Flight;
import airline.models.Seat;

import java.util.List;

/**
 * Service interface for seat management operations.
 */
public interface SeatService {
    
    /**
     * Gets all available seats for a flight.
     */
    List<Seat> getAvailableSeats(Flight flight);
    
    /**
     * Gets available seats by class.
     */
    List<Seat> getAvailableSeats(Flight flight, SeatClass seatClass);
    
    /**
     * Gets a specific seat on a flight.
     */
    Seat getSeat(Flight flight, String seatNumber);
    
    /**
     * Temporarily blocks a seat during booking.
     */
    boolean blockSeat(Flight flight, String seatNumber);
    
    /**
     * Unblocks a temporarily blocked seat.
     */
    boolean unblockSeat(Flight flight, String seatNumber);
    
    /**
     * Books a seat for a passenger.
     */
    boolean bookSeat(Flight flight, String seatNumber, String passengerId);
    
    /**
     * Releases a booked seat.
     */
    boolean releaseSeat(Flight flight, String seatNumber);
    
    /**
     * Generates a seat map display.
     */
    String generateSeatMap(Flight flight);
}



