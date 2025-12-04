package concertbooking.factories;

import concertbooking.enums.SectionType;
import concertbooking.models.Seat;
import concertbooking.models.Section;
import concertbooking.models.Venue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Factory for creating venues with sections and seats
 */
public class VenueFactory {
    
    /**
     * Create a section with seats
     */
    public static Section createSection(String name, SectionType type, int rows, int seatsPerRow) {
        String sectionId = "SEC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= rows; row++) {
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                String seatId = String.format("SEAT-%s-%d-%d", sectionId, row, seatNum);
                Seat seat = new Seat(seatId, sectionId, type, row, seatNum);
                seats.add(seat);
            }
        }
        
        return Section.builder()
            .id(sectionId)
            .name(name)
            .type(type)
            .rows(rows)
            .seatsPerRow(seatsPerRow)
            .seats(seats)
            .build();
    }
    
    /**
     * Create a small venue (theater-style, ~500 seats)
     */
    public static Venue createSmallVenue(String name, String address, String city) {
        String venueId = "VEN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        return Venue.builder()
            .id(venueId)
            .name(name)
            .address(address)
            .city(city)
            .addSection(createSection("VIP Front", SectionType.VIP, 3, 20))
            .addSection(createSection("Premium Center", SectionType.PREMIUM, 5, 25))
            .addSection(createSection("Standard", SectionType.GENERAL, 10, 30))
            .build();
    }
    
    /**
     * Create a medium venue (arena-style, ~5000 seats)
     */
    public static Venue createMediumVenue(String name, String address, String city) {
        String venueId = "VEN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        return Venue.builder()
            .id(venueId)
            .name(name)
            .address(address)
            .city(city)
            .addSection(createSection("VIP Floor", SectionType.VIP, 5, 30))
            .addSection(createSection("Platinum", SectionType.PLATINUM, 10, 40))
            .addSection(createSection("Gold Lower", SectionType.GOLD, 15, 50))
            .addSection(createSection("Silver Upper", SectionType.SILVER, 20, 50))
            .addSection(createSection("Balcony", SectionType.BALCONY, 10, 60))
            .build();
    }
    
    /**
     * Create a large venue (stadium-style, ~20000 seats)
     */
    public static Venue createLargeVenue(String name, String address, String city) {
        String venueId = "VEN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        return Venue.builder()
            .id(venueId)
            .name(name)
            .address(address)
            .city(city)
            .addSection(createSection("VIP Pit", SectionType.VIP, 10, 50))
            .addSection(createSection("Platinum Floor", SectionType.PLATINUM, 20, 60))
            .addSection(createSection("Gold Lower Bowl", SectionType.GOLD, 30, 80))
            .addSection(createSection("Silver Upper Bowl", SectionType.SILVER, 40, 100))
            .addSection(createSection("General Admission", SectionType.GENERAL, 50, 100))
            .build();
    }
    
    /**
     * Create a custom venue with specified sections
     */
    public static Venue createCustomVenue(String name, String address, String city, 
                                          List<Section> sections) {
        String venueId = "VEN-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        Venue.Builder builder = Venue.builder()
            .id(venueId)
            .name(name)
            .address(address)
            .city(city);
        
        sections.forEach(builder::addSection);
        
        return builder.build();
    }
}



