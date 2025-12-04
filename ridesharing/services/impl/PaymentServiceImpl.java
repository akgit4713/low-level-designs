package ridesharing.services.impl;

import ridesharing.enums.PaymentMethod;
import ridesharing.enums.PaymentStatus;
import ridesharing.exceptions.PaymentException;
import ridesharing.models.Payment;
import ridesharing.models.Ride;
import ridesharing.repositories.PaymentRepository;
import ridesharing.services.PaymentService;
import ridesharing.strategies.payment.PaymentStrategy;
import ridesharing.strategies.payment.WalletPaymentStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of PaymentService.
 * Uses configurable payment strategies.
 */
public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final List<PaymentStrategy> paymentStrategies;
    private final WalletPaymentStrategy walletStrategy;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                             List<PaymentStrategy> paymentStrategies) {
        this.paymentRepository = paymentRepository;
        this.paymentStrategies = paymentStrategies;
        
        // Find wallet strategy for balance management
        this.walletStrategy = paymentStrategies.stream()
                .filter(s -> s instanceof WalletPaymentStrategy)
                .map(s -> (WalletPaymentStrategy) s)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Payment processPayment(Ride ride, PaymentMethod paymentMethod) {
        if (ride.getFare() == null) {
            throw new PaymentException("Cannot process payment: fare not calculated");
        }
        
        Payment payment = new Payment(
                ride.getRideId(),
                ride.getPassengerId(),
                ride.getFare().getTotalAmount(),
                paymentMethod
        );
        
        // Find appropriate strategy
        PaymentStrategy strategy = findStrategy(payment);
        if (strategy == null) {
            throw new PaymentException("No payment strategy found for method: " + paymentMethod);
        }
        
        boolean success = strategy.processPayment(payment);
        if (!success) {
            throw new PaymentException("Payment processing failed");
        }
        
        return paymentRepository.save(payment);
    }

    @Override
    public boolean refundPayment(String paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            return false;
        }
        
        Payment payment = paymentOpt.get();
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            return false;
        }
        
        PaymentStrategy strategy = findStrategy(payment);
        if (strategy != null && strategy.refundPayment(payment)) {
            paymentRepository.save(payment);
            return true;
        }
        
        return false;
    }

    @Override
    public Optional<Payment> getPaymentForRide(String rideId) {
        return paymentRepository.findByRideId(rideId);
    }

    @Override
    public void addWalletBalance(String passengerId, double amount) {
        if (walletStrategy == null) {
            throw new PaymentException("Wallet payments not configured");
        }
        walletStrategy.addBalance(passengerId, amount);
    }

    @Override
    public double getWalletBalance(String passengerId) {
        if (walletStrategy == null) {
            return 0.0;
        }
        return walletStrategy.getBalance(passengerId);
    }

    private PaymentStrategy findStrategy(Payment payment) {
        return paymentStrategies.stream()
                .filter(s -> s.canHandle(payment))
                .findFirst()
                .orElse(null);
    }
}



