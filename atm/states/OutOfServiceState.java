package atm.states;

import atm.ATM;
import atm.enums.ATMStateType;
import atm.exceptions.ATMException;
import atm.models.Card;

/**
 * State when ATM is out of service.
 */
public class OutOfServiceState extends ATMState {

    private final String reason;

    public OutOfServiceState(ATM atm, String reason) {
        super(atm);
        this.reason = reason;
    }

    @Override
    public ATMStateType getStateType() {
        return ATMStateType.OUT_OF_SERVICE;
    }

    @Override
    public void insertCard(Card card) {
        throw new ATMException("ATM is currently out of service: " + reason);
    }

    @Override
    public String getDisplayMessage() {
        return "ATM OUT OF SERVICE\n" + reason + "\nWe apologize for the inconvenience.";
    }
}



