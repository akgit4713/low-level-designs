package bookmyshow.repositories.impl;

import bookmyshow.models.Payment;
import bookmyshow.repositories.PaymentRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of PaymentRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryPaymentRepository implements PaymentRepository {
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    private final Map<String, String> bookingIndex = new ConcurrentHashMap<>();  // bookingId -> paymentId

    @Override
    public void save(Payment payment) {
        payments.put(payment.getId(), payment);
        bookingIndex.put(payment.getBookingId(), payment.getId());
    }

    @Override
    public Optional<Payment> findById(String id) {
        return Optional.ofNullable(payments.get(id));
    }

    @Override
    public Optional<Payment> findByBookingId(String bookingId) {
        String paymentId = bookingIndex.get(bookingId);
        return paymentId != null ? findById(paymentId) : Optional.empty();
    }

    @Override
    public List<Payment> findAll() {
        return payments.values().stream().toList();
    }

    @Override
    public void delete(String id) {
        Payment payment = payments.remove(id);
        if (payment != null) {
            bookingIndex.remove(payment.getBookingId());
        }
    }

    @Override
    public boolean exists(String id) {
        return payments.containsKey(id);
    }
}



