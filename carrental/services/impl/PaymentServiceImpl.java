package carrental.services.impl;

import carrental.enums.PaymentMethod;
import carrental.enums.PaymentStatus;
import carrental.exceptions.PaymentException;
import carrental.exceptions.ReservationNotFoundException;
import carrental.models.Payment;
import carrental.models.Reservation;
import carrental.repositories.PaymentRepository;
import carrental.repositories.ReservationRepository;
import carrental.services.PaymentService;
import carrental.strategies.payment.CashPaymentStrategy;
import carrental.strategies.payment.CreditCardPaymentStrategy;
import carrental.strategies.payment.DebitCardPaymentStrategy;
import carrental.strategies.payment.PaymentStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of PaymentService.
 * Uses Strategy pattern for different payment methods.
 */
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final Map<PaymentMethod, PaymentStrategy> paymentStrategies;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.paymentStrategies = initializeStrategies();
    }

    private Map<PaymentMethod, PaymentStrategy> initializeStrategies() {
        Map<PaymentMethod, PaymentStrategy> strategies = new HashMap<>();
        strategies.put(PaymentMethod.CREDIT_CARD, new CreditCardPaymentStrategy());
        strategies.put(PaymentMethod.DEBIT_CARD, new DebitCardPaymentStrategy());
        strategies.put(PaymentMethod.CASH, new CashPaymentStrategy());
        return strategies;
    }

    /**
     * Allows registering custom payment strategies.
     */
    public void registerPaymentStrategy(PaymentMethod method, PaymentStrategy strategy) {
        paymentStrategies.put(method, strategy);
    }

    @Override
    public Payment processPayment(String reservationId, PaymentMethod paymentMethod) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        
        PaymentStrategy strategy = paymentStrategies.get(paymentMethod);
        if (strategy == null) {
            throw new PaymentException("Unsupported payment method: " + paymentMethod);
        }
        
        // Create payment record
        Payment payment = new Payment.Builder()
            .id(UUID.randomUUID().toString())
            .reservation(reservation)
            .amount(reservation.getTotalAmount())
            .paymentMethod(paymentMethod)
            .build();
        
        paymentRepository.save(payment);
        
        // Process payment using strategy
        boolean success = strategy.processPayment(payment);
        
        if (!success) {
            throw new PaymentException("Payment processing failed for " + paymentMethod.getDisplayName());
        }
        
        paymentRepository.save(payment);
        return payment;
    }

    @Override
    public boolean processRefund(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentException("Payment not found: " + paymentId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentException("Can only refund completed payments");
        }
        
        PaymentStrategy strategy = paymentStrategies.get(payment.getPaymentMethod());
        boolean success = strategy.processRefund(payment);
        
        if (success) {
            paymentRepository.save(payment);
        }
        
        return success;
    }

    @Override
    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentException("Payment not found: " + paymentId));
    }

    @Override
    public List<Payment> getPaymentsByReservation(String reservationId) {
        return paymentRepository.findByReservationId(reservationId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}



