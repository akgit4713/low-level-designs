package concertbooking.models;

import concertbooking.enums.SectionType;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an entry in the waitlist for a sold-out concert
 */
public class WaitlistEntry {
    private final String id;
    private final String concertId;
    private final String userId;
    private final int requestedSeats;
    private final SectionType preferredSection;
    private final LocalDateTime createdAt;
    private volatile boolean notified;
    private volatile LocalDateTime notifiedAt;
    private volatile LocalDateTime expiresAt;

    public WaitlistEntry(String id, String concertId, String userId, 
                         int requestedSeats, SectionType preferredSection) {
        this.id = Objects.requireNonNull(id, "Waitlist entry ID is required");
        this.concertId = Objects.requireNonNull(concertId, "Concert ID is required");
        this.userId = Objects.requireNonNull(userId, "User ID is required");
        this.requestedSeats = requestedSeats;
        this.preferredSection = preferredSection;
        this.createdAt = LocalDateTime.now();
        this.notified = false;
    }

    public String getId() { return id; }
    public String getConcertId() { return concertId; }
    public String getUserId() { return userId; }
    public int getRequestedSeats() { return requestedSeats; }
    public SectionType getPreferredSection() { return preferredSection; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isNotified() { return notified; }
    public LocalDateTime getNotifiedAt() { return notifiedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }

    public void markNotified(int validityMinutes) {
        this.notified = true;
        this.notifiedAt = LocalDateTime.now();
        this.expiresAt = this.notifiedAt.plusMinutes(validityMinutes);
    }

    public boolean isExpired() {
        return notified && expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WaitlistEntry that = (WaitlistEntry) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("WaitlistEntry{id='%s', concertId='%s', userId='%s', seats=%d, notified=%b}",
            id, concertId, userId, requestedSeats, notified);
    }
}



