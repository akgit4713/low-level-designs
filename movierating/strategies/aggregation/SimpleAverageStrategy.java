package movierating.strategies.aggregation;

import movierating.models.Rating;
import movierating.models.User;

import java.util.List;
import java.util.Map;

/**
 * Simple average aggregation strategy - all ratings have equal weight.
 */
public class SimpleAverageStrategy implements RatingAggregationStrategy {
    
    @Override
    public double calculateAggregateRating(List<Rating> ratings, Map<String, User> userMap) {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }
        
        double sum = 0;
        for (Rating rating : ratings) {
            sum += rating.getRatingValue().getValue();
        }
        
        return sum / ratings.size();
    }
    
    @Override
    public String getDescription() {
        return "Simple average - all ratings weighted equally";
    }
}


