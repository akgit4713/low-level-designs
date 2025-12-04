package concertbooking;

import concertbooking.enums.*;
import concertbooking.models.*;
import concertbooking.observers.*;
import concertbooking.repositories.*;
import concertbooking.repositories.impl.*;
import concertbooking.services.*;
import concertbooking.services.impl.*;
import concertbooking.strategies.notification.*;
import concertbooking.strategies.payment.*;
import concertbooking.strategies.pricing.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Facade class for the Concert Ticket Booking System
 * Provides a simplified interface to all subsystems
 */
public class ConcertBookingSystem {
    
    private final String systemName;
    
    // Repositories
    private final ConcertRepository concertRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final WaitlistRepository waitlistRepository;
    
    // Services
    private final ConcertService concertService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final WaitlistService waitlistService;
    private final SearchService searchService;
    
    // Observers
    private final NotificationObserver notificationObserver;
    private final WaitlistNotificationObserver waitlistNotificationObserver;
    
    public ConcertBookingSystem(String systemName) {
        this.systemName = systemName;
        
        // Initialize repositories
        this.concertRepository = new InMemoryConcertRepository();
        this.bookingRepository = new InMemoryBookingRepository();
        this.userRepository = new InMemoryUserRepository();
        this.waitlistRepository = new InMemoryWaitlistRepository();
        
        // Initialize pricing strategy (can be swapped)
        PricingStrategy pricingStrategy = new StandardPricingStrategy();
        
        // Initialize services
        this.concertService = new ConcertServiceImpl(concertRepository);
        this.bookingService = new BookingServiceImpl(
            bookingRepository, concertRepository, userRepository, pricingStrategy
        );
        this.paymentService = new PaymentServiceImpl();
        this.notificationService = new NotificationServiceImpl();
        this.waitlistService = new WaitlistServiceImpl(waitlistRepository, concertRepository);
        this.searchService = new SearchServiceImpl(concertRepository);
        
        // Initialize observers
        this.notificationObserver = new NotificationObserver();
        this.waitlistNotificationObserver = new WaitlistNotificationObserver();
        
        // Wire up observers
        bookingService.addObserver(notificationObserver);
        waitlistService.addObserver(waitlistNotificationObserver);
        
        // Setup default payment strategies
        setupPaymentStrategies();
        
        // Setup default notification strategies
        setupNotificationStrategies();
        
        System.out.println("=".repeat(60));
        System.out.println("  " + systemName + " - Concert Ticket Booking System");
        System.out.println("=".repeat(60));
    }
    
    private void setupPaymentStrategies() {
        paymentService.registerPaymentStrategy(PaymentMethod.CREDIT_CARD, 
            new CardPaymentStrategy(PaymentMethod.CREDIT_CARD));
        paymentService.registerPaymentStrategy(PaymentMethod.DEBIT_CARD, 
            new CardPaymentStrategy(PaymentMethod.DEBIT_CARD));
        paymentService.registerPaymentStrategy(PaymentMethod.UPI, 
            new UPIPaymentStrategy());
        paymentService.registerPaymentStrategy(PaymentMethod.NET_BANKING, 
            new NetBankingPaymentStrategy());
        paymentService.registerPaymentStrategy(PaymentMethod.WALLET, 
            new WalletPaymentStrategy());
    }
    
    private void setupNotificationStrategies() {
        notificationService.registerNotificationStrategy(NotificationType.EMAIL, 
            new EmailNotificationStrategy());
        notificationService.registerNotificationStrategy(NotificationType.SMS, 
            new SMSNotificationStrategy());
    }
    
    // ==================== User Operations ====================
    
    public User registerUser(String id, String name, String email, String phone) {
        User user = new User(id, name, email, phone);
        userRepository.save(user);
        System.out.println("[USER] Registered: " + user.getName());
        return user;
    }
    
    public Optional<User> getUser(String userId) {
        return userRepository.findById(userId);
    }
    
    // ==================== Concert Operations ====================
    
    public Concert createConcert(Concert concert) {
        Concert saved = concertService.createConcert(concert);
        System.out.println("[CONCERT] Created: " + concert.getName() + " by " + concert.getArtist());
        return saved;
    }
    
    public void openSales(String concertId) {
        concertService.openSales(concertId);
    }
    
    public Optional<Concert> getConcert(String concertId) {
        return concertService.getConcert(concertId);
    }
    
    public List<Concert> getUpcomingConcerts() {
        return concertService.getUpcomingConcerts();
    }
    
    public List<Concert> getBookableConcerts() {
        return concertService.getBookableConcerts();
    }
    
    public List<Seat> getAvailableSeats(String concertId) {
        return concertService.getAvailableSeats(concertId);
    }
    
    public List<Seat> getAvailableSeatsBySection(String concertId, SectionType sectionType) {
        return concertService.getAvailableSeatsBySection(concertId, sectionType);
    }
    
    // ==================== Search Operations ====================
    
    public List<Concert> searchByArtist(String artist) {
        return searchService.searchByArtist(artist);
    }
    
    public List<Concert> searchByVenue(String venueOrCity) {
        return searchService.searchByVenue(venueOrCity);
    }
    
    public List<Concert> searchByDate(String date) {
        return searchService.searchByDate(date);
    }
    
    public List<Concert> search(String artist, String venue, String date) {
        return searchService.searchWithAnyCriteria(artist, venue, date);
    }
    
    // ==================== Booking Operations ====================
    
    /**
     * Initiate a booking by selecting seats
     * This holds the seats for a limited time (15 minutes by default)
     */
    public Booking initiateBooking(String userId, String concertId, List<String> seatIds) {
        return bookingService.initiateBooking(userId, concertId, seatIds);
    }
    
    /**
     * Complete booking by processing payment
     */
    public BookingResult completeBooking(String bookingId, PaymentMethod paymentMethod) {
        Booking booking = bookingService.getBooking(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        
        // Process payment
        Payment payment = paymentService.processPayment(
            bookingId, 
            booking.getUserId(), 
            booking.getTotalAmount(), 
            paymentMethod
        );
        
        if (!payment.isSuccessful()) {
            return new BookingResult(booking, payment, List.of(), false, 
                "Payment failed: " + payment.getFailureReason());
        }
        
        // Confirm booking
        Booking confirmedBooking = bookingService.confirmBooking(bookingId, payment.getId());
        
        // Generate tickets
        List<Ticket> tickets = bookingService.generateTickets(bookingId);
        
        // Send confirmation
        User user = userRepository.findById(booking.getUserId())
            .orElseThrow(() -> new IllegalStateException("User not found"));
        notificationService.sendBookingConfirmation(user, bookingId, tickets);
        
        return new BookingResult(confirmedBooking, payment, tickets, true, "Booking successful!");
    }
    
    public Booking cancelBooking(String bookingId) {
        Booking booking = bookingService.cancelBooking(bookingId);
        
        // If booking was confirmed, check for waitlist notifications
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            int releasedSeats = booking.getSeatCount();
            waitlistService.notifyWaitlistedUsers(booking.getConcertId(), releasedSeats);
            
            // Send cancellation notification
            userRepository.findById(booking.getUserId()).ifPresent(user -> 
                notificationService.sendBookingCancellation(user, bookingId));
        }
        
        return booking;
    }
    
    public Optional<Booking> getBooking(String bookingId) {
        return bookingService.getBooking(bookingId);
    }
    
    public List<Booking> getUserBookings(String userId) {
        return bookingService.getUserBookings(userId);
    }
    
    public List<Ticket> getTickets(String bookingId) {
        return bookingService.generateTickets(bookingId);
    }
    
    // ==================== Waitlist Operations ====================
    
    public WaitlistEntry joinWaitlist(String userId, String concertId, 
                                       int requestedSeats, SectionType preferredSection) {
        return waitlistService.joinWaitlist(userId, concertId, requestedSeats, preferredSection);
    }
    
    public void leaveWaitlist(String userId, String concertId) {
        waitlistService.leaveWaitlist(userId, concertId);
    }
    
    public int getWaitlistPosition(String userId, String concertId) {
        return waitlistService.getWaitlistPosition(userId, concertId);
    }
    
    // ==================== Payment Operations ====================
    
    public boolean processRefund(String paymentId) {
        return paymentService.processRefund(paymentId);
    }
    
    public PaymentMethod[] getAvailablePaymentMethods() {
        return paymentService.getAvailablePaymentMethods();
    }
    
    public BigDecimal getProcessingFee(PaymentMethod method, BigDecimal amount) {
        return paymentService.getProcessingFee(method, amount);
    }
    
    // ==================== Maintenance Operations ====================
    
    public void cleanupExpiredBookings() {
        bookingService.cleanupExpiredBookings();
    }
    
    // ==================== Service Accessors (for advanced use) ====================
    
    public ConcertService getConcertService() { return concertService; }
    public BookingService getBookingService() { return bookingService; }
    public PaymentService getPaymentService() { return paymentService; }
    public NotificationService getNotificationService() { return notificationService; }
    public WaitlistService getWaitlistService() { return waitlistService; }
    public SearchService getSearchService() { return searchService; }
    
    public String getSystemName() { return systemName; }
    
    // ==================== Inner Classes ====================
    
    /**
     * Result of a complete booking operation
     */
    public record BookingResult(
        Booking booking,
        Payment payment,
        List<Ticket> tickets,
        boolean success,
        String message
    ) {}
}



