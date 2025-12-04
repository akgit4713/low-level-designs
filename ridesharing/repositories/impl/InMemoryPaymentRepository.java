package ridesharing.repositories.impl;

import ridesharing.enums.PaymentStatus;
import ridesharing.models.Payment;
import ridesharing.repositories.PaymentRepository;

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
        payments.put(payment.getPaymentId(), payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(payments.get(paymentId));
    }

    @Override
    public Optional<Payment> findByRideId(String rideId) {
        return payments.values().stream()
                .filter(payment -> payment.getRideId().equals(rideId))
                .findFirst();
    }

    @Override
    public List<Payment> findByPassengerId(String passengerId) {
        return payments.values().stream()
                .filter(payment -> payment.getPassengerId().equals(passengerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return payments.values().stream()
                .filter(payment -> payment.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findAll() {
        return List.copyOf(payments.values());
    }
}



