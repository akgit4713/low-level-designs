package concertbooking.services.impl;

import concertbooking.enums.ConcertStatus;
import concertbooking.enums.SectionType;
import concertbooking.exceptions.ConcertNotFoundException;
import concertbooking.models.Concert;
import concertbooking.models.Seat;
import concertbooking.repositories.ConcertRepository;
import concertbooking.services.ConcertService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ConcertService
 */
public class ConcertServiceImpl implements ConcertService {
    
    private final ConcertRepository concertRepository;
    
    public ConcertServiceImpl(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
    }
    
    @Override
    public Concert createConcert(Concert concert) {
        return concertRepository.save(concert);
    }
    
    @Override
    public Optional<Concert> getConcert(String concertId) {
        return concertRepository.findById(concertId);
    }
    
    @Override
    public List<Concert> getAllConcerts() {
        return concertRepository.findAll();
    }
    
    @Override
    public List<Concert> getUpcomingConcerts() {
        return concertRepository.findUpcoming();
    }
    
    @Override
    public List<Concert> getBookableConcerts() {
        return concertRepository.findBookable();
    }
    
    @Override
    public List<Concert> getConcertsByStatus(ConcertStatus status) {
        return concertRepository.findByStatus(status);
    }
    
    @Override
    public List<Concert> getConcertsByArtist(String artist) {
        return concertRepository.findByArtist(artist);
    }
    
    @Override
    public List<Concert> getConcertsByCity(String city) {
        return concertRepository.findByVenueCity(city);
    }
    
    @Override
    public List<Concert> getConcertsByDateRange(LocalDateTime start, LocalDateTime end) {
        return concertRepository.findByDateRange(start, end);
    }
    
    @Override
    public void updateConcertStatus(String concertId, ConcertStatus status) {
        Concert concert = concertRepository.findById(concertId)
            .orElseThrow(() -> ConcertNotFoundException.byId(concertId));
        concert.setStatus(status);
        concertRepository.save(concert);
    }
    
    @Override
    public void openSales(String concertId) {
        Concert concert = concertRepository.findById(concertId)
            .orElseThrow(() -> ConcertNotFoundException.byId(concertId));
        concert.setStatus(ConcertStatus.ON_SALE);
        concert.setSalesStartAt(LocalDateTime.now());
        concertRepository.save(concert);
        System.out.println("[CONCERT] Sales opened for: " + concert.getName());
    }
    
    @Override
    public void closeSales(String concertId) {
        Concert concert = concertRepository.findById(concertId)
            .orElseThrow(() -> ConcertNotFoundException.byId(concertId));
        concert.setSalesEndAt(LocalDateTime.now());
        concertRepository.save(concert);
        System.out.println("[CONCERT] Sales closed for: " + concert.getName());
    }
    
    @Override
    public List<Seat> getAvailableSeats(String concertId) {
        Concert concert = concertRepository.findById(concertId)
            .orElseThrow(() -> ConcertNotFoundException.byId(concertId));
        return concert.getAvailableSeats();
    }
    
    @Override
    public List<Seat> getAvailableSeatsBySection(String concertId, SectionType sectionType) {
        Concert concert = concertRepository.findById(concertId)
            .orElseThrow(() -> ConcertNotFoundException.byId(concertId));
        return concert.getAvailableSeatsBySection(sectionType);
    }
    
    @Override
    public Optional<Seat> getSeat(String concertId, String seatId) {
        return concertRepository.findById(concertId)
            .flatMap(concert -> concert.getSeat(seatId));
    }
}



