package airline.strategies.search;

import airline.models.FlightSearchResult;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy that sorts flights by shortest duration first.
 */
public class FastestFlightStrategy implements FlightSearchStrategy {

    @Override
    public List<FlightSearchResult> sortFlights(List<FlightSearchResult> flights) {
        return flights.stream()
                .sorted(Comparator.comparing(FlightSearchResult::getDuration))
                .collect(Collectors.toList());
    }

    @Override
    public String getDescription() {
        return "Fastest flights first";
    }
}



