package digitalwallet.services.impl;

import digitalwallet.enums.PaymentMethodType;
import digitalwallet.exceptions.InvalidPaymentMethodException;
import digitalwallet.exceptions.WalletException;
import digitalwallet.models.BankAccount;
import digitalwallet.models.CreditCard;
import digitalwallet.models.PaymentMethod;
import digitalwallet.repositories.PaymentMethodRepository;
import digitalwallet.services.PaymentMethodService;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of PaymentMethodService.
 */
public class PaymentMethodServiceImpl implements PaymentMethodService {
    
    private final PaymentMethodRepository paymentMethodRepository;
    private static final int MAX_PAYMENT_METHODS = 10;

    public PaymentMethodServiceImpl(PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = Objects.requireNonNull(paymentMethodRepository);
    }

    @Override
    public CreditCard addCreditCard(String userId, String cardNumber, String expiry,
                                     String cvv, String cardholderName) {
        validateCardNumber(cardNumber);
        validateMaxPaymentMethods(userId);

        // Generate token (in production, use a tokenization service)
        String encryptedToken = generateToken(cardNumber, cvv);

        CreditCard card = CreditCard.builder()
            .id(UUID.randomUUID().toString())
            .userId(userId)
            .cardNumber(cardNumber) // This stores only last 4 digits
            .cardholderName(cardholderName)
            .expiry(expiry)
            .encryptedToken(encryptedToken)
            .build();

        return (CreditCard) paymentMethodRepository.save(card);
    }

    @Override
    public BankAccount addBankAccount(String userId, String bankName, String routingNumber,
                                       String accountNumber, String accountType, 
                                       String accountHolderName) {
        validateRoutingNumber(routingNumber);
        validateAccountNumber(accountNumber);
        validateMaxPaymentMethods(userId);

        String encryptedAccount = generateToken(accountNumber, routingNumber);

        BankAccount account = BankAccount.builder()
            .id(UUID.randomUUID().toString())
            .userId(userId)
            .bankName(bankName)
            .routingNumber(routingNumber)
            .accountNumber(accountNumber)
            .accountType(accountType)
            .accountHolderName(accountHolderName)
            .encryptedAccountNumber(encryptedAccount)
            .build();

        return (BankAccount) paymentMethodRepository.save(account);
    }

    @Override
    public Optional<PaymentMethod> getPaymentMethod(String paymentMethodId) {
        return paymentMethodRepository.findById(paymentMethodId);
    }

    @Override
    public List<PaymentMethod> getPaymentMethods(String userId) {
        return paymentMethodRepository.findByUserId(userId);
    }

    @Override
    public List<PaymentMethod> getActivePaymentMethods(String userId) {
        return paymentMethodRepository.findActiveByUserId(userId);
    }

    @Override
    public List<CreditCard> getCreditCards(String userId) {
        return paymentMethodRepository.findByUserIdAndType(userId, PaymentMethodType.CREDIT_CARD)
            .stream()
            .filter(pm -> pm instanceof CreditCard)
            .map(pm -> (CreditCard) pm)
            .collect(Collectors.toList());
    }

    @Override
    public List<BankAccount> getBankAccounts(String userId) {
        return paymentMethodRepository.findByUserIdAndType(userId, PaymentMethodType.BANK_ACCOUNT)
            .stream()
            .filter(pm -> pm instanceof BankAccount)
            .map(pm -> (BankAccount) pm)
            .collect(Collectors.toList());
    }

    @Override
    public void removePaymentMethod(String paymentMethodId) {
        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)
            .orElseThrow(() -> new InvalidPaymentMethodException("Payment method not found"));
        
        pm.deactivate();
        paymentMethodRepository.save(pm);
    }

    @Override
    public void verifyPaymentMethod(String paymentMethodId) {
        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)
            .orElseThrow(() -> new InvalidPaymentMethodException("Payment method not found"));
        
        // In production, this would involve actual verification
        pm.markVerified();
        paymentMethodRepository.save(pm);
    }

    @Override
    public void setNickname(String paymentMethodId, String nickname) {
        PaymentMethod pm = paymentMethodRepository.findById(paymentMethodId)
            .orElseThrow(() -> new InvalidPaymentMethodException("Payment method not found"));
        
        pm.setNickname(nickname);
        paymentMethodRepository.save(pm);
    }

    private void validateCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 13 || cardNumber.length() > 19) {
            throw new InvalidPaymentMethodException("Invalid card number length");
        }
        // Luhn algorithm check
        if (!isValidLuhn(cardNumber)) {
            throw new InvalidPaymentMethodException("Invalid card number");
        }
    }

    private boolean isValidLuhn(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));
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

    private void validateRoutingNumber(String routingNumber) {
        if (routingNumber == null || routingNumber.length() != 9) {
            throw new InvalidPaymentMethodException("Invalid routing number");
        }
    }

    private void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4 || accountNumber.length() > 17) {
            throw new InvalidPaymentMethodException("Invalid account number");
        }
    }

    private void validateMaxPaymentMethods(String userId) {
        if (paymentMethodRepository.countByUserId(userId) >= MAX_PAYMENT_METHODS) {
            throw new WalletException("Maximum number of payment methods reached");
        }
    }

    private String generateToken(String... parts) {
        // In production, use a proper tokenization service
        return Base64.getEncoder().encodeToString(
            String.join("|", parts).getBytes()
        );
    }
}



