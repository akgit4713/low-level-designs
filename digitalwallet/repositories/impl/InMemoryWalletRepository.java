package digitalwallet.repositories.impl;

import digitalwallet.models.Wallet;
import digitalwallet.repositories.WalletRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of WalletRepository.
 * Uses ConcurrentHashMap for thread-safety.
 */
public class InMemoryWalletRepository implements WalletRepository {
    
    private final ConcurrentHashMap<String, Wallet> wallets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> userIdIndex = new ConcurrentHashMap<>();

    @Override
    public Wallet save(Wallet wallet) {
        wallets.put(wallet.getId(), wallet);
        userIdIndex.put(wallet.getUserId(), wallet.getId());
        return wallet;
    }

    @Override
    public Optional<Wallet> findById(String id) {
        return Optional.ofNullable(wallets.get(id));
    }

    @Override
    public List<Wallet> findAll() {
        return new ArrayList<>(wallets.values());
    }

    @Override
    public boolean deleteById(String id) {
        Wallet wallet = wallets.remove(id);
        if (wallet != null) {
            userIdIndex.remove(wallet.getUserId());
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(String id) {
        return wallets.containsKey(id);
    }

    @Override
    public long count() {
        return wallets.size();
    }

    @Override
    public Optional<Wallet> findByUserId(String userId) {
        String walletId = userIdIndex.get(userId);
        return walletId != null ? findById(walletId) : Optional.empty();
    }

    @Override
    public boolean existsByUserId(String userId) {
        return userIdIndex.containsKey(userId);
    }
}



