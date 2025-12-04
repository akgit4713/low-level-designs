package linkedin.repositories.impl;

import linkedin.models.Company;
import linkedin.repositories.CompanyRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryCompanyRepository implements CompanyRepository {
    
    private final Map<String, Company> companies = new ConcurrentHashMap<>();
    
    @Override
    public Company save(Company company) {
        companies.put(company.getId(), company);
        return company;
    }
    
    @Override
    public Optional<Company> findById(String id) {
        return Optional.ofNullable(companies.get(id));
    }
    
    @Override
    public List<Company> findAll() {
        return new ArrayList<>(companies.values());
    }
    
    @Override
    public void delete(String id) {
        companies.remove(id);
    }
    
    @Override
    public boolean existsById(String id) {
        return companies.containsKey(id);
    }
    
    @Override
    public Optional<Company> findByName(String name) {
        return companies.values().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();
    }
    
    @Override
    public List<Company> findByNameContaining(String name) {
        String lowerName = name.toLowerCase();
        return companies.values().stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerName))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Company> findByIndustry(String industry) {
        String lowerIndustry = industry.toLowerCase();
        return companies.values().stream()
                .filter(c -> c.getIndustry() != null &&
                        c.getIndustry().toLowerCase().contains(lowerIndustry))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Company> findByLocation(String location) {
        String lowerLocation = location.toLowerCase();
        return companies.values().stream()
                .filter(c -> c.getLocation() != null &&
                        c.getLocation().toLowerCase().contains(lowerLocation))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Company> findByEmployeeId(String employeeId) {
        return companies.values().stream()
                .filter(c -> c.hasEmployee(employeeId))
                .collect(Collectors.toList());
    }
}



