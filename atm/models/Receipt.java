package atm.models;

import atm.enums.Denomination;
import atm.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Represents a receipt printed after a transaction.
 */
public class Receipt {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    
    private final String atmId;
    private final String atmLocation;
    private final Transaction transaction;
    private final String maskedCardNumber;
    private final Map<Denomination, Integer> dispensedNotes;

    public Receipt(String atmId, String atmLocation, Transaction transaction, 
                   String maskedCardNumber, Map<Denomination, Integer> dispensedNotes) {
        this.atmId = atmId;
        this.atmLocation = atmLocation;
        this.transaction = transaction;
        this.maskedCardNumber = maskedCardNumber;
        this.dispensedNotes = dispensedNotes;
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        String divider = "════════════════════════════════════════";
        
        sb.append("\n").append(divider).append("\n");
        sb.append("           ATM TRANSACTION RECEIPT\n");
        sb.append(divider).append("\n");
        sb.append("ATM ID      : ").append(atmId).append("\n");
        sb.append("Location    : ").append(atmLocation).append("\n");
        sb.append("Date/Time   : ").append(transaction.getTimestamp().format(DATE_FORMAT)).append("\n");
        sb.append(divider).append("\n");
        sb.append("Card Number : ").append(maskedCardNumber).append("\n");
        sb.append("Account     : ").append(maskAccountNumber(transaction.getAccountNumber())).append("\n");
        sb.append(divider).append("\n");
        sb.append("Transaction : ").append(transaction.getType().getDisplayName()).append("\n");
        sb.append("TXN ID      : ").append(transaction.getTransactionId()).append("\n");
        sb.append("Status      : ").append(transaction.getStatus()).append("\n");
        
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            sb.append("Amount      : ₹").append(transaction.getAmount()).append("\n");
        }
        
        if (transaction.getType() == TransactionType.WITHDRAWAL && dispensedNotes != null && !dispensedNotes.isEmpty()) {
            sb.append(divider).append("\n");
            sb.append("Notes Dispensed:\n");
            for (Map.Entry<Denomination, Integer> entry : dispensedNotes.entrySet()) {
                if (entry.getValue() > 0) {
                    sb.append("  ₹").append(entry.getKey().getValue())
                      .append(" x ").append(entry.getValue())
                      .append(" = ₹").append(entry.getKey().getValue() * entry.getValue())
                      .append("\n");
                }
            }
        }
        
        if (transaction.getBalanceAfter() != null) {
            sb.append(divider).append("\n");
            sb.append("Balance     : ₹").append(transaction.getBalanceAfter()).append("\n");
        }
        
        sb.append(divider).append("\n");
        sb.append("     Thank you for using our ATM!\n");
        sb.append("    Please take your card and cash.\n");
        sb.append(divider).append("\n");
        
        return sb.toString();
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }
        return "XXXXXXXX" + accountNumber.substring(accountNumber.length() - 4);
    }

    public Transaction getTransaction() {
        return transaction;
    }
}



