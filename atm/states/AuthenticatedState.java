package atm.states;

import atm.ATM;
import atm.enums.ATMStateType;
import atm.enums.TransactionType;
import atm.models.Account;

/**
 * State after successful PIN verification - ready for transaction selection.
 */
public class AuthenticatedState extends ATMState {

    public AuthenticatedState(ATM atm) {
        super(atm);
    }

    @Override
    public ATMStateType getStateType() {
        return ATMStateType.AUTHENTICATED;
    }

    @Override
    public void selectTransaction(TransactionType type) {
        // Load account information
        Account account = atm.getBankService().getAccount(atm.getCurrentCard().getCardNumber());
        atm.setCurrentAccount(account);
        atm.setSelectedTransactionType(type);
        
        atm.setState(new TransactionState(atm, type));
        System.out.println("✓ Selected: " + type.getDisplayName());
    }

    @Override
    public void cancel() {
        System.out.println("Session ended. Ejecting card...");
        atm.ejectCard();
        atm.setState(new IdleState(atm));
    }

    @Override
    public void ejectCard() {
        System.out.println("Thank you for using our ATM. Ejecting card...");
        atm.ejectCard();
        atm.setState(new IdleState(atm));
    }

    @Override
    public String getDisplayMessage() {
        return "Please select a transaction:\n" +
               "1. Balance Inquiry\n" +
               "2. Cash Withdrawal\n" +
               "3. Cash Deposit\n" +
               "4. Mini Statement";
    }
}



