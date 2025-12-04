package concertbooking.enums;

/**
 * Represents the status of a concert
 */
public enum ConcertStatus {
    SCHEDULED("Concert is scheduled"),
    ON_SALE("Tickets are on sale"),
    SOLD_OUT("All tickets sold"),
    ONGOING("Concert is happening"),
    COMPLETED("Concert has ended"),
    CANCELLED("Concert cancelled"),
    POSTPONED("Concert postponed");

    private final String description;

    ConcertStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isBookable() {
        return this == ON_SALE;
    }

    public boolean canJoinWaitlist() {
        return this == SOLD_OUT;
    }
}



