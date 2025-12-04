package linkedin.models;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    private final String userId;
    private String headline;
    private String summary;
    private String profilePictureUrl;
    private String location;
    private String industry;
    private List<Experience> experiences;
    private List<Education> educations;
    private List<Skill> skills;
    
    public Profile(String userId) {
        this.userId = userId;
        this.experiences = new ArrayList<>();
        this.educations = new ArrayList<>();
        this.skills = new ArrayList<>();
    }
    
    // Getters
    public String getUserId() { return userId; }
    public String getHeadline() { return headline; }
    public String getSummary() { return summary; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public String getLocation() { return location; }
    public String getIndustry() { return industry; }
    public List<Experience> getExperiences() { return new ArrayList<>(experiences); }
    public List<Education> getEducations() { return new ArrayList<>(educations); }
    public List<Skill> getSkills() { return new ArrayList<>(skills); }
    
    // Setters
    public void setHeadline(String headline) { this.headline = headline; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public void setLocation(String location) { this.location = location; }
    public void setIndustry(String industry) { this.industry = industry; }
    
    // Add methods
    public void addExperience(Experience experience) {
        this.experiences.add(experience);
    }
    
    public void removeExperience(String experienceId) {
        this.experiences.removeIf(e -> e.getId().equals(experienceId));
    }
    
    public void addEducation(Education education) {
        this.educations.add(education);
    }
    
    public void removeEducation(String educationId) {
        this.educations.removeIf(e -> e.getId().equals(educationId));
    }
    
    public void addSkill(Skill skill) {
        this.skills.add(skill);
    }
    
    public void removeSkill(String skillId) {
        this.skills.removeIf(s -> s.getId().equals(skillId));
    }
    
    @Override
    public String toString() {
        return "Profile{userId='" + userId + "', headline='" + headline + 
               "', location='" + location + "'}";
    }
}



