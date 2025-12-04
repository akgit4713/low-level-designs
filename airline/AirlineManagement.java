package airline;

import airline.enums.*;
import airline.factories.TicketFactory;
import airline.models.*;
import airline.observers.*;
import airline.repositories.impl.*;
import airline.services.*;
import airline.services.impl.*;
import airline.strategies.payment.*;
import airline.strategies.pricing.*;
import airline.strategies.refund.*;
import airline.strategies.search.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Facade class that provides a simplified interface to the airline management system.
 * Wires all components together and provides high-level operations.
 */
public class AirlineManagement {
    
    private final String airlineName;
    
    // Repositories
    private final InMemoryFlightRepository flightRepository;
    private final InMemoryBookingRepository bookingRepository;
    private final InMemoryAircraftRepository aircraftRepository;
    private final InMemoryCrewRepository crewRepository;
    private final InMemoryPassengerRepository passengerRepository;
    
    // Services
    private final FlightService flightService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final SeatService seatService;
    private final PassengerService passengerService;
    private final AircraftService aircraftService;
    private final CrewService crewService;
    
    // Strategies
    private final PricingStrategy pricingStrategy;
    
    // Factories
    private final TicketFactory ticketFactory;

    public AirlineManagement(String airlineName) {
        this(airlineName, new DynamicPricingStrategy());
    }

    public AirlineManagement(String airlineName, PricingStrategy pricingStrategy) {
        this.airlineName = airlineName;
        this.pricingStrategy = pricingStrategy;
        
        // Initialize repositories
        this.flightRepository = new InMemoryFlightRepository();
        this.bookingRepository = new InMemoryBookingRepository();
        this.aircraftRepository = new InMemoryAircraftRepository();
        this.crewRepository = new InMemoryCrewRepository();
        this.passengerRepository = new InMemoryPassengerRepository();
        
        // Initialize services with dependency injection
        this.flightService = new FlightServiceImpl(flightRepository, pricingStrategy);
        this.bookingService = new BookingServiceImpl(bookingRepository, pricingStrategy);
        PaymentServiceImpl paymentServiceImpl = new PaymentServiceImpl(pricingStrategy);
        this.paymentService = paymentServiceImpl;
        this.seatService = new SeatServiceImpl();
        this.passengerService = new PassengerServiceImpl(passengerRepository);
        this.aircraftService = new AircraftServiceImpl(aircraftRepository);
        this.crewService = new CrewServiceImpl(crewRepository);
        
        // Initialize factories
        this.ticketFactory = new TicketFactory(pricingStrategy);
        
        // Set up default payment strategies
        setupPaymentStrategies(paymentServiceImpl);
        
        // Set up default refund strategy
        paymentServiceImpl.setRefundStrategy(new TimeBasedRefundStrategy());
        
        // Set up default observers
        setupObservers();
    }

    private void setupPaymentStrategies(PaymentServiceImpl paymentService) {
        paymentService.registerPaymentStrategy(PaymentMethod.CREDIT_CARD, new CreditCardPaymentStrategy());
        paymentService.registerPaymentStrategy(PaymentMethod.DEBIT_CARD, new DebitCardPaymentStrategy());
        paymentService.registerPaymentStrategy(PaymentMethod.WALLET, new WalletPaymentStrategy());
        paymentService.registerPaymentStrategy(PaymentMethod.NET_BANKING, new NetBankingPaymentStrategy());
    }

    private void setupObservers() {
        EmailNotificationObserver emailObserver = new EmailNotificationObserver();
        SMSNotificationObserver smsObserver = new SMSNotificationObserver();
        
        flightService.addObserver(emailObserver);
        flightService.addObserver(smsObserver);
        bookingService.addObserver(emailObserver);
        bookingService.addObserver(smsObserver);
    }

    // === Airport Operations ===
    
    public Airport createAirport(String code, String name, String city, String country, String timezone) {
        return new Airport(code, name, city, country, timezone);
    }

    // === Aircraft Operations ===
    
    public Aircraft addAircraft(String registrationNumber, String model, String manufacturer,
                                 int economySeats, int businessSeats, int firstClassSeats) {
        Aircraft aircraft = Aircraft.builder()
                .id("AC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .registrationNumber(registrationNumber)
                .model(model)
                .manufacturer(manufacturer)
                .addSeats(SeatClass.ECONOMY, economySeats)
                .addSeats(SeatClass.BUSINESS, businessSeats)
                .addSeats(SeatClass.FIRST, firstClassSeats)
                .build();
        
        return aircraftService.addAircraft(aircraft);
    }

    public List<Aircraft> getAvailableAircraft() {
        return aircraftService.getAvailableAircraft();
    }

    // === Crew Operations ===
    
    public Crew addCrewMember(String firstName, String lastName, CrewRole role, String... certifications) {
        Crew.Builder builder = Crew.builder()
                .id("CRW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .employeeId("EMP-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .firstName(firstName)
                .lastName(lastName)
                .role(role);
        
        for (String cert : certifications) {
            builder.addCertification(cert);
        }
        
        return crewService.addCrewMember(builder.build());
    }

    public void assignCrewToFlight(String crewId, Flight flight) {
        crewService.assignToFlight(crewId, flight);
    }

    public boolean validateCrewAssignment(Flight flight) {
        return crewService.validateCrewAssignment(flight);
    }

    // === Flight Operations ===
    
    public Flight addFlight(String flightNumber, Airport source, Airport destination,
                            LocalDateTime departure, LocalDateTime arrival, Aircraft aircraft,
                            BigDecimal economyPrice) {
        Flight flight = Flight.builder()
                .flightNumber(flightNumber)
                .source(source)
                .destination(destination)
                .departureTime(departure)
                .arrivalTime(arrival)
                .aircraft(aircraft)
                .basePrice(SeatClass.ECONOMY, economyPrice)
                .basePrice(SeatClass.PREMIUM_ECONOMY, economyPrice.multiply(new BigDecimal("1.5")))
                .basePrice(SeatClass.BUSINESS, economyPrice.multiply(new BigDecimal("2.5")))
                .basePrice(SeatClass.FIRST, economyPrice.multiply(new BigDecimal("4.0")))
                .build();
        
        return flightService.addFlight(flight);
    }

    public Optional<Flight> getFlight(String flightNumber) {
        return flightService.getFlight(flightNumber);
    }

    public List<FlightSearchResult> searchFlights(Airport source, Airport destination, LocalDate date) {
        return flightService.searchFlights(source, destination, date);
    }

    public void setSearchStrategy(FlightSearchStrategy strategy) {
        ((FlightServiceImpl) flightService).setSearchStrategy(strategy);
    }

    public void updateFlightStatus(String flightNumber, FlightStatus status) {
        flightService.updateFlightStatus(flightNumber, status);
    }

    public void delayFlight(String flightNumber, String reason) {
        flightService.delayFlight(flightNumber, reason);
    }

    public void cancelFlight(String flightNumber, String reason) {
        flightService.cancelFlight(flightNumber, reason);
    }

    // === Passenger Operations ===
    
    public Passenger registerPassenger(String firstName, String lastName, String email, 
                                        String phone, LocalDate dob, String passportNumber, 
                                        String nationality) {
        Passenger passenger = Passenger.builder()
                .id("PAX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .dateOfBirth(dob)
                .passportNumber(passportNumber)
                .nationality(nationality)
                .build();
        
        return passengerService.registerPassenger(passenger);
    }

    public Optional<Passenger> getPassenger(String passengerId) {
        return passengerService.getPassenger(passengerId);
    }

    public void addBaggage(String passengerId, BaggageType type, double weight) {
        Baggage baggage = new Baggage(type, weight);
        passengerService.addBaggage(passengerId, baggage);
    }

    // === Booking Operations ===
    
    public Booking createBooking(Flight flight, List<BookingService.PassengerSeatSelection> passengers) {
        return bookingService.createBooking(flight, passengers);
    }

    public Booking createBooking(Flight flight, Passenger passenger, String seatNumber) {
        return bookingService.createBooking(flight, 
                List.of(new BookingService.PassengerSeatSelection(passenger, seatNumber)));
    }

    public Optional<Booking> getBooking(String bookingId) {
        return bookingService.getBooking(bookingId);
    }

    public Optional<Booking> getBookingByPnr(String pnr) {
        return bookingService.getBookingByPnr(pnr);
    }

    public void confirmBooking(String bookingId, PaymentMethod paymentMethod) {
        Booking booking = bookingService.getBooking(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        
        // Process payment
        Payment payment = paymentService.processPayment(booking, paymentMethod);
        
        if (payment.isSuccessful()) {
            bookingService.confirmBooking(bookingId);
        }
    }

    public void cancelBooking(String bookingId, String reason) {
        bookingService.cancelBooking(bookingId, reason);
    }

    public Payment processRefund(String bookingId) {
        Booking booking = bookingService.getBooking(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        
        return paymentService.processRefund(booking);
    }

    // === Ticket Operations ===
    
    public List<Ticket> issueTickets(Booking booking) {
        return ticketFactory.createTickets(booking);
    }

    // === Seat Operations ===
    
    public List<Seat> getAvailableSeats(Flight flight) {
        return seatService.getAvailableSeats(flight);
    }

    public List<Seat> getAvailableSeats(Flight flight, SeatClass seatClass) {
        return seatService.getAvailableSeats(flight, seatClass);
    }

    public String getSeatMap(Flight flight) {
        return seatService.generateSeatMap(flight);
    }

    // === Configuration ===
    
    public void addFlightObserver(FlightObserver observer) {
        flightService.addObserver(observer);
    }

    public void addBookingObserver(BookingObserver observer) {
        bookingService.addObserver(observer);
    }

    // === Getters for Services ===
    
    public FlightService getFlightService() {
        return flightService;
    }

    public BookingService getBookingService() {
        return bookingService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public SeatService getSeatService() {
        return seatService;
    }

    public PassengerService getPassengerService() {
        return passengerService;
    }

    public AircraftService getAircraftService() {
        return aircraftService;
    }

    public CrewService getCrewService() {
        return crewService;
    }

    public String getAirlineName() {
        return airlineName;
    }

    @Override
    public String toString() {
        return "AirlineManagement{name='" + airlineName + "'}";
    }
}



