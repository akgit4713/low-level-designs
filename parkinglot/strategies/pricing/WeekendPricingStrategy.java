package parkinglot.strategies.pricing;

import parkinglot.enums.VehicleType;
import parkinglot.models.ParkingTicket;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Weekend pricing strategy with higher rates on weekends.
 * Applies a multiplier to base hourly rates on Saturday and Sunday.
 */
public class WeekendPricingStrategy implements PricingStrategy {
    
    private final Map<VehicleType, Double> baseHourlyRates;
    private final double weekendMultiplier;

    /**
     * Creates strategy with default rates and 1.5x weekend multiplier.
     */
    public WeekendPricingStrategy() {
        this(1.5);
    }

    /**
     * Creates strategy with custom weekend multiplier.
     * 
     * @param weekendMultiplier Multiplier applied on weekends (e.g., 1.5 = 50% higher)
     */
    public WeekendPricingStrategy(double weekendMultiplier) {
        this.baseHourlyRates = Map.of(
            VehicleType.MOTORCYCLE, 1.0,
            VehicleType.CAR, 2.0,
            VehicleType.TRUCK, 3.0
        );
        this.weekendMultiplier = weekendMultiplier;
    }

    @Override
    public double calculateFee(ParkingTicket ticket) {
        long hours = Math.max(1, ticket.calculateDuration().toHours() + 1);
        double ratePerHour = baseHourlyRates.getOrDefault(ticket.getVehicle().getType(), 2.0);
        
        // Apply weekend multiplier if entry was on weekend
        LocalDateTime entryTime = ticket.getEntryTime();
        if (isWeekend(entryTime)) {
            ratePerHour *= weekendMultiplier;
        }
        
        return hours * ratePerHour;
    }

    private boolean isWeekend(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    @Override
    public String getDescription() {
        return String.format("Weekend pricing: Base rates with %.1fx multiplier on weekends", weekendMultiplier);
    }
}



