package parkinglot.models;

import parkinglot.strategies.payment.CashPaymentProcessor;
import parkinglot.strategies.payment.PaymentProcessor;

/**
 * Represents an exit gate in the parking lot.
 * Handles payment processing and vehicle exit.
 */
public class ExitGate {
    private final String gateId;
    private PaymentProcessor paymentProcessor;

    public ExitGate(String gateId) {
        this.gateId = gateId;
        this.paymentProcessor = new CashPaymentProcessor(); // Default payment processor
    }

    public ExitGate(String gateId, PaymentProcessor paymentProcessor) {
        this.gateId = gateId;
        this.paymentProcessor = paymentProcessor;
    }

    public String getGateId() {
        return gateId;
    }

    public PaymentProcessor getPaymentProcessor() {
        return paymentProcessor;
    }

    public void setPaymentProcessor(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    /**
     * Processes payment for a parking ticket.
     */
    public PaymentResult processPayment(double amount, ParkingTicket ticket) {
        System.out.println("🚧 Gate " + gateId + ": Processing exit for " + ticket.getVehicle().getLicensePlate());
        return paymentProcessor.processPayment(amount, ticket);
    }

    /**
     * Shows goodbye message after successful exit.
     */
    public void showGoodbye(Vehicle vehicle) {
        System.out.println("🚧 Gate " + gateId + ": Goodbye " + vehicle.getLicensePlate() + "! Thank you for parking.");
    }

    @Override
    public String toString() {
        return String.format("ExitGate[%s, %s]", gateId, paymentProcessor.getPaymentMethodName());
    }
}



