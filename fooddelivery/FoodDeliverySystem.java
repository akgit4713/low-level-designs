package fooddelivery;

import fooddelivery.factories.*;
import fooddelivery.observers.*;
import fooddelivery.repositories.*;
import fooddelivery.repositories.impl.*;
import fooddelivery.services.*;
import fooddelivery.services.impl.*;
import fooddelivery.strategies.delivery.DeliveryAssignmentStrategy;
import fooddelivery.strategies.pricing.*;
import fooddelivery.strategies.search.RestaurantSearchStrategy;

/**
 * Main facade for the Food Delivery System.
 * Wires together all components using Dependency Injection.
 */
public class FoodDeliverySystem {
    
    // Repositories
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    
    // Services
    private final CustomerService customerService;
    private final RestaurantService restaurantService;
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final DeliveryService deliveryService;
    
    // Event Publisher
    private final OrderEventPublisher eventPublisher;
    
    // Factories
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final SearchStrategyFactory searchStrategyFactory;
    private final DeliveryStrategyFactory deliveryStrategyFactory;
    
    public FoodDeliverySystem() {
        // Initialize repositories
        this.userRepository = new InMemoryUserRepository();
        this.restaurantRepository = new InMemoryRestaurantRepository();
        this.menuItemRepository = new InMemoryMenuItemRepository();
        this.orderRepository = new InMemoryOrderRepository();
        this.cartRepository = new InMemoryCartRepository();
        this.paymentRepository = new InMemoryPaymentRepository();
        this.deliveryRepository = new InMemoryDeliveryRepository();
        this.deliveryAgentRepository = new InMemoryDeliveryAgentRepository();
        
        // Initialize factories
        this.paymentStrategyFactory = new PaymentStrategyFactory();
        this.searchStrategyFactory = new SearchStrategyFactory();
        this.deliveryStrategyFactory = new DeliveryStrategyFactory();
        
        // Initialize event publisher
        this.eventPublisher = new OrderEventPublisher();
        
        // Initialize strategies
        RestaurantSearchStrategy searchStrategy = searchStrategyFactory.createStrategy(
            SearchStrategyFactory.SearchType.NEAREST);
        DeliveryAssignmentStrategy assignmentStrategy = deliveryStrategyFactory.createStrategy(
            DeliveryStrategyFactory.AssignmentType.NEAREST);
        DeliveryFeeStrategy deliveryFeeStrategy = new DistanceBasedFeeStrategy();
        
        // Initialize services with dependency injection
        this.customerService = new CustomerServiceImpl(userRepository);
        
        this.restaurantService = new RestaurantServiceImpl(
            restaurantRepository, menuItemRepository, searchStrategy);
        
        this.cartService = new CartServiceImpl(cartRepository);
        
        this.paymentService = new PaymentServiceImpl(
            paymentRepository, paymentStrategyFactory.getAllStrategies());
        
        this.deliveryService = new DeliveryServiceImpl(
            deliveryAgentRepository, deliveryRepository, orderRepository,
            assignmentStrategy, eventPublisher);
        
        this.orderService = new OrderServiceImpl(
            orderRepository, cartRepository, restaurantRepository,
            paymentService, deliveryService, deliveryFeeStrategy, eventPublisher);
    }
    
    // Getters for services
    public CustomerService getCustomerService() {
        return customerService;
    }
    
    public RestaurantService getRestaurantService() {
        return restaurantService;
    }
    
    public CartService getCartService() {
        return cartService;
    }
    
    public OrderService getOrderService() {
        return orderService;
    }
    
    public PaymentService getPaymentService() {
        return paymentService;
    }
    
    public DeliveryService getDeliveryService() {
        return deliveryService;
    }
    
    public OrderEventPublisher getEventPublisher() {
        return eventPublisher;
    }
    
    // Factory access for customization
    public PaymentStrategyFactory getPaymentStrategyFactory() {
        return paymentStrategyFactory;
    }
    
    public SearchStrategyFactory getSearchStrategyFactory() {
        return searchStrategyFactory;
    }
    
    public DeliveryStrategyFactory getDeliveryStrategyFactory() {
        return deliveryStrategyFactory;
    }
    
    /**
     * Register notification observers for real-time updates.
     */
    public void registerCustomerObserver(String customerId) {
        eventPublisher.registerObserver(new CustomerNotificationObserver(customerId));
    }
    
    public void registerRestaurantObserver(String restaurantId) {
        eventPublisher.registerObserver(new RestaurantNotificationObserver(restaurantId));
    }
    
    public void registerDeliveryAgentObserver(String agentId) {
        eventPublisher.registerObserver(new DeliveryAgentNotificationObserver(agentId));
    }
    
    /**
     * Shutdown the system gracefully.
     */
    public void shutdown() {
        eventPublisher.shutdown();
    }
}



