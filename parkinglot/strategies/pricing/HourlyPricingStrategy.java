package parkinglot.strategies.pricing;

import parkinglot.models.ParkingTicket;

import java.util.Map;

/**
 * Default hourly pricing strategy.
 * Charges per hour with rates varying by vehicle type.
 */
public class HourlyPricingStrategy implements PricingStrategy {
    
    private final Map<parkinglot.enums.VehicleType, Double> hourlyRates;

    /**
     * Creates strategy with default rates.
     * Default: Motorcycle = $1/hr, Car = $2/hr, Truck = $3/hr
     */
    public HourlyPricingStrategy() {
        this.hourlyRates = Map.of(
            parkinglot.enums.VehicleType.MOTORCYCLE, 1.0,
            parkinglot.enums.VehicleType.CAR, 2.0,
            parkinglot.enums.VehicleType.TRUCK, 3.0
        );
    }

    /**
     * Creates strategy with custom rates.
     * 
     * @param hourlyRates Map of vehicle types to hourly rates
     */
    public HourlyPricingStrategy(Map<parkinglot.enums.VehicleType, Double> hourlyRates) {
        this.hourlyRates = Map.copyOf(hourlyRates);
    }

    @Override
    public double calculateFee(ParkingTicket ticket) {
        long hours = Math.max(1, ticket.calculateDuration().toHours() + 1); // Minimum 1 hour
        double ratePerHour = hourlyRates.getOrDefault(ticket.getVehicle().getType(), 2.0);
        return hours * ratePerHour;
    }

    @Override
    public String getDescription() {
        return "Hourly pricing: Motorcycle=$1/hr, Car=$2/hr, Truck=$3/hr";
    }
    
    public double getRateForVehicleType(parkinglot.enums.VehicleType type) {
        return hourlyRates.getOrDefault(type, 2.0);
    }
}



