package restaurant.services;

import restaurant.models.Bill;
import restaurant.models.Order;
import restaurant.strategies.discount.DiscountStrategy;
import restaurant.strategies.tax.TaxStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for billing
 */
public interface BillingService {
    
    /**
     * Generate a bill for an order
     */
    Bill generateBill(Order order);
    
    /**
     * Generate bill with specific discounts
     */
    Bill generateBill(Order order, List<DiscountStrategy> discounts);
    
    /**
     * Get bill by ID
     */
    Optional<Bill> getBill(String billId);
    
    /**
     * Get bill for an order
     */
    Optional<Bill> getBillForOrder(String orderId);
    
    /**
     * Add discount strategy
     */
    void addDiscountStrategy(DiscountStrategy strategy);
    
    /**
     * Add tax strategy
     */
    void addTaxStrategy(TaxStrategy strategy);
    
    /**
     * Remove discount strategy
     */
    void removeDiscountStrategy(DiscountStrategy strategy);
}

