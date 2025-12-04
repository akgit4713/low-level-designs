package restaurant.services.impl;

import restaurant.exceptions.ReservationException;
import restaurant.models.Reservation;
import restaurant.models.Table;
import restaurant.repositories.impl.InMemoryReservationRepository;
import restaurant.repositories.impl.InMemoryTableRepository;
import restaurant.services.ReservationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of ReservationService
 */
public class ReservationServiceImpl implements ReservationService {
    
    private final InMemoryReservationRepository reservationRepository;
    private final InMemoryTableRepository tableRepository;
    
    public ReservationServiceImpl(InMemoryReservationRepository reservationRepository,
                                  InMemoryTableRepository tableRepository) {
        this.reservationRepository = reservationRepository;
        this.tableRepository = tableRepository;
    }
    
    @Override
    public synchronized Reservation makeReservation(String customerName, String customerPhone,
                                                     Table table, LocalDateTime dateTime,
                                                     int partySize, String specialRequests) {
        // Validate table can accommodate party
        if (!table.canAccommodate(partySize)) {
            throw ReservationException.invalidPartySize(partySize, table.getCapacity());
        }
        
        // Check if table is available at requested time
        int defaultDuration = 90; // minutes
        if (!isTableAvailable(table, dateTime, defaultDuration)) {
            throw ReservationException.tableNotAvailable(table.getId(), dateTime.toString());
        }
        
        // Create reservation
        String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8);
        Reservation reservation = new Reservation(
            reservationId, customerName, customerPhone,
            table, dateTime, partySize, defaultDuration, specialRequests
        );
        
        // Reserve the table
        table.tryReserve();
        
        return reservationRepository.save(reservation);
    }
    
    @Override
    public Optional<Reservation> getReservation(String reservationId) {
        return reservationRepository.findById(reservationId);
    }
    
    @Override
    public void cancelReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> ReservationException.reservationNotFound(reservationId));
        
        reservation.cancel();
    }
    
    @Override
    public void checkInReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> ReservationException.reservationNotFound(reservationId));
        
        reservation.checkIn();
    }
    
    @Override
    public List<Table> findAvailableTables(LocalDateTime dateTime, int partySize, int durationMinutes) {
        return tableRepository.findAvailableForPartySize(partySize).stream()
            .filter(table -> isTableAvailable(table, dateTime, durationMinutes))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Reservation> getReservationsForDate(LocalDate date) {
        return reservationRepository.findByDate(date);
    }
    
    @Override
    public List<Reservation> getUpcomingReservations(int hoursAhead) {
        return reservationRepository.findUpcoming(hoursAhead);
    }
    
    @Override
    public boolean isTableAvailable(Table table, LocalDateTime dateTime, int durationMinutes) {
        LocalDateTime endTime = dateTime.plusMinutes(durationMinutes);
        List<Reservation> overlapping = reservationRepository.findOverlapping(
            table.getId(), dateTime, endTime
        );
        return overlapping.isEmpty();
    }
}

