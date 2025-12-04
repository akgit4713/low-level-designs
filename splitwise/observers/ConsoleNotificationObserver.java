package splitwise.observers;

import splitwise.models.Expense;
import splitwise.models.Split;
import splitwise.models.Transaction;

/**
 * Observer that prints notifications to console.
 * Can be replaced with email, push, or other notification systems.
 */
public class ConsoleNotificationObserver implements ExpenseObserver, SettlementObserver {
    
    @Override
    public void onExpenseAdded(Expense expense) {
        System.out.println("\n📝 NEW EXPENSE ADDED");
        System.out.println("   Description: " + expense.getDescription());
        System.out.println("   Amount: $" + expense.getAmount());
        System.out.println("   Paid by: " + expense.getPayerId());
        System.out.println("   Split method: " + expense.getSplitMethod());
        System.out.println("   Participants:");
        for (Split split : expense.getSplits()) {
            System.out.println("     - " + split.getUserId() + ": $" + split.getAmount());
        }
    }
    
    @Override
    public void onExpenseDeleted(Expense expense) {
        System.out.println("\n🗑️ EXPENSE DELETED");
        System.out.println("   Description: " + expense.getDescription());
        System.out.println("   Amount: $" + expense.getAmount());
    }
    
    @Override
    public void onSettlement(Transaction settlement) {
        System.out.println("\n💰 SETTLEMENT RECORDED");
        System.out.println("   From: " + settlement.getFromUserId());
        System.out.println("   To: " + settlement.getToUserId());
        System.out.println("   Amount: $" + settlement.getAmount());
    }
}



