package linkedin.models;

import linkedin.enums.ExperienceLevel;
import linkedin.enums.JobType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JobPosting {
    private final String id;
    private String title;
    private String description;
    private List<String> requirements;
    private String location;
    private JobType jobType;
    private ExperienceLevel experienceLevel;
    private String companyId;
    private String postedById; // Recruiter's user ID
    private String salaryRange;
    private final LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private final List<JobApplication> applications;
    
    private JobPosting(Builder builder) {
        this.id = UUID.randomUUID().toString();
        this.title = builder.title;
        this.description = builder.description;
        this.requirements = builder.requirements;
        this.location = builder.location;
        this.jobType = builder.jobType;
        this.experienceLevel = builder.experienceLevel;
        this.companyId = builder.companyId;
        this.postedById = builder.postedById;
        this.salaryRange = builder.salaryRange;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = builder.expiresAt;
        this.isActive = true;
        this.applications = new ArrayList<>();
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<String> getRequirements() { return new ArrayList<>(requirements); }
    public String getLocation() { return location; }
    public JobType getJobType() { return jobType; }
    public ExperienceLevel getExperienceLevel() { return experienceLevel; }
    public String getCompanyId() { return companyId; }
    public String getPostedById() { return postedById; }
    public String getSalaryRange() { return salaryRange; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isActive() { return isActive; }
    public List<JobApplication> getApplications() { return new ArrayList<>(applications); }
    
    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setRequirements(List<String> requirements) { this.requirements = requirements; }
    public void setLocation(String location) { this.location = location; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }
    public void setExperienceLevel(ExperienceLevel experienceLevel) { this.experienceLevel = experienceLevel; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setActive(boolean active) { isActive = active; }
    
    // Methods
    public void addApplication(JobApplication application) {
        this.applications.add(application);
    }
    
    public boolean hasApplied(String userId) {
        return applications.stream()
                .anyMatch(app -> app.getApplicantId().equals(userId));
    }
    
    @Override
    public String toString() {
        return "JobPosting{id='" + id + "', title='" + title + 
               "', company='" + companyId + "', active=" + isActive + "}";
    }
    
    // Builder Pattern
    public static class Builder {
        private String title;
        private String description;
        private List<String> requirements = new ArrayList<>();
        private String location;
        private JobType jobType = JobType.FULL_TIME;
        private ExperienceLevel experienceLevel = ExperienceLevel.MID;
        private String companyId;
        private String postedById;
        private String salaryRange;
        private LocalDateTime expiresAt;
        
        public Builder(String title, String companyId, String postedById) {
            this.title = title;
            this.companyId = companyId;
            this.postedById = postedById;
        }
        
        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }
        
        public Builder withRequirements(List<String> requirements) {
            this.requirements = requirements;
            return this;
        }
        
        public Builder addRequirement(String requirement) {
            this.requirements.add(requirement);
            return this;
        }
        
        public Builder withLocation(String location) {
            this.location = location;
            return this;
        }
        
        public Builder withJobType(JobType jobType) {
            this.jobType = jobType;
            return this;
        }
        
        public Builder withExperienceLevel(ExperienceLevel experienceLevel) {
            this.experienceLevel = experienceLevel;
            return this;
        }
        
        public Builder withSalaryRange(String salaryRange) {
            this.salaryRange = salaryRange;
            return this;
        }
        
        public Builder withExpiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }
        
        public JobPosting build() {
            return new JobPosting(this);
        }
    }
}



