package restaurant.services;

import restaurant.models.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service interface for generating reports and analytics
 */
public interface ReportService {
    
    /**
     * Generate sales report for a date range
     */
    SalesReport generateSalesReport(LocalDateTime start, LocalDateTime end);
    
    /**
     * Generate inventory report
     */
    InventoryReport generateInventoryReport();
    
    /**
     * Generate staff performance report
     */
    StaffPerformanceReport generateStaffReport();
    
    /**
     * Get daily sales summary
     */
    DailySummary getDailySummary(LocalDate date);
    
    // Report DTOs
    
    record SalesReport(
        LocalDateTime startDate,
        LocalDateTime endDate,
        int totalOrders,
        BigDecimal totalRevenue,
        BigDecimal averageOrderValue,
        Map<String, Integer> itemsSold,
        Map<String, BigDecimal> categoryRevenue
    ) {}
    
    record InventoryReport(
        int totalItems,
        List<InventoryItem> lowStockItems,
        List<InventoryItem> depletedItems,
        BigDecimal totalInventoryValue
    ) {}
    
    record StaffPerformanceReport(
        List<StaffPerformance> performances
    ) {}
    
    record StaffPerformance(
        String staffId,
        String staffName,
        int ordersServed,
        double averageRating
    ) {}
    
    record DailySummary(
        LocalDate date,
        int totalOrders,
        int completedOrders,
        int cancelledOrders,
        BigDecimal totalRevenue,
        int reservationsCount
    ) {}
}

