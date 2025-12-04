package linkedin;

import linkedin.observers.*;
import linkedin.repositories.*;
import linkedin.repositories.impl.*;
import linkedin.services.*;
import linkedin.strategies.search.*;

/**
 * Main facade class for the LinkedIn platform.
 * Provides a single entry point and handles dependency wiring.
 */
public class LinkedIn {
    
    private final UserService userService;
    private final ConnectionService connectionService;
    private final MessagingService messagingService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final SearchService searchService;
    private final NotificationService notificationService;
    
    // Repositories (accessible for testing)
    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository;
    private final MessageRepository messageRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final NotificationRepository notificationRepository;
    
    public LinkedIn() {
        // Initialize repositories
        this.userRepository = new InMemoryUserRepository();
        this.connectionRepository = new InMemoryConnectionRepository();
        this.messageRepository = new InMemoryMessageRepository();
        this.companyRepository = new InMemoryCompanyRepository();
        this.jobRepository = new InMemoryJobRepository();
        this.notificationRepository = new InMemoryNotificationRepository();
        
        // Initialize notification service and observers
        this.notificationService = new NotificationService(notificationRepository);
        setupNotificationObservers();
        
        // Initialize services with dependencies
        this.userService = new UserService(userRepository);
        this.connectionService = new ConnectionService(connectionRepository, userRepository, notificationService);
        this.messagingService = new MessagingService(messageRepository, userRepository, connectionRepository, notificationService);
        this.companyService = new CompanyService(companyRepository, userRepository);
        this.jobService = new JobService(jobRepository, userRepository, companyRepository, notificationService);
        
        // Initialize search service with default strategy
        SearchRankingStrategy defaultStrategy = new HybridRankingStrategy();
        this.searchService = new SearchService(userRepository, companyRepository, jobRepository, 
                                               connectionRepository, defaultStrategy);
    }
    
    /**
     * Constructor for dependency injection (useful for testing)
     */
    public LinkedIn(UserRepository userRepository,
                   ConnectionRepository connectionRepository,
                   MessageRepository messageRepository,
                   CompanyRepository companyRepository,
                   JobRepository jobRepository,
                   NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.connectionRepository = connectionRepository;
        this.messageRepository = messageRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.notificationRepository = notificationRepository;
        
        this.notificationService = new NotificationService(notificationRepository);
        setupNotificationObservers();
        
        this.userService = new UserService(userRepository);
        this.connectionService = new ConnectionService(connectionRepository, userRepository, notificationService);
        this.messagingService = new MessagingService(messageRepository, userRepository, connectionRepository, notificationService);
        this.companyService = new CompanyService(companyRepository, userRepository);
        this.jobService = new JobService(jobRepository, userRepository, companyRepository, notificationService);
        
        SearchRankingStrategy defaultStrategy = new HybridRankingStrategy();
        this.searchService = new SearchService(userRepository, companyRepository, jobRepository, 
                                               connectionRepository, defaultStrategy);
    }
    
    private void setupNotificationObservers() {
        // Add in-app notification observer
        notificationService.addObserver(new InAppNotificationObserver(notificationRepository));
        
        // Add email notification observer
        notificationService.addObserver(new EmailNotificationObserver(userRepository));
        
        // Add push notification observer
        notificationService.addObserver(new PushNotificationObserver());
    }
    
    // Service getters
    public UserService getUserService() { return userService; }
    public ConnectionService getConnectionService() { return connectionService; }
    public MessagingService getMessagingService() { return messagingService; }
    public CompanyService getCompanyService() { return companyService; }
    public JobService getJobService() { return jobService; }
    public SearchService getSearchService() { return searchService; }
    public NotificationService getNotificationService() { return notificationService; }
    
    /**
     * Change the search ranking strategy at runtime
     */
    public void setSearchRankingStrategy(SearchRankingStrategy strategy) {
        searchService.setRankingStrategy(strategy);
    }
    
    /**
     * Add a custom notification observer
     */
    public void addNotificationObserver(NotificationObserver observer) {
        notificationService.addObserver(observer);
    }
}



