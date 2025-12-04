package concertbooking;

import concertbooking.enums.*;
import concertbooking.factories.VenueFactory;
import concertbooking.models.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Demo application for the Concert Ticket Booking System
 */
public class Main {
    
    public static void main(String[] args) {
        // Initialize the system
        ConcertBookingSystem system = new ConcertBookingSystem("TicketMaster Pro");
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  DEMO: Concert Ticket Booking System");
        System.out.println("=".repeat(60) + "\n");
        
        // ==================== 1. Setup Venues ====================
        System.out.println("\n>>> STEP 1: Creating Venues...\n");
        
        Venue madisonSquareGarden = VenueFactory.createMediumVenue(
            "Madison Square Garden",
            "4 Pennsylvania Plaza",
            "New York"
        );
        System.out.println("Created venue: " + madisonSquareGarden);
        
        Venue stapleCenter = VenueFactory.createLargeVenue(
            "Crypto.com Arena",
            "1111 S Figueroa St",
            "Los Angeles"
        );
        System.out.println("Created venue: " + stapleCenter);
        
        // ==================== 2. Create Concerts ====================
        System.out.println("\n>>> STEP 2: Creating Concerts...\n");
        
        Concert taylorSwiftConcert = Concert.builder()
            .id("CONCERT-001")
            .name("The Eras Tour")
            .artist("Taylor Swift")
            .description("Experience all the eras of Taylor Swift's music")
            .venue(madisonSquareGarden)
            .dateTime(LocalDateTime.now().plusMonths(2))
            .durationMinutes(180)
            .basePrice(new BigDecimal("150.00"))
            .build();
        system.createConcert(taylorSwiftConcert);
        
        Concert coldplayConcert = Concert.builder()
            .id("CONCERT-002")
            .name("Music of the Spheres World Tour")
            .artist("Coldplay")
            .description("An immersive experience of light and music")
            .venue(stapleCenter)
            .dateTime(LocalDateTime.now().plusMonths(3))
            .durationMinutes(150)
            .basePrice(new BigDecimal("120.00"))
            .build();
        system.createConcert(coldplayConcert);
        
        // ==================== 3. Open Sales ====================
        System.out.println("\n>>> STEP 3: Opening Ticket Sales...\n");
        
        system.openSales("CONCERT-001");
        system.openSales("CONCERT-002");
        
        // ==================== 4. Register Users ====================
        System.out.println("\n>>> STEP 4: Registering Users...\n");
        
        User alice = system.registerUser("USER-001", "Alice Johnson", 
            "alice@email.com", "+1-555-0101");
        User bob = system.registerUser("USER-002", "Bob Smith", 
            "bob@email.com", "+1-555-0102");
        User charlie = system.registerUser("USER-003", "Charlie Brown", 
            "charlie@email.com", null);
        
        // ==================== 5. Search for Concerts ====================
        System.out.println("\n>>> STEP 5: Searching for Concerts...\n");
        
        System.out.println("Searching for 'Taylor':");
        List<Concert> taylorResults = system.searchByArtist("Taylor");
        taylorResults.forEach(c -> System.out.println("  - " + c.getName() + " by " + c.getArtist()));
        
        System.out.println("\nSearching for concerts in 'New York':");
        List<Concert> nyResults = system.searchByVenue("New York");
        nyResults.forEach(c -> System.out.println("  - " + c.getName() + " at " + c.getVenue().getName()));
        
        // ==================== 6. View Available Seats ====================
        System.out.println("\n>>> STEP 6: Viewing Available Seats...\n");
        
        Concert concert = system.getConcert("CONCERT-001").orElseThrow();
        System.out.println("Concert: " + concert.getName());
        System.out.println("Venue: " + concert.getVenue().getName());
        System.out.println("Date: " + concert.getDateTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")));
        System.out.println("Total seats: " + concert.getTotalSeats());
        System.out.println("Available seats: " + concert.getAvailableSeatsCount());
        
        System.out.println("\nSeats by section:");
        for (SectionType sectionType : SectionType.values()) {
            List<Seat> sectionSeats = system.getAvailableSeatsBySection("CONCERT-001", sectionType);
            if (!sectionSeats.isEmpty()) {
                System.out.printf("  %s: %d seats @ $%.2f each%n", 
                    sectionType.getDisplayName(), 
                    sectionSeats.size(),
                    concert.getSectionPrice(sectionType));
            }
        }
        
        // ==================== 7. Book Tickets - Alice ====================
        System.out.println("\n>>> STEP 7: Alice Books VIP Tickets...\n");
        
        // Get 2 VIP seats
        List<Seat> vipSeats = system.getAvailableSeatsBySection("CONCERT-001", SectionType.VIP);
        List<String> aliceSeatIds = vipSeats.stream()
            .limit(2)
            .map(Seat::getId)
            .toList();
        
        System.out.println("Alice selects seats: " + aliceSeatIds.size());
        
        // Initiate booking (holds seats for 15 minutes)
        Booking aliceBooking = system.initiateBooking(alice.getId(), "CONCERT-001", aliceSeatIds);
        System.out.println("Booking created: " + aliceBooking.getId());
        System.out.println("Total amount: $" + aliceBooking.getTotalAmount());
        System.out.println("Expires at: " + aliceBooking.getExpiresAt()
            .format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
        
        // Complete booking with payment
        System.out.println("\nProcessing payment...");
        ConcertBookingSystem.BookingResult result = system.completeBooking(
            aliceBooking.getId(), 
            PaymentMethod.CREDIT_CARD
        );
        
        System.out.println("\nBooking " + (result.success() ? "SUCCESSFUL" : "FAILED"));
        System.out.println(result.message());
        
        if (result.success()) {
            System.out.println("\nTickets generated: " + result.tickets().size());
            result.tickets().forEach(System.out::println);
        }
        
        // ==================== 8. Book Tickets - Bob ====================
        System.out.println("\n>>> STEP 8: Bob Books Gold Section Tickets...\n");
        
        List<Seat> goldSeats = system.getAvailableSeatsBySection("CONCERT-001", SectionType.GOLD);
        List<String> bobSeatIds = goldSeats.stream()
            .limit(4)
            .map(Seat::getId)
            .toList();
        
        Booking bobBooking = system.initiateBooking(bob.getId(), "CONCERT-001", bobSeatIds);
        ConcertBookingSystem.BookingResult bobResult = system.completeBooking(
            bobBooking.getId(), 
            PaymentMethod.UPI
        );
        
        System.out.println("Bob's booking: " + (bobResult.success() ? "SUCCESSFUL" : "FAILED"));
        System.out.println("Tickets: " + bobResult.tickets().size());
        
        // ==================== 9. Concurrent Booking Attempt ====================
        System.out.println("\n>>> STEP 9: Concurrent Booking Test...\n");
        
        // Get the same seats that Alice already booked
        try {
            system.initiateBooking(charlie.getId(), "CONCERT-001", aliceSeatIds);
            System.out.println("ERROR: This should have failed!");
        } catch (Exception e) {
            System.out.println("Expected: Charlie cannot book Alice's seats");
            System.out.println("Exception: " + e.getMessage());
        }
        
        // ==================== 10. User's Booking History ====================
        System.out.println("\n>>> STEP 10: Viewing Booking History...\n");
        
        List<Booking> aliceBookings = system.getUserBookings(alice.getId());
        System.out.println("Alice's bookings: " + aliceBookings.size());
        aliceBookings.forEach(b -> System.out.println("  - " + b));
        
        // ==================== 11. Cancel Booking ====================
        System.out.println("\n>>> STEP 11: Bob Cancels His Booking...\n");
        
        Booking cancelledBooking = system.cancelBooking(bobBooking.getId());
        System.out.println("Booking cancelled: " + cancelledBooking.getId());
        System.out.println("Status: " + cancelledBooking.getStatus());
        
        // Check seats are released
        int availableAfterCancel = system.getAvailableSeatsBySection("CONCERT-001", SectionType.GOLD).size();
        System.out.println("Gold section seats now available: " + availableAfterCancel);
        
        // ==================== 12. Waitlist Demo ====================
        System.out.println("\n>>> STEP 12: Waitlist Demo...\n");
        
        // Simulate sold out concert
        system.getConcert("CONCERT-002").ifPresent(c -> c.setStatus(ConcertStatus.SOLD_OUT));
        
        // Charlie joins waitlist
        WaitlistEntry charlieWaitlist = system.joinWaitlist(
            charlie.getId(), 
            "CONCERT-002", 
            2, 
            SectionType.PLATINUM
        );
        System.out.println("Charlie joined waitlist, position: " 
            + system.getWaitlistPosition(charlie.getId(), "CONCERT-002"));
        
        // Bob also joins
        WaitlistEntry bobWaitlist = system.joinWaitlist(
            bob.getId(), 
            "CONCERT-002", 
            3, 
            SectionType.GOLD
        );
        System.out.println("Bob joined waitlist, position: " 
            + system.getWaitlistPosition(bob.getId(), "CONCERT-002"));
        
        // Simulate seats becoming available
        System.out.println("\n[SYSTEM] Simulating 4 seats becoming available...");
        system.getWaitlistService().notifyWaitlistedUsers("CONCERT-002", 4);
        
        // ==================== 13. Summary ====================
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  DEMO COMPLETE - System Summary");
        System.out.println("=".repeat(60));
        
        System.out.println("\nConcert: " + concert.getName());
        System.out.println("  Total seats: " + concert.getTotalSeats());
        System.out.println("  Booked: " + concert.getBookedSeatsCount());
        System.out.println("  Available: " + concert.getAvailableSeatsCount());
        
        System.out.println("\nAvailable Payment Methods:");
        for (PaymentMethod method : system.getAvailablePaymentMethods()) {
            BigDecimal fee = system.getProcessingFee(method, new BigDecimal("100"));
            System.out.printf("  - %s (fee on $100: $%.2f)%n", method.getDisplayName(), fee);
        }
        
        System.out.println("\n" + "=".repeat(60) + "\n");
    }
}



