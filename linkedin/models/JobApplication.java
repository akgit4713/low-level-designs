package linkedin.models;

import linkedin.enums.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class JobApplication {
    private final String id;
    private final String jobId;
    private final String applicantId;
    private String resumeUrl;
    private String coverLetter;
    private ApplicationStatus status;
    private final LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    
    public JobApplication(String jobId, String applicantId) {
        this.id = UUID.randomUUID().toString();
        this.jobId = jobId;
        this.applicantId = applicantId;
        this.status = ApplicationStatus.PENDING;
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public String getJobId() { return jobId; }
    public String getApplicantId() { return applicantId; }
    public String getResumeUrl() { return resumeUrl; }
    public String getCoverLetter() { return coverLetter; }
    public ApplicationStatus getStatus() { return status; }
    public LocalDateTime getAppliedAt() { return appliedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setResumeUrl(String resumeUrl) { this.resumeUrl = resumeUrl; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    
    public void updateStatus(ApplicationStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "JobApplication{id='" + id + "', jobId='" + jobId + 
               "', applicantId='" + applicantId + "', status=" + status + "}";
    }
}



