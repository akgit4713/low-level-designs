package linkedin.strategies.search;

import java.util.HashSet;
import java.util.Set;

/**
 * Context object containing information relevant to search ranking.
 */
public class SearchContext {
    private final String searcherId;
    private final String query;
    private final Set<String> connectionIds;
    private final String searcherLocation;
    private final String searcherIndustry;
    
    private SearchContext(Builder builder) {
        this.searcherId = builder.searcherId;
        this.query = builder.query;
        this.connectionIds = builder.connectionIds;
        this.searcherLocation = builder.searcherLocation;
        this.searcherIndustry = builder.searcherIndustry;
    }
    
    public String getSearcherId() { return searcherId; }
    public String getQuery() { return query; }
    public Set<String> getConnectionIds() { return new HashSet<>(connectionIds); }
    public String getSearcherLocation() { return searcherLocation; }
    public String getSearcherIndustry() { return searcherIndustry; }
    
    public boolean isConnection(String userId) {
        return connectionIds.contains(userId);
    }
    
    public static class Builder {
        private String searcherId;
        private String query;
        private Set<String> connectionIds = new HashSet<>();
        private String searcherLocation;
        private String searcherIndustry;
        
        public Builder(String searcherId, String query) {
            this.searcherId = searcherId;
            this.query = query;
        }
        
        public Builder withConnectionIds(Set<String> connectionIds) {
            this.connectionIds = connectionIds;
            return this;
        }
        
        public Builder withSearcherLocation(String location) {
            this.searcherLocation = location;
            return this;
        }
        
        public Builder withSearcherIndustry(String industry) {
            this.searcherIndustry = industry;
            return this;
        }
        
        public SearchContext build() {
            return new SearchContext(this);
        }
    }
}



