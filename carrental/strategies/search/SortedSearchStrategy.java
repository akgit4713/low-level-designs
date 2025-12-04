package carrental.strategies.search;

import carrental.models.Car;
import carrental.models.SearchCriteria;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy that returns results sorted by price (lowest first).
 */
public class SortedSearchStrategy implements SearchStrategy {

    public enum SortBy {
        PRICE_ASC,
        PRICE_DESC,
        YEAR_DESC,
        YEAR_ASC
    }

    private final SortBy sortBy;

    public SortedSearchStrategy() {
        this(SortBy.PRICE_ASC);
    }

    public SortedSearchStrategy(SortBy sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public List<Car> search(List<Car> cars, SearchCriteria criteria) {
        return cars.stream()
            .filter(car -> car.isAvailable())
            .filter(car -> criteria.matches(car))
            .sorted(getComparator())
            .collect(Collectors.toList());
    }

    private Comparator<Car> getComparator() {
        switch (sortBy) {
            case PRICE_DESC:
                return Comparator.comparing(Car::getEffectivePricePerDay).reversed();
            case YEAR_DESC:
                return Comparator.comparingInt(Car::getYear).reversed();
            case YEAR_ASC:
                return Comparator.comparingInt(Car::getYear);
            case PRICE_ASC:
            default:
                return Comparator.comparing(Car::getEffectivePricePerDay);
        }
    }

    @Override
    public String getStrategyName() {
        return "Sorted Search (by " + sortBy + ")";
    }
}



