package atm.states;

import atm.ATM;
import atm.enums.ATMStateType;
import atm.enums.TransactionType;
import atm.exceptions.ATMException;
import atm.exceptions.TransactionException;
import atm.models.Receipt;
import atm.models.Transaction;
import atm.strategies.TransactionStrategy;

import java.math.BigDecimal;

/**
 * State for handling specific transaction operations.
 */
public class TransactionState extends ATMState {
    
    private final TransactionType transactionType;
    private BigDecimal amount;
    private boolean amountEntered = false;

    public TransactionState(ATM atm, TransactionType transactionType) {
        super(atm);
        this.transactionType = transactionType;
    }

    @Override
    public ATMStateType getStateType() {
        return ATMStateType.TRANSACTION_SELECTED;
    }

    @Override
    public void enterAmount(BigDecimal amount) {
        if (transactionType == TransactionType.BALANCE_INQUIRY ||
            transactionType == TransactionType.MINI_STATEMENT) {
            throw new ATMException("Amount not required for " + transactionType.getDisplayName());
        }

        validateAmount(amount);
        this.amount = amount;
        this.amountEntered = true;
        System.out.println("✓ Amount entered: ₹" + amount);
        System.out.println("Press confirm to proceed or cancel to abort.");
    }

    @Override
    public void acceptDeposit(BigDecimal amount) {
        if (transactionType != TransactionType.DEPOSIT) {
            throw new ATMException("Deposit not allowed in current transaction");
        }
        validateAmount(amount);
        this.amount = amount;
        this.amountEntered = true;
        System.out.println("✓ Cash deposited: ₹" + amount);
    }

    @Override
    public void confirmTransaction() {
        if (transactionType == TransactionType.WITHDRAWAL && !amountEntered) {
            throw new ATMException("Please enter withdrawal amount first");
        }
        if (transactionType == TransactionType.DEPOSIT && !amountEntered) {
            throw new ATMException("Please deposit cash first");
        }

        try {
            atm.setState(new ProcessingState(atm));
            
            TransactionStrategy strategy = atm.getTransactionStrategy(transactionType);
            Transaction transaction = strategy.execute(
                atm.getCurrentAccount(),
                atm.getCurrentCard(),
                amount,
                atm
            );
            
            // Generate and print receipt
            Receipt receipt = atm.generateReceipt(transaction);
            System.out.println(receipt.print());
            
            // Return to authenticated state for another transaction
            System.out.println("Would you like to perform another transaction?");
            atm.setState(new AuthenticatedState(atm));
            
        } catch (TransactionException e) {
            System.out.println("✗ Transaction failed: " + e.getMessage());
            atm.setState(new AuthenticatedState(atm));
            throw e;
        }
    }

    @Override
    public void cancel() {
        System.out.println("Transaction cancelled. Returning to main menu...");
        atm.setState(new AuthenticatedState(atm));
    }

    @Override
    public void ejectCard() {
        System.out.println("Cancelling transaction and ejecting card...");
        atm.ejectCard();
        atm.setState(new IdleState(atm));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ATMException("Invalid amount. Amount must be greater than zero.");
        }
        if (transactionType == TransactionType.WITHDRAWAL) {
            if (amount.remainder(BigDecimal.valueOf(100)).compareTo(BigDecimal.ZERO) != 0) {
                throw new ATMException("Withdrawal amount must be a multiple of ₹100");
            }
            if (amount.compareTo(BigDecimal.valueOf(25000)) > 0) {
                throw new ATMException("Maximum withdrawal per transaction is ₹25,000");
            }
        }
    }

    @Override
    public String getDisplayMessage() {
        return switch (transactionType) {
            case BALANCE_INQUIRY -> "Press confirm to check balance.";
            case WITHDRAWAL -> amountEntered 
                ? "Amount: ₹" + amount + ". Press confirm or cancel."
                : "Enter withdrawal amount (multiple of ₹100):";
            case DEPOSIT -> amountEntered
                ? "Deposited: ₹" + amount + ". Press confirm or cancel."
                : "Please insert cash into the deposit slot.";
            case MINI_STATEMENT -> "Press confirm to view mini statement.";
            case PIN_CHANGE -> "Enter new 4-digit PIN:";
        };
    }
}



