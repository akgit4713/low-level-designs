package digitalwallet.repositories.impl;

import digitalwallet.enums.PaymentMethodType;
import digitalwallet.models.PaymentMethod;
import digitalwallet.repositories.PaymentMethodRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of PaymentMethodRepository.
 * Uses ConcurrentHashMap for thread-safety.
 */
public class InMemoryPaymentMethodRepository implements PaymentMethodRepository {
    
    private final ConcurrentHashMap<String, PaymentMethod> paymentMethods = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> userIndex = new ConcurrentHashMap<>();

    @Override
    public PaymentMethod save(PaymentMethod paymentMethod) {
        paymentMethods.put(paymentMethod.getId(), paymentMethod);
        userIndex.computeIfAbsent(paymentMethod.getUserId(), k -> 
            Collections.synchronizedList(new ArrayList<>())).add(paymentMethod.getId());
        return paymentMethod;
    }

    @Override
    public Optional<PaymentMethod> findById(String id) {
        return Optional.ofNullable(paymentMethods.get(id));
    }

    @Override
    public List<PaymentMethod> findAll() {
        return new ArrayList<>(paymentMethods.values());
    }

    @Override
    public boolean deleteById(String id) {
        PaymentMethod pm = paymentMethods.remove(id);
        if (pm != null) {
            List<String> userPms = userIndex.get(pm.getUserId());
            if (userPms != null) {
                userPms.remove(id);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(String id) {
        return paymentMethods.containsKey(id);
    }

    @Override
    public long count() {
        return paymentMethods.size();
    }

    @Override
    public List<PaymentMethod> findByUserId(String userId) {
        List<String> ids = userIndex.get(userId);
        if (ids == null) {
            return new ArrayList<>();
        }
        return ids.stream()
            .map(paymentMethods::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public List<PaymentMethod> findActiveByUserId(String userId) {
        return findByUserId(userId).stream()
            .filter(PaymentMethod::isActive)
            .collect(Collectors.toList());
    }

    @Override
    public List<PaymentMethod> findByUserIdAndType(String userId, PaymentMethodType type) {
        return findByUserId(userId).stream()
            .filter(pm -> pm.getType() == type)
            .collect(Collectors.toList());
    }

    @Override
    public long countByUserId(String userId) {
        List<String> ids = userIndex.get(userId);
        return ids != null ? ids.size() : 0;
    }
}



