package onlineauction.enums;

/**
 * Represents the lifecycle status of an auction listing.
 */
public enum AuctionStatus {
    /**
     * Auction is created but not yet published
     */
    DRAFT("Draft - Not yet active"),
    
    /**
     * Auction is live and accepting bids
     */
    ACTIVE("Active - Accepting bids"),
    
    /**
     * Auction has ended (time expired)
     */
    ENDED("Ended - Bidding closed"),
    
    /**
     * Auction was cancelled by the seller
     */
    CANCELLED("Cancelled by seller"),
    
    /**
     * Auction ended with a successful sale
     */
    SOLD("Sold to highest bidder");
    
    private final String description;
    
    AuctionStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if the auction status allows new bids
     */
    public boolean allowsBidding() {
        return this == ACTIVE;
    }
    
    /**
     * Check if the auction can be cancelled
     */
    public boolean canBeCancelled() {
        return this == DRAFT || this == ACTIVE;
    }
}



