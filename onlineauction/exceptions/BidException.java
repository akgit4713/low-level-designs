package onlineauction.exceptions;

import java.math.BigDecimal;

/**
 * Exception for bid-related errors.
 */
public class BidException extends RuntimeException {
    
    private final String auctionId;
    private final BigDecimal bidAmount;
    private final ErrorCode errorCode;
    
    public BidException(String message) {
        super(message);
        this.auctionId = null;
        this.bidAmount = null;
        this.errorCode = ErrorCode.GENERAL_ERROR;
    }
    
    public BidException(String message, String auctionId, BigDecimal bidAmount, ErrorCode errorCode) {
        super(message);
        this.auctionId = auctionId;
        this.bidAmount = bidAmount;
        this.errorCode = errorCode;
    }
    
    public BidException(String message, ErrorCode errorCode) {
        super(message);
        this.auctionId = null;
        this.bidAmount = null;
        this.errorCode = errorCode;
    }
    
    public String getAuctionId() {
        return auctionId;
    }
    
    public BigDecimal getBidAmount() {
        return bidAmount;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public enum ErrorCode {
        GENERAL_ERROR,
        BID_TOO_LOW,
        BID_BELOW_STARTING_PRICE,
        BID_INCREMENT_TOO_SMALL,
        AUCTION_NOT_ACTIVE,
        BIDDER_IS_SELLER,
        INVALID_BID_AMOUNT,
        BIDDER_ALREADY_HIGHEST
    }
}



