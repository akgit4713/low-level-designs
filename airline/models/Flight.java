package airline.models;

import airline.enums.FlightStatus;
import airline.enums.SeatClass;
import airline.exceptions.FlightException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Represents a flight with schedule, seats, and crew.
 * Thread-safe for concurrent seat booking operations.
 */
public class Flight {
    private final String flightNumber;
    private final Airport source;
    private final Airport destination;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
    private final Aircraft aircraft;
    private final Map<SeatClass, BigDecimal> basePrices;
    private final Map<String, Seat> seats; // seatNumber -> Seat
    private final List<Crew> crewMembers;
    private volatile FlightStatus status;
    private volatile LocalDateTime actualDepartureTime;
    private volatile LocalDateTime actualArrivalTime;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private Flight(Builder builder) {
        this.flightNumber = builder.flightNumber;
        this.source = builder.source;
        this.destination = builder.destination;
        this.departureTime = builder.departureTime;
        this.arrivalTime = builder.arrivalTime;
        this.aircraft = builder.aircraft;
        this.basePrices = new EnumMap<>(builder.basePrices);
        this.seats = new ConcurrentHashMap<>();
        this.crewMembers = Collections.synchronizedList(new ArrayList<>());
        this.status = FlightStatus.SCHEDULED;
        
        // Initialize seats from aircraft configuration
        initializeSeats();
    }

    public static Builder builder() {
        return new Builder();
    }

    private void initializeSeats() {
        int currentRow = 1;
        char[] columns = {'A', 'B', 'C', 'D', 'E', 'F'}; // Standard 3-3 configuration
        
        for (SeatClass seatClass : SeatClass.values()) {
            int seatCount = aircraft.getSeatCount(seatClass);
            int seatsPerRow = 6;
            int rows = (int) Math.ceil((double) seatCount / seatsPerRow);
            
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < seatsPerRow && (r * seatsPerRow + c) < seatCount; c++) {
                    String seatNumber = currentRow + "" + columns[c];
                    boolean isWindow = c == 0 || c == 5;
                    boolean isAisle = c == 2 || c == 3;
                    
                    Seat seat = Seat.builder()
                            .seatNumber(seatNumber)
                            .seatClass(seatClass)
                            .row(currentRow)
                            .column(columns[c])
                            .windowSeat(isWindow)
                            .aisleSeat(isAisle)
                            .exitRow(currentRow == 10 || currentRow == 20) // Example exit rows
                            .build();
                    
                    seats.put(seatNumber, seat);
                }
                currentRow++;
            }
        }
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public Airport getSource() {
        return source;
    }

    public Airport getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public Duration getDuration() {
        return Duration.between(departureTime, arrivalTime);
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public BigDecimal getBasePrice(SeatClass seatClass) {
        return basePrices.getOrDefault(seatClass, BigDecimal.ZERO);
    }

    public Map<SeatClass, BigDecimal> getBasePrices() {
        return new EnumMap<>(basePrices);
    }

    /**
     * Updates flight status with validation.
     */
    public void setStatus(FlightStatus newStatus) {
        lock.writeLock().lock();
        try {
            if (!this.status.canTransitionTo(newStatus)) {
                throw new FlightException("Invalid status transition from " + this.status + " to " + newStatus);
            }
            this.status = newStatus;
            
            if (newStatus == FlightStatus.DEPARTED) {
                this.actualDepartureTime = LocalDateTime.now();
            } else if (newStatus == FlightStatus.LANDED) {
                this.actualArrivalTime = LocalDateTime.now();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public LocalDateTime getActualDepartureTime() {
        return actualDepartureTime;
    }

    public LocalDateTime getActualArrivalTime() {
        return actualArrivalTime;
    }

    // === Seat Operations ===

    public Optional<Seat> getSeat(String seatNumber) {
        return Optional.ofNullable(seats.get(seatNumber));
    }

    public List<Seat> getAllSeats() {
        return new ArrayList<>(seats.values());
    }

    public List<Seat> getAvailableSeats() {
        return seats.values().stream()
                .filter(Seat::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Seat> getAvailableSeats(SeatClass seatClass) {
        return seats.values().stream()
                .filter(seat -> seat.getSeatClass() == seatClass && seat.isAvailable())
                .collect(Collectors.toList());
    }

    public int getAvailableSeatCount() {
        return (int) seats.values().stream().filter(Seat::isAvailable).count();
    }

    public int getAvailableSeatCount(SeatClass seatClass) {
        return (int) seats.values().stream()
                .filter(seat -> seat.getSeatClass() == seatClass && seat.isAvailable())
                .count();
    }

    /**
     * Books a seat on this flight.
     * @return true if booking successful
     */
    public boolean bookSeat(String seatNumber, String passengerId) {
        Seat seat = seats.get(seatNumber);
        if (seat == null) {
            throw new FlightException("Seat " + seatNumber + " not found on flight " + flightNumber);
        }
        return seat.book(passengerId);
    }

    /**
     * Releases a booked seat.
     */
    public boolean releaseSeat(String seatNumber) {
        Seat seat = seats.get(seatNumber);
        if (seat == null) {
            return false;
        }
        return seat.release();
    }

    // === Crew Operations ===

    public void addCrewMember(Crew crew) {
        crewMembers.add(crew);
    }

    public void removeCrewMember(Crew crew) {
        crewMembers.remove(crew);
    }

    public List<Crew> getCrewMembers() {
        return new ArrayList<>(crewMembers);
    }

    @Override
    public String toString() {
        return String.format("Flight[%s | %s → %s | %s | %s | Available: %d/%d]",
                flightNumber,
                source.getCode(),
                destination.getCode(),
                departureTime.toString(),
                status,
                getAvailableSeatCount(),
                seats.size());
    }

    public static class Builder {
        private String flightNumber;
        private Airport source;
        private Airport destination;
        private LocalDateTime departureTime;
        private LocalDateTime arrivalTime;
        private Aircraft aircraft;
        private final Map<SeatClass, BigDecimal> basePrices = new EnumMap<>(SeatClass.class);

        public Builder flightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
            return this;
        }

        public Builder source(Airport source) {
            this.source = source;
            return this;
        }

        public Builder destination(Airport destination) {
            this.destination = destination;
            return this;
        }

        public Builder departureTime(LocalDateTime departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        public Builder arrivalTime(LocalDateTime arrivalTime) {
            this.arrivalTime = arrivalTime;
            return this;
        }

        public Builder aircraft(Aircraft aircraft) {
            this.aircraft = aircraft;
            return this;
        }

        public Builder basePrice(SeatClass seatClass, BigDecimal price) {
            this.basePrices.put(seatClass, price);
            return this;
        }

        public Flight build() {
            if (flightNumber == null || source == null || destination == null ||
                    departureTime == null || arrivalTime == null || aircraft == null) {
                throw new IllegalStateException("Flight requires all mandatory fields");
            }
            if (arrivalTime.isBefore(departureTime)) {
                throw new IllegalStateException("Arrival time must be after departure time");
            }
            return new Flight(this);
        }
    }
}



