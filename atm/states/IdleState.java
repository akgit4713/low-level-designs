package atm.states;

import atm.ATM;
import atm.enums.ATMStateType;
import atm.exceptions.ATMException;
import atm.models.Card;

/**
 * Initial state - ATM is ready and waiting for card insertion.
 */
public class IdleState extends ATMState {

    public IdleState(ATM atm) {
        super(atm);
    }

    @Override
    public ATMStateType getStateType() {
        return ATMStateType.IDLE;
    }

    @Override
    public void insertCard(Card card) {
        if (card == null) {
            throw new ATMException("Invalid card");
        }
        if (card.isBlocked()) {
            throw new ATMException("This card is blocked. Please contact your bank.");
        }
        if (card.isExpired()) {
            throw new ATMException("This card has expired. Please use a valid card.");
        }
        
        atm.setCurrentCard(card);
        atm.setState(new CardInsertedState(atm));
        System.out.println("✓ Card accepted. Please enter your PIN.");
    }

    @Override
    public String getDisplayMessage() {
        return "Welcome! Please insert your card.";
    }
}



