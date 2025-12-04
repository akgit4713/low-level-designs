package restaurant.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a bill/invoice for an order
 */
public class Bill {
    private final String id;
    private final Order order;
    private final LocalDateTime generatedAt;
    private final BigDecimal subtotal;
    private final List<BillLineItem> lineItems;
    private final List<DiscountEntry> discounts;
    private final List<TaxEntry> taxes;
    private final BigDecimal totalAmount;
    
    private volatile boolean paid;
    private LocalDateTime paidAt;

    private Bill(Builder builder) {
        this.id = builder.id;
        this.order = builder.order;
        this.generatedAt = LocalDateTime.now();
        this.lineItems = Collections.unmodifiableList(new ArrayList<>(builder.lineItems));
        this.subtotal = calculateSubtotal();
        this.discounts = Collections.unmodifiableList(new ArrayList<>(builder.discounts));
        this.taxes = Collections.unmodifiableList(new ArrayList<>(builder.taxes));
        this.totalAmount = calculateTotal();
        this.paid = false;
    }

    private BigDecimal calculateSubtotal() {
        return lineItems.stream()
            .map(BillLineItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotal() {
        BigDecimal afterDiscount = subtotal;
        
        // Apply discounts
        for (DiscountEntry discount : discounts) {
            afterDiscount = afterDiscount.subtract(discount.getAmount());
        }
        
        // Ensure we don't go negative
        if (afterDiscount.compareTo(BigDecimal.ZERO) < 0) {
            afterDiscount = BigDecimal.ZERO;
        }
        
        // Apply taxes
        BigDecimal taxAmount = BigDecimal.ZERO;
        for (TaxEntry tax : taxes) {
            taxAmount = taxAmount.add(
                afterDiscount.multiply(tax.getRate()).setScale(2, RoundingMode.HALF_UP)
            );
        }
        
        return afterDiscount.add(taxAmount).setScale(2, RoundingMode.HALF_UP);
    }

    public String getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public List<BillLineItem> getLineItems() {
        return lineItems;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public List<DiscountEntry> getDiscounts() {
        return discounts;
    }

    public List<TaxEntry> getTaxes() {
        return taxes;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public boolean isPaid() {
        return paid;
    }

    public Optional<LocalDateTime> getPaidAt() {
        return Optional.ofNullable(paidAt);
    }

    public void markPaid() {
        this.paid = true;
        this.paidAt = LocalDateTime.now();
    }

    public BigDecimal getTotalDiscountAmount() {
        return discounts.stream()
            .map(DiscountEntry::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalTaxAmount() {
        BigDecimal afterDiscount = subtotal.subtract(getTotalDiscountAmount());
        if (afterDiscount.compareTo(BigDecimal.ZERO) < 0) {
            afterDiscount = BigDecimal.ZERO;
        }
        
        BigDecimal taxAmount = BigDecimal.ZERO;
        for (TaxEntry tax : taxes) {
            taxAmount = taxAmount.add(
                afterDiscount.multiply(tax.getRate()).setScale(2, RoundingMode.HALF_UP)
            );
        }
        return taxAmount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("====== BILL ======\n");
        sb.append("Bill ID: ").append(id).append("\n");
        sb.append("Order ID: ").append(order.getId()).append("\n");
        sb.append("Generated: ").append(generatedAt).append("\n");
        sb.append("------------------\n");
        
        for (BillLineItem item : lineItems) {
            sb.append(String.format("%-30s %10s\n", item.getDescription(), item.getAmount()));
        }
        
        sb.append("------------------\n");
        sb.append(String.format("%-30s %10s\n", "Subtotal:", subtotal));
        
        for (DiscountEntry discount : discounts) {
            sb.append(String.format("%-30s -%9s\n", discount.getName() + ":", discount.getAmount()));
        }
        
        for (TaxEntry tax : taxes) {
            BigDecimal taxAmt = subtotal.subtract(getTotalDiscountAmount())
                .multiply(tax.getRate()).setScale(2, RoundingMode.HALF_UP);
            sb.append(String.format("%-30s %10s\n", tax.getName() + " (" + tax.getRate().multiply(BigDecimal.valueOf(100)) + "%):", taxAmt));
        }
        
        sb.append("==================\n");
        sb.append(String.format("%-30s %10s\n", "TOTAL:", totalAmount));
        sb.append("==================\n");
        
        return sb.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Order order;
        private List<BillLineItem> lineItems = new ArrayList<>();
        private List<DiscountEntry> discounts = new ArrayList<>();
        private List<TaxEntry> taxes = new ArrayList<>();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder order(Order order) {
            this.order = order;
            return this;
        }

        public Builder addLineItem(BillLineItem item) {
            this.lineItems.add(item);
            return this;
        }

        public Builder addDiscount(DiscountEntry discount) {
            this.discounts.add(discount);
            return this;
        }

        public Builder addTax(TaxEntry tax) {
            this.taxes.add(tax);
            return this;
        }

        public Bill build() {
            Objects.requireNonNull(id, "Bill ID is required");
            Objects.requireNonNull(order, "Order is required");
            
            if (lineItems.isEmpty()) {
                // Auto-populate from order
                for (OrderItem item : order.getItems()) {
                    lineItems.add(new BillLineItem(
                        item.getQuantity() + "x " + item.getMenuItem().getName(),
                        item.getItemTotal()
                    ));
                }
            }
            
            return new Bill(this);
        }
    }

    /**
     * A line item in the bill
     */
    public static class BillLineItem {
        private final String description;
        private final BigDecimal amount;

        public BillLineItem(String description, BigDecimal amount) {
            this.description = description;
            this.amount = amount;
        }

        public String getDescription() {
            return description;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }

    /**
     * A discount entry in the bill
     */
    public static class DiscountEntry {
        private final String name;
        private final BigDecimal amount;

        public DiscountEntry(String name, BigDecimal amount) {
            this.name = name;
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }

    /**
     * A tax entry in the bill
     */
    public static class TaxEntry {
        private final String name;
        private final BigDecimal rate; // e.g., 0.18 for 18%

        public TaxEntry(String name, BigDecimal rate) {
            this.name = name;
            this.rate = rate;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getRate() {
            return rate;
        }
    }
}

