package deliveryservice;

import deliveryservice.services.DeliveryService;
import deliveryservice.strategies.CostCalculationStrategy;
import deliveryservice.strategies.DistanceBasedCostStrategy;
import deliveryservice.strategies.DurationBasedCostStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== Delivery Service Demo ===\n");
        
        // Demo 1: Duration-based cost (default strategy)
        demoDurationBasedCost();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Demo 2: Custom rate duration-based cost
        demoCustomRateCost();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Demo 3: Distance-based cost (extensibility demo)
        demoDistanceBasedCost();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Demo 4: Payment settlement functionality
        demoPaymentSettlement();
        
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // Demo 5: Why BigDecimal matters
        demoBigDecimalPrecision();
    }
    
    private static void demoDurationBasedCost() {
        System.out.println("Demo 1: Duration-Based Cost Strategy (rate = 1.0/minute)");
        System.out.println("-".repeat(50));
        
        // Default strategy: cost = duration in minutes
        DeliveryService service = new DeliveryService();
        
        // Add drivers
        service.addDriver("D001");
        service.addDriver("D002");
        
        LocalDateTime now = LocalDateTime.now();
        
        // Driver 1: Two deliveries
        // Delivery 1: 30 minutes -> cost = 30
        service.addDelivery("D001", now, now.plusMinutes(30));
        System.out.println("Added delivery for D001: 30 minutes, cost = 30.00");
        
        // Delivery 2: 45 minutes -> cost = 45
        service.addDelivery("D001", now.plusHours(1), now.plusHours(1).plusMinutes(45));
        System.out.println("Added delivery for D001: 45 minutes, cost = 45.00");
        
        // Driver 2: One delivery
        // Delivery 3: 60 minutes -> cost = 60
        service.addDelivery("D002", now.plusHours(2), now.plusHours(3));
        System.out.println("Added delivery for D002: 60 minutes, cost = 60.00");
        
        System.out.println("\nTotal Cost (O(1) retrieval): " + service.getTotalCost());
        System.out.println("Expected: 135.00 (30 + 45 + 60)");
        
        System.out.println("\nCost breakdown by driver:");
        System.out.println("  D001: " + service.getTotalCostForDriver("D001"));
        System.out.println("  D002: " + service.getTotalCostForDriver("D002"));
    }
    
    private static void demoCustomRateCost() {
        System.out.println("Demo 2: Duration-Based Cost with Custom Rate ($2.50/minute)");
        System.out.println("-".repeat(50));
        
        // Custom rate: $2.50 per minute (using String for exact BigDecimal representation)
        CostCalculationStrategy customStrategy = new DurationBasedCostStrategy("2.50");
        DeliveryService service = new DeliveryService(customStrategy);
        
        service.addDriver("D001");
        
        LocalDateTime now = LocalDateTime.now();
        
        // 20 minutes delivery -> cost = 20 * 2.50 = 50
        service.addDelivery("D001", now, now.plusMinutes(20));
        System.out.println("Added delivery: 20 minutes @ $2.50/min = $50.00");
        
        // 40 minutes delivery -> cost = 40 * 2.50 = 100
        service.addDelivery("D001", now.plusHours(1), now.plusHours(1).plusMinutes(40));
        System.out.println("Added delivery: 40 minutes @ $2.50/min = $100.00");
        
        System.out.println("\nTotal Cost: $" + service.getTotalCost());
        System.out.println("Expected: $150.00");
    }
    
    private static void demoDistanceBasedCost() {
        System.out.println("Demo 3: Distance-Based Cost Strategy (Extensibility Demo)");
        System.out.println("-".repeat(50));
        
        // Distance-based: base fare $5 + $1.5/km, estimated speed 30 km/h
        // Using String constructor to avoid floating-point representation issues
        CostCalculationStrategy distanceStrategy = new DistanceBasedCostStrategy("5.00", "1.50", "30.00");
        DeliveryService service = new DeliveryService(distanceStrategy);
        
        service.addDriver("D001");
        
        LocalDateTime now = LocalDateTime.now();
        
        // 60 minutes -> estimated 30 km -> cost = 5 + (30 * 1.5) = 50
        service.addDelivery("D001", now, now.plusMinutes(60));
        System.out.println("Added delivery: 60 minutes (~30 km) = $50.00");
        
        // 30 minutes -> estimated 15 km -> cost = 5 + (15 * 1.5) = 27.5
        service.addDelivery("D001", now.plusHours(2), now.plusHours(2).plusMinutes(30));
        System.out.println("Added delivery: 30 minutes (~15 km) = $27.50");
        
        System.out.println("\nTotal Cost: $" + service.getTotalCost());
        System.out.println("Expected: $77.50");
    }
    
    private static void demoPaymentSettlement() {
        System.out.println("Demo 4: Payment Settlement (payUpToTime & getCostToBePaid)");
        System.out.println("-".repeat(50));
        
        DeliveryService service = new DeliveryService();
        service.addDriver("D001");
        service.addDriver("D002");
        
        // Base time for our deliveries
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 15, 9, 0);
        
        // Add deliveries at different times
        // Delivery 1: 9:00 - 9:30 (30 min = $30)
        service.addDelivery("D001", baseTime, baseTime.plusMinutes(30));
        System.out.println("Delivery 1: 9:00-9:30, cost = $30.00");
        
        // Delivery 2: 10:00 - 10:45 (45 min = $45)
        service.addDelivery("D001", baseTime.plusHours(1), baseTime.plusHours(1).plusMinutes(45));
        System.out.println("Delivery 2: 10:00-10:45, cost = $45.00");
        
        // Delivery 3: 11:00 - 12:00 (60 min = $60)
        service.addDelivery("D002", baseTime.plusHours(2), baseTime.plusHours(3));
        System.out.println("Delivery 3: 11:00-12:00, cost = $60.00");
        
        // Delivery 4: 14:00 - 14:20 (20 min = $20)
        service.addDelivery("D002", baseTime.plusHours(5), baseTime.plusHours(5).plusMinutes(20));
        System.out.println("Delivery 4: 14:00-14:20, cost = $20.00");
        
        System.out.println("\n--- Initial State ---");
        System.out.println("Total Cost: $" + service.getTotalCost());
        System.out.println("Cost To Be Paid (O(1)): $" + service.getCostToBePaid());
        System.out.println("Total Paid: $" + service.getTotalPaidCost());
        
        // Pay up to 11:00 - should pay deliveries 1 and 2 (endTime <= 11:00)
        // Delivery 1 ends at 9:30, Delivery 2 ends at 10:45
        LocalDateTime paymentCutoff1 = baseTime.plusHours(2);  // 11:00
        System.out.println("\n--- Pay up to 11:00 ---");
        BigDecimal paid1 = service.payUpToTime(paymentCutoff1);
        System.out.println("Amount Paid: $" + paid1 + " (deliveries ending by 11:00)");
        System.out.println("Cost To Be Paid (O(1)): $" + service.getCostToBePaid());
        System.out.println("Total Paid: $" + service.getTotalPaidCost());
        
        // Pay up to 13:00 - should pay delivery 3 (endTime = 12:00)
        LocalDateTime paymentCutoff2 = baseTime.plusHours(4);  // 13:00
        System.out.println("\n--- Pay up to 13:00 ---");
        BigDecimal paid2 = service.payUpToTime(paymentCutoff2);
        System.out.println("Amount Paid: $" + paid2 + " (deliveries ending by 13:00)");
        System.out.println("Cost To Be Paid (O(1)): $" + service.getCostToBePaid());
        System.out.println("Total Paid: $" + service.getTotalPaidCost());
        
        // Pay up to 15:00 - should pay delivery 4 (endTime = 14:20)
        LocalDateTime paymentCutoff3 = baseTime.plusHours(6);  // 15:00
        System.out.println("\n--- Pay up to 15:00 ---");
        BigDecimal paid3 = service.payUpToTime(paymentCutoff3);
        System.out.println("Amount Paid: $" + paid3 + " (deliveries ending by 15:00)");
        System.out.println("Cost To Be Paid (O(1)): $" + service.getCostToBePaid());
        System.out.println("Total Paid: $" + service.getTotalPaidCost());
        
        System.out.println("\n--- Final State ---");
        System.out.println("All deliveries paid! Cost To Be Paid: $" + service.getCostToBePaid());
    }
    
    private static void demoBigDecimalPrecision() {
        System.out.println("Demo 5: Why BigDecimal Matters for Financial Calculations");
        System.out.println("-".repeat(50));
        
        // Double precision issue - the classic floating-point problem
        double doubleResult = 0.1 + 0.2;
        System.out.println("double: 0.1 + 0.2 = " + doubleResult);
        System.out.println("Is 0.1 + 0.2 == 0.3 with double? " + (doubleResult == 0.3 ? "YES" : "NO (INCORRECT!)"));
        
        // BigDecimal precision - no floating-point errors
        BigDecimal bd1 = new BigDecimal("0.1");
        BigDecimal bd2 = new BigDecimal("0.2");
        BigDecimal bdResult = bd1.add(bd2);
        System.out.println("\nBigDecimal: 0.1 + 0.2 = " + bdResult);
        System.out.println("Is 0.1 + 0.2 == 0.3 with BigDecimal? " + 
                (bdResult.compareTo(new BigDecimal("0.3")) == 0 ? "YES (CORRECT!)" : "NO"));
        
        System.out.println("\n=> BigDecimal avoids floating-point precision issues in financial calculations.");
        System.out.println("=> Always use String constructor: new BigDecimal(\"2.50\") not new BigDecimal(2.50)");
    }
}
