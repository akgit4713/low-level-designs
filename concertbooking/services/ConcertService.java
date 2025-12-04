package concertbooking.services;

import concertbooking.enums.ConcertStatus;
import concertbooking.enums.SectionType;
import concertbooking.models.Concert;
import concertbooking.models.Seat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing concerts
 */
public interface ConcertService {
    
    Concert createConcert(Concert concert);
    
    Optional<Concert> getConcert(String concertId);
    
    List<Concert> getAllConcerts();
    
    List<Concert> getUpcomingConcerts();
    
    List<Concert> getBookableConcerts();
    
    List<Concert> getConcertsByStatus(ConcertStatus status);
    
    List<Concert> getConcertsByArtist(String artist);
    
    List<Concert> getConcertsByCity(String city);
    
    List<Concert> getConcertsByDateRange(LocalDateTime start, LocalDateTime end);
    
    void updateConcertStatus(String concertId, ConcertStatus status);
    
    void openSales(String concertId);
    
    void closeSales(String concertId);
    
    List<Seat> getAvailableSeats(String concertId);
    
    List<Seat> getAvailableSeatsBySection(String concertId, SectionType sectionType);
    
    Optional<Seat> getSeat(String concertId, String seatId);
}



