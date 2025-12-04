package linkedin.models;

import java.util.UUID;

public class Skill {
    private final String id;
    private String name;
    private int endorsementCount;
    
    public Skill(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.endorsementCount = 0;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getEndorsementCount() { return endorsementCount; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    
    public void addEndorsement() {
        this.endorsementCount++;
    }
    
    @Override
    public String toString() {
        return "Skill{name='" + name + "', endorsements=" + endorsementCount + "}";
    }
}



