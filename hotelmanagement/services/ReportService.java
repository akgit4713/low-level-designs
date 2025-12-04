package hotelmanagement.services;

import hotelmanagement.enums.RoomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Service interface for generating reports and analytics
 */
public interface ReportService {
    
    /**
     * Generate occupancy report for a date range
     */
    OccupancyReport generateOccupancyReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Generate revenue report for a date range
     */
    RevenueReport generateRevenueReport(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get current occupancy rate
     */
    double getCurrentOccupancyRate();
    
    /**
     * Get daily summary for a specific date
     */
    DailySummary getDailySummary(LocalDate date);
    
    // Report data classes
    
    record OccupancyReport(
        LocalDate startDate,
        LocalDate endDate,
        int totalRooms,
        int occupiedRooms,
        double averageOccupancyRate,
        Map<RoomType, Double> occupancyByType,
        int totalCheckIns,
        int totalCheckOuts,
        int cancellations,
        int noShows
    ) {}
    
    record RevenueReport(
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalRevenue,
        BigDecimal roomRevenue,
        BigDecimal serviceRevenue,
        BigDecimal averageDailyRate,
        BigDecimal revenuePerAvailableRoom,
        Map<RoomType, BigDecimal> revenueByRoomType,
        int totalReservations,
        int totalPayments
    ) {}
    
    record DailySummary(
        LocalDate date,
        int checkIns,
        int checkOuts,
        int currentOccupancy,
        int availableRooms,
        double occupancyRate,
        BigDecimal dayRevenue,
        int pendingReservations
    ) {}
}



