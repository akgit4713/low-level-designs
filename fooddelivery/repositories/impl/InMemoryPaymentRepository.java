package fooddelivery.repositories.impl;

import fooddelivery.enums.PaymentStatus;
import fooddelivery.models.Payment;
import fooddelivery.repositories.PaymentRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of PaymentRepository.
 */
public class InMemoryPaymentRepository implements PaymentRepository {
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    private final Map<String, String> orderIdIndex = new ConcurrentHashMap<>();

    @Override
    public Payment save(Payment payment) {
        payments.put(payment.getId(), payment);
        orderIdIndex.put(payment.getOrderId(), payment.getId());
        return payment;
    }

    @Override
    public Optional<Payment> findById(String id) {
        return Optional.ofNullable(payments.get(id));
    }

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        String paymentId = orderIdIndex.get(orderId);
        return paymentId != null ? findById(paymentId) : Optional.empty();
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return payments.values().stream()
                .filter(p -> p.getStatus() == status)
                .toList();
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }

    @Override
    public void delete(String id) {
        Payment payment = payments.remove(id);
        if (payment != null) {
            orderIdIndex.remove(payment.getOrderId());
        }
    }

    @Override
    public boolean existsById(String id) {
        return payments.containsKey(id);
    }
}



