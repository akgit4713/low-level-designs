package digitalwallet.exceptions;

/**
 * Exception thrown when a fund transfer fails.
 */
public class TransferException extends WalletException {
    
    private final String transferId;
    private final String fromWalletId;
    private final String toWalletId;

    public TransferException(String message) {
        super(message, "TRANSFER_ERROR");
        this.transferId = null;
        this.fromWalletId = null;
        this.toWalletId = null;
    }

    public TransferException(String message, String transferId) {
        super(message, "TRANSFER_ERROR");
        this.transferId = transferId;
        this.fromWalletId = null;
        this.toWalletId = null;
    }

    public TransferException(String message, String fromWalletId, String toWalletId) {
        super(message, "TRANSFER_ERROR");
        this.transferId = null;
        this.fromWalletId = fromWalletId;
        this.toWalletId = toWalletId;
    }

    public TransferException(String message, String transferId, String fromWalletId, 
                            String toWalletId, Throwable cause) {
        super(message, "TRANSFER_ERROR", cause);
        this.transferId = transferId;
        this.fromWalletId = fromWalletId;
        this.toWalletId = toWalletId;
    }

    public String getTransferId() {
        return transferId;
    }

    public String getFromWalletId() {
        return fromWalletId;
    }

    public String getToWalletId() {
        return toWalletId;
    }
}



