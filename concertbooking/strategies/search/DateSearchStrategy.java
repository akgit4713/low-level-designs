package concertbooking.strategies.search;

import concertbooking.models.Concert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy to find concerts by date
 */
public class DateSearchStrategy implements SearchStrategy {
    
    private static final DateTimeFormatter[] FORMATTERS = {
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy")
    };
    
    @Override
    public List<Concert> search(List<Concert> concerts, String query) {
        if (query == null || query.isBlank()) {
            return concerts;
        }
        
        LocalDate searchDate = parseDate(query.trim());
        if (searchDate == null) {
            return List.of(); // Return empty if date format is invalid
        }
        
        return concerts.stream()
            .filter(concert -> concert.getDateTime().toLocalDate().equals(searchDate))
            .collect(Collectors.toList());
    }
    
    private LocalDate parseDate(String dateStr) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }
        return null;
    }
    
    @Override
    public String getStrategyName() {
        return "Date Search";
    }
}



