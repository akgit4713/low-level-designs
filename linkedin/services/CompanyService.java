package linkedin.services;

import linkedin.exceptions.LinkedInException;
import linkedin.exceptions.UserNotFoundException;
import linkedin.exceptions.ValidationException;
import linkedin.models.Company;
import linkedin.repositories.CompanyRepository;
import linkedin.repositories.UserRepository;

import java.util.List;

/**
 * Service for managing companies.
 */
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    
    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }
    
    public Company createCompany(String name, String industry, String creatorUserId) {
        validateCompanyCreation(name, creatorUserId);
        
        if (companyRepository.findByName(name).isPresent()) {
            throw new ValidationException("Company with this name already exists: " + name);
        }
        
        Company company = new Company(name, industry);
        company.addEmployee(creatorUserId); // Creator becomes first employee
        
        return companyRepository.save(company);
    }
    
    public Company getCompanyById(String companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new LinkedInException("Company not found: " + companyId));
    }
    
    public Company getCompanyByName(String name) {
        return companyRepository.findByName(name)
                .orElseThrow(() -> new LinkedInException("Company not found: " + name));
    }
    
    public void updateCompanyDetails(String companyId, String description, String website, 
                                     String location, String size) {
        Company company = getCompanyById(companyId);
        
        if (description != null) company.setDescription(description);
        if (website != null) company.setWebsite(website);
        if (location != null) company.setLocation(location);
        if (size != null) company.setSize(size);
        
        companyRepository.save(company);
    }
    
    public void addEmployee(String companyId, String userId) {
        Company company = getCompanyById(companyId);
        validateUser(userId);
        
        company.addEmployee(userId);
        companyRepository.save(company);
    }
    
    public void removeEmployee(String companyId, String userId) {
        Company company = getCompanyById(companyId);
        company.removeEmployee(userId);
        companyRepository.save(company);
    }
    
    public void followCompany(String companyId, String userId) {
        Company company = getCompanyById(companyId);
        validateUser(userId);
        
        company.addFollower(userId);
        companyRepository.save(company);
    }
    
    public void unfollowCompany(String companyId, String userId) {
        Company company = getCompanyById(companyId);
        company.removeFollower(userId);
        companyRepository.save(company);
    }
    
    public List<Company> searchByName(String name) {
        return companyRepository.findByNameContaining(name);
    }
    
    public List<Company> searchByIndustry(String industry) {
        return companyRepository.findByIndustry(industry);
    }
    
    public List<Company> getCompaniesByEmployee(String userId) {
        return companyRepository.findByEmployeeId(userId);
    }
    
    public boolean isEmployee(String companyId, String userId) {
        Company company = getCompanyById(companyId);
        return company.hasEmployee(userId);
    }
    
    private void validateCompanyCreation(String name, String creatorUserId) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Company name is required");
        }
        validateUser(creatorUserId);
    }
    
    private void validateUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}



