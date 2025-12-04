package concertbooking.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a ticket for a concert seat
 * Generated after successful booking confirmation
 */
public class Ticket {
    private final String id;
    private final String ticketNumber;
    private final String bookingId;
    private final String concertId;
    private final String concertName;
    private final String artistName;
    private final String venueName;
    private final LocalDateTime concertDateTime;
    private final String seatId;
    private final String seatLabel;
    private final String sectionName;
    private final BigDecimal price;
    private final String userId;
    private final String userName;
    private final LocalDateTime issuedAt;
    private final String qrCode;

    private Ticket(Builder builder) {
        this.id = builder.id;
        this.ticketNumber = generateTicketNumber();
        this.bookingId = builder.bookingId;
        this.concertId = builder.concertId;
        this.concertName = builder.concertName;
        this.artistName = builder.artistName;
        this.venueName = builder.venueName;
        this.concertDateTime = builder.concertDateTime;
        this.seatId = builder.seatId;
        this.seatLabel = builder.seatLabel;
        this.sectionName = builder.sectionName;
        this.price = builder.price;
        this.userId = builder.userId;
        this.userName = builder.userName;
        this.issuedAt = LocalDateTime.now();
        this.qrCode = generateQRCode();
    }

    private String generateTicketNumber() {
        return "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateQRCode() {
        // In real implementation, this would generate actual QR code data
        return String.format("QR:%s:%s:%s", id, ticketNumber, bookingId);
    }

    public String getId() { return id; }
    public String getTicketNumber() { return ticketNumber; }
    public String getBookingId() { return bookingId; }
    public String getConcertId() { return concertId; }
    public String getConcertName() { return concertName; }
    public String getArtistName() { return artistName; }
    public String getVenueName() { return venueName; }
    public LocalDateTime getConcertDateTime() { return concertDateTime; }
    public String getSeatId() { return seatId; }
    public String getSeatLabel() { return seatLabel; }
    public String getSectionName() { return sectionName; }
    public BigDecimal getPrice() { return price; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public String getQrCode() { return qrCode; }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return Objects.equals(id, ticket.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("""
            ╔══════════════════════════════════════════════════════════╗
            ║                     CONCERT TICKET                        ║
            ╠══════════════════════════════════════════════════════════╣
            ║  Ticket #: %-45s ║
            ║  Concert:  %-45s ║
            ║  Artist:   %-45s ║
            ║  Venue:    %-45s ║
            ║  Date:     %-45s ║
            ║  Seat:     %-45s ║
            ║  Section:  %-45s ║
            ║  Price:    $%-44s ║
            ║  Holder:   %-45s ║
            ╠══════════════════════════════════════════════════════════╣
            ║  QR: %-50s ║
            ╚══════════════════════════════════════════════════════════╝
            """,
            ticketNumber, concertName, artistName, venueName,
            concertDateTime, seatLabel, sectionName, price, userName, qrCode);
    }

    public static class Builder {
        private String id;
        private String bookingId;
        private String concertId;
        private String concertName;
        private String artistName;
        private String venueName;
        private LocalDateTime concertDateTime;
        private String seatId;
        private String seatLabel;
        private String sectionName;
        private BigDecimal price;
        private String userId;
        private String userName;

        public Builder id(String id) { this.id = id; return this; }
        public Builder bookingId(String bookingId) { this.bookingId = bookingId; return this; }
        public Builder concertId(String concertId) { this.concertId = concertId; return this; }
        public Builder concertName(String concertName) { this.concertName = concertName; return this; }
        public Builder artistName(String artistName) { this.artistName = artistName; return this; }
        public Builder venueName(String venueName) { this.venueName = venueName; return this; }
        public Builder concertDateTime(LocalDateTime concertDateTime) { this.concertDateTime = concertDateTime; return this; }
        public Builder seatId(String seatId) { this.seatId = seatId; return this; }
        public Builder seatLabel(String seatLabel) { this.seatLabel = seatLabel; return this; }
        public Builder sectionName(String sectionName) { this.sectionName = sectionName; return this; }
        public Builder price(BigDecimal price) { this.price = price; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder userName(String userName) { this.userName = userName; return this; }

        public Ticket build() {
            Objects.requireNonNull(id, "Ticket ID is required");
            Objects.requireNonNull(bookingId, "Booking ID is required");
            Objects.requireNonNull(concertId, "Concert ID is required");
            Objects.requireNonNull(seatId, "Seat ID is required");
            return new Ticket(this);
        }
    }
}



