package airline.strategies.search;

import airline.models.FlightSearchResult;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy that sorts flights by earliest departure time.
 */
public class EarliestDepartureStrategy implements FlightSearchStrategy {

    @Override
    public List<FlightSearchResult> sortFlights(List<FlightSearchResult> flights) {
        return flights.stream()
                .sorted(Comparator.comparing(f -> f.getFlight().getDepartureTime()))
                .collect(Collectors.toList());
    }

    @Override
    public String getDescription() {
        return "Earliest departure first";
    }
}



