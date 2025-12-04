package linkedin.models;

import java.time.LocalDate;
import java.util.UUID;

public class Experience {
    private final String id;
    private String title;
    private String company;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private boolean isCurrent;
    
    public Experience(String title, String company, LocalDate startDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.company = company;
        this.startDate = startDate;
        this.isCurrent = true;
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getDescription() { return description; }
    public boolean isCurrent() { return isCurrent; }
    
    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setCompany(String company) { this.company = company; }
    public void setLocation(String location) { this.location = location; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { 
        this.endDate = endDate;
        if (endDate != null) {
            this.isCurrent = false;
        }
    }
    public void setDescription(String description) { this.description = description; }
    public void setCurrent(boolean current) { isCurrent = current; }
    
    @Override
    public String toString() {
        return "Experience{title='" + title + "', company='" + company + 
               "', current=" + isCurrent + "}";
    }
}



