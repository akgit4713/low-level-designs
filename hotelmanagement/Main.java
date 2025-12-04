package hotelmanagement;

import hotelmanagement.enums.*;
import hotelmanagement.models.*;
import hotelmanagement.services.ReportService;
import hotelmanagement.strategies.pricing.WeekendPricingStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * Demo application showcasing the Hotel Management System
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=" .repeat(60));
        System.out.println("      HOTEL MANAGEMENT SYSTEM DEMO");
        System.out.println("=".repeat(60));
        
        // Initialize hotel
        Hotel hotel = new Hotel("Grand Plaza Hotel");
        System.out.println("\n🏨 Welcome to " + hotel.getName() + "!\n");
        
        // Set weekend pricing strategy
        hotel.setPricingStrategy(new WeekendPricingStrategy());
        
        // === 1. Add Rooms ===
        System.out.println("📋 Adding rooms to the hotel...\n");
        
        hotel.addRoom(Room.builder()
            .roomNumber("101")
            .floor(1)
            .type(RoomType.SINGLE)
            .baseRate(new BigDecimal("99.99"))
            .capacity(1)
            .amenities(Set.of("WiFi", "TV", "Air Conditioning"))
            .description("Cozy single room on ground floor")
            .build());
        
        hotel.addRoom(Room.builder()
            .roomNumber("102")
            .floor(1)
            .type(RoomType.DOUBLE)
            .baseRate(new BigDecimal("149.99"))
            .capacity(2)
            .amenities(Set.of("WiFi", "TV", "Air Conditioning", "Mini Bar"))
            .description("Comfortable double room")
            .build());
        
        hotel.addRoom(Room.builder()
            .roomNumber("201")
            .floor(2)
            .type(RoomType.DELUXE)
            .baseRate(new BigDecimal("249.99"))
            .capacity(2)
            .amenities(Set.of("WiFi", "TV", "Air Conditioning", "Mini Bar", "City View", "Jacuzzi"))
            .description("Deluxe room with city view and jacuzzi")
            .build());
        
        hotel.addRoom(Room.builder()
            .roomNumber("301")
            .floor(3)
            .type(RoomType.SUITE)
            .baseRate(new BigDecimal("499.99"))
            .capacity(4)
            .amenities(Set.of("WiFi", "Smart TV", "Air Conditioning", "Mini Bar", 
                             "Ocean View", "Jacuzzi", "Living Room", "Kitchen"))
            .description("Luxurious suite with ocean view")
            .build());
        
        System.out.println("✅ Added " + hotel.getAllRooms().size() + " rooms");
        hotel.getAllRooms().forEach(room -> 
            System.out.println("   " + room.getRoomNumber() + " - " + room.getType() + 
                             " ($" + room.getBaseRate() + "/night)"));
        
        // === 2. Register Guests ===
        System.out.println("\n📋 Registering guests...\n");
        
        Guest guest1 = hotel.registerGuest(
            "John Doe", 
            "john.doe@email.com", 
            "+1-555-0101",
            "Passport",
            "AB123456"
        );
        
        // Create a returning guest with loyalty points
        Guest guest2 = Guest.builder()
            .name("Jane Smith")
            .email("jane.smith@email.com")
            .phone("+1-555-0102")
            .idType("Driver's License")
            .idNumber("DL789012")
            .totalStays(12) // Silver member
            .loyaltyPoints(500)
            .build();
        hotel.registerGuest(guest2);
        
        System.out.println("✅ Registered guests:");
        System.out.println("   " + guest1.getName() + " - " + guest1.getLoyaltyTier());
        System.out.println("   " + guest2.getName() + " - " + guest2.getLoyaltyTier() + 
                         " (" + guest2.getLoyaltyPoints() + " points)");
        
        // === 3. Make Reservations ===
        System.out.println("\n📋 Making reservations...\n");
        
        LocalDate today = LocalDate.now();
        
        // John books a deluxe room for 3 nights
        Reservation res1 = hotel.makeReservation(
            guest1.getId(),
            RoomType.DELUXE,
            today,
            today.plusDays(3),
            2
        );
        System.out.println("✅ Reservation created: " + res1.getId());
        System.out.println("   Guest: " + res1.getGuest().getName());
        System.out.println("   Room: " + res1.getRoom().getRoomNumber() + " (" + res1.getRoom().getType() + ")");
        System.out.println("   Dates: " + res1.getCheckInDate() + " to " + res1.getCheckOutDate());
        System.out.println("   Rate: $" + res1.getRoomRatePerNight() + "/night");
        
        // Confirm the reservation
        hotel.confirmReservation(res1.getId());
        
        // === 4. Check-In ===
        System.out.println("\n📋 Processing check-in...\n");
        
        hotel.checkIn(res1.getId());
        
        // === 5. Add Service Charges ===
        System.out.println("\n📋 Adding service charges...\n");
        
        hotel.addServiceCharge(res1.getId(), ServiceType.ROOM_SERVICE, 
            new BigDecimal("45.00"), "Dinner - Steak & Wine");
        hotel.addServiceCharge(res1.getId(), ServiceType.MINIBAR, 
            new BigDecimal("25.00"), "Beverages");
        hotel.addServiceCharge(res1.getId(), ServiceType.SPA, 
            new BigDecimal("100.00"), "Relaxation Massage");
        
        System.out.println("✅ Service charges added:");
        hotel.getReservation(res1.getId()).ifPresent(r -> 
            r.getServiceCharges().forEach(sc -> 
                System.out.println("   - " + sc.getServiceType() + ": $" + sc.getAmount())));
        
        // === 6. Check-Out & Generate Bill ===
        System.out.println("\n📋 Processing check-out...\n");
        
        Bill bill = hotel.checkOut(res1.getId());
        
        // Print bill
        System.out.println(bill.getFormattedSummary());
        
        // === 7. Process Payment ===
        System.out.println("📋 Processing payment...\n");
        
        Payment payment = hotel.processPayment(
            bill.getId(), 
            bill.getTotalAmount(), 
            PaymentMethod.CREDIT_CARD
        );
        
        if (payment.isSuccessful()) {
            System.out.println("✅ Payment successful!");
            System.out.println("   Transaction Ref: " + payment.getTransactionReference());
        }
        
        // === 8. Housekeeping ===
        System.out.println("\n📋 Housekeeping tasks...\n");
        
        System.out.println("Pending cleaning tasks:");
        hotel.getPendingCleaningTasks().forEach(task ->
            System.out.println("   " + task.getRoom().getRoomNumber() + " - " + task.getTaskType()));
        
        // Complete cleaning for room 201
        hotel.getRoom("201").ifPresent(room -> 
            hotel.markRoomClean(room.getId()));
        
        // === 9. Generate Reports ===
        System.out.println("\n📋 Generating reports...\n");
        
        // Daily summary
        ReportService.DailySummary summary = hotel.getTodaySummary();
        System.out.println("📊 TODAY'S SUMMARY");
        System.out.println("   Check-ins: " + summary.checkIns());
        System.out.println("   Check-outs: " + summary.checkOuts());
        System.out.println("   Current Occupancy: " + summary.currentOccupancy() + " rooms");
        System.out.println("   Available Rooms: " + summary.availableRooms());
        System.out.println("   Occupancy Rate: " + String.format("%.1f%%", summary.occupancyRate()));
        System.out.println("   Day Revenue: $" + summary.dayRevenue());
        
        // Room availability
        System.out.println("\n📊 ROOM AVAILABILITY BY TYPE");
        hotel.getRoomService().getAvailabilityByType().forEach((type, count) ->
            System.out.println("   " + type + ": " + count + " available"));
        
        // === 10. Demonstrate Jane's Reservation (Loyalty Member) ===
        System.out.println("\n" + "=".repeat(60));
        System.out.println("      LOYALTY MEMBER BOOKING DEMO");
        System.out.println("=".repeat(60));
        
        // Jane (Silver member) books a suite for 7 nights (long stay discount)
        Reservation res2 = hotel.makeReservation(
            guest2.getId(),
            RoomType.SUITE,
            today.plusDays(5),
            today.plusDays(12),  // 7 nights for long stay discount
            3
        );
        
        hotel.confirmReservation(res2.getId());
        System.out.println("\n✅ Reservation created for loyalty member: " + res2.getId());
        System.out.println("   Guest: " + res2.getGuest().getName() + " (" + 
                         res2.getGuest().getLoyaltyTier() + " Member)");
        System.out.println("   Loyalty Discount: " + res2.getGuest().getLoyaltyDiscountPercent() + "%");
        System.out.println("   Room: " + res2.getRoom().getRoomNumber() + " (" + res2.getRoom().getType() + ")");
        System.out.println("   Stay: " + res2.getNumberOfNights() + " nights");
        System.out.println("   (Eligible for Long Stay Discount!)");
        
        // === Final Summary ===
        System.out.println("\n" + "=".repeat(60));
        System.out.println("      DEMO COMPLETE");
        System.out.println("=".repeat(60));
        System.out.println("\nThank you for using " + hotel.getName() + "!");
        System.out.println("Total Rooms: " + hotel.getAllRooms().size());
        System.out.println("Available Rooms: " + hotel.getAvailableRooms().size());
        System.out.println("Active Reservations: " + hotel.getActiveReservations().size());
    }
}



