package bookmyshow.models;

import bookmyshow.enums.SeatStatus;
import bookmyshow.enums.ShowStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Represents a movie show at a specific time and screen.
 */
public class Show {
    private final String id;
    private final String movieId;
    private final String screenId;
    private final String theaterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ShowStatus status;
    private BigDecimal basePrice;
    private final Map<String, ShowSeat> showSeats;  // seatId -> ShowSeat

    public Show(String movieId, String screenId, String theaterId, 
                LocalDateTime startTime, LocalDateTime endTime, BigDecimal basePrice) {
        this.id = UUID.randomUUID().toString();
        this.movieId = movieId;
        this.screenId = screenId;
        this.theaterId = theaterId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.basePrice = basePrice;
        this.status = ShowStatus.SCHEDULED;
        this.showSeats = new ConcurrentHashMap<>();
    }

    // Getters
    public String getId() { return id; }
    public String getMovieId() { return movieId; }
    public String getScreenId() { return screenId; }
    public String getTheaterId() { return theaterId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public ShowStatus getStatus() { return status; }
    public BigDecimal getBasePrice() { return basePrice; }
    public Map<String, ShowSeat> getShowSeats() { return Collections.unmodifiableMap(showSeats); }

    // Setters
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setStatus(ShowStatus status) { this.status = status; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    // Seat management
    public void addShowSeat(ShowSeat showSeat) {
        showSeats.put(showSeat.getSeat().getId(), showSeat);
    }

    public ShowSeat getShowSeat(String seatId) {
        return showSeats.get(seatId);
    }

    public List<ShowSeat> getAvailableSeats() {
        return showSeats.values().stream()
            .filter(ShowSeat::isAvailable)
            .collect(Collectors.toList());
    }

    public List<ShowSeat> getSeatsByStatus(SeatStatus status) {
        return showSeats.values().stream()
            .filter(ss -> ss.getStatus() == status)
            .collect(Collectors.toList());
    }

    public int getAvailableSeatCount() {
        return (int) showSeats.values().stream()
            .filter(ShowSeat::isAvailable)
            .count();
    }

    public int getTotalSeatCount() {
        return showSeats.size();
    }

    public int getBookedSeatCount() {
        return (int) showSeats.values().stream()
            .filter(ss -> ss.getStatus() == SeatStatus.BOOKED)
            .count();
    }

    public boolean isShowFull() {
        return getAvailableSeatCount() == 0;
    }

    public List<ShowSeat> getShowSeatsByIds(List<String> seatIds) {
        List<ShowSeat> result = new ArrayList<>();
        for (String seatId : seatIds) {
            ShowSeat showSeat = showSeats.get(seatId);
            if (showSeat != null) {
                result.add(showSeat);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Show{id='%s', movieId='%s', startTime=%s, status=%s, available=%d/%d}", 
            id, movieId, startTime, status, getAvailableSeatCount(), getTotalSeatCount());
    }
}



