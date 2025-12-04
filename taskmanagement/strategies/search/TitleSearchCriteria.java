package taskmanagement.strategies.search;

import taskmanagement.models.Task;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Criteria for filtering tasks by title (supports partial match and regex).
 */
public class TitleSearchCriteria implements SearchCriteria {
    
    private final String searchTerm;
    private final Pattern pattern;
    private final boolean caseSensitive;
    private final boolean useRegex;

    /**
     * Creates criteria for case-insensitive partial title match.
     */
    public TitleSearchCriteria(String searchTerm) {
        this(searchTerm, false, false);
    }

    /**
     * Creates criteria with specified options.
     */
    public TitleSearchCriteria(String searchTerm, boolean caseSensitive, boolean useRegex) {
        this.searchTerm = Objects.requireNonNull(searchTerm, "Search term cannot be null");
        this.caseSensitive = caseSensitive;
        this.useRegex = useRegex;
        
        if (useRegex) {
            int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
            this.pattern = Pattern.compile(searchTerm, flags);
        } else {
            this.pattern = null;
        }
    }

    @Override
    public boolean matches(Task task) {
        String title = task.getTitle();
        if (title == null) {
            return false;
        }
        
        if (useRegex) {
            return pattern.matcher(title).find();
        }
        
        if (caseSensitive) {
            return title.contains(searchTerm);
        }
        
        return title.toLowerCase().contains(searchTerm.toLowerCase());
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder("Tasks with title ");
        if (useRegex) {
            sb.append("matching pattern: ").append(searchTerm);
        } else {
            sb.append("containing: '").append(searchTerm).append("'");
        }
        if (!caseSensitive && !useRegex) {
            sb.append(" (case-insensitive)");
        }
        return sb.toString();
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public boolean isUseRegex() {
        return useRegex;
    }
}



