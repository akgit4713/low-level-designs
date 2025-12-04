package airline.services.impl;

import airline.enums.PaymentMethod;
import airline.enums.SeatClass;
import airline.exceptions.PaymentException;
import airline.models.Booking;
import airline.models.Payment;
import airline.models.Seat;
import airline.services.PaymentService;
import airline.strategies.payment.PaymentStrategy;
import airline.strategies.pricing.PricingStrategy;
import airline.strategies.refund.RefundStrategy;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of PaymentService.
 */
public class PaymentServiceImpl implements PaymentService {
    
    private final Map<PaymentMethod, PaymentStrategy> paymentStrategies = new EnumMap<>(PaymentMethod.class);
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    private final PricingStrategy pricingStrategy;
    private RefundStrategy refundStrategy;

    public PaymentServiceImpl(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public void registerPaymentStrategy(PaymentMethod method, PaymentStrategy strategy) {
        paymentStrategies.put(method, strategy);
    }

    public void setRefundStrategy(RefundStrategy refundStrategy) {
        this.refundStrategy = refundStrategy;
    }

    @Override
    public Payment processPayment(Booking booking, PaymentMethod method) {
        PaymentStrategy strategy = paymentStrategies.get(method);
        if (strategy == null) {
            throw new PaymentException("Payment method not supported: " + method);
        }

        BigDecimal amount = calculateBookingPrice(booking);
        Payment payment = new Payment(booking.getId(), amount, method);
        
        boolean success = strategy.processPayment(payment);
        if (!success) {
            throw new PaymentException("Payment failed: " + payment.getFailureReason());
        }
        
        booking.setPayment(payment);
        payments.put(payment.getId(), payment);
        
        return payment;
    }

    @Override
    public Payment processRefund(Booking booking) {
        if (refundStrategy == null) {
            throw new PaymentException("No refund strategy configured");
        }
        
        Payment originalPayment = booking.getPayment();
        if (originalPayment == null) {
            throw new PaymentException("No payment found for booking");
        }
        
        BigDecimal refundAmount = refundStrategy.calculateRefund(booking);
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("⚠️ No refund applicable for this booking");
            return null;
        }
        
        PaymentStrategy strategy = paymentStrategies.get(originalPayment.getMethod());
        if (strategy == null) {
            throw new PaymentException("Cannot process refund - payment method not available");
        }
        
        boolean success = strategy.processRefund(originalPayment, refundAmount);
        if (!success) {
            throw new PaymentException("Refund processing failed");
        }
        
        System.out.println("✓ Refund of $" + refundAmount + " processed successfully");
        return originalPayment;
    }

    @Override
    public BigDecimal calculateBookingPrice(Booking booking) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (Booking.BookingPassenger bp : booking.getPassengers()) {
            Seat seat = booking.getFlight().getSeat(bp.getSeatNumber()).orElse(null);
            if (seat != null) {
                SeatClass seatClass = seat.getSeatClass();
                total = total.add(pricingStrategy.calculatePrice(booking.getFlight(), seatClass));
                total = total.add(seat.getExtraCharge());
            }
        }
        
        // Add baggage charges
        for (Booking.BookingPassenger bp : booking.getPassengers()) {
            for (var baggage : bp.getPassenger().getBaggage()) {
                total = total.add(baggage.getExtraCharge());
            }
        }
        
        return total;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return payments.get(paymentId);
    }
}



