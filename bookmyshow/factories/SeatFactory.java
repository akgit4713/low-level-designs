package bookmyshow.factories;

import bookmyshow.enums.SeatType;
import bookmyshow.models.Seat;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating seat layouts for screens.
 */
public class SeatFactory {
    
    /**
     * Create a standard seat layout for a screen.
     * @param screenId The screen ID
     * @param regularRows Number of regular seat rows
     * @param premiumRows Number of premium seat rows
     * @param reclinerRows Number of recliner rows
     * @param seatsPerRow Number of seats per row
     * @return List of seats
     */
    public static List<Seat> createStandardLayout(
            String screenId, 
            int regularRows, 
            int premiumRows, 
            int reclinerRows, 
            int seatsPerRow) {
        
        List<Seat> seats = new ArrayList<>();
        char rowLabel = 'A';
        
        // Regular seats (front rows)
        for (int i = 0; i < regularRows; i++) {
            for (int j = 1; j <= seatsPerRow; j++) {
                seats.add(new Seat(screenId, String.valueOf(rowLabel), j, SeatType.REGULAR));
            }
            rowLabel++;
        }
        
        // Premium seats (middle rows)
        for (int i = 0; i < premiumRows; i++) {
            for (int j = 1; j <= seatsPerRow; j++) {
                seats.add(new Seat(screenId, String.valueOf(rowLabel), j, SeatType.PREMIUM));
            }
            rowLabel++;
        }
        
        // Recliner seats (back rows)
        for (int i = 0; i < reclinerRows; i++) {
            // Recliners typically have fewer seats per row
            int reclinerSeatsPerRow = Math.max(seatsPerRow / 2, 4);
            for (int j = 1; j <= reclinerSeatsPerRow; j++) {
                seats.add(new Seat(screenId, String.valueOf(rowLabel), j, SeatType.RECLINER));
            }
            rowLabel++;
        }
        
        return seats;
    }
    
    /**
     * Create a simple uniform seat layout.
     * @param screenId The screen ID
     * @param rows Number of rows
     * @param seatsPerRow Seats per row
     * @param seatType Type of all seats
     * @return List of seats
     */
    public static List<Seat> createUniformLayout(
            String screenId, 
            int rows, 
            int seatsPerRow, 
            SeatType seatType) {
        
        List<Seat> seats = new ArrayList<>();
        char rowLabel = 'A';
        
        for (int i = 0; i < rows; i++) {
            for (int j = 1; j <= seatsPerRow; j++) {
                seats.add(new Seat(screenId, String.valueOf(rowLabel), j, seatType));
            }
            rowLabel++;
        }
        
        return seats;
    }
    
    /**
     * Create a VIP screen layout with wheelchair accessible seats.
     * @param screenId The screen ID
     * @param vipRows VIP seat rows
     * @param premiumRows Premium seat rows
     * @param wheelchairSeats Number of wheelchair accessible seats
     * @param seatsPerRow Seats per row
     * @return List of seats
     */
    public static List<Seat> createVIPLayout(
            String screenId,
            int vipRows,
            int premiumRows,
            int wheelchairSeats,
            int seatsPerRow) {
        
        List<Seat> seats = new ArrayList<>();
        char rowLabel = 'A';
        
        // VIP seats (recliner-style at the back)
        for (int i = 0; i < vipRows; i++) {
            for (int j = 1; j <= seatsPerRow; j++) {
                seats.add(new Seat(screenId, String.valueOf(rowLabel), j, SeatType.VIP));
            }
            rowLabel++;
        }
        
        // Premium seats
        for (int i = 0; i < premiumRows; i++) {
            for (int j = 1; j <= seatsPerRow; j++) {
                seats.add(new Seat(screenId, String.valueOf(rowLabel), j, SeatType.PREMIUM));
            }
            rowLabel++;
        }
        
        // Wheelchair accessible seats (front row near aisle)
        String wheelchairRow = String.valueOf(rowLabel);
        for (int j = 1; j <= wheelchairSeats; j++) {
            seats.add(new Seat(screenId, wheelchairRow, j, SeatType.WHEELCHAIR));
        }
        
        return seats;
    }
}



