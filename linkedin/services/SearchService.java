package linkedin.services;

import linkedin.enums.ConnectionStatus;
import linkedin.models.*;
import linkedin.repositories.*;
import linkedin.strategies.search.SearchContext;
import linkedin.strategies.search.SearchRankingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Unified search service for users, companies, and jobs.
 * Uses Strategy pattern for ranking results.
 */
public class SearchService {
    
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ConnectionRepository connectionRepository;
    private SearchRankingStrategy rankingStrategy;
    
    public SearchService(UserRepository userRepository,
                        CompanyRepository companyRepository,
                        JobRepository jobRepository,
                        ConnectionRepository connectionRepository,
                        SearchRankingStrategy rankingStrategy) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.connectionRepository = connectionRepository;
        this.rankingStrategy = rankingStrategy;
    }
    
    public void setRankingStrategy(SearchRankingStrategy strategy) {
        this.rankingStrategy = strategy;
    }
    
    public List<SearchResult> searchAll(String query, String searcherId) {
        List<SearchResult> results = new ArrayList<>();
        
        results.addAll(searchUsers(query));
        results.addAll(searchCompanies(query));
        results.addAll(searchJobs(query));
        
        SearchContext context = buildSearchContext(searcherId, query);
        return rankingStrategy.rank(results, context);
    }
    
    public List<SearchResult> searchUsersOnly(String query, String searcherId) {
        List<SearchResult> results = searchUsers(query);
        SearchContext context = buildSearchContext(searcherId, query);
        return rankingStrategy.rank(results, context);
    }
    
    public List<SearchResult> searchCompaniesOnly(String query, String searcherId) {
        List<SearchResult> results = searchCompanies(query);
        SearchContext context = buildSearchContext(searcherId, query);
        return rankingStrategy.rank(results, context);
    }
    
    public List<SearchResult> searchJobsOnly(String query, String searcherId) {
        List<SearchResult> results = searchJobs(query);
        SearchContext context = buildSearchContext(searcherId, query);
        return rankingStrategy.rank(results, context);
    }
    
    private List<SearchResult> searchUsers(String query) {
        List<User> users = userRepository.findByNameContaining(query);
        
        return users.stream()
                .map(user -> {
                    Profile profile = user.getProfile();
                    String subtitle = profile != null ? profile.getHeadline() : "";
                    String description = profile != null ? 
                            (profile.getLocation() != null ? profile.getLocation() : "") + " " +
                            (profile.getIndustry() != null ? profile.getIndustry() : "") : "";
                    
                    return new SearchResult(
                            user.getId(),
                            SearchResult.ResultType.USER,
                            user.getName(),
                            subtitle,
                            description
                    );
                })
                .collect(Collectors.toList());
    }
    
    private List<SearchResult> searchCompanies(String query) {
        List<Company> companies = companyRepository.findByNameContaining(query);
        
        return companies.stream()
                .map(company -> new SearchResult(
                        company.getId(),
                        SearchResult.ResultType.COMPANY,
                        company.getName(),
                        company.getIndustry(),
                        company.getDescription() != null ? company.getDescription() : ""
                ))
                .collect(Collectors.toList());
    }
    
    private List<SearchResult> searchJobs(String query) {
        List<JobPosting> jobs = jobRepository.findByTitleContaining(query);
        
        return jobs.stream()
                .filter(JobPosting::isActive)
                .map(job -> {
                    String companyName = companyRepository.findById(job.getCompanyId())
                            .map(Company::getName)
                            .orElse("");
                    
                    return new SearchResult(
                            job.getId(),
                            SearchResult.ResultType.JOB,
                            job.getTitle(),
                            companyName + " - " + job.getLocation(),
                            job.getDescription() != null ? job.getDescription() : ""
                    );
                })
                .collect(Collectors.toList());
    }
    
    private SearchContext buildSearchContext(String searcherId, String query) {
        SearchContext.Builder builder = new SearchContext.Builder(searcherId, query);
        
        // Get searcher's connections
        Set<String> connectionIds = connectionRepository
                .findByUserIdAndStatus(searcherId, ConnectionStatus.ACCEPTED)
                .stream()
                .map(conn -> conn.getOtherUserId(searcherId))
                .collect(Collectors.toSet());
        builder.withConnectionIds(connectionIds);
        
        // Get searcher's profile info
        userRepository.findById(searcherId).ifPresent(user -> {
            Profile profile = user.getProfile();
            if (profile != null) {
                if (profile.getLocation() != null) {
                    builder.withSearcherLocation(profile.getLocation());
                }
                if (profile.getIndustry() != null) {
                    builder.withSearcherIndustry(profile.getIndustry());
                }
            }
        });
        
        return builder.build();
    }
}



