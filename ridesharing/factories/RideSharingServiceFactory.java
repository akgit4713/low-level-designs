package ridesharing.factories;

import ridesharing.enums.RideType;
import ridesharing.observers.*;
import ridesharing.repositories.*;
import ridesharing.repositories.impl.*;
import ridesharing.services.*;
import ridesharing.services.impl.*;
import ridesharing.strategies.distance.*;
import ridesharing.strategies.matching.*;
import ridesharing.strategies.payment.*;
import ridesharing.strategies.pricing.*;

import java.util.Arrays;
import java.util.List;

/**
 * Factory for creating and wiring up the RideSharingService.
 * Follows Factory Pattern for complex object creation.
 */
public class RideSharingServiceFactory {
    
    /**
     * Create a fully configured RideSharingService with default settings.
     */
    public static RideSharingService createDefault() {
        // Create repositories
        RideRepository rideRepository = new InMemoryRideRepository();
        DriverRepository driverRepository = new InMemoryDriverRepository();
        PassengerRepository passengerRepository = new InMemoryPassengerRepository();
        PaymentRepository paymentRepository = new InMemoryPaymentRepository();
        
        // Create strategies
        DistanceCalculationStrategy distanceStrategy = new HaversineDistanceStrategy();
        PricingStrategy pricingStrategy = new StandardPricingStrategy();
        
        // Create composite matching strategy
        DriverMatchingStrategy nearestStrategy = new NearestDriverStrategy(distanceStrategy);
        DriverMatchingStrategy ratingStrategy = new RatingBasedMatchingStrategy(distanceStrategy);
        CompositeMatchingStrategy matchingStrategy = new CompositeMatchingStrategy(nearestStrategy);
        matchingStrategy.registerStrategy(RideType.PREMIUM, ratingStrategy);
        
        // Create payment strategies
        WalletPaymentStrategy walletStrategy = new WalletPaymentStrategy();
        List<PaymentStrategy> paymentStrategies = Arrays.asList(
                new CardPaymentStrategy(),
                walletStrategy,
                new CashPaymentStrategy()
        );
        
        // Create notification service with observers
        NotificationService notificationService = new NotificationServiceImpl();
        notificationService.registerObserver(new PassengerNotificationObserver());
        notificationService.registerObserver(new DriverNotificationObserver());
        notificationService.registerObserver(new AnalyticsObserver());
        
        // Create services
        UserService userService = new UserServiceImpl(passengerRepository, driverRepository);
        
        FareService fareService = new FareServiceImpl(
                distanceStrategy, pricingStrategy, rideRepository);
        
        PaymentService paymentService = new PaymentServiceImpl(
                paymentRepository, paymentStrategies);
        
        DriverMatchingService driverMatchingService = new DriverMatchingServiceImpl(
                driverRepository, matchingStrategy);
        
        TrackingService trackingService = new TrackingServiceImpl(
                rideRepository, driverRepository, fareService, notificationService);
        
        RideService rideService = new RideServiceImpl(
                rideRepository, driverRepository, passengerRepository,
                driverMatchingService, fareService, paymentService, notificationService);
        
        return new RideSharingService(
                rideService, userService, fareService, 
                paymentService, trackingService, notificationService);
    }
    
    /**
     * Create a RideSharingService with custom configuration.
     */
    public static RideSharingService create(
            PricingStrategy pricingStrategy,
            DriverMatchingStrategy matchingStrategy,
            List<PaymentStrategy> paymentStrategies) {
        
        // Create repositories
        RideRepository rideRepository = new InMemoryRideRepository();
        DriverRepository driverRepository = new InMemoryDriverRepository();
        PassengerRepository passengerRepository = new InMemoryPassengerRepository();
        PaymentRepository paymentRepository = new InMemoryPaymentRepository();
        
        DistanceCalculationStrategy distanceStrategy = new HaversineDistanceStrategy();
        
        // Create notification service with observers
        NotificationService notificationService = new NotificationServiceImpl();
        notificationService.registerObserver(new PassengerNotificationObserver());
        notificationService.registerObserver(new DriverNotificationObserver());
        
        // Create services
        UserService userService = new UserServiceImpl(passengerRepository, driverRepository);
        
        FareService fareService = new FareServiceImpl(
                distanceStrategy, pricingStrategy, rideRepository);
        
        PaymentService paymentService = new PaymentServiceImpl(
                paymentRepository, paymentStrategies);
        
        DriverMatchingService driverMatchingService = new DriverMatchingServiceImpl(
                driverRepository, matchingStrategy);
        
        TrackingService trackingService = new TrackingServiceImpl(
                rideRepository, driverRepository, fareService, notificationService);
        
        RideService rideService = new RideServiceImpl(
                rideRepository, driverRepository, passengerRepository,
                driverMatchingService, fareService, paymentService, notificationService);
        
        return new RideSharingService(
                rideService, userService, fareService, 
                paymentService, trackingService, notificationService);
    }
}



