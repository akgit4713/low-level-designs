package digitalwallet.services.impl;

import digitalwallet.enums.Currency;
import digitalwallet.enums.TransactionStatus;
import digitalwallet.enums.TransactionType;
import digitalwallet.enums.TransferType;
import digitalwallet.exceptions.TransferException;
import digitalwallet.exceptions.WalletException;
import digitalwallet.models.Transaction;
import digitalwallet.models.Transfer;
import digitalwallet.models.Wallet;
import digitalwallet.observers.TransferObserver;
import digitalwallet.repositories.TransferRepository;
import digitalwallet.repositories.WalletRepository;
import digitalwallet.services.CurrencyService;
import digitalwallet.services.TransactionService;
import digitalwallet.services.TransferService;
import digitalwallet.services.WalletService;
import digitalwallet.strategies.fee.FeeCalculationStrategy;
import digitalwallet.strategies.fraud.FraudDetectionStrategy;
import digitalwallet.strategies.fraud.FraudDetectionStrategy.FraudCheckResult;
import digitalwallet.strategies.validation.CompositeValidationStrategy;
import digitalwallet.strategies.validation.TransferValidationStrategy.ValidationResult;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of TransferService.
 * Handles P2P and external transfers with validation and fraud detection.
 */
public class TransferServiceImpl implements TransferService {
    
    private final TransferRepository transferRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final CurrencyService currencyService;
    private final CompositeValidationStrategy validationStrategy;
    private final FeeCalculationStrategy feeStrategy;
    private final FraudDetectionStrategy fraudDetectionStrategy;
    private final List<TransferObserver> observers;
    private final Map<String, Lock> walletLocks;

    public TransferServiceImpl(TransferRepository transferRepository,
                                WalletRepository walletRepository,
                                WalletService walletService,
                                TransactionService transactionService,
                                CurrencyService currencyService,
                                CompositeValidationStrategy validationStrategy,
                                FeeCalculationStrategy feeStrategy,
                                FraudDetectionStrategy fraudDetectionStrategy) {
        this.transferRepository = Objects.requireNonNull(transferRepository);
        this.walletRepository = Objects.requireNonNull(walletRepository);
        this.walletService = Objects.requireNonNull(walletService);
        this.transactionService = Objects.requireNonNull(transactionService);
        this.currencyService = Objects.requireNonNull(currencyService);
        this.validationStrategy = validationStrategy;
        this.feeStrategy = feeStrategy;
        this.fraudDetectionStrategy = fraudDetectionStrategy;
        this.observers = new ArrayList<>();
        this.walletLocks = new HashMap<>();
    }

    public void addObserver(TransferObserver observer) {
        observers.add(observer);
    }

    @Override
    public Transfer transfer(String fromWalletId, String toWalletId, BigDecimal amount,
                              Currency currency, String description) {
        return transferWithConversion(fromWalletId, toWalletId, amount, currency, currency, description);
    }

    @Override
    public Transfer transferWithConversion(String fromWalletId, String toWalletId,
                                            BigDecimal amount, Currency fromCurrency,
                                            Currency toCurrency, String description) {
        // Get wallets
        Wallet sourceWallet = walletRepository.findById(fromWalletId)
            .orElseThrow(() -> new TransferException("Source wallet not found"));
        Wallet targetWallet = walletRepository.findById(toWalletId)
            .orElseThrow(() -> new TransferException("Target wallet not found"));

        // Calculate conversion if needed
        BigDecimal convertedAmount = amount;
        if (fromCurrency != toCurrency) {
            convertedAmount = currencyService.convert(amount, fromCurrency, toCurrency);
        }

        // Build transfer
        Transfer transfer = Transfer.builder()
            .id(UUID.randomUUID().toString())
            .fromWalletId(fromWalletId)
            .toWalletId(toWalletId)
            .type(TransferType.P2P)
            .amount(amount)
            .sourceCurrency(fromCurrency)
            .targetCurrency(toCurrency)
            .convertedAmount(convertedAmount)
            .fee(feeStrategy.calculateFee(amount, false, fromCurrency != toCurrency))
            .description(description)
            .idempotencyKey(UUID.randomUUID().toString())
            .build();

        // Validate
        ValidationResult validation = validationStrategy.validate(transfer, sourceWallet, targetWallet);
        if (!validation.isValid()) {
            throw new TransferException(validation.getMessage());
        }

        // Fraud check
        if (fraudDetectionStrategy != null) {
            FraudCheckResult fraudResult = fraudDetectionStrategy.check(transfer);
            if (fraudResult.isBlocked()) {
                throw new TransferException("Transfer blocked: " + fraudResult.getReason());
            }
            if (fraudResult.needsReview()) {
                transfer = saveTransfer(transfer);
                notifyTransferNeedsReview(transfer, fraudResult.getReason());
                return transfer;
            }
        }

        // Execute transfer with locking
        return executeTransfer(transfer, sourceWallet, targetWallet);
    }

    private Transfer executeTransfer(Transfer transfer, Wallet sourceWallet, Wallet targetWallet) {
        // Order locks by wallet ID to prevent deadlock
        String first = sourceWallet.getId().compareTo(targetWallet.getId()) < 0 ? 
            sourceWallet.getId() : targetWallet.getId();
        String second = sourceWallet.getId().compareTo(targetWallet.getId()) < 0 ? 
            targetWallet.getId() : sourceWallet.getId();

        Lock firstLock = getWalletLock(first);
        Lock secondLock = getWalletLock(second);

        firstLock.lock();
        try {
            secondLock.lock();
            try {
                transfer.markProcessing();
                saveTransfer(transfer);
                notifyTransferInitiated(transfer);

                // Debit source
                walletService.debit(sourceWallet.getId(), 
                    transfer.getTotalDebitAmount(), transfer.getSourceCurrency());
                
                Transaction debitTx = transactionService.createTransaction(
                    sourceWallet.getId(),
                    TransactionType.TRANSFER_OUT,
                    transfer.getAmount(),
                    transfer.getSourceCurrency(),
                    transfer.getDescription(),
                    transfer.getId()
                );
                debitTx.markCompleted();

                // Credit target
                walletService.credit(targetWallet.getId(), 
                    transfer.getConvertedAmount(), transfer.getTargetCurrency());
                
                Transaction creditTx = transactionService.createTransaction(
                    targetWallet.getId(),
                    TransactionType.TRANSFER_IN,
                    transfer.getConvertedAmount(),
                    transfer.getTargetCurrency(),
                    "From: " + transfer.getDescription(),
                    transfer.getId()
                );
                creditTx.markCompleted();

                // Record fee if applicable
                if (transfer.getFee().compareTo(BigDecimal.ZERO) > 0) {
                    transactionService.createTransaction(
                        sourceWallet.getId(),
                        TransactionType.FEE,
                        transfer.getFee(),
                        transfer.getSourceCurrency(),
                        "Transfer fee",
                        transfer.getId()
                    ).markCompleted();
                }

                // Complete transfer
                transfer.markCompleted(debitTx.getId(), creditTx.getId());
                saveTransfer(transfer);
                notifyTransferCompleted(transfer);

                return transfer;

            } finally {
                secondLock.unlock();
            }
        } catch (Exception e) {
            transfer.markFailed(e.getMessage());
            saveTransfer(transfer);
            notifyTransferFailed(transfer, e.getMessage());
            throw e;
        } finally {
            firstLock.unlock();
        }
    }

    @Override
    public Transfer transferToExternal(String fromWalletId, String paymentMethodId,
                                        BigDecimal amount, Currency currency, String description) {
        Wallet sourceWallet = walletRepository.findById(fromWalletId)
            .orElseThrow(() -> new TransferException("Source wallet not found"));

        Transfer transfer = Transfer.builder()
            .id(UUID.randomUUID().toString())
            .fromWalletId(fromWalletId)
            .externalAccountId(paymentMethodId)
            .type(TransferType.EXTERNAL_BANK)
            .amount(amount)
            .sourceCurrency(currency)
            .targetCurrency(currency)
            .convertedAmount(amount)
            .fee(feeStrategy.calculateFee(amount, true, false))
            .description(description)
            .idempotencyKey(UUID.randomUUID().toString())
            .build();

        // Validate
        ValidationResult validation = validationStrategy.validate(transfer, sourceWallet, null);
        if (!validation.isValid()) {
            throw new TransferException(validation.getMessage());
        }

        // Debit wallet
        transfer.markProcessing();
        saveTransfer(transfer);
        notifyTransferInitiated(transfer);

        try {
            walletService.debit(fromWalletId, transfer.getTotalDebitAmount(), currency);

            Transaction tx = transactionService.createTransaction(
                fromWalletId,
                TransactionType.WITHDRAWAL,
                amount,
                currency,
                description,
                transfer.getId()
            );
            tx.markCompleted();

            // In real system, would initiate bank transfer here
            transfer.markCompleted(tx.getId(), null);
            saveTransfer(transfer);
            notifyTransferCompleted(transfer);

            return transfer;

        } catch (Exception e) {
            transfer.markFailed(e.getMessage());
            saveTransfer(transfer);
            notifyTransferFailed(transfer, e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<Transfer> getTransfer(String transferId) {
        return transferRepository.findById(transferId);
    }

    @Override
    public List<Transfer> getTransfers(String walletId) {
        return transferRepository.findByWalletId(walletId);
    }

    @Override
    public List<Transfer> getOutgoingTransfers(String walletId) {
        return transferRepository.findByFromWalletId(walletId);
    }

    @Override
    public List<Transfer> getIncomingTransfers(String walletId) {
        return transferRepository.findByToWalletId(walletId);
    }

    @Override
    public void cancelTransfer(String transferId) {
        Transfer transfer = transferRepository.findById(transferId)
            .orElseThrow(() -> new TransferException("Transfer not found"));

        if (transfer.getStatus() != TransactionStatus.PENDING) {
            throw new TransferException("Can only cancel pending transfers");
        }

        transfer.markCancelled();
        saveTransfer(transfer);
    }

    @Override
    public Optional<Transfer> findByIdempotencyKey(String idempotencyKey) {
        return transferRepository.findByIdempotencyKey(idempotencyKey);
    }

    @Override
    public List<Transfer> getPendingTransfers() {
        return transferRepository.findByStatus(TransactionStatus.PENDING);
    }

    private Transfer saveTransfer(Transfer transfer) {
        return transferRepository.save(transfer);
    }

    private synchronized Lock getWalletLock(String walletId) {
        return walletLocks.computeIfAbsent(walletId, k -> new ReentrantLock());
    }

    // Observer notifications
    private void notifyTransferInitiated(Transfer transfer) {
        for (TransferObserver observer : observers) {
            observer.onTransferInitiated(transfer);
        }
    }

    private void notifyTransferCompleted(Transfer transfer) {
        for (TransferObserver observer : observers) {
            observer.onTransferCompleted(transfer);
        }
    }

    private void notifyTransferFailed(Transfer transfer, String reason) {
        for (TransferObserver observer : observers) {
            observer.onTransferFailed(transfer, reason);
        }
    }

    private void notifyTransferNeedsReview(Transfer transfer, String reason) {
        for (TransferObserver observer : observers) {
            observer.onTransferNeedsReview(transfer, reason);
        }
    }
}



