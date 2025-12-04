package digitalwallet.repositories.impl;

import digitalwallet.enums.TransactionStatus;
import digitalwallet.models.Transfer;
import digitalwallet.repositories.TransferRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * In-memory implementation of TransferRepository.
 * Uses ConcurrentHashMap for thread-safety.
 */
public class InMemoryTransferRepository implements TransferRepository {
    
    private final ConcurrentHashMap<String, Transfer> transfers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> fromWalletIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> toWalletIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> idempotencyIndex = new ConcurrentHashMap<>();

    @Override
    public Transfer save(Transfer transfer) {
        transfers.put(transfer.getId(), transfer);
        
        // Update from wallet index
        fromWalletIndex.computeIfAbsent(transfer.getFromWalletId(), k -> 
            Collections.synchronizedList(new ArrayList<>())).add(transfer.getId());
        
        // Update to wallet index
        if (transfer.getToWalletId() != null) {
            toWalletIndex.computeIfAbsent(transfer.getToWalletId(), k -> 
                Collections.synchronizedList(new ArrayList<>())).add(transfer.getId());
        }
        
        // Update idempotency index
        if (transfer.getIdempotencyKey() != null) {
            idempotencyIndex.put(transfer.getIdempotencyKey(), transfer.getId());
        }
        
        return transfer;
    }

    @Override
    public Optional<Transfer> findById(String id) {
        return Optional.ofNullable(transfers.get(id));
    }

    @Override
    public List<Transfer> findAll() {
        return new ArrayList<>(transfers.values());
    }

    @Override
    public boolean deleteById(String id) {
        Transfer transfer = transfers.remove(id);
        if (transfer != null) {
            List<String> fromList = fromWalletIndex.get(transfer.getFromWalletId());
            if (fromList != null) {
                fromList.remove(id);
            }
            if (transfer.getToWalletId() != null) {
                List<String> toList = toWalletIndex.get(transfer.getToWalletId());
                if (toList != null) {
                    toList.remove(id);
                }
            }
            if (transfer.getIdempotencyKey() != null) {
                idempotencyIndex.remove(transfer.getIdempotencyKey());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(String id) {
        return transfers.containsKey(id);
    }

    @Override
    public long count() {
        return transfers.size();
    }

    @Override
    public List<Transfer> findByFromWalletId(String walletId) {
        List<String> ids = fromWalletIndex.get(walletId);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
            .map(transfers::get)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(Transfer::getCreatedAt))
            .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findByToWalletId(String walletId) {
        List<String> ids = toWalletIndex.get(walletId);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
            .map(transfers::get)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(Transfer::getCreatedAt))
            .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findByWalletId(String walletId) {
        Set<String> allIds = new HashSet<>();
        
        List<String> fromIds = fromWalletIndex.get(walletId);
        if (fromIds != null) {
            allIds.addAll(fromIds);
        }
        
        List<String> toIds = toWalletIndex.get(walletId);
        if (toIds != null) {
            allIds.addAll(toIds);
        }
        
        return allIds.stream()
            .map(transfers::get)
            .filter(Objects::nonNull)
            .sorted(Comparator.comparing(Transfer::getCreatedAt))
            .collect(Collectors.toList());
    }

    @Override
    public Optional<Transfer> findByIdempotencyKey(String idempotencyKey) {
        String transferId = idempotencyIndex.get(idempotencyKey);
        return transferId != null ? findById(transferId) : Optional.empty();
    }

    @Override
    public List<Transfer> findByStatus(TransactionStatus status) {
        return transfers.values().stream()
            .filter(t -> t.getStatus() == status)
            .sorted(Comparator.comparing(Transfer::getCreatedAt))
            .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return transfers.values().stream()
            .filter(t -> !t.getCreatedAt().isBefore(start) && !t.getCreatedAt().isAfter(end))
            .sorted(Comparator.comparing(Transfer::getCreatedAt))
            .collect(Collectors.toList());
    }

    @Override
    public List<Transfer> findPendingOlderThan(LocalDateTime cutoff) {
        return transfers.values().stream()
            .filter(t -> t.getStatus() == TransactionStatus.PENDING || 
                        t.getStatus() == TransactionStatus.PROCESSING)
            .filter(t -> t.getCreatedAt().isBefore(cutoff))
            .sorted(Comparator.comparing(Transfer::getCreatedAt))
            .collect(Collectors.toList());
    }
}



