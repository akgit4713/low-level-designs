package hotelmanagement.services.impl;

import hotelmanagement.enums.ReservationStatus;
import hotelmanagement.enums.RoomStatus;
import hotelmanagement.enums.ServiceType;
import hotelmanagement.exceptions.ReservationException;
import hotelmanagement.models.Bill;
import hotelmanagement.models.Reservation;
import hotelmanagement.models.Room;
import hotelmanagement.models.ServiceCharge;
import hotelmanagement.observers.ReservationObserver;
import hotelmanagement.observers.RoomStatusObserver;
import hotelmanagement.repositories.ReservationRepository;
import hotelmanagement.repositories.RoomRepository;
import hotelmanagement.services.BillingService;
import hotelmanagement.services.CheckInOutService;
import hotelmanagement.services.GuestService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of CheckInOutService
 */
public class CheckInOutServiceImpl implements CheckInOutService {
    
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final BillingService billingService;
    private final GuestService guestService;
    private final List<ReservationObserver> reservationObservers = new CopyOnWriteArrayList<>();
    private final List<RoomStatusObserver> roomObservers = new CopyOnWriteArrayList<>();
    
    public CheckInOutServiceImpl(ReservationRepository reservationRepository,
                                  RoomRepository roomRepository,
                                  BillingService billingService,
                                  GuestService guestService) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.billingService = billingService;
        this.guestService = guestService;
    }
    
    @Override
    public Reservation checkIn(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> ReservationException.reservationNotFound(reservationId));
        
        if (!canCheckIn(reservationId)) {
            throw ReservationException.invalidStateTransition(
                reservationId, reservation.getStatus(), ReservationStatus.CHECKED_IN);
        }
        
        // Check in the guest
        reservation.checkIn();
        
        // Update room status to occupied
        Room room = reservation.getRoom();
        RoomStatus oldStatus = room.getStatus();
        room.occupy();
        roomRepository.save(room);
        
        // Save reservation
        reservationRepository.save(reservation);
        
        // Notify observers
        notifyCheckIn(reservation);
        notifyRoomStatusChange(room, oldStatus, RoomStatus.OCCUPIED);
        
        System.out.println("✅ Check-in complete for " + reservation.getGuest().getName());
        System.out.println("   Room: " + room.getRoomNumber() + " (Floor " + room.getFloor() + ")");
        
        return reservation;
    }
    
    @Override
    public Bill checkOut(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> ReservationException.reservationNotFound(reservationId));
        
        if (!canCheckOut(reservationId)) {
            throw ReservationException.notCheckedIn(reservationId);
        }
        
        // Check out the guest
        reservation.checkOut();
        
        // Mark room for cleaning
        Room room = reservation.getRoom();
        RoomStatus oldStatus = room.getStatus();
        room.markForCleaning();
        roomRepository.save(room);
        
        // Generate bill
        Bill bill = billingService.generateBill(reservation);
        
        // Update guest stay count and add loyalty points
        guestService.incrementStayCount(reservation.getGuest().getId());
        int pointsEarned = bill.getTotalAmount().intValue(); // 1 point per dollar
        guestService.addLoyaltyPoints(reservation.getGuest().getId(), pointsEarned);
        
        // Save reservation
        reservationRepository.save(reservation);
        
        // Notify observers
        notifyCheckOut(reservation);
        notifyRoomStatusChange(room, oldStatus, RoomStatus.CLEANING);
        notifyRoomNeedsCleaning(room);
        
        System.out.println("✅ Check-out complete for " + reservation.getGuest().getName());
        System.out.println("   " + reservation.getGuest().getName() + " earned " + pointsEarned + " loyalty points!");
        
        return bill;
    }
    
    @Override
    public boolean canCheckIn(String reservationId) {
        return reservationRepository.findById(reservationId)
            .map(r -> r.getStatus() == ReservationStatus.CONFIRMED &&
                      !r.getCheckInDate().isAfter(LocalDate.now()))
            .orElse(false);
    }
    
    @Override
    public boolean canCheckOut(String reservationId) {
        return reservationRepository.findById(reservationId)
            .map(r -> r.getStatus() == ReservationStatus.CHECKED_IN)
            .orElse(false);
    }
    
    @Override
    public Reservation earlyCheckIn(String reservationId, BigDecimal earlyCheckInFee) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> ReservationException.reservationNotFound(reservationId));
        
        // Add early check-in fee
        if (earlyCheckInFee != null && earlyCheckInFee.compareTo(BigDecimal.ZERO) > 0) {
            reservation.addServiceCharge(
                new ServiceCharge(ServiceType.ROOM_SERVICE, earlyCheckInFee, "Early Check-In Fee")
            );
        }
        
        return checkIn(reservationId);
    }
    
    @Override
    public Bill lateCheckOut(String reservationId, BigDecimal lateCheckOutFee) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> ReservationException.reservationNotFound(reservationId));
        
        // Add late check-out fee
        if (lateCheckOutFee != null && lateCheckOutFee.compareTo(BigDecimal.ZERO) > 0) {
            reservation.addServiceCharge(
                new ServiceCharge(ServiceType.ROOM_SERVICE, lateCheckOutFee, "Late Check-Out Fee")
            );
            reservationRepository.save(reservation);
        }
        
        return checkOut(reservationId);
    }
    
    // Observer management
    public void addReservationObserver(ReservationObserver observer) {
        reservationObservers.add(observer);
    }
    
    public void addRoomObserver(RoomStatusObserver observer) {
        roomObservers.add(observer);
    }
    
    // Notification methods
    private void notifyCheckIn(Reservation reservation) {
        for (ReservationObserver observer : reservationObservers) {
            observer.onCheckIn(reservation);
        }
    }
    
    private void notifyCheckOut(Reservation reservation) {
        for (ReservationObserver observer : reservationObservers) {
            observer.onCheckOut(reservation);
        }
    }
    
    private void notifyRoomStatusChange(Room room, RoomStatus oldStatus, RoomStatus newStatus) {
        for (RoomStatusObserver observer : roomObservers) {
            observer.onRoomStatusChanged(room, oldStatus, newStatus);
        }
    }
    
    private void notifyRoomNeedsCleaning(Room room) {
        for (RoomStatusObserver observer : roomObservers) {
            observer.onRoomNeedsCleaning(room);
        }
    }
}



