package hotelmanagement.models;

import hotelmanagement.enums.ServiceType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an additional service charge during a guest's stay
 */
public class ServiceCharge {
    private final String id;
    private final ServiceType serviceType;
    private final BigDecimal amount;
    private final String description;
    private final LocalDateTime chargedAt;
    private final int quantity;

    public ServiceCharge(ServiceType serviceType, BigDecimal amount, String description) {
        this(serviceType, amount, description, 1);
    }

    public ServiceCharge(ServiceType serviceType, BigDecimal amount, String description, int quantity) {
        this.id = "SVC-" + UUID.randomUUID().toString().substring(0, 8);
        this.serviceType = serviceType;
        this.amount = amount;
        this.description = description;
        this.quantity = quantity;
        this.chargedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public BigDecimal getAmount() {
        return amount.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getUnitAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getChargedAt() {
        return chargedAt;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceCharge that = (ServiceCharge) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ServiceCharge{type=%s, amount=%s, qty=%d, desc='%s'}",
            serviceType, amount, quantity, description);
    }
}



