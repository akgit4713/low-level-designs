package onlineauction.enums;

/**
 * Types of notifications that can be sent to users.
 */
public enum NotificationType {
    /**
     * User has been outbid on an auction
     */
    OUTBID("You have been outbid!", Priority.HIGH),
    
    /**
     * User won an auction
     */
    AUCTION_WON("Congratulations! You won the auction!", Priority.HIGH),
    
    /**
     * User's auction has ended
     */
    AUCTION_ENDED("Your auction has ended", Priority.MEDIUM),
    
    /**
     * Auction is ending soon (reminder)
     */
    AUCTION_ENDING_SOON("Auction ending soon!", Priority.MEDIUM),
    
    /**
     * A new bid was placed on user's auction
     */
    NEW_BID_RECEIVED("New bid received on your auction", Priority.LOW),
    
    /**
     * User's bid was successfully placed
     */
    BID_CONFIRMED("Your bid has been placed", Priority.LOW),
    
    /**
     * Auction was cancelled
     */
    AUCTION_CANCELLED("Auction has been cancelled", Priority.MEDIUM),
    
    /**
     * Welcome notification for new users
     */
    WELCOME("Welcome to Online Auction!", Priority.LOW);
    
    private final String defaultMessage;
    private final Priority priority;
    
    NotificationType(String defaultMessage, Priority priority) {
        this.defaultMessage = defaultMessage;
        this.priority = priority;
    }
    
    public String getDefaultMessage() {
        return defaultMessage;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public enum Priority {
        LOW, MEDIUM, HIGH
    }
}



