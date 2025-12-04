package airline.services.impl;

import airline.enums.SeatClass;
import airline.enums.SeatStatus;
import airline.exceptions.SeatException;
import airline.models.Flight;
import airline.models.Seat;
import airline.services.SeatService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SeatService.
 */
public class SeatServiceImpl implements SeatService {

    @Override
    public List<Seat> getAvailableSeats(Flight flight) {
        return flight.getAvailableSeats();
    }

    @Override
    public List<Seat> getAvailableSeats(Flight flight, SeatClass seatClass) {
        return flight.getAvailableSeats(seatClass);
    }

    @Override
    public Seat getSeat(Flight flight, String seatNumber) {
        return flight.getSeat(seatNumber)
                .orElseThrow(() -> new SeatException("Seat " + seatNumber + " not found"));
    }

    @Override
    public boolean blockSeat(Flight flight, String seatNumber) {
        Seat seat = getSeat(flight, seatNumber);
        return seat.block();
    }

    @Override
    public boolean unblockSeat(Flight flight, String seatNumber) {
        Seat seat = getSeat(flight, seatNumber);
        return seat.unblock();
    }

    @Override
    public boolean bookSeat(Flight flight, String seatNumber, String passengerId) {
        return flight.bookSeat(seatNumber, passengerId);
    }

    @Override
    public boolean releaseSeat(Flight flight, String seatNumber) {
        return flight.releaseSeat(seatNumber);
    }

    @Override
    public String generateSeatMap(Flight flight) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("═".repeat(50)).append("\n");
        sb.append("        SEAT MAP - Flight ").append(flight.getFlightNumber()).append("\n");
        sb.append("═".repeat(50)).append("\n\n");
        
        // Group seats by class
        for (SeatClass seatClass : SeatClass.values()) {
            List<Seat> classSeats = flight.getAllSeats().stream()
                    .filter(s -> s.getSeatClass() == seatClass)
                    .collect(Collectors.toList());
            
            if (classSeats.isEmpty()) continue;
            
            sb.append("  ").append(seatClass.getDisplayName()).append("\n");
            sb.append("  ").append("-".repeat(40)).append("\n");
            
            // Group by row
            int currentRow = -1;
            for (Seat seat : classSeats) {
                if (seat.getRow() != currentRow) {
                    if (currentRow != -1) sb.append("\n");
                    currentRow = seat.getRow();
                    sb.append(String.format("  Row %2d: ", currentRow));
                }
                
                String seatDisplay = switch (seat.getStatus()) {
                    case AVAILABLE -> "[" + seat.getColumn() + "]";
                    case BOOKED -> "[X]";
                    case BLOCKED -> "[B]";
                    case UNAVAILABLE -> "[-]";
                };
                sb.append(seatDisplay).append(" ");
            }
            sb.append("\n\n");
        }
        
        sb.append("  Legend: [A-F] = Available, [X] = Booked, [B] = Blocked, [-] = Unavailable\n");
        sb.append("═".repeat(50)).append("\n");
        
        return sb.toString();
    }
}



