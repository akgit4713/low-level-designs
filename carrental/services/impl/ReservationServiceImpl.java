package carrental.services.impl;

import carrental.enums.CarStatus;
import carrental.enums.ReservationStatus;
import carrental.exceptions.CarNotAvailableException;
import carrental.exceptions.InvalidDateRangeException;
import carrental.exceptions.ReservationException;
import carrental.exceptions.ReservationNotFoundException;
import carrental.models.Car;
import carrental.models.Customer;
import carrental.models.Reservation;
import carrental.observers.ReservationObserver;
import carrental.repositories.ReservationRepository;
import carrental.services.CarService;
import carrental.services.CustomerService;
import carrental.services.ReservationService;
import carrental.strategies.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of ReservationService.
 * Handles concurrent reservation creation with locking.
 * Uses Observer pattern for notifications.
 */
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarService carService;
    private final CustomerService customerService;
    private final PricingStrategy pricingStrategy;
    private final List<ReservationObserver> observers = new ArrayList<>();
    
    // Lock for handling concurrent reservations
    private final Lock reservationLock = new ReentrantLock();

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  CarService carService,
                                  CustomerService customerService,
                                  PricingStrategy pricingStrategy) {
        this.reservationRepository = reservationRepository;
        this.carService = carService;
        this.customerService = customerService;
        this.pricingStrategy = pricingStrategy;
    }

    /**
     * Registers an observer for reservation events.
     */
    public void addObserver(ReservationObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer.
     */
    public void removeObserver(ReservationObserver observer) {
        observers.remove(observer);
    }

    @Override
    public Reservation createReservation(String customerId, String carId, 
                                         LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        
        // Validate customer can rent
        if (!customerService.canCustomerRent(customerId)) {
            throw new ReservationException("Customer is not eligible to rent. Please check driver's license validity.");
        }
        
        Customer customer = customerService.getCustomerById(customerId);
        Car car = carService.getCarById(carId);
        
        // Use lock to prevent double booking
        reservationLock.lock();
        try {
            // Check car availability within lock
            if (!carService.isCarAvailable(carId, startDate, endDate)) {
                throw new CarNotAvailableException(carId, startDate, endDate);
            }
            
            // Calculate price
            BigDecimal totalAmount = pricingStrategy.calculatePrice(car, startDate, endDate);
            
            // Create reservation
            Reservation reservation = new Reservation.Builder()
                .id(UUID.randomUUID().toString())
                .car(car)
                .customer(customer)
                .startDate(startDate)
                .endDate(endDate)
                .totalAmount(totalAmount)
                .status(ReservationStatus.PENDING)
                .build();
            
            reservationRepository.save(reservation);
            
            // Notify observers
            notifyObservers(observer -> observer.onReservationCreated(reservation));
            
            return reservation;
        } finally {
            reservationLock.unlock();
        }
    }

    @Override
    public Reservation getReservationById(String reservationId) {
        return reservationRepository.findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId));
    }

    @Override
    public List<Reservation> getReservationsByCustomer(String customerId) {
        return reservationRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Reservation> getReservationsByCar(String carId) {
        return reservationRepository.findByCarId(carId);
    }

    @Override
    public Reservation modifyReservation(String reservationId, LocalDate newStartDate, LocalDate newEndDate) {
        validateDateRange(newStartDate, newEndDate);
        
        Reservation reservation = getReservationById(reservationId);
        
        if (!reservation.isModifiable()) {
            throw new ReservationException("Reservation cannot be modified in current status: " + reservation.getStatus());
        }
        
        reservationLock.lock();
        try {
            // Check if new dates are available (excluding current reservation)
            List<Reservation> conflicting = reservationRepository
                .findActiveByCarIdAndDateRange(reservation.getCar().getId(), newStartDate, newEndDate);
            
            boolean hasConflict = conflicting.stream()
                .anyMatch(r -> !r.getId().equals(reservationId));
            
            if (hasConflict) {
                throw new CarNotAvailableException(reservation.getCar().getId(), newStartDate, newEndDate);
            }
            
            // Update dates
            reservation.updateDates(newStartDate, newEndDate);
            
            // Recalculate price
            BigDecimal newPrice = pricingStrategy.calculatePrice(reservation);
            reservation.setTotalAmount(newPrice);
            
            reservationRepository.save(reservation);
            
            // Notify observers
            notifyObservers(observer -> observer.onReservationModified(reservation));
            
            return reservation;
        } finally {
            reservationLock.unlock();
        }
    }

    @Override
    public Reservation confirmReservation(String reservationId) {
        Reservation reservation = getReservationById(reservationId);
        
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ReservationException("Can only confirm pending reservations");
        }
        
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
        
        // Notify observers
        notifyObservers(observer -> observer.onReservationConfirmed(reservation));
        
        return reservation;
    }

    @Override
    public Reservation cancelReservation(String reservationId) {
        Reservation reservation = getReservationById(reservationId);
        
        if (!reservation.isCancellable()) {
            throw new ReservationException("Reservation cannot be cancelled in current status: " + reservation.getStatus());
        }
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        
        // Notify observers
        notifyObservers(observer -> observer.onReservationCancelled(reservation));
        
        return reservation;
    }

    @Override
    public Reservation startRental(String reservationId) {
        Reservation reservation = getReservationById(reservationId);
        
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new ReservationException("Can only start rental for confirmed reservations");
        }
        
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.getCar().setStatus(CarStatus.RENTED);
        reservationRepository.save(reservation);
        
        return reservation;
    }

    @Override
    public Reservation completeReservation(String reservationId) {
        Reservation reservation = getReservationById(reservationId);
        
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new ReservationException("Can only complete active reservations");
        }
        
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.getCar().setStatus(CarStatus.AVAILABLE);
        reservationRepository.save(reservation);
        
        // Notify observers
        notifyObservers(observer -> observer.onReservationCompleted(reservation));
        
        return reservation;
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new InvalidDateRangeException("Start date and end date are required");
        }
        if (startDate.isBefore(LocalDate.now())) {
            throw new InvalidDateRangeException("Start date cannot be in the past");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException(startDate, endDate);
        }
    }

    private void notifyObservers(java.util.function.Consumer<ReservationObserver> action) {
        observers.forEach(action);
    }
}



