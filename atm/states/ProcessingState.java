package atm.states;

import atm.ATM;
import atm.enums.ATMStateType;

/**
 * Transient state while transaction is being processed.
 * No user interaction allowed during this state.
 */
public class ProcessingState extends ATMState {

    public ProcessingState(ATM atm) {
        super(atm);
    }

    @Override
    public ATMStateType getStateType() {
        return ATMStateType.PROCESSING;
    }

    @Override
    public String getDisplayMessage() {
        return "Processing your transaction. Please wait...";
    }

    // All other operations throw InvalidStateException (inherited behavior)
}



