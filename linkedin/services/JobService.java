package linkedin.services;

import linkedin.enums.ApplicationStatus;
import linkedin.enums.ExperienceLevel;
import linkedin.enums.JobType;
import linkedin.exceptions.JobNotFoundException;
import linkedin.exceptions.LinkedInException;
import linkedin.exceptions.UserNotFoundException;
import linkedin.exceptions.ValidationException;
import linkedin.factories.NotificationFactory;
import linkedin.models.*;
import linkedin.repositories.CompanyRepository;
import linkedin.repositories.JobRepository;
import linkedin.repositories.UserRepository;

import java.util.List;

/**
 * Service for managing job postings and applications.
 */
public class JobService {
    
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;
    
    public JobService(JobRepository jobRepository,
                     UserRepository userRepository,
                     CompanyRepository companyRepository,
                     NotificationService notificationService) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.notificationService = notificationService;
    }
    
    public JobPosting createJobPosting(String title, String description, String companyId, 
                                        String recruiterId, JobType jobType, 
                                        ExperienceLevel experienceLevel, String location) {
        validateJobPosting(title, companyId, recruiterId);
        
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new LinkedInException("Company not found: " + companyId));
        
        // Verify recruiter is an employee
        if (!company.hasEmployee(recruiterId)) {
            throw new ValidationException("Only company employees can post jobs");
        }
        
        JobPosting job = new JobPosting.Builder(title, companyId, recruiterId)
                .withDescription(description)
                .withJobType(jobType)
                .withExperienceLevel(experienceLevel)
                .withLocation(location)
                .build();
        
        jobRepository.save(job);
        
        // Notify company followers
        notifyFollowersAboutJob(company, job);
        
        return job;
    }
    
    public JobPosting getJobById(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));
    }
    
    public void updateJobPosting(String jobId, String recruiterId, String description, 
                                 String location, String salaryRange) {
        JobPosting job = getJobById(jobId);
        
        if (!job.getPostedById().equals(recruiterId)) {
            throw new ValidationException("Only the job poster can update this posting");
        }
        
        if (description != null) job.setDescription(description);
        if (location != null) job.setLocation(location);
        if (salaryRange != null) job.setSalaryRange(salaryRange);
        
        jobRepository.save(job);
    }
    
    public void closeJobPosting(String jobId, String recruiterId) {
        JobPosting job = getJobById(jobId);
        
        if (!job.getPostedById().equals(recruiterId)) {
            throw new ValidationException("Only the job poster can close this posting");
        }
        
        job.setActive(false);
        jobRepository.save(job);
    }
    
    public JobApplication applyForJob(String jobId, String applicantId, 
                                      String resumeUrl, String coverLetter) {
        JobPosting job = getJobById(jobId);
        validateUser(applicantId);
        
        if (!job.isActive()) {
            throw new ValidationException("This job posting is no longer active");
        }
        
        if (job.hasApplied(applicantId)) {
            throw new ValidationException("You have already applied for this job");
        }
        
        // Applicant should not be the recruiter
        if (job.getPostedById().equals(applicantId)) {
            throw new ValidationException("Cannot apply to your own job posting");
        }
        
        JobApplication application = new JobApplication(jobId, applicantId);
        application.setResumeUrl(resumeUrl);
        application.setCoverLetter(coverLetter);
        
        jobRepository.saveApplication(application);
        job.addApplication(application);
        jobRepository.save(job);
        
        // Notify recruiter
        User applicant = userRepository.findById(applicantId).get();
        User recruiter = userRepository.findById(job.getPostedById()).get();
        Notification notification = NotificationFactory.createJobApplicationNotification(applicant, recruiter, job);
        notificationService.notify(notification);
        
        return application;
    }
    
    public void updateApplicationStatus(String applicationId, String recruiterId, 
                                       ApplicationStatus newStatus) {
        JobApplication application = jobRepository.findApplicationById(applicationId)
                .orElseThrow(() -> new LinkedInException("Application not found"));
        
        JobPosting job = getJobById(application.getJobId());
        
        if (!job.getPostedById().equals(recruiterId)) {
            throw new ValidationException("Only the job poster can update application status");
        }
        
        application.updateStatus(newStatus);
        jobRepository.saveApplication(application);
        
        // Notify applicant
        User applicant = userRepository.findById(application.getApplicantId()).get();
        Notification notification = NotificationFactory.createApplicationStatusNotification(
                applicant, job, newStatus.name());
        notificationService.notify(notification);
    }
    
    public List<JobPosting> searchJobs(String query) {
        return jobRepository.findByTitleContaining(query);
    }
    
    public List<JobPosting> searchJobsByLocation(String location) {
        return jobRepository.findByLocation(location);
    }
    
    public List<JobPosting> searchJobsByType(JobType jobType) {
        return jobRepository.findByJobType(jobType);
    }
    
    public List<JobPosting> searchJobsByExperience(ExperienceLevel level) {
        return jobRepository.findByExperienceLevel(level);
    }
    
    public List<JobPosting> getActiveJobs() {
        return jobRepository.findActiveJobs();
    }
    
    public List<JobPosting> getJobsByCompany(String companyId) {
        return jobRepository.findByCompanyId(companyId);
    }
    
    public List<JobPosting> getJobsPostedByUser(String userId) {
        return jobRepository.findByPostedById(userId);
    }
    
    public List<JobApplication> getApplicationsForJob(String jobId, String recruiterId) {
        JobPosting job = getJobById(jobId);
        
        if (!job.getPostedById().equals(recruiterId)) {
            throw new ValidationException("Only the job poster can view applications");
        }
        
        return jobRepository.findApplicationsByJobId(jobId);
    }
    
    public List<JobApplication> getApplicationsByUser(String userId) {
        validateUser(userId);
        return jobRepository.findApplicationsByApplicantId(userId);
    }
    
    private void notifyFollowersAboutJob(Company company, JobPosting job) {
        for (String followerId : company.getFollowerIds()) {
            userRepository.findById(followerId).ifPresent(follower -> {
                Notification notification = NotificationFactory.createJobPostedNotification(
                        follower, job, company);
                notificationService.notify(notification);
            });
        }
    }
    
    private void validateJobPosting(String title, String companyId, String recruiterId) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Job title is required");
        }
        if (companyId == null) {
            throw new ValidationException("Company is required");
        }
        validateUser(recruiterId);
    }
    
    private void validateUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}



