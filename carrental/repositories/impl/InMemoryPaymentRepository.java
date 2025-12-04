package carrental.repositories.impl;

import carrental.enums.PaymentStatus;
import carrental.models.Payment;
import carrental.repositories.PaymentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of PaymentRepository.
 */
public class InMemoryPaymentRepository implements PaymentRepository {
    
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();

    @Override
    public Payment save(Payment payment) {
        payments.put(payment.getId(), payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(String id) {
        return Optional.ofNullable(payments.get(id));
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }

    @Override
    public boolean deleteById(String id) {
        return payments.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return payments.containsKey(id);
    }

    @Override
    public long count() {
        return payments.size();
    }

    @Override
    public List<Payment> findByReservationId(String reservationId) {
        return payments.values().stream()
            .filter(p -> p.getReservation().getId().equals(reservationId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return payments.values().stream()
            .filter(p -> p.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public Payment findByTransactionReference(String transactionReference) {
        return payments.values().stream()
            .filter(p -> transactionReference.equals(p.getTransactionReference()))
            .findFirst()
            .orElse(null);
    }
}



