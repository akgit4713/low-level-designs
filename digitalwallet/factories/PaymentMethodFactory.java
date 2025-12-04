package digitalwallet.factories;

import digitalwallet.enums.PaymentMethodType;
import digitalwallet.exceptions.InvalidPaymentMethodException;
import digitalwallet.models.BankAccount;
import digitalwallet.models.CreditCard;
import digitalwallet.models.PaymentMethod;
import java.util.UUID;

/**
 * Factory for creating payment methods.
 * Encapsulates validation and creation logic.
 */
public class PaymentMethodFactory {

    /**
     * Create a credit card payment method
     */
    public static CreditCard createCreditCard(String userId, String cardNumber, 
                                               String expiry, String cvv, 
                                               String cardholderName) {
        validateCardNumber(cardNumber);
        validateExpiry(expiry);
        validateCvv(cvv);
        
        String encryptedToken = generateToken(cardNumber, cvv);
        
        return CreditCard.builder()
            .id(UUID.randomUUID().toString())
            .userId(userId)
            .type(PaymentMethodType.CREDIT_CARD)
            .cardNumber(cardNumber)
            .cardholderName(cardholderName)
            .expiry(expiry)
            .encryptedToken(encryptedToken)
            .build();
    }

    /**
     * Create a debit card payment method
     */
    public static CreditCard createDebitCard(String userId, String cardNumber,
                                              String expiry, String cvv,
                                              String cardholderName) {
        validateCardNumber(cardNumber);
        validateExpiry(expiry);
        validateCvv(cvv);
        
        String encryptedToken = generateToken(cardNumber, cvv);
        
        return CreditCard.builder()
            .id(UUID.randomUUID().toString())
            .userId(userId)
            .type(PaymentMethodType.DEBIT_CARD)
            .cardNumber(cardNumber)
            .cardholderName(cardholderName)
            .expiry(expiry)
            .encryptedToken(encryptedToken)
            .build();
    }

    /**
     * Create a bank account payment method
     */
    public static BankAccount createBankAccount(String userId, String bankName,
                                                 String routingNumber, String accountNumber,
                                                 String accountType, String accountHolderName) {
        validateRoutingNumber(routingNumber);
        validateAccountNumber(accountNumber);
        
        String encryptedAccount = generateToken(accountNumber, routingNumber);
        
        return BankAccount.builder()
            .id(UUID.randomUUID().toString())
            .userId(userId)
            .bankName(bankName)
            .routingNumber(routingNumber)
            .accountNumber(accountNumber)
            .accountType(accountType)
            .accountHolderName(accountHolderName)
            .encryptedAccountNumber(encryptedAccount)
            .build();
    }

    // Validation methods
    private static void validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new InvalidPaymentMethodException("Card number is required");
        }
        
        String cleaned = cardNumber.replaceAll("\\s+", "");
        if (cleaned.length() < 13 || cleaned.length() > 19) {
            throw new InvalidPaymentMethodException("Invalid card number length");
        }
        
        if (!cleaned.matches("\\d+")) {
            throw new InvalidPaymentMethodException("Card number must contain only digits");
        }
        
        if (!isValidLuhn(cleaned)) {
            throw new InvalidPaymentMethodException("Invalid card number (failed Luhn check)");
        }
    }

    private static void validateExpiry(String expiry) {
        if (expiry == null || !expiry.matches("\\d{2}/\\d{2}")) {
            throw new InvalidPaymentMethodException("Expiry must be in MM/YY format");
        }
        
        String[] parts = expiry.split("/");
        int month = Integer.parseInt(parts[0]);
        if (month < 1 || month > 12) {
            throw new InvalidPaymentMethodException("Invalid expiry month");
        }
    }

    private static void validateCvv(String cvv) {
        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            throw new InvalidPaymentMethodException("CVV must be 3 or 4 digits");
        }
    }

    private static void validateRoutingNumber(String routingNumber) {
        if (routingNumber == null || routingNumber.length() != 9) {
            throw new InvalidPaymentMethodException("Routing number must be 9 digits");
        }
        if (!routingNumber.matches("\\d+")) {
            throw new InvalidPaymentMethodException("Routing number must contain only digits");
        }
    }

    private static void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4 || accountNumber.length() > 17) {
            throw new InvalidPaymentMethodException("Account number must be 4-17 digits");
        }
        if (!accountNumber.matches("\\d+")) {
            throw new InvalidPaymentMethodException("Account number must contain only digits");
        }
    }

    /**
     * Validate card number using Luhn algorithm
     */
    private static boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(cardNumber.charAt(i));
            
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            
            sum += n;
            alternate = !alternate;
        }
        
        return (sum % 10 == 0);
    }

    /**
     * Generate an encrypted token (simulated)
     * In production, use a proper tokenization service
     */
    private static String generateToken(String... parts) {
        return java.util.Base64.getEncoder().encodeToString(
            String.join("|", parts).getBytes()
        );
    }
}



