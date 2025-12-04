package linkedin.exceptions;

public class JobNotFoundException extends LinkedInException {
    
    public JobNotFoundException(String jobId) {
        super("Job not found with ID: " + jobId);
    }
}



