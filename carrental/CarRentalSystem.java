package carrental;

import carrental.enums.CarType;
import carrental.enums.PaymentMethod;
import carrental.models.*;
import carrental.observers.EmailNotificationObserver;
import carrental.observers.ReservationObserver;
import carrental.observers.SMSNotificationObserver;
import carrental.repositories.*;
import carrental.repositories.impl.*;
import carrental.services.*;
import carrental.services.impl.*;
import carrental.strategies.pricing.PricingStrategy;
import carrental.strategies.pricing.StandardPricingStrategy;
import carrental.strategies.search.BasicSearchStrategy;
import carrental.strategies.search.SearchStrategy;

import java.time.LocalDate;
import java.util.List;

/**
 * Main facade for the Car Rental System.
 * Provides a simplified API for all car rental operations.
 * 
 * This is the main entry point for clients interacting with the system.
 * Uses Facade Pattern to simplify complex subsystem interactions.
 */
public class CarRentalSystem {

    private final CarService carService;
    private final CustomerService customerService;
    private final ReservationServiceImpl reservationService;
    private final PaymentService paymentService;

    /**
     * Creates a new CarRentalSystem with default configurations.
     */
    public CarRentalSystem() {
        this(new StandardPricingStrategy(), new BasicSearchStrategy());
    }

    /**
     * Creates a new CarRentalSystem with custom strategies.
     * Demonstrates Dependency Injection and Strategy Pattern.
     */
    public CarRentalSystem(PricingStrategy pricingStrategy, SearchStrategy searchStrategy) {
        // Initialize repositories
        CarRepository carRepository = new InMemoryCarRepository();
        CustomerRepository customerRepository = new InMemoryCustomerRepository();
        ReservationRepository reservationRepository = new InMemoryReservationRepository();
        PaymentRepository paymentRepository = new InMemoryPaymentRepository();

        // Initialize services with dependencies
        this.carService = new CarServiceImpl(carRepository, reservationRepository, searchStrategy);
        this.customerService = new CustomerServiceImpl(customerRepository);
        this.reservationService = new ReservationServiceImpl(
            reservationRepository, carService, customerService, pricingStrategy);
        this.paymentService = new PaymentServiceImpl(paymentRepository, reservationRepository);

        // Register default observers
        reservationService.addObserver(new EmailNotificationObserver());
        reservationService.addObserver(new SMSNotificationObserver());
    }

    /**
     * Advanced constructor with full dependency injection.
     */
    public CarRentalSystem(CarService carService,
                           CustomerService customerService,
                           ReservationServiceImpl reservationService,
                           PaymentService paymentService) {
        this.carService = carService;
        this.customerService = customerService;
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    // ==================== CAR OPERATIONS ====================

    /**
     * Adds a car to the fleet.
     */
    public Car addCar(Car car) {
        return carService.addCar(car);
    }

    /**
     * Gets a car by ID.
     */
    public Car getCar(String carId) {
        return carService.getCarById(carId);
    }

    /**
     * Gets all cars in the fleet.
     */
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }

    /**
     * Gets all available cars.
     */
    public List<Car> getAvailableCars() {
        return carService.getAvailableCars();
    }

    /**
     * Searches for cars based on criteria.
     */
    public List<Car> searchCars(SearchCriteria criteria) {
        return carService.searchCars(criteria);
    }

    /**
     * Gets available cars for specific dates.
     */
    public List<Car> getAvailableCars(LocalDate startDate, LocalDate endDate) {
        return carService.getAvailableCarsForDates(startDate, endDate);
    }

    /**
     * Gets all cars of a specific type.
     */
    public List<Car> getCarsByType(CarType type) {
        return carService.getCarsByType(type);
    }

    // ==================== CUSTOMER OPERATIONS ====================

    /**
     * Registers a new customer.
     */
    public Customer registerCustomer(Customer customer) {
        return customerService.registerCustomer(customer);
    }

    /**
     * Gets a customer by ID.
     */
    public Customer getCustomer(String customerId) {
        return customerService.getCustomerById(customerId);
    }

    /**
     * Gets all customers.
     */
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    // ==================== RESERVATION OPERATIONS ====================

    /**
     * Creates a new reservation.
     */
    public Reservation makeReservation(String customerId, String carId, 
                                        LocalDate startDate, LocalDate endDate) {
        return reservationService.createReservation(customerId, carId, startDate, endDate);
    }

    /**
     * Gets a reservation by ID.
     */
    public Reservation getReservation(String reservationId) {
        return reservationService.getReservationById(reservationId);
    }

    /**
     * Gets all reservations for a customer.
     */
    public List<Reservation> getCustomerReservations(String customerId) {
        return reservationService.getReservationsByCustomer(customerId);
    }

    /**
     * Modifies a reservation's dates.
     */
    public Reservation modifyReservation(String reservationId, 
                                          LocalDate newStartDate, LocalDate newEndDate) {
        return reservationService.modifyReservation(reservationId, newStartDate, newEndDate);
    }

    /**
     * Confirms a reservation.
     */
    public Reservation confirmReservation(String reservationId) {
        return reservationService.confirmReservation(reservationId);
    }

    /**
     * Cancels a reservation.
     */
    public Reservation cancelReservation(String reservationId) {
        return reservationService.cancelReservation(reservationId);
    }

    /**
     * Starts the rental (car picked up).
     */
    public Reservation pickupCar(String reservationId) {
        return reservationService.startRental(reservationId);
    }

    /**
     * Completes the rental (car returned).
     */
    public Reservation returnCar(String reservationId) {
        return reservationService.completeReservation(reservationId);
    }

    /**
     * Gets all reservations.
     */
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    // ==================== PAYMENT OPERATIONS ====================

    /**
     * Processes payment for a reservation.
     */
    public Payment processPayment(String reservationId, PaymentMethod method) {
        return paymentService.processPayment(reservationId, method);
    }

    /**
     * Refunds a payment.
     */
    public boolean refundPayment(String paymentId) {
        return paymentService.processRefund(paymentId);
    }

    /**
     * Gets payments for a reservation.
     */
    public List<Payment> getPayments(String reservationId) {
        return paymentService.getPaymentsByReservation(reservationId);
    }

    // ==================== OBSERVER MANAGEMENT ====================

    /**
     * Adds a reservation observer.
     */
    public void addReservationObserver(ReservationObserver observer) {
        reservationService.addObserver(observer);
    }

    /**
     * Removes a reservation observer.
     */
    public void removeReservationObserver(ReservationObserver observer) {
        reservationService.removeObserver(observer);
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Checks if a car is available for given dates.
     */
    public boolean isCarAvailable(String carId, LocalDate startDate, LocalDate endDate) {
        return carService.isCarAvailable(carId, startDate, endDate);
    }

    /**
     * Prints system status summary.
     */
    public void printStatus() {
        System.out.println("\n========== CAR RENTAL SYSTEM STATUS ==========");
        System.out.println("Total Cars: " + getAllCars().size());
        System.out.println("Available Cars: " + getAvailableCars().size());
        System.out.println("Total Customers: " + getAllCustomers().size());
        System.out.println("Total Reservations: " + getAllReservations().size());
        System.out.println("==============================================\n");
    }
}



