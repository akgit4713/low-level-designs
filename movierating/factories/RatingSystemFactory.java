package movierating.factories;

import movierating.observers.RatingObserver;
import movierating.observers.StatisticsObserver;
import movierating.observers.UserLevelUpdateObserver;
import movierating.services.MovieService;
import movierating.services.RatingService;
import movierating.services.UserService;
import movierating.services.impl.InMemoryMovieService;
import movierating.services.impl.InMemoryUserService;
import movierating.services.impl.RatingServiceImpl;
import movierating.strategies.aggregation.RatingAggregationStrategy;
import movierating.strategies.aggregation.WeightedAverageStrategy;
import movierating.strategies.promotion.CompositePromotionStrategy;
import movierating.strategies.promotion.HelpfulnessPromotionStrategy;
import movierating.strategies.promotion.LevelPromotionStrategy;
import movierating.strategies.promotion.RatingCountPromotionStrategy;
import movierating.strategies.weight.HelpfulnessWeightStrategy;
import movierating.strategies.weight.LevelBasedWeightStrategy;
import movierating.strategies.weight.WeightCalculationStrategy;

/**
 * Factory for creating a complete rating system with all components wired together.
 * 
 * Factory Pattern: Encapsulates complex object creation.
 * Dependency Injection: Wires dependencies together.
 */
public class RatingSystemFactory {
    
    /**
     * Container for all rating system components.
     */
    public static class RatingSystem {
        private final MovieService movieService;
        private final UserService userService;
        private final RatingServiceImpl ratingService;
        private final StatisticsObserver statisticsObserver;
        private final LevelPromotionStrategy promotionStrategy;
        
        public RatingSystem(MovieService movieService, UserService userService, 
                            RatingServiceImpl ratingService, StatisticsObserver statisticsObserver,
                            LevelPromotionStrategy promotionStrategy) {
            this.movieService = movieService;
            this.userService = userService;
            this.ratingService = ratingService;
            this.statisticsObserver = statisticsObserver;
            this.promotionStrategy = promotionStrategy;
        }
        
        public MovieService getMovieService() {
            return movieService;
        }
        
        public UserService getUserService() {
            return userService;
        }
        
        public RatingService getRatingService() {
            return ratingService;
        }
        
        public StatisticsObserver getStatisticsObserver() {
            return statisticsObserver;
        }
        
        public LevelPromotionStrategy getPromotionStrategy() {
            return promotionStrategy;
        }
    }
    
    /**
     * Create a complete rating system with default configuration.
     * Uses weighted average aggregation with level-based weights.
     * @return A fully configured RatingSystem
     */
    public static RatingSystem createDefaultRatingSystem() {
        // Create services
        UserService userService = new InMemoryUserService();
        MovieService movieService = new InMemoryMovieService();
        
        // Create weight strategy
        WeightCalculationStrategy weightStrategy = new LevelBasedWeightStrategy();
        
        // Create aggregation strategy
        RatingAggregationStrategy aggregationStrategy = new WeightedAverageStrategy(weightStrategy);
        
        // Create rating service
        RatingServiceImpl ratingService = new RatingServiceImpl(userService, aggregationStrategy);
        
        // Create promotion strategy
        CompositePromotionStrategy promotionStrategy = new CompositePromotionStrategy(
                CompositePromotionStrategy.EvaluationMode.ANY_CAN_PROMOTE);
        promotionStrategy.addStrategy(new RatingCountPromotionStrategy());
        promotionStrategy.addStrategy(new HelpfulnessPromotionStrategy());
        
        // Create and register observers
        StatisticsObserver statisticsObserver = new StatisticsObserver();
        UserLevelUpdateObserver levelObserver = new UserLevelUpdateObserver(
                promotionStrategy,
                (user, oldLevel, newLevel) -> System.out.printf(
                        "🎉 %s has been %s from %s to %s!%n",
                        user.getUsername(),
                        newLevel.ordinal() > oldLevel.ordinal() ? "PROMOTED" : "DEMOTED",
                        oldLevel.getDisplayName(),
                        newLevel.getDisplayName()
                )
        );
        
        ratingService.registerObserver(statisticsObserver);
        ratingService.registerObserver(levelObserver);
        
        return new RatingSystem(movieService, userService, ratingService, 
                statisticsObserver, promotionStrategy);
    }
    
    /**
     * Create a rating system with helpfulness-weighted ratings.
     * Ratings that are marked helpful have more impact.
     * @return A fully configured RatingSystem
     */
    public static RatingSystem createHelpfulnessWeightedSystem() {
        UserService userService = new InMemoryUserService();
        MovieService movieService = new InMemoryMovieService();
        
        WeightCalculationStrategy weightStrategy = new HelpfulnessWeightStrategy();
        RatingAggregationStrategy aggregationStrategy = new WeightedAverageStrategy(weightStrategy);
        
        RatingServiceImpl ratingService = new RatingServiceImpl(userService, aggregationStrategy);
        
        LevelPromotionStrategy promotionStrategy = new HelpfulnessPromotionStrategy();
        
        StatisticsObserver statisticsObserver = new StatisticsObserver();
        UserLevelUpdateObserver levelObserver = new UserLevelUpdateObserver(
                promotionStrategy,
                (user, oldLevel, newLevel) -> System.out.printf(
                        "🎉 %s level changed: %s -> %s%n",
                        user.getUsername(), oldLevel.getDisplayName(), newLevel.getDisplayName()
                )
        );
        
        ratingService.registerObserver(statisticsObserver);
        ratingService.registerObserver(levelObserver);
        
        return new RatingSystem(movieService, userService, ratingService, 
                statisticsObserver, promotionStrategy);
    }
}


