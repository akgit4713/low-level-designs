package hotelmanagement;

import hotelmanagement.enums.*;
import hotelmanagement.models.*;
import hotelmanagement.observers.*;
import hotelmanagement.repositories.impl.*;
import hotelmanagement.services.*;
import hotelmanagement.services.impl.*;
import hotelmanagement.strategies.discount.*;
import hotelmanagement.strategies.payment.*;
import hotelmanagement.strategies.pricing.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Facade class that provides a simplified interface to the hotel management system
 * Wires all components together and provides high-level operations
 */
public class Hotel {
    
    private final String name;
    
    // Repositories
    private final InMemoryRoomRepository roomRepository;
    private final InMemoryReservationRepository reservationRepository;
    private final InMemoryGuestRepository guestRepository;
    private final InMemoryBillRepository billRepository;
    
    // Services
    private final RoomService roomService;
    private final ReservationService reservationService;
    private final GuestService guestService;
    private final CheckInOutService checkInOutService;
    private final BillingService billingService;
    private final PaymentService paymentService;
    private final HousekeepingService housekeepingService;
    private final ReportService reportService;
    
    public Hotel(String name) {
        this.name = name;
        
        // Initialize repositories
        this.roomRepository = new InMemoryRoomRepository();
        this.reservationRepository = new InMemoryReservationRepository();
        this.guestRepository = new InMemoryGuestRepository();
        this.billRepository = new InMemoryBillRepository();
        
        // Initialize services with dependency injection
        this.roomService = new RoomServiceImpl(roomRepository, reservationRepository);
        this.guestService = new GuestServiceImpl(guestRepository);
        this.reservationService = new ReservationServiceImpl(reservationRepository, roomRepository);
        this.billingService = new BillingServiceImpl(billRepository);
        this.paymentService = new PaymentServiceImpl();
        this.housekeepingService = new HousekeepingServiceImpl(roomRepository);
        this.checkInOutService = new CheckInOutServiceImpl(
            reservationRepository, roomRepository, billingService, guestService
        );
        this.reportService = new ReportServiceImpl(roomRepository, reservationRepository, billRepository);
        
        // Set up default configurations
        setupObservers();
        setupPaymentStrategies();
        setupDiscountStrategies();
    }
    
    private void setupObservers() {
        // Email notifications for reservations
        reservationService.addObserver(new EmailNotificationObserver());
        
        // Housekeeping notifications
        roomService.addObserver(new HousekeepingNotificationObserver());
        
        // Add observers to check-in/out service
        CheckInOutServiceImpl checkInOutImpl = (CheckInOutServiceImpl) checkInOutService;
        checkInOutImpl.addReservationObserver(new EmailNotificationObserver());
        checkInOutImpl.addRoomObserver(new HousekeepingNotificationObserver());
    }
    
    private void setupPaymentStrategies() {
        paymentService.registerPaymentStrategy(PaymentMethod.CASH, new CashPaymentStrategy());
        paymentService.registerPaymentStrategy(PaymentMethod.CREDIT_CARD, 
            new CardPaymentStrategy(PaymentMethod.CREDIT_CARD));
        paymentService.registerPaymentStrategy(PaymentMethod.DEBIT_CARD, 
            new CardPaymentStrategy(PaymentMethod.DEBIT_CARD));
        paymentService.registerPaymentStrategy(PaymentMethod.ONLINE, new OnlinePaymentStrategy());
    }
    
    private void setupDiscountStrategies() {
        billingService.addDiscountStrategy(new LoyaltyDiscountStrategy());
        billingService.addDiscountStrategy(new LongStayDiscountStrategy());
    }
    
    // === Room Operations ===
    
    public Room addRoom(String roomNumber, int floor, RoomType type, BigDecimal baseRate, 
                        int capacity, Set<String> amenities) {
        Room room = Room.builder()
            .roomNumber(roomNumber)
            .floor(floor)
            .type(type)
            .baseRate(baseRate)
            .capacity(capacity)
            .amenities(amenities)
            .build();
        return roomService.addRoom(room);
    }
    
    public Room addRoom(Room room) {
        return roomService.addRoom(room);
    }
    
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }
    
    public List<Room> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }
    
    public List<Room> getAvailableRoomsByType(RoomType type) {
        return roomService.getAvailableRoomsByType(type);
    }
    
    public Optional<Room> getRoom(String roomNumber) {
        return roomService.getRoomByNumber(roomNumber);
    }
    
    public void setPricingStrategy(PricingStrategy strategy) {
        roomService.setPricingStrategy(strategy);
    }
    
    // === Guest Operations ===
    
    public Guest registerGuest(Guest guest) {
        return guestService.registerGuest(guest);
    }
    
    public Guest registerGuest(String name, String email, String phone, 
                               String idType, String idNumber) {
        Guest guest = Guest.builder()
            .name(name)
            .email(email)
            .phone(phone)
            .idType(idType)
            .idNumber(idNumber)
            .build();
        return guestService.registerGuest(guest);
    }
    
    public Optional<Guest> findGuestByEmail(String email) {
        return guestService.findByEmail(email);
    }
    
    public Optional<Guest> getGuest(String guestId) {
        return guestService.getGuest(guestId);
    }
    
    public List<Guest> searchGuests(String name) {
        return guestService.searchByName(name);
    }
    
    // === Reservation Operations ===
    
    public Reservation makeReservation(String guestId, RoomType roomType, 
                                        LocalDate checkIn, LocalDate checkOut, 
                                        int numberOfGuests) {
        Guest guest = guestService.getGuest(guestId)
            .orElseThrow(() -> new IllegalArgumentException("Guest not found: " + guestId));
        
        Room room = roomService.findAvailableRoom(roomType, checkIn, checkOut)
            .orElseThrow(() -> new IllegalArgumentException(
                "No available room of type " + roomType + " for the requested dates"));
        
        return reservationService.createReservation(guest, room, checkIn, checkOut, numberOfGuests);
    }
    
    public Reservation makeReservation(Guest guest, Room room, LocalDate checkIn, 
                                        LocalDate checkOut, int numberOfGuests) {
        return reservationService.createReservation(guest, room, checkIn, checkOut, numberOfGuests);
    }
    
    public void confirmReservation(String reservationId) {
        reservationService.confirmReservation(reservationId);
    }
    
    public void cancelReservation(String reservationId) {
        reservationService.cancelReservation(reservationId);
    }
    
    public Optional<Reservation> getReservation(String reservationId) {
        return reservationService.getReservation(reservationId);
    }
    
    public List<Reservation> getTodayCheckIns() {
        return reservationService.getTodayCheckIns();
    }
    
    public List<Reservation> getTodayCheckOuts() {
        return reservationService.getTodayCheckOuts();
    }
    
    public List<Reservation> getActiveReservations() {
        return reservationService.getActiveReservations();
    }
    
    // === Check-In/Out Operations ===
    
    public Reservation checkIn(String reservationId) {
        return checkInOutService.checkIn(reservationId);
    }
    
    public Bill checkOut(String reservationId) {
        return checkInOutService.checkOut(reservationId);
    }
    
    // === Service Charges ===
    
    public void addServiceCharge(String reservationId, ServiceType type, 
                                  BigDecimal amount, String description) {
        reservationService.addServiceCharge(reservationId, type, amount, description);
    }
    
    // === Billing & Payment ===
    
    public Bill generateBill(String reservationId) {
        Reservation reservation = reservationService.getReservation(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));
        return billingService.generateBill(reservation);
    }
    
    public Optional<Bill> getBill(String billId) {
        return billingService.getBill(billId);
    }
    
    public Payment processPayment(String billId, BigDecimal amount, PaymentMethod method) {
        Bill bill = billingService.getBill(billId)
            .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));
        return paymentService.processPayment(bill, amount, method);
    }
    
    // === Housekeeping ===
    
    public HousekeepingTask createCleaningTask(String roomNumber, String priority) {
        Room room = roomService.getRoomByNumber(roomNumber)
            .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomNumber));
        return housekeepingService.createCleaningTask(room, priority);
    }
    
    public void completeCleaningTask(String taskId) {
        housekeepingService.completeTask(taskId);
    }
    
    public void markRoomClean(String roomId) {
        housekeepingService.markRoomClean(roomId);
    }
    
    public List<HousekeepingTask> getPendingCleaningTasks() {
        return housekeepingService.getPendingTasks();
    }
    
    // === Reporting ===
    
    public ReportService.OccupancyReport getOccupancyReport(LocalDate start, LocalDate end) {
        return reportService.generateOccupancyReport(start, end);
    }
    
    public ReportService.RevenueReport getRevenueReport(LocalDate start, LocalDate end) {
        return reportService.generateRevenueReport(start, end);
    }
    
    public ReportService.DailySummary getTodaySummary() {
        return reportService.getDailySummary(LocalDate.now());
    }
    
    public double getCurrentOccupancyRate() {
        return reportService.getCurrentOccupancyRate();
    }
    
    // === Configuration ===
    
    public void addDiscountStrategy(DiscountStrategy discount) {
        billingService.addDiscountStrategy(discount);
    }
    
    public void setTaxRate(String taxName, double rate) {
        billingService.setTaxRate(taxName, rate);
    }
    
    public void addReservationObserver(ReservationObserver observer) {
        reservationService.addObserver(observer);
    }
    
    public void addRoomObserver(RoomStatusObserver observer) {
        roomService.addObserver(observer);
    }
    
    // === Service Accessors (for advanced use) ===
    
    public RoomService getRoomService() {
        return roomService;
    }
    
    public ReservationService getReservationService() {
        return reservationService;
    }
    
    public GuestService getGuestService() {
        return guestService;
    }
    
    public BillingService getBillingService() {
        return billingService;
    }
    
    public PaymentService getPaymentService() {
        return paymentService;
    }
    
    public HousekeepingService getHousekeepingService() {
        return housekeepingService;
    }
    
    public ReportService getReportService() {
        return reportService;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return "Hotel{name='" + name + "', rooms=" + roomService.getAllRooms().size() + "}";
    }
}



