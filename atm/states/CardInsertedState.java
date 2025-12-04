package atm.states;

import atm.ATM;
import atm.enums.ATMStateType;
import atm.exceptions.AuthenticationException;
import atm.models.Card;

/**
 * State after card is inserted - waiting for PIN entry.
 */
public class CardInsertedState extends ATMState {
    
    private static final int MAX_PIN_ATTEMPTS = 3;

    public CardInsertedState(ATM atm) {
        super(atm);
    }

    @Override
    public ATMStateType getStateType() {
        return ATMStateType.CARD_INSERTED;
    }

    @Override
    public void enterPin(String pin) {
        Card card = atm.getCurrentCard();
        
        if (pin == null || pin.length() != 4 || !pin.matches("\\d{4}")) {
            throw new AuthenticationException("Invalid PIN format. PIN must be 4 digits.");
        }

        boolean isValid = atm.getAuthenticationService().validatePin(card.getCardNumber(), pin);
        
        if (isValid) {
            card.resetFailedAttempts();
            atm.setState(new AuthenticatedState(atm));
            System.out.println("✓ PIN verified. Please select a transaction.");
        } else {
            card.incrementFailedAttempts();
            int remaining = MAX_PIN_ATTEMPTS - card.getFailedAttempts();
            
            if (card.isBlocked()) {
                System.out.println("✗ Card blocked due to too many incorrect attempts.");
                atm.ejectCard();
                atm.setState(new IdleState(atm));
                throw new AuthenticationException("Card blocked due to too many incorrect PIN attempts.", 0);
            }
            
            throw new AuthenticationException(
                "Incorrect PIN. " + remaining + " attempt(s) remaining.",
                remaining
            );
        }
    }

    @Override
    public void cancel() {
        System.out.println("Transaction cancelled. Ejecting card...");
        atm.ejectCard();
        atm.setState(new IdleState(atm));
    }

    @Override
    public void ejectCard() {
        System.out.println("Ejecting card...");
        atm.ejectCard();
        atm.setState(new IdleState(atm));
    }

    @Override
    public String getDisplayMessage() {
        return "Please enter your 4-digit PIN.";
    }
}



