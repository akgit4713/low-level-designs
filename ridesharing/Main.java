package ridesharing;

import ridesharing.enums.RideType;
import ridesharing.enums.VehicleType;
import ridesharing.factories.RideSharingService;
import ridesharing.factories.RideSharingServiceFactory;
import ridesharing.models.*;
import ridesharing.observers.AnalyticsObserver;

/**
 * Main class demonstrating the Ride-Sharing Service.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("       RIDE-SHARING SERVICE DEMO");
        System.out.println("=".repeat(60));
        
        // Create the service using factory
        RideSharingService service = RideSharingServiceFactory.createDefault();
        
        // Add analytics observer
        AnalyticsObserver analytics = new AnalyticsObserver();
        service.registerObserver(analytics);
        
        // ============ DEMO: Register Users ============
        System.out.println("\n📱 REGISTERING USERS...\n");
        
        // Register passengers
        Passenger alice = service.registerPassenger("Alice Smith", "alice@email.com", "555-0101");
        Passenger bob = service.registerPassenger("Bob Johnson", "bob@email.com", "555-0102");
        System.out.println("Registered passenger: " + alice);
        System.out.println("Registered passenger: " + bob);
        
        // Register drivers with vehicles
        Vehicle vehicle1 = Vehicle.builder()
                .vehicleId("V001")
                .licensePlate("ABC-1234")
                .make("Toyota")
                .model("Camry")
                .color("Black")
                .year(2022)
                .vehicleType(VehicleType.SEDAN)
                .build();
        
        Vehicle vehicle2 = Vehicle.builder()
                .vehicleId("V002")
                .licensePlate("XYZ-5678")
                .make("Mercedes")
                .model("E-Class")
                .color("Silver")
                .year(2023)
                .vehicleType(VehicleType.LUXURY)
                .build();
        
        Driver john = service.registerDriver("John Driver", "john@driver.com", "555-0201", 
                vehicle1, "DL-12345");
        Driver jane = service.registerDriver("Jane Driver", "jane@driver.com", "555-0202", 
                vehicle2, "DL-67890");
        System.out.println("Registered driver: " + john);
        System.out.println("Registered driver: " + jane);
        
        // ============ DEMO: Set Drivers Online ============
        System.out.println("\n🟢 SETTING DRIVERS ONLINE...\n");
        
        // Drivers go online with their locations
        Location johnLocation = new Location(40.7128, -74.0060, "Times Square, NYC");
        Location janeLocation = new Location(40.7580, -73.9855, "Central Park, NYC");
        
        service.setDriverOnline(john.getUserId(), johnLocation);
        service.setDriverOnline(jane.getUserId(), janeLocation);
        System.out.println("John is online at: " + johnLocation);
        System.out.println("Jane is online at: " + janeLocation);
        
        // ============ DEMO: Get Fare Estimate ============
        System.out.println("\n💰 FARE ESTIMATES...\n");
        
        Location pickup = new Location(40.7484, -73.9857, "Empire State Building");
        Location dropoff = new Location(40.6892, -74.0445, "Statue of Liberty");
        
        Fare regularFare = service.getFareEstimate(pickup, dropoff, RideType.REGULAR);
        Fare premiumFare = service.getFareEstimate(pickup, dropoff, RideType.PREMIUM);
        Fare poolFare = service.getFareEstimate(pickup, dropoff, RideType.POOL);
        
        System.out.println("From: " + pickup.getAddress());
        System.out.println("To: " + dropoff.getAddress());
        System.out.println();
        System.out.println("Regular fare: " + regularFare);
        System.out.println("Premium fare: " + premiumFare);
        System.out.println("Pool fare: " + poolFare);
        
        // ============ DEMO: Complete Ride Flow ============
        System.out.println("\n🚗 RIDE FLOW DEMO...\n");
        
        // Alice requests a regular ride
        System.out.println("--- Alice Requests a Ride ---");
        Ride ride1 = service.requestRide(
                alice.getUserId(),
                pickup,
                dropoff,
                RideType.REGULAR
        );
        System.out.println("Ride requested: " + ride1.getRideId());
        System.out.println("Status: " + ride1.getStatus());
        System.out.println("Estimated fare: $" + String.format("%.2f", ride1.getFare().getTotalAmount()));
        
        // If a driver was matched, they accept
        if (ride1.getDriverId() != null) {
            System.out.println("\n--- Driver Accepts Ride ---");
            service.acceptRide(ride1.getRideId(), ride1.getDriverId());
            System.out.println("Status: " + service.getRide(ride1.getRideId()).get().getStatus());
        } else {
            // Manually accept with nearest driver (John)
            System.out.println("\n--- John Accepts Ride ---");
            service.acceptRide(ride1.getRideId(), john.getUserId());
        }
        
        // Driver arrives
        System.out.println("\n--- Driver Arrives ---");
        service.driverArrived(ride1.getRideId());
        System.out.println("Status: " + service.getRide(ride1.getRideId()).get().getStatus());
        
        // Start ride
        System.out.println("\n--- Ride Starts ---");
        service.startRide(ride1.getRideId());
        System.out.println("Status: " + service.getRide(ride1.getRideId()).get().getStatus());
        
        // Simulate location updates during ride
        System.out.println("\n--- Location Updates ---");
        Location midway = new Location(40.7200, -74.0100, "Midtown Manhattan");
        service.updateDriverLocation(john.getUserId(), midway);
        System.out.println("ETA to destination: " + service.getETAMinutes(ride1.getRideId()) + " minutes");
        
        // Complete ride
        System.out.println("\n--- Ride Completes ---");
        Ride completedRide = service.completeRide(ride1.getRideId());
        System.out.println("Status: " + completedRide.getStatus());
        System.out.println("Final fare: $" + String.format("%.2f", completedRide.getFare().getTotalAmount()));
        System.out.println("Driver earnings: $" + String.format("%.2f", completedRide.getFare().getDriverEarnings()));
        System.out.println("Payment status: " + completedRide.getPayment().getStatus());
        
        // Ratings
        System.out.println("\n--- Ratings ---");
        service.rateDriver(ride1.getRideId(), 5);
        service.ratePassenger(ride1.getRideId(), 5);
        System.out.println("Ratings submitted!");
        
        // Check updated driver rating
        Driver updatedJohn = service.getDriver(john.getUserId()).get();
        System.out.println("John's rating: " + String.format("%.2f", updatedJohn.getRating()));
        System.out.println("John's total earnings: $" + String.format("%.2f", updatedJohn.getTotalEarnings()));
        
        // ============ DEMO: Premium Ride ============
        System.out.println("\n\n🌟 PREMIUM RIDE DEMO...\n");
        
        // Bob requests a premium ride
        Location bobPickup = new Location(40.7614, -73.9776, "MoMA");
        Location bobDropoff = new Location(40.7527, -73.9772, "Grand Central");
        
        Ride premiumRide = service.requestRide(
                bob.getUserId(),
                bobPickup,
                bobDropoff,
                RideType.PREMIUM
        );
        
        System.out.println("Premium ride requested: " + premiumRide.getRideId());
        System.out.println("Estimated fare: $" + String.format("%.2f", premiumRide.getFare().getTotalAmount()));
        
        // Jane (with luxury car) accepts
        service.acceptRide(premiumRide.getRideId(), jane.getUserId());
        service.driverArrived(premiumRide.getRideId());
        service.startRide(premiumRide.getRideId());
        Ride completedPremium = service.completeRide(premiumRide.getRideId());
        
        System.out.println("Premium ride completed!");
        System.out.println("Final fare: $" + String.format("%.2f", completedPremium.getFare().getTotalAmount()));
        
        // ============ DEMO: Ride Cancellation ============
        System.out.println("\n\n❌ CANCELLATION DEMO...\n");
        
        // Reset John's status to available
        service.setDriverOnline(john.getUserId(), johnLocation);
        
        Ride cancelRide = service.requestRide(
                alice.getUserId(),
                pickup,
                dropoff,
                RideType.REGULAR
        );
        System.out.println("Ride requested: " + cancelRide.getRideId());
        
        service.cancelRide(cancelRide.getRideId(), "Changed my plans");
        System.out.println("Status: " + service.getRide(cancelRide.getRideId()).get().getStatus());
        
        // ============ DEMO: Wallet Payment ============
        System.out.println("\n\n💳 WALLET PAYMENT DEMO...\n");
        
        service.addWalletBalance(alice.getUserId(), 100.0);
        System.out.println("Added $100 to Alice's wallet");
        System.out.println("Alice's wallet balance: $" + String.format("%.2f", 
                service.getWalletBalance(alice.getUserId())));
        
        // ============ ANALYTICS SUMMARY ============
        analytics.printSummary();
        
        System.out.println("=".repeat(60));
        System.out.println("       DEMO COMPLETED SUCCESSFULLY!");
        System.out.println("=".repeat(60));
    }
}



