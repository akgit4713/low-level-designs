package atm.states;

import atm.ATM;
import atm.enums.ATMStateType;
import atm.enums.TransactionType;
import atm.exceptions.InvalidStateException;
import atm.models.Card;

import java.math.BigDecimal;

/**
 * Abstract base class for ATM states.
 * Implements the State Pattern for managing ATM behavior based on current state.
 */
public abstract class ATMState {
    
    protected final ATM atm;

    protected ATMState(ATM atm) {
        this.atm = atm;
    }

    /**
     * Get the type of this state.
     */
    public abstract ATMStateType getStateType();

    /**
     * Insert a card into the ATM.
     */
    public void insertCard(Card card) {
        throw new InvalidStateException(getStateType(), "insert card");
    }

    /**
     * Enter PIN for authentication.
     */
    public void enterPin(String pin) {
        throw new InvalidStateException(getStateType(), "enter PIN");
    }

    /**
     * Select a transaction type.
     */
    public void selectTransaction(TransactionType type) {
        throw new InvalidStateException(getStateType(), "select transaction");
    }

    /**
     * Enter withdrawal amount.
     */
    public void enterAmount(BigDecimal amount) {
        throw new InvalidStateException(getStateType(), "enter amount");
    }

    /**
     * Accept cash deposit.
     */
    public void acceptDeposit(BigDecimal amount) {
        throw new InvalidStateException(getStateType(), "accept deposit");
    }

    /**
     * Confirm the current transaction.
     */
    public void confirmTransaction() {
        throw new InvalidStateException(getStateType(), "confirm transaction");
    }

    /**
     * Cancel current operation and return to appropriate state.
     */
    public void cancel() {
        throw new InvalidStateException(getStateType(), "cancel");
    }

    /**
     * Eject the card.
     */
    public void ejectCard() {
        throw new InvalidStateException(getStateType(), "eject card");
    }

    /**
     * Get display message for current state.
     */
    public abstract String getDisplayMessage();
}



