package hotelmanagement.services.impl;

import hotelmanagement.enums.PaymentStatus;
import hotelmanagement.enums.ReservationStatus;
import hotelmanagement.enums.RoomStatus;
import hotelmanagement.enums.RoomType;
import hotelmanagement.models.Bill;
import hotelmanagement.models.Reservation;
import hotelmanagement.models.Room;
import hotelmanagement.repositories.BillRepository;
import hotelmanagement.repositories.ReservationRepository;
import hotelmanagement.repositories.RoomRepository;
import hotelmanagement.services.ReportService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of ReportService
 */
public class ReportServiceImpl implements ReportService {
    
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final BillRepository billRepository;
    
    public ReportServiceImpl(RoomRepository roomRepository,
                              ReservationRepository reservationRepository,
                              BillRepository billRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.billRepository = billRepository;
    }
    
    @Override
    public OccupancyReport generateOccupancyReport(LocalDate startDate, LocalDate endDate) {
        List<Room> allRooms = roomRepository.findAll();
        int totalRooms = allRooms.size();
        
        List<Room> occupiedRooms = roomRepository.findByStatus(RoomStatus.OCCUPIED);
        int currentOccupied = occupiedRooms.size();
        
        // Get reservations in date range
        List<Reservation> allReservations = reservationRepository.findAll();
        
        int totalCheckIns = (int) allReservations.stream()
            .filter(r -> !r.getCheckInDate().isBefore(startDate) && !r.getCheckInDate().isAfter(endDate))
            .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN || r.getStatus() == ReservationStatus.CHECKED_OUT)
            .count();
        
        int totalCheckOuts = (int) allReservations.stream()
            .filter(r -> !r.getCheckOutDate().isBefore(startDate) && !r.getCheckOutDate().isAfter(endDate))
            .filter(r -> r.getStatus() == ReservationStatus.CHECKED_OUT)
            .count();
        
        int cancellations = (int) allReservations.stream()
            .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
            .count();
        
        int noShows = (int) allReservations.stream()
            .filter(r -> r.getStatus() == ReservationStatus.NO_SHOW)
            .count();
        
        double avgOccupancy = totalRooms > 0 ? (double) currentOccupied / totalRooms * 100 : 0;
        
        // Occupancy by type
        Map<RoomType, Double> occupancyByType = Arrays.stream(RoomType.values())
            .collect(Collectors.toMap(
                type -> type,
                type -> {
                    long total = roomRepository.findByType(type).size();
                    long occupied = roomRepository.findByType(type).stream()
                        .filter(r -> r.getStatus() == RoomStatus.OCCUPIED)
                        .count();
                    return total > 0 ? (double) occupied / total * 100 : 0;
                }
            ));
        
        return new OccupancyReport(
            startDate, endDate, totalRooms, currentOccupied,
            avgOccupancy, occupancyByType, totalCheckIns, totalCheckOuts,
            cancellations, noShows
        );
    }
    
    @Override
    public RevenueReport generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        List<Bill> bills = billRepository.findByDateRange(startDate, endDate);
        List<Bill> paidBills = bills.stream()
            .filter(b -> b.getPaymentStatus() == PaymentStatus.COMPLETED)
            .toList();
        
        BigDecimal totalRevenue = paidBills.stream()
            .map(Bill::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal roomRevenue = paidBills.stream()
            .map(Bill::getRoomCharges)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal serviceRevenue = totalRevenue.subtract(roomRevenue);
        
        // Average daily rate
        long totalNights = paidBills.stream()
            .mapToLong(b -> b.getReservation().getNumberOfNights())
            .sum();
        
        BigDecimal avgDailyRate = totalNights > 0 
            ? roomRevenue.divide(BigDecimal.valueOf(totalNights), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        // Revenue per available room (RevPAR)
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long totalRooms = roomRepository.count();
        long totalRoomNights = totalRooms * daysBetween;
        
        BigDecimal revPAR = totalRoomNights > 0
            ? totalRevenue.divide(BigDecimal.valueOf(totalRoomNights), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        // Revenue by room type
        Map<RoomType, BigDecimal> revenueByType = Arrays.stream(RoomType.values())
            .collect(Collectors.toMap(
                type -> type,
                type -> paidBills.stream()
                    .filter(b -> b.getReservation().getRoom().getType() == type)
                    .map(Bill::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
            ));
        
        int totalReservations = paidBills.size();
        int totalPayments = (int) paidBills.stream()
            .filter(b -> b.getPaymentId().isPresent())
            .count();
        
        return new RevenueReport(
            startDate, endDate, totalRevenue, roomRevenue, serviceRevenue,
            avgDailyRate, revPAR, revenueByType, totalReservations, totalPayments
        );
    }
    
    @Override
    public double getCurrentOccupancyRate() {
        long totalRooms = roomRepository.count();
        if (totalRooms == 0) return 0;
        
        long occupiedRooms = roomRepository.findByStatus(RoomStatus.OCCUPIED).size();
        return (double) occupiedRooms / totalRooms * 100;
    }
    
    @Override
    public DailySummary getDailySummary(LocalDate date) {
        int checkIns = (int) reservationRepository.findByCheckInDate(date).stream()
            .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN || 
                        r.getStatus() == ReservationStatus.CHECKED_OUT)
            .count();
        
        int checkOuts = (int) reservationRepository.findByCheckOutDate(date).stream()
            .filter(r -> r.getStatus() == ReservationStatus.CHECKED_OUT)
            .count();
        
        int currentOccupancy = roomRepository.findByStatus(RoomStatus.OCCUPIED).size();
        int availableRooms = roomRepository.findAvailable().size();
        int totalRooms = roomRepository.findAll().size();
        
        double occupancyRate = totalRooms > 0 ? (double) currentOccupancy / totalRooms * 100 : 0;
        
        List<Bill> todayBills = billRepository.findByGeneratedDate(date);
        BigDecimal dayRevenue = todayBills.stream()
            .filter(b -> b.getPaymentStatus() == PaymentStatus.COMPLETED)
            .map(Bill::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int pendingReservations = (int) reservationRepository.findByCheckInDate(date).stream()
            .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
            .count();
        
        return new DailySummary(
            date, checkIns, checkOuts, currentOccupancy, availableRooms,
            occupancyRate, dayRevenue, pendingReservations
        );
    }
}



