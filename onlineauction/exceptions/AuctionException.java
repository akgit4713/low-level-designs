package onlineauction.exceptions;

/**
 * Base exception for auction-related errors.
 */
public class AuctionException extends RuntimeException {
    
    private final String auctionId;
    private final ErrorCode errorCode;
    
    public AuctionException(String message) {
        super(message);
        this.auctionId = null;
        this.errorCode = ErrorCode.GENERAL_ERROR;
    }
    
    public AuctionException(String message, String auctionId) {
        super(message);
        this.auctionId = auctionId;
        this.errorCode = ErrorCode.GENERAL_ERROR;
    }
    
    public AuctionException(String message, String auctionId, ErrorCode errorCode) {
        super(message);
        this.auctionId = auctionId;
        this.errorCode = errorCode;
    }
    
    public AuctionException(String message, Throwable cause) {
        super(message, cause);
        this.auctionId = null;
        this.errorCode = ErrorCode.GENERAL_ERROR;
    }
    
    public String getAuctionId() {
        return auctionId;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public enum ErrorCode {
        GENERAL_ERROR,
        AUCTION_NOT_FOUND,
        AUCTION_NOT_ACTIVE,
        AUCTION_ALREADY_ENDED,
        AUCTION_CANCELLED,
        INVALID_DURATION,
        INVALID_STARTING_PRICE,
        SELLER_CANNOT_BID
    }
}



