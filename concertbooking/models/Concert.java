package concertbooking.models;

import concertbooking.enums.ConcertStatus;
import concertbooking.enums.SeatStatus;
import concertbooking.enums.SectionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Represents a concert event
 */
public class Concert {
    private final String id;
    private final String name;
    private final String artist;
    private final String description;
    private final Venue venue;
    private final LocalDateTime dateTime;
    private final int durationMinutes;
    private final BigDecimal basePrice;
    private volatile ConcertStatus status;
    
    // Seats for this concert (copied from venue, with concert-specific status)
    private final Map<String, Seat> seats = new ConcurrentHashMap<>();
    private final Map<SectionType, BigDecimal> sectionPrices = new ConcurrentHashMap<>();
    
    private final LocalDateTime createdAt;
    private volatile LocalDateTime salesStartAt;
    private volatile LocalDateTime salesEndAt;

    private Concert(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.artist = builder.artist;
        this.description = builder.description;
        this.venue = builder.venue;
        this.dateTime = builder.dateTime;
        this.durationMinutes = builder.durationMinutes;
        this.basePrice = builder.basePrice;
        this.status = ConcertStatus.SCHEDULED;
        this.createdAt = LocalDateTime.now();
        this.salesStartAt = builder.salesStartAt;
        this.salesEndAt = builder.salesEndAt;
        
        // Initialize seats from venue
        initializeSeats();
        
        // Initialize section prices
        initializeSectionPrices();
    }

    private void initializeSeats() {
        for (Section section : venue.getSections()) {
            for (Seat venueSeat : section.getSeats()) {
                // Create a concert-specific copy of the seat
                Seat concertSeat = new Seat(
                    id + "-" + venueSeat.getId(),
                    section.getId(),
                    section.getType(),
                    venueSeat.getRowNumber(),
                    venueSeat.getSeatNumber()
                );
                seats.put(concertSeat.getId(), concertSeat);
            }
        }
    }

    private void initializeSectionPrices() {
        for (SectionType type : SectionType.values()) {
            BigDecimal multiplier = BigDecimal.valueOf(type.getPriceMultiplier());
            sectionPrices.put(type, basePrice.multiply(multiplier));
        }
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getArtist() { return artist; }
    public String getDescription() { return description; }
    public Venue getVenue() { return venue; }
    public LocalDateTime getDateTime() { return dateTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public BigDecimal getBasePrice() { return basePrice; }
    public ConcertStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getSalesStartAt() { return salesStartAt; }
    public LocalDateTime getSalesEndAt() { return salesEndAt; }

    public void setStatus(ConcertStatus status) {
        this.status = status;
    }

    public void setSalesStartAt(LocalDateTime salesStartAt) {
        this.salesStartAt = salesStartAt;
    }

    public void setSalesEndAt(LocalDateTime salesEndAt) {
        this.salesEndAt = salesEndAt;
    }

    public BigDecimal getSectionPrice(SectionType sectionType) {
        return sectionPrices.getOrDefault(sectionType, basePrice);
    }

    public void setSectionPrice(SectionType sectionType, BigDecimal price) {
        sectionPrices.put(sectionType, price);
    }

    public Optional<Seat> getSeat(String seatId) {
        return Optional.ofNullable(seats.get(seatId));
    }

    public List<Seat> getAllSeats() {
        return new ArrayList<>(seats.values());
    }

    public List<Seat> getAvailableSeats() {
        return seats.values().stream()
            .filter(Seat::isAvailable)
            .collect(Collectors.toList());
    }

    public List<Seat> getAvailableSeatsBySection(SectionType sectionType) {
        return seats.values().stream()
            .filter(seat -> seat.getSectionType() == sectionType && seat.isAvailable())
            .collect(Collectors.toList());
    }

    public int getTotalSeats() {
        return seats.size();
    }

    public int getAvailableSeatsCount() {
        return (int) seats.values().stream()
            .filter(Seat::isAvailable)
            .count();
    }

    public int getBookedSeatsCount() {
        return (int) seats.values().stream()
            .filter(seat -> seat.getStatus() == SeatStatus.BOOKED)
            .count();
    }

    public boolean isSoldOut() {
        return getAvailableSeatsCount() == 0;
    }

    public boolean isBookable() {
        if (!status.isBookable()) return false;
        LocalDateTime now = LocalDateTime.now();
        if (salesStartAt != null && now.isBefore(salesStartAt)) return false;
        if (salesEndAt != null && now.isAfter(salesEndAt)) return false;
        return dateTime.isAfter(now);
    }

    /**
     * Calculates total price for given seats
     */
    public BigDecimal calculateTotalPrice(List<Seat> selectedSeats) {
        return selectedSeats.stream()
            .map(seat -> getSectionPrice(seat.getSectionType()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Concert concert = (Concert) o;
        return Objects.equals(id, concert.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Concert{id='%s', name='%s', artist='%s', venue='%s', dateTime=%s, status=%s}",
            id, name, artist, venue.getName(), dateTime, status);
    }

    public static class Builder {
        private String id;
        private String name;
        private String artist;
        private String description;
        private Venue venue;
        private LocalDateTime dateTime;
        private int durationMinutes = 120;
        private BigDecimal basePrice;
        private LocalDateTime salesStartAt;
        private LocalDateTime salesEndAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder artist(String artist) { this.artist = artist; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder venue(Venue venue) { this.venue = venue; return this; }
        public Builder dateTime(LocalDateTime dateTime) { this.dateTime = dateTime; return this; }
        public Builder durationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; return this; }
        public Builder basePrice(BigDecimal basePrice) { this.basePrice = basePrice; return this; }
        public Builder salesStartAt(LocalDateTime salesStartAt) { this.salesStartAt = salesStartAt; return this; }
        public Builder salesEndAt(LocalDateTime salesEndAt) { this.salesEndAt = salesEndAt; return this; }

        public Concert build() {
            Objects.requireNonNull(id, "Concert ID is required");
            Objects.requireNonNull(name, "Concert name is required");
            Objects.requireNonNull(artist, "Artist is required");
            Objects.requireNonNull(venue, "Venue is required");
            Objects.requireNonNull(dateTime, "Date/time is required");
            Objects.requireNonNull(basePrice, "Base price is required");
            
            if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Base price must be positive");
            }
            
            return new Concert(this);
        }
    }
}



