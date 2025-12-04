package digitalwallet.services.impl;

import digitalwallet.enums.Currency;
import digitalwallet.exceptions.WalletException;
import digitalwallet.models.Wallet;
import digitalwallet.models.WalletBalance;
import digitalwallet.observers.WalletObserver;
import digitalwallet.repositories.WalletRepository;
import digitalwallet.services.WalletService;
import java.math.BigDecimal;
import java.util.*;

/**
 * Implementation of WalletService.
 */
public class WalletServiceImpl implements WalletService {
    
    private final WalletRepository walletRepository;
    private final List<WalletObserver> observers;
    private final BigDecimal lowBalanceThreshold;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = Objects.requireNonNull(walletRepository);
        this.observers = new ArrayList<>();
        this.lowBalanceThreshold = new BigDecimal("100");
    }

    public void addObserver(WalletObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(WalletObserver observer) {
        observers.remove(observer);
    }

    @Override
    public Wallet createWallet(String userId, Currency defaultCurrency) {
        // Check if wallet already exists
        if (walletRepository.existsByUserId(userId)) {
            throw new WalletException("Wallet already exists for user: " + userId);
        }

        Wallet wallet = new Wallet(
            UUID.randomUUID().toString(),
            userId,
            defaultCurrency
        );

        walletRepository.save(wallet);
        notifyWalletCreated(wallet);
        
        return wallet;
    }

    @Override
    public Optional<Wallet> getWallet(String walletId) {
        return walletRepository.findById(walletId);
    }

    @Override
    public Optional<Wallet> getWalletByUserId(String userId) {
        return walletRepository.findByUserId(userId);
    }

    @Override
    public BigDecimal getBalance(String walletId, Currency currency) {
        Wallet wallet = getWalletOrThrow(walletId);
        return wallet.getAvailableBalance(currency);
    }

    @Override
    public Map<Currency, WalletBalance> getAllBalances(String walletId) {
        Wallet wallet = getWalletOrThrow(walletId);
        return wallet.getAllBalances();
    }

    @Override
    public void credit(String walletId, BigDecimal amount, Currency currency) {
        Wallet wallet = getWalletOrThrow(walletId);
        BigDecimal oldBalance = wallet.getAvailableBalance(currency);
        
        wallet.credit(amount, currency);
        walletRepository.save(wallet);
        
        BigDecimal newBalance = wallet.getAvailableBalance(currency);
        notifyBalanceChanged(wallet, currency, oldBalance, newBalance);
    }

    @Override
    public void debit(String walletId, BigDecimal amount, Currency currency) {
        Wallet wallet = getWalletOrThrow(walletId);
        BigDecimal oldBalance = wallet.getAvailableBalance(currency);
        
        wallet.debit(amount, currency);
        walletRepository.save(wallet);
        
        BigDecimal newBalance = wallet.getAvailableBalance(currency);
        notifyBalanceChanged(wallet, currency, oldBalance, newBalance);
        
        // Check low balance
        if (newBalance.compareTo(lowBalanceThreshold) < 0) {
            notifyLowBalance(wallet, currency, newBalance);
        }
    }

    @Override
    public void setDailyTransferLimit(String walletId, BigDecimal limit) {
        Wallet wallet = getWalletOrThrow(walletId);
        wallet.setDailyTransferLimit(limit);
        walletRepository.save(wallet);
    }

    @Override
    public void setDailyWithdrawalLimit(String walletId, BigDecimal limit) {
        Wallet wallet = getWalletOrThrow(walletId);
        wallet.setDailyWithdrawalLimit(limit);
        walletRepository.save(wallet);
    }

    @Override
    public void deactivateWallet(String walletId) {
        Wallet wallet = getWalletOrThrow(walletId);
        wallet.deactivate();
        walletRepository.save(wallet);
        notifyWalletDeactivated(wallet);
    }

    @Override
    public void activateWallet(String walletId) {
        Wallet wallet = getWalletOrThrow(walletId);
        wallet.activate();
        walletRepository.save(wallet);
    }

    private Wallet getWalletOrThrow(String walletId) {
        return walletRepository.findById(walletId)
            .orElseThrow(() -> new WalletException("Wallet not found: " + walletId));
    }

    // Observer notifications
    private void notifyWalletCreated(Wallet wallet) {
        for (WalletObserver observer : observers) {
            observer.onWalletCreated(wallet);
        }
    }

    private void notifyBalanceChanged(Wallet wallet, Currency currency, 
                                       BigDecimal oldBalance, BigDecimal newBalance) {
        for (WalletObserver observer : observers) {
            observer.onBalanceChanged(wallet, currency, oldBalance, newBalance);
        }
    }

    private void notifyWalletDeactivated(Wallet wallet) {
        for (WalletObserver observer : observers) {
            observer.onWalletDeactivated(wallet);
        }
    }

    private void notifyLowBalance(Wallet wallet, Currency currency, BigDecimal balance) {
        for (WalletObserver observer : observers) {
            observer.onLowBalance(wallet, currency, balance);
        }
    }
}



