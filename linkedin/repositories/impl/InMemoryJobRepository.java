package linkedin.repositories.impl;

import linkedin.enums.ExperienceLevel;
import linkedin.enums.JobType;
import linkedin.models.JobApplication;
import linkedin.models.JobPosting;
import linkedin.repositories.JobRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryJobRepository implements JobRepository {
    
    private final Map<String, JobPosting> jobs = new ConcurrentHashMap<>();
    private final Map<String, JobApplication> applications = new ConcurrentHashMap<>();
    
    @Override
    public JobPosting save(JobPosting job) {
        jobs.put(job.getId(), job);
        return job;
    }
    
    @Override
    public Optional<JobPosting> findById(String id) {
        return Optional.ofNullable(jobs.get(id));
    }
    
    @Override
    public List<JobPosting> findAll() {
        return new ArrayList<>(jobs.values());
    }
    
    @Override
    public void delete(String id) {
        jobs.remove(id);
    }
    
    @Override
    public boolean existsById(String id) {
        return jobs.containsKey(id);
    }
    
    @Override
    public List<JobPosting> findActiveJobs() {
        return jobs.values().stream()
                .filter(JobPosting::isActive)
                .sorted(Comparator.comparing(JobPosting::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JobPosting> findByCompanyId(String companyId) {
        return jobs.values().stream()
                .filter(j -> j.getCompanyId().equals(companyId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JobPosting> findByPostedById(String userId) {
        return jobs.values().stream()
                .filter(j -> j.getPostedById().equals(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JobPosting> findByTitleContaining(String title) {
        String lowerTitle = title.toLowerCase();
        return jobs.values().stream()
                .filter(j -> j.isActive() && j.getTitle().toLowerCase().contains(lowerTitle))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JobPosting> findByLocation(String location) {
        String lowerLocation = location.toLowerCase();
        return jobs.values().stream()
                .filter(j -> j.isActive() && j.getLocation() != null &&
                        j.getLocation().toLowerCase().contains(lowerLocation))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JobPosting> findByJobType(JobType jobType) {
        return jobs.values().stream()
                .filter(j -> j.isActive() && j.getJobType() == jobType)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JobPosting> findByExperienceLevel(ExperienceLevel level) {
        return jobs.values().stream()
                .filter(j -> j.isActive() && j.getExperienceLevel() == level)
                .collect(Collectors.toList());
    }
    
    @Override
    public JobApplication saveApplication(JobApplication application) {
        applications.put(application.getId(), application);
        return application;
    }
    
    @Override
    public Optional<JobApplication> findApplicationById(String id) {
        return Optional.ofNullable(applications.get(id));
    }
    
    @Override
    public List<JobApplication> findApplicationsByJobId(String jobId) {
        return applications.values().stream()
                .filter(a -> a.getJobId().equals(jobId))
                .sorted(Comparator.comparing(JobApplication::getAppliedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public List<JobApplication> findApplicationsByApplicantId(String applicantId) {
        return applications.values().stream()
                .filter(a -> a.getApplicantId().equals(applicantId))
                .sorted(Comparator.comparing(JobApplication::getAppliedAt).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<JobApplication> findApplicationByJobAndApplicant(String jobId, String applicantId) {
        return applications.values().stream()
                .filter(a -> a.getJobId().equals(jobId) && a.getApplicantId().equals(applicantId))
                .findFirst();
    }
}



