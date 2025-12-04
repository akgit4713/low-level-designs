package digitalwallet.enums;

/**
 * Types of fund transfers in the digital wallet system.
 */
public enum TransferType {
    P2P("Peer-to-Peer Transfer", true),
    EXTERNAL_BANK("External Bank Transfer", true),
    SELF_TRANSFER("Self Transfer Between Currencies", false),
    MERCHANT_PAYMENT("Merchant Payment", true);

    private final String displayName;
    private final boolean requiresRecipient;

    TransferType(String displayName, boolean requiresRecipient) {
        this.displayName = displayName;
        this.requiresRecipient = requiresRecipient;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean requiresRecipient() {
        return requiresRecipient;
    }
}



