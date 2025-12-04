package carrental.strategies.search;

import carrental.models.Car;
import carrental.models.SearchCriteria;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic search strategy that filters cars based on all criteria.
 */
public class BasicSearchStrategy implements SearchStrategy {

    @Override
    public List<Car> search(List<Car> cars, SearchCriteria criteria) {
        return cars.stream()
            .filter(car -> car.isAvailable())
            .filter(car -> criteria.matches(car))
            .collect(Collectors.toList());
    }

    @Override
    public String getStrategyName() {
        return "Basic Search";
    }
}



