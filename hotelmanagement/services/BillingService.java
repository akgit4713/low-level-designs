package hotelmanagement.services;

import hotelmanagement.models.Bill;
import hotelmanagement.models.Reservation;
import hotelmanagement.strategies.discount.DiscountStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for billing operations
 */
public interface BillingService {
    
    /**
     * Generate a bill for a reservation
     * @param reservation The reservation to bill
     * @return The generated bill
     */
    Bill generateBill(Reservation reservation);
    
    /**
     * Get a bill by ID
     */
    Optional<Bill> getBill(String billId);
    
    /**
     * Get bill by reservation ID
     */
    Optional<Bill> getBillByReservation(String reservationId);
    
    /**
     * Get all unpaid bills
     */
    List<Bill> getUnpaidBills();
    
    /**
     * Add a discount strategy
     */
    void addDiscountStrategy(DiscountStrategy strategy);
    
    /**
     * Remove a discount strategy
     */
    void removeDiscountStrategy(DiscountStrategy strategy);
    
    /**
     * Set tax rate
     * @param taxName Name of the tax (e.g., "GST", "Service Tax")
     * @param rate Tax rate as percentage (e.g., 18.0 for 18%)
     */
    void setTaxRate(String taxName, double rate);
}



