package restaurant.services.impl;

import restaurant.models.Bill;
import restaurant.models.Order;
import restaurant.services.BillingService;
import restaurant.strategies.discount.DiscountStrategy;
import restaurant.strategies.tax.TaxStrategy;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of BillingService
 */
public class BillingServiceImpl implements BillingService {
    
    private final Map<String, Bill> bills = new ConcurrentHashMap<>();
    private final Map<String, Bill> orderToBill = new ConcurrentHashMap<>();
    private final List<DiscountStrategy> discountStrategies = new ArrayList<>();
    private final List<TaxStrategy> taxStrategies = new ArrayList<>();
    
    @Override
    public Bill generateBill(Order order) {
        return generateBill(order, discountStrategies);
    }
    
    @Override
    public Bill generateBill(Order order, List<DiscountStrategy> discounts) {
        String billId = "BILL-" + UUID.randomUUID().toString().substring(0, 8);
        BigDecimal subtotal = order.calculateSubtotal();
        
        Bill.Builder builder = Bill.builder()
            .id(billId)
            .order(order);
        
        // Apply applicable discounts
        for (DiscountStrategy strategy : discounts) {
            if (strategy.isApplicable(order)) {
                BigDecimal discountAmount = strategy.calculateDiscount(order, subtotal);
                if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                    builder.addDiscount(new Bill.DiscountEntry(
                        strategy.getDiscountName(), discountAmount
                    ));
                }
            }
        }
        
        // Apply applicable taxes
        for (TaxStrategy strategy : taxStrategies) {
            if (strategy.isApplicable(order)) {
                builder.addTax(new Bill.TaxEntry(
                    strategy.getTaxName(), strategy.getTaxRate()
                ));
            }
        }
        
        Bill bill = builder.build();
        bills.put(bill.getId(), bill);
        orderToBill.put(order.getId(), bill);
        
        return bill;
    }
    
    @Override
    public Optional<Bill> getBill(String billId) {
        return Optional.ofNullable(bills.get(billId));
    }
    
    @Override
    public Optional<Bill> getBillForOrder(String orderId) {
        return Optional.ofNullable(orderToBill.get(orderId));
    }
    
    @Override
    public void addDiscountStrategy(DiscountStrategy strategy) {
        discountStrategies.add(strategy);
    }
    
    @Override
    public void addTaxStrategy(TaxStrategy strategy) {
        taxStrategies.add(strategy);
    }
    
    @Override
    public void removeDiscountStrategy(DiscountStrategy strategy) {
        discountStrategies.remove(strategy);
    }
}

