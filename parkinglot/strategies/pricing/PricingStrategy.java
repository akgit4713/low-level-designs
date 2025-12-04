package parkinglot.strategies.pricing;

import parkinglot.models.ParkingTicket;

/**
 * Strategy interface for calculating parking fees.
 * Implements the Strategy Pattern to allow different pricing algorithms.
 * 
 * Extension point: Implement this interface to add new pricing models
 * (e.g., weekend rates, loyalty discounts, dynamic pricing).
 */
public interface PricingStrategy {
    
    /**
     * Calculates the parking fee for the given ticket.
     * 
     * @param ticket The parking ticket containing duration and vehicle info
     * @return The calculated fee amount
     */
    double calculateFee(ParkingTicket ticket);
    
    /**
     * Gets a description of this pricing strategy.
     */
    String getDescription();
}



