package hotelmanagement.services.impl;

import hotelmanagement.models.Bill;
import hotelmanagement.models.Reservation;
import hotelmanagement.models.ServiceCharge;
import hotelmanagement.repositories.BillRepository;
import hotelmanagement.services.BillingService;
import hotelmanagement.strategies.discount.DiscountStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of BillingService
 */
public class BillingServiceImpl implements BillingService {
    
    private final BillRepository billRepository;
    private final List<DiscountStrategy> discountStrategies = new CopyOnWriteArrayList<>();
    private final Map<String, Double> taxRates = new LinkedHashMap<>();
    
    public BillingServiceImpl(BillRepository billRepository) {
        this.billRepository = billRepository;
        
        // Set default tax rates
        taxRates.put("GST", 18.0);
    }
    
    @Override
    public Bill generateBill(Reservation reservation) {
        // Calculate room charges
        BigDecimal roomCharges = reservation.calculateRoomCharges();
        
        // Get service charges
        List<ServiceCharge> serviceCharges = reservation.getServiceCharges();
        BigDecimal serviceTotal = serviceCharges.stream()
            .map(ServiceCharge::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate subtotal
        BigDecimal subtotal = roomCharges.add(serviceTotal);
        
        // Build bill with discounts and taxes
        Bill.Builder billBuilder = Bill.builder()
            .reservation(reservation)
            .roomCharges(roomCharges)
            .serviceCharges(serviceCharges);
        
        // Apply applicable discounts
        BigDecimal discountableAmount = subtotal;
        for (DiscountStrategy strategy : getApplicableDiscounts(reservation)) {
            BigDecimal discountAmount = strategy.calculateDiscount(reservation, discountableAmount);
            if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                billBuilder.addDiscount(strategy.getDiscountName(), discountAmount);
                discountableAmount = discountableAmount.subtract(discountAmount);
            }
        }
        
        // Apply taxes on discounted amount
        for (Map.Entry<String, Double> tax : taxRates.entrySet()) {
            BigDecimal taxAmount = discountableAmount
                .multiply(BigDecimal.valueOf(tax.getValue()))
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            billBuilder.addTax(tax.getKey(), tax.getValue(), taxAmount);
        }
        
        Bill bill = billBuilder.build();
        return billRepository.save(bill);
    }
    
    @Override
    public Optional<Bill> getBill(String billId) {
        return billRepository.findById(billId);
    }
    
    @Override
    public Optional<Bill> getBillByReservation(String reservationId) {
        return billRepository.findByReservationId(reservationId);
    }
    
    @Override
    public List<Bill> getUnpaidBills() {
        return billRepository.findUnpaidBills();
    }
    
    @Override
    public void addDiscountStrategy(DiscountStrategy strategy) {
        discountStrategies.add(strategy);
        // Sort by priority
        discountStrategies.sort(Comparator.comparingInt(DiscountStrategy::getPriority));
    }
    
    @Override
    public void removeDiscountStrategy(DiscountStrategy strategy) {
        discountStrategies.remove(strategy);
    }
    
    @Override
    public void setTaxRate(String taxName, double rate) {
        taxRates.put(taxName, rate);
    }
    
    /**
     * Get applicable discount strategies for a reservation
     */
    private List<DiscountStrategy> getApplicableDiscounts(Reservation reservation) {
        return discountStrategies.stream()
            .filter(strategy -> strategy.isApplicable(reservation))
            .toList();
    }
}



