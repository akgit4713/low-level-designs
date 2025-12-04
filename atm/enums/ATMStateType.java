package atm.enums;

/**
 * Represents the different states an ATM can be in.
 * Used with State Pattern for managing ATM behavior.
 */
public enum ATMStateType {
    IDLE("Idle - Ready for card insertion"),
    CARD_INSERTED("Card inserted - Awaiting PIN"),
    AUTHENTICATED("Authenticated - Select transaction"),
    TRANSACTION_SELECTED("Transaction selected - Enter details"),
    PROCESSING("Processing transaction"),
    DISPENSING("Dispensing cash"),
    OUT_OF_SERVICE("ATM out of service");

    private final String description;

    ATMStateType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



