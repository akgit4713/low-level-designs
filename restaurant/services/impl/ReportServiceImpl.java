package restaurant.services.impl;

import restaurant.enums.OrderStatus;
import restaurant.models.*;
import restaurant.repositories.OrderRepository;
import restaurant.repositories.impl.InMemoryInventoryRepository;
import restaurant.repositories.impl.InMemoryReservationRepository;
import restaurant.repositories.impl.InMemoryStaffRepository;
import restaurant.services.ReportService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of ReportService
 */
public class ReportServiceImpl implements ReportService {
    
    private final OrderRepository orderRepository;
    private final InMemoryInventoryRepository inventoryRepository;
    private final InMemoryStaffRepository staffRepository;
    private final InMemoryReservationRepository reservationRepository;
    
    public ReportServiceImpl(OrderRepository orderRepository,
                             InMemoryInventoryRepository inventoryRepository,
                             InMemoryStaffRepository staffRepository,
                             InMemoryReservationRepository reservationRepository) {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.staffRepository = staffRepository;
        this.reservationRepository = reservationRepository;
    }
    
    @Override
    public SalesReport generateSalesReport(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByDateRange(start, end).stream()
            .filter(o -> o.getStatus() == OrderStatus.COMPLETED || o.getStatus() == OrderStatus.PAID)
            .collect(Collectors.toList());
        
        int totalOrders = orders.size();
        BigDecimal totalRevenue = orders.stream()
            .map(Order::calculateSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal avgOrderValue = totalOrders > 0 
            ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        // Items sold count
        Map<String, Integer> itemsSold = new HashMap<>();
        Map<String, BigDecimal> categoryRevenue = new HashMap<>();
        
        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                String itemName = item.getMenuItem().getName();
                String category = item.getMenuItem().getCategory().getDisplayName();
                
                itemsSold.merge(itemName, item.getQuantity(), Integer::sum);
                categoryRevenue.merge(category, item.getItemTotal(), BigDecimal::add);
            }
        }
        
        return new SalesReport(start, end, totalOrders, totalRevenue, avgOrderValue, itemsSold, categoryRevenue);
    }
    
    @Override
    public InventoryReport generateInventoryReport() {
        List<InventoryItem> allItems = inventoryRepository.findAll();
        List<InventoryItem> lowStock = inventoryRepository.findLowStock();
        List<InventoryItem> depleted = allItems.stream()
            .filter(i -> i.getQuantity() <= 0)
            .collect(Collectors.toList());
        
        BigDecimal totalValue = allItems.stream()
            .map(item -> item.getIngredient().getCostPerUnit()
                .multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new InventoryReport(allItems.size(), lowStock, depleted, totalValue);
    }
    
    @Override
    public StaffPerformanceReport generateStaffReport() {
        List<StaffPerformance> performances = staffRepository.findAll().stream()
            .map(staff -> new StaffPerformance(
                staff.getId(),
                staff.getName(),
                staff.getOrdersServed(),
                staff.getAverageRating()
            ))
            .sorted(Comparator.comparingDouble(StaffPerformance::averageRating).reversed())
            .collect(Collectors.toList());
        
        return new StaffPerformanceReport(performances);
    }
    
    @Override
    public DailySummary getDailySummary(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        List<Order> orders = orderRepository.findByDateRange(startOfDay, endOfDay);
        
        int total = orders.size();
        int completed = (int) orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
            .count();
        int cancelled = (int) orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.CANCELLED)
            .count();
        
        BigDecimal revenue = orders.stream()
            .filter(o -> o.getStatus() == OrderStatus.COMPLETED || o.getStatus() == OrderStatus.PAID)
            .map(Order::calculateSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int reservations = reservationRepository.findByDate(date).size();
        
        return new DailySummary(date, total, completed, cancelled, revenue, reservations);
    }
}

