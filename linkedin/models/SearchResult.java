package linkedin.models;

public class SearchResult {
    
    public enum ResultType {
        USER, COMPANY, JOB
    }
    
    private final String id;
    private final ResultType type;
    private final String title;
    private final String subtitle;
    private final String description;
    private double relevanceScore;
    
    public SearchResult(String id, ResultType type, String title, String subtitle, String description) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.relevanceScore = 0.0;
    }
    
    // Getters
    public String getId() { return id; }
    public ResultType getType() { return type; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getDescription() { return description; }
    public double getRelevanceScore() { return relevanceScore; }
    
    // Setters
    public void setRelevanceScore(double relevanceScore) { this.relevanceScore = relevanceScore; }
    
    @Override
    public String toString() {
        return "SearchResult{type=" + type + ", title='" + title + 
               "', score=" + relevanceScore + "}";
    }
}



