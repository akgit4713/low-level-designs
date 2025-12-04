package linkedin.repositories;

import linkedin.enums.ExperienceLevel;
import linkedin.enums.JobType;
import linkedin.models.JobApplication;
import linkedin.models.JobPosting;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends Repository<JobPosting, String> {
    List<JobPosting> findActiveJobs();
    List<JobPosting> findByCompanyId(String companyId);
    List<JobPosting> findByPostedById(String userId);
    List<JobPosting> findByTitleContaining(String title);
    List<JobPosting> findByLocation(String location);
    List<JobPosting> findByJobType(JobType jobType);
    List<JobPosting> findByExperienceLevel(ExperienceLevel level);
    
    // Application methods
    JobApplication saveApplication(JobApplication application);
    Optional<JobApplication> findApplicationById(String id);
    List<JobApplication> findApplicationsByJobId(String jobId);
    List<JobApplication> findApplicationsByApplicantId(String applicantId);
    Optional<JobApplication> findApplicationByJobAndApplicant(String jobId, String applicantId);
}



