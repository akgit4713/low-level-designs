package linkedin.models;

import java.util.UUID;

public class Education {
    private final String id;
    private String institution;
    private String degree;
    private String fieldOfStudy;
    private int startYear;
    private int endYear;
    private String description;
    private String grade;
    
    public Education(String institution, String degree, String fieldOfStudy, int startYear) {
        this.id = UUID.randomUUID().toString();
        this.institution = institution;
        this.degree = degree;
        this.fieldOfStudy = fieldOfStudy;
        this.startYear = startYear;
    }
    
    // Getters
    public String getId() { return id; }
    public String getInstitution() { return institution; }
    public String getDegree() { return degree; }
    public String getFieldOfStudy() { return fieldOfStudy; }
    public int getStartYear() { return startYear; }
    public int getEndYear() { return endYear; }
    public String getDescription() { return description; }
    public String getGrade() { return grade; }
    
    // Setters
    public void setInstitution(String institution) { this.institution = institution; }
    public void setDegree(String degree) { this.degree = degree; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }
    public void setStartYear(int startYear) { this.startYear = startYear; }
    public void setEndYear(int endYear) { this.endYear = endYear; }
    public void setDescription(String description) { this.description = description; }
    public void setGrade(String grade) { this.grade = grade; }
    
    @Override
    public String toString() {
        return "Education{institution='" + institution + "', degree='" + degree + 
               "', fieldOfStudy='" + fieldOfStudy + "'}";
    }
}



