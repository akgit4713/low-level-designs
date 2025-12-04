package hotelmanagement.repositories.impl;

import hotelmanagement.enums.PaymentStatus;
import hotelmanagement.models.Bill;
import hotelmanagement.repositories.BillRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of BillRepository
 */
public class InMemoryBillRepository implements BillRepository {
    
    private final ConcurrentHashMap<String, Bill> bills = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> reservationIndex = new ConcurrentHashMap<>();
    
    @Override
    public Bill save(Bill bill) {
        bills.put(bill.getId(), bill);
        reservationIndex.put(bill.getReservation().getId(), bill.getId());
        return bill;
    }
    
    @Override
    public Optional<Bill> findById(String id) {
        return Optional.ofNullable(bills.get(id));
    }
    
    @Override
    public List<Bill> findAll() {
        return new ArrayList<>(bills.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        Bill bill = bills.remove(id);
        if (bill != null) {
            reservationIndex.remove(bill.getReservation().getId());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean existsById(String id) {
        return bills.containsKey(id);
    }
    
    @Override
    public long count() {
        return bills.size();
    }
    
    @Override
    public Optional<Bill> findByReservationId(String reservationId) {
        String billId = reservationIndex.get(reservationId);
        return billId != null ? findById(billId) : Optional.empty();
    }
    
    @Override
    public List<Bill> findByPaymentStatus(PaymentStatus status) {
        return bills.values().stream()
            .filter(b -> b.getPaymentStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Bill> findUnpaidBills() {
        return bills.values().stream()
            .filter(b -> b.getPaymentStatus() == PaymentStatus.PENDING)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Bill> findByGeneratedDate(LocalDate date) {
        return bills.values().stream()
            .filter(b -> b.getGeneratedAt().toLocalDate().equals(date))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Bill> findByDateRange(LocalDate start, LocalDate end) {
        return bills.values().stream()
            .filter(b -> {
                LocalDate billDate = b.getGeneratedAt().toLocalDate();
                return !billDate.isBefore(start) && !billDate.isAfter(end);
            })
            .collect(Collectors.toList());
    }
}



