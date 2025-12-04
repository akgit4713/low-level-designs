package atm.services.impl;

import atm.services.AuthenticationService;
import atm.services.BankService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of AuthenticationService.
 */
public class DefaultAuthenticationService implements AuthenticationService {
    
    private static final int MAX_ATTEMPTS = 3;
    private final BankService bankService;
    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();

    public DefaultAuthenticationService(BankService bankService) {
        this.bankService = bankService;
    }

    @Override
    public boolean validatePin(String cardNumber, String pin) {
        return bankService.validatePin(cardNumber, pin);
    }

    @Override
    public boolean isCardBlocked(String cardNumber) {
        var card = bankService.getCard(cardNumber);
        return card != null && card.isBlocked();
    }

    @Override
    public void blockCard(String cardNumber) {
        bankService.blockCard(cardNumber);
        failedAttempts.remove(cardNumber);
    }

    @Override
    public int recordFailedAttempt(String cardNumber) {
        int attempts = failedAttempts.merge(cardNumber, 1, Integer::sum);
        int remaining = MAX_ATTEMPTS - attempts;
        
        if (remaining <= 0) {
            blockCard(cardNumber);
        }
        
        return Math.max(0, remaining);
    }

    @Override
    public void resetFailedAttempts(String cardNumber) {
        failedAttempts.remove(cardNumber);
    }
}



