package carrental;

import carrental.enums.CarType;
import carrental.enums.PaymentMethod;
import carrental.factories.CarFactory;
import carrental.factories.CustomerFactory;
import carrental.models.*;
import carrental.strategies.pricing.LongTermDiscountPricingStrategy;
import carrental.strategies.pricing.WeekendPricingStrategy;
import carrental.strategies.search.SortedSearchStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Main class demonstrating the Car Rental System usage.
 * Shows all major features and flows.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║               CAR RENTAL SYSTEM DEMONSTRATION                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        // Create system with standard pricing
        CarRentalSystem rentalSystem = new CarRentalSystem();

        // ==================== DEMO 1: Add Cars ====================
        System.out.println("▓▓▓ DEMO 1: Adding Cars to Fleet ▓▓▓\n");
        
        Car sedan = CarFactory.createSedan("Toyota", "Camry", 2023, "ABC-1234");
        Car suv = CarFactory.createSUV("Honda", "CR-V", 2023, "XYZ-5678");
        Car luxury = CarFactory.createLuxury("Mercedes", "E-Class", 2024, "LUX-9999");
        Car sports = CarFactory.createSports("Ford", "Mustang", 2023, "SPT-4567");
        Car hatchback = CarFactory.createHatchback("Volkswagen", "Golf", 2022, "HTB-1111");

        rentalSystem.addCar(sedan);
        rentalSystem.addCar(suv);
        rentalSystem.addCar(luxury);
        rentalSystem.addCar(sports);
        rentalSystem.addCar(hatchback);

        System.out.println("Added 5 cars to the fleet:");
        rentalSystem.getAllCars().forEach(car -> System.out.println("  → " + car));
        System.out.println();

        // ==================== DEMO 2: Register Customers ====================
        System.out.println("▓▓▓ DEMO 2: Registering Customers ▓▓▓\n");
        
        Customer john = CustomerFactory.createSampleCustomer("John Doe", "john@email.com");
        Customer jane = CustomerFactory.createSampleCustomer("Jane Smith", "jane@email.com");
        
        rentalSystem.registerCustomer(john);
        rentalSystem.registerCustomer(jane);

        System.out.println("Registered customers:");
        rentalSystem.getAllCustomers().forEach(c -> System.out.println("  → " + c));
        System.out.println();

        // ==================== DEMO 3: Search Cars ====================
        System.out.println("▓▓▓ DEMO 3: Searching for Cars ▓▓▓\n");
        
        // Search by type
        System.out.println("Available SUVs:");
        rentalSystem.getCarsByType(CarType.SUV).forEach(c -> System.out.println("  → " + c));
        System.out.println();

        // Search with criteria
        SearchCriteria criteria = new SearchCriteria.Builder()
            .priceRange(BigDecimal.valueOf(40), BigDecimal.valueOf(100))
            .minYear(2023)
            .build();
        
        System.out.println("Cars matching criteria (price $40-$100, year >= 2023):");
        rentalSystem.searchCars(criteria).forEach(c -> System.out.println("  → " + c));
        System.out.println();

        // ==================== DEMO 4: Make Reservation ====================
        System.out.println("▓▓▓ DEMO 4: Making a Reservation ▓▓▓\n");
        
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(5);
        
        System.out.println("John is reserving the Toyota Camry for " + startDate + " to " + endDate);
        Reservation reservation = rentalSystem.makeReservation(
            john.getId(), sedan.getId(), startDate, endDate);
        
        System.out.println("\nReservation created: " + reservation);
        System.out.println("Duration: " + reservation.getDurationInDays() + " days");
        System.out.println("Total: $" + reservation.getTotalAmount());
        System.out.println();

        // ==================== DEMO 5: Process Payment ====================
        System.out.println("▓▓▓ DEMO 5: Processing Payment ▓▓▓\n");
        
        Payment payment = rentalSystem.processPayment(reservation.getId(), PaymentMethod.CREDIT_CARD);
        System.out.println("Payment processed: " + payment);
        System.out.println();

        // ==================== DEMO 6: Confirm Reservation ====================
        System.out.println("▓▓▓ DEMO 6: Confirming Reservation ▓▓▓\n");
        
        Reservation confirmed = rentalSystem.confirmReservation(reservation.getId());
        System.out.println("Reservation confirmed: " + confirmed);
        System.out.println();

        // ==================== DEMO 7: Modify Reservation ====================
        System.out.println("▓▓▓ DEMO 7: Modifying Reservation ▓▓▓\n");
        
        LocalDate newEndDate = LocalDate.now().plusDays(7);
        System.out.println("Extending reservation until " + newEndDate);
        
        Reservation modified = rentalSystem.modifyReservation(
            reservation.getId(), startDate, newEndDate);
        System.out.println("Modified reservation: " + modified);
        System.out.println("New total: $" + modified.getTotalAmount());
        System.out.println();

        // ==================== DEMO 8: Check Availability ====================
        System.out.println("▓▓▓ DEMO 8: Checking Availability ▓▓▓\n");
        
        boolean available = rentalSystem.isCarAvailable(
            sedan.getId(), startDate, endDate);
        System.out.println("Is Toyota Camry available for " + startDate + " to " + endDate + "? " + available);
        
        System.out.println("\nAvailable cars for these dates:");
        rentalSystem.getAvailableCars(startDate, endDate).forEach(c -> System.out.println("  → " + c));
        System.out.println();

        // ==================== DEMO 9: Pickup and Return ====================
        System.out.println("▓▓▓ DEMO 9: Pickup and Return Flow ▓▓▓\n");
        
        // Create another reservation for Jane
        Reservation janeReservation = rentalSystem.makeReservation(
            jane.getId(), suv.getId(), startDate, startDate.plusDays(2));
        
        rentalSystem.processPayment(janeReservation.getId(), PaymentMethod.DEBIT_CARD);
        rentalSystem.confirmReservation(janeReservation.getId());
        
        System.out.println("Jane picks up the Honda CR-V...");
        Reservation active = rentalSystem.pickupCar(janeReservation.getId());
        System.out.println("Status: " + active.getStatus());
        
        System.out.println("\nJane returns the car...");
        Reservation completed = rentalSystem.returnCar(janeReservation.getId());
        System.out.println("Status: " + completed.getStatus());
        System.out.println();

        // ==================== DEMO 10: Cancel Reservation ====================
        System.out.println("▓▓▓ DEMO 10: Cancelling a Reservation ▓▓▓\n");
        
        // Create a new reservation to cancel
        Reservation toCancel = rentalSystem.makeReservation(
            john.getId(), sports.getId(), 
            LocalDate.now().plusDays(10), 
            LocalDate.now().plusDays(12));
        
        System.out.println("Created reservation to cancel: " + toCancel.getId());
        Reservation cancelled = rentalSystem.cancelReservation(toCancel.getId());
        System.out.println("Cancelled reservation status: " + cancelled.getStatus());
        System.out.println();

        // ==================== DEMO 11: Different Pricing Strategies ====================
        System.out.println("▓▓▓ DEMO 11: Different Pricing Strategies ▓▓▓\n");
        
        // Weekend pricing
        CarRentalSystem weekendSystem = new CarRentalSystem(
            new WeekendPricingStrategy(), 
            new SortedSearchStrategy());
        
        Car weekendCar = CarFactory.createSedan("Honda", "Accord", 2023, "WKD-1234");
        weekendSystem.addCar(weekendCar);
        weekendSystem.registerCustomer(john);
        
        // Find a date range that includes weekend
        LocalDate fridayStart = LocalDate.now().plusDays(5);
        LocalDate sundayEnd = fridayStart.plusDays(2);
        
        System.out.println("Weekend pricing for " + fridayStart + " to " + sundayEnd + ":");
        // Would show higher prices for weekend days

        // Long-term discount pricing
        CarRentalSystem longTermSystem = new CarRentalSystem(
            new LongTermDiscountPricingStrategy(),
            new SortedSearchStrategy());
        
        Car longTermCar = CarFactory.createSUV("Jeep", "Grand Cherokee", 2023, "LNG-5678");
        longTermSystem.addCar(longTermCar);
        longTermSystem.registerCustomer(jane);
        
        System.out.println("\nLong-term discount pricing:");
        System.out.println("  3-6 days: 5% discount");
        System.out.println("  7-13 days: 10% discount");
        System.out.println("  14-29 days: 15% discount");
        System.out.println("  30+ days: 20% discount");
        System.out.println();

        // ==================== System Status ====================
        rentalSystem.printStatus();

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    DEMO COMPLETED                             ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
}



