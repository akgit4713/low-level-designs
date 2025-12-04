package airline;

import airline.enums.*;
import airline.models.*;
import airline.services.BookingService;
import airline.strategies.search.CheapestFlightStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Demo class showcasing the Airline Management System functionality.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          AIRLINE MANAGEMENT SYSTEM - DEMO                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        // Initialize the airline management system
        AirlineManagement airline = new AirlineManagement("SkyHigh Airways");
        System.out.println("✓ Initialized: " + airline.getAirlineName() + "\n");

        // === Step 1: Create Airports ===
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 1: Creating Airports");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        Airport jfk = airline.createAirport("JFK", "John F. Kennedy International", 
                "New York", "USA", "America/New_York");
        Airport lax = airline.createAirport("LAX", "Los Angeles International", 
                "Los Angeles", "USA", "America/Los_Angeles");
        Airport ord = airline.createAirport("ORD", "O'Hare International", 
                "Chicago", "USA", "America/Chicago");
        
        System.out.println("  ✓ Created: " + jfk);
        System.out.println("  ✓ Created: " + lax);
        System.out.println("  ✓ Created: " + ord);

        // === Step 2: Add Aircraft ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 2: Adding Aircraft to Fleet");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        Aircraft boeing737 = airline.addAircraft("N12345", "737-800", "Boeing", 
                120, 24, 12);
        Aircraft airbusA320 = airline.addAircraft("N67890", "A320neo", "Airbus", 
                140, 20, 8);
        
        System.out.println("  ✓ Added: " + boeing737);
        System.out.println("  ✓ Added: " + airbusA320);

        // === Step 3: Add Crew Members ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 3: Adding Crew Members");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        Crew pilot1 = airline.addCrewMember("John", "Smith", CrewRole.PILOT, "737-800", "A320neo");
        Crew coPilot1 = airline.addCrewMember("Jane", "Doe", CrewRole.CO_PILOT, "737-800", "A320neo");
        Crew purser1 = airline.addCrewMember("Mary", "Johnson", CrewRole.PURSER);
        Crew attendant1 = airline.addCrewMember("Tom", "Wilson", CrewRole.FLIGHT_ATTENDANT);
        Crew attendant2 = airline.addCrewMember("Sarah", "Brown", CrewRole.FLIGHT_ATTENDANT);
        
        System.out.println("  ✓ Added: " + pilot1);
        System.out.println("  ✓ Added: " + coPilot1);
        System.out.println("  ✓ Added: " + purser1);
        System.out.println("  ✓ Added: " + attendant1);
        System.out.println("  ✓ Added: " + attendant2);

        // === Step 4: Create Flights ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 4: Creating Flights");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0);
        
        Flight flight1 = airline.addFlight("SH101", jfk, lax, 
                tomorrow, 
                tomorrow.plusHours(5).plusMinutes(30),
                boeing737,
                new BigDecimal("299.99"));
        
        Flight flight2 = airline.addFlight("SH102", lax, ord,
                tomorrow.plusHours(2),
                tomorrow.plusHours(6),
                airbusA320,
                new BigDecimal("199.99"));
        
        System.out.println("  ✓ Created: " + flight1);
        System.out.println("  ✓ Created: " + flight2);

        // === Step 5: Assign Crew to Flight ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 5: Assigning Crew to Flight SH101");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        airline.assignCrewToFlight(pilot1.getId(), flight1);
        airline.assignCrewToFlight(coPilot1.getId(), flight1);
        airline.assignCrewToFlight(purser1.getId(), flight1);
        airline.assignCrewToFlight(attendant1.getId(), flight1);
        
        boolean crewValid = airline.validateCrewAssignment(flight1);
        System.out.println("  Crew assignment valid: " + crewValid);

        // === Step 6: Search Flights ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 6: Searching Flights (JFK → LAX)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        airline.setSearchStrategy(new CheapestFlightStrategy());
        List<FlightSearchResult> searchResults = airline.searchFlights(jfk, lax, tomorrow.toLocalDate());
        
        for (FlightSearchResult result : searchResults) {
            System.out.println(result);
        }

        // === Step 7: View Seat Map ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 7: Viewing Seat Map for SH101");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        System.out.println(airline.getSeatMap(flight1));

        // === Step 8: Register Passengers ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 8: Registering Passengers");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        Passenger passenger1 = airline.registerPassenger("Alice", "Williams", 
                "alice@email.com", "+1-555-0101", 
                LocalDate.of(1990, 5, 15), "US123456", "USA");
        
        Passenger passenger2 = airline.registerPassenger("Bob", "Davis",
                "bob@email.com", "+1-555-0102",
                LocalDate.of(1985, 8, 22), "US789012", "USA");
        
        System.out.println("  ✓ Registered: " + passenger1);
        System.out.println("  ✓ Registered: " + passenger2);

        // Add baggage for passenger
        airline.addBaggage(passenger1.getId(), BaggageType.CHECKED, 20.0);
        airline.addBaggage(passenger1.getId(), BaggageType.CABIN, 7.0);
        System.out.println("  ✓ Added baggage for " + passenger1.getFullName());

        // === Step 9: Create Booking ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 9: Creating Booking for Flight SH101");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        List<BookingService.PassengerSeatSelection> selections = List.of(
                new BookingService.PassengerSeatSelection(passenger1, "1A"),  // First class
                new BookingService.PassengerSeatSelection(passenger2, "1B")   // First class
        );
        
        Booking booking = airline.createBooking(flight1, selections);
        System.out.println("  ✓ " + booking);
        System.out.println("  PNR: " + booking.getPnr());

        // === Step 10: Process Payment and Confirm Booking ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 10: Processing Payment and Confirming Booking");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        airline.confirmBooking(booking.getId(), PaymentMethod.CREDIT_CARD);
        
        // Refresh booking to get updated status
        booking = airline.getBooking(booking.getId()).orElseThrow();
        System.out.println("  Booking Status: " + booking.getStatus());

        // === Step 11: Issue Tickets ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 11: Issuing E-Tickets");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        List<Ticket> tickets = airline.issueTickets(booking);
        for (Ticket ticket : tickets) {
            System.out.println(ticket);
        }

        // === Step 12: View Updated Seat Map ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 12: Updated Seat Map (after booking)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        System.out.println(airline.getSeatMap(flight1));

        // === Step 13: Flight Status Updates ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 13: Flight Status Updates");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        airline.updateFlightStatus("SH101", FlightStatus.BOARDING);

        // === Step 14: Cancel a Booking (Demo) ===
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("STEP 14: Booking Cancellation Demo (New Booking)");
        System.out.println("═══════════════════════════════════════════════════════════════");
        
        // Create another booking to demonstrate cancellation
        Passenger passenger3 = airline.registerPassenger("Charlie", "Evans",
                "charlie@email.com", "+1-555-0103",
                LocalDate.of(1988, 3, 10), "US345678", "USA");
        
        Booking cancelBooking = airline.createBooking(flight1, passenger3, "2A");
        airline.confirmBooking(cancelBooking.getId(), PaymentMethod.WALLET);
        
        System.out.println("  Created and confirmed booking: " + cancelBooking.getPnr());
        
        // Cancel the booking
        airline.cancelBooking(cancelBooking.getId(), "Customer requested cancellation");
        
        // Process refund
        Payment refund = airline.processRefund(cancelBooking.getId());

        // === Summary ===
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    DEMO COMPLETED                            ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println("\nThe Airline Management System demonstrated:");
        System.out.println("  ✓ Airport and Aircraft management");
        System.out.println("  ✓ Crew management and assignment");
        System.out.println("  ✓ Flight creation and scheduling");
        System.out.println("  ✓ Flight search with dynamic pricing");
        System.out.println("  ✓ Seat selection and seat map visualization");
        System.out.println("  ✓ Passenger registration and baggage handling");
        System.out.println("  ✓ Booking creation and confirmation");
        System.out.println("  ✓ Payment processing");
        System.out.println("  ✓ E-Ticket generation");
        System.out.println("  ✓ Flight status updates with notifications");
        System.out.println("  ✓ Booking cancellation and refunds");
    }
}



