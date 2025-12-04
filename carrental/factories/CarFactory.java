package carrental.factories;

import carrental.enums.CarType;
import carrental.models.Car;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Factory for creating Car instances.
 * Follows Factory Pattern for consistent object creation.
 */
public class CarFactory {

    /**
     * Creates a new car with a generated ID.
     */
    public static Car createCar(String make, String model, int year, 
                                 String licensePlate, CarType carType, 
                                 BigDecimal basePricePerDay) {
        return new Car.Builder()
            .id(UUID.randomUUID().toString())
            .make(make)
            .model(model)
            .year(year)
            .licensePlate(licensePlate)
            .carType(carType)
            .basePricePerDay(basePricePerDay)
            .build();
    }

    /**
     * Creates a sedan with default pricing.
     */
    public static Car createSedan(String make, String model, int year, String licensePlate) {
        return createCar(make, model, year, licensePlate, CarType.SEDAN, BigDecimal.valueOf(50));
    }

    /**
     * Creates an SUV with default pricing.
     */
    public static Car createSUV(String make, String model, int year, String licensePlate) {
        return createCar(make, model, year, licensePlate, CarType.SUV, BigDecimal.valueOf(60));
    }

    /**
     * Creates a luxury car with default pricing.
     */
    public static Car createLuxury(String make, String model, int year, String licensePlate) {
        return createCar(make, model, year, licensePlate, CarType.LUXURY, BigDecimal.valueOf(100));
    }

    /**
     * Creates a sports car with default pricing.
     */
    public static Car createSports(String make, String model, int year, String licensePlate) {
        return createCar(make, model, year, licensePlate, CarType.SPORTS, BigDecimal.valueOf(80));
    }

    /**
     * Creates a hatchback with default pricing.
     */
    public static Car createHatchback(String make, String model, int year, String licensePlate) {
        return createCar(make, model, year, licensePlate, CarType.HATCHBACK, BigDecimal.valueOf(35));
    }
}



