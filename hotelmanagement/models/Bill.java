package hotelmanagement.models;

import hotelmanagement.enums.PaymentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a bill for a reservation including room charges, services, discounts, and taxes
 */
public class Bill {
    private final String id;
    private final Reservation reservation;
    private final LocalDateTime generatedAt;
    private final BigDecimal roomCharges;
    private final List<ServiceCharge> serviceCharges;
    private final List<DiscountLine> discounts;
    private final List<TaxLine> taxes;
    private final BigDecimal subtotal;
    private final BigDecimal totalDiscounts;
    private final BigDecimal totalTaxes;
    private final BigDecimal totalAmount;
    
    private volatile PaymentStatus paymentStatus;
    private String paymentId;

    private Bill(Builder builder) {
        this.id = builder.id;
        this.reservation = builder.reservation;
        this.roomCharges = builder.roomCharges;
        this.serviceCharges = Collections.unmodifiableList(new ArrayList<>(builder.serviceCharges));
        this.discounts = Collections.unmodifiableList(new ArrayList<>(builder.discounts));
        this.taxes = Collections.unmodifiableList(new ArrayList<>(builder.taxes));
        this.subtotal = builder.subtotal;
        this.totalDiscounts = builder.totalDiscounts;
        this.totalTaxes = builder.totalTaxes;
        this.totalAmount = builder.totalAmount;
        this.paymentStatus = PaymentStatus.PENDING;
        this.generatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public BigDecimal getRoomCharges() {
        return roomCharges;
    }

    public List<ServiceCharge> getServiceCharges() {
        return serviceCharges;
    }

    public List<DiscountLine> getDiscounts() {
        return discounts;
    }

    public List<TaxLine> getTaxes() {
        return taxes;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getTotalDiscounts() {
        return totalDiscounts;
    }

    public BigDecimal getTotalTaxes() {
        return totalTaxes;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Optional<String> getPaymentId() {
        return Optional.ofNullable(paymentId);
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public boolean isPaid() {
        return paymentStatus == PaymentStatus.COMPLETED;
    }

    /**
     * Get formatted bill summary
     */
    public String getFormattedSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(50)).append("\n");
        sb.append("              HOTEL BILL\n");
        sb.append("=".repeat(50)).append("\n");
        sb.append(String.format("Bill ID: %s\n", id));
        sb.append(String.format("Guest: %s\n", reservation.getGuest().getName()));
        sb.append(String.format("Room: %s (%s)\n", reservation.getRoom().getRoomNumber(), 
            reservation.getRoom().getType()));
        sb.append(String.format("Stay: %s to %s (%d nights)\n", 
            reservation.getCheckInDate(), 
            reservation.getCheckOutDate(),
            reservation.getNumberOfNights()));
        sb.append("-".repeat(50)).append("\n");
        
        sb.append(String.format("Room Charges: %s x %d nights = $%s\n",
            reservation.getRoomRatePerNight(),
            reservation.getNumberOfNights(),
            roomCharges));
        
        if (!serviceCharges.isEmpty()) {
            sb.append("\nService Charges:\n");
            for (ServiceCharge sc : serviceCharges) {
                sb.append(String.format("  - %s: $%s\n", sc.getDescription(), sc.getAmount()));
            }
        }
        
        sb.append("-".repeat(50)).append("\n");
        sb.append(String.format("Subtotal: $%s\n", subtotal));
        
        if (!discounts.isEmpty()) {
            sb.append("\nDiscounts:\n");
            for (DiscountLine dl : discounts) {
                sb.append(String.format("  - %s: -$%s\n", dl.description(), dl.amount()));
            }
            sb.append(String.format("Total Discounts: -$%s\n", totalDiscounts));
        }
        
        if (!taxes.isEmpty()) {
            sb.append("\nTaxes:\n");
            for (TaxLine tl : taxes) {
                sb.append(String.format("  - %s (%.1f%%): $%s\n", tl.name(), tl.rate(), tl.amount()));
            }
        }
        
        sb.append("=".repeat(50)).append("\n");
        sb.append(String.format("TOTAL AMOUNT: $%s\n", totalAmount));
        sb.append(String.format("Payment Status: %s\n", paymentStatus));
        sb.append("=".repeat(50)).append("\n");
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return Objects.equals(id, bill.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Bill{id='%s', reservation='%s', total=%s, status=%s}",
            id, reservation.getId(), totalAmount, paymentStatus);
    }

    // Value objects for bill line items
    public record DiscountLine(String description, BigDecimal amount) {}
    public record TaxLine(String name, double rate, BigDecimal amount) {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Reservation reservation;
        private BigDecimal roomCharges = BigDecimal.ZERO;
        private List<ServiceCharge> serviceCharges = new ArrayList<>();
        private List<DiscountLine> discounts = new ArrayList<>();
        private List<TaxLine> taxes = new ArrayList<>();
        private BigDecimal subtotal = BigDecimal.ZERO;
        private BigDecimal totalDiscounts = BigDecimal.ZERO;
        private BigDecimal totalTaxes = BigDecimal.ZERO;
        private BigDecimal totalAmount = BigDecimal.ZERO;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder reservation(Reservation reservation) {
            this.reservation = reservation;
            return this;
        }

        public Builder roomCharges(BigDecimal roomCharges) {
            this.roomCharges = roomCharges;
            return this;
        }

        public Builder serviceCharges(List<ServiceCharge> serviceCharges) {
            this.serviceCharges = new ArrayList<>(serviceCharges);
            return this;
        }

        public Builder addDiscount(String description, BigDecimal amount) {
            this.discounts.add(new DiscountLine(description, amount));
            this.totalDiscounts = this.totalDiscounts.add(amount);
            return this;
        }

        public Builder addTax(String name, double rate, BigDecimal amount) {
            this.taxes.add(new TaxLine(name, rate, amount));
            this.totalTaxes = this.totalTaxes.add(amount);
            return this;
        }

        public Bill build() {
            if (id == null || id.isBlank()) {
                id = "BILL-" + UUID.randomUUID().toString().substring(0, 8);
            }
            Objects.requireNonNull(reservation, "Reservation is required");
            
            // Calculate subtotal
            BigDecimal serviceTotal = serviceCharges.stream()
                .map(ServiceCharge::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            subtotal = roomCharges.add(serviceTotal);
            
            // Calculate total
            totalAmount = subtotal
                .subtract(totalDiscounts)
                .add(totalTaxes)
                .setScale(2, RoundingMode.HALF_UP);
            
            return new Bill(this);
        }
    }
}



