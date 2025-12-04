package airline.models;

import airline.enums.SeatClass;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

/**
 * Represents a flight search result with availability and pricing.
 */
public class FlightSearchResult {
    private final Flight flight;
    private final Map<SeatClass, Integer> availability;
    private final Map<SeatClass, BigDecimal> prices;

    public FlightSearchResult(Flight flight, Map<SeatClass, Integer> availability,
                              Map<SeatClass, BigDecimal> prices) {
        this.flight = flight;
        this.availability = availability;
        this.prices = prices;
    }

    public Flight getFlight() {
        return flight;
    }

    public Map<SeatClass, Integer> getAvailability() {
        return availability;
    }

    public Map<SeatClass, BigDecimal> getPrices() {
        return prices;
    }

    public int getAvailableSeats(SeatClass seatClass) {
        return availability.getOrDefault(seatClass, 0);
    }

    public BigDecimal getPrice(SeatClass seatClass) {
        return prices.getOrDefault(seatClass, BigDecimal.ZERO);
    }

    public int getTotalAvailableSeats() {
        return availability.values().stream().mapToInt(Integer::intValue).sum();
    }

    public BigDecimal getLowestPrice() {
        return prices.values().stream()
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    public Duration getDuration() {
        return flight.getDuration();
    }

    @Override
    public String toString() {
        return String.format("""
                ┌─────────────────────────────────────────────────────────────┐
                │ Flight: %-52s │
                │ Route: %s → %-48s │
                │ Departure: %-49s │
                │ Arrival: %-51s │
                │ Duration: %-50s │
                ├─────────────────────────────────────────────────────────────┤
                │ Economy: %d seats from $%-35.2f │
                │ Premium Economy: %d seats from $%-27.2f │
                │ Business: %d seats from $%-34.2f │
                │ First: %d seats from $%-37.2f │
                └─────────────────────────────────────────────────────────────┘
                """,
                flight.getFlightNumber(),
                flight.getSource().getCode(), flight.getDestination().getCode(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                formatDuration(getDuration()),
                getAvailableSeats(SeatClass.ECONOMY), getPrice(SeatClass.ECONOMY),
                getAvailableSeats(SeatClass.PREMIUM_ECONOMY), getPrice(SeatClass.PREMIUM_ECONOMY),
                getAvailableSeats(SeatClass.BUSINESS), getPrice(SeatClass.BUSINESS),
                getAvailableSeats(SeatClass.FIRST), getPrice(SeatClass.FIRST));
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return hours + "h " + minutes + "m";
    }
}



