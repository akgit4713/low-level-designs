package bookmyshow.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a city where theaters are located.
 */
public class City {
    private final String id;
    private String name;
    private String state;
    private String country;
    private final List<String> theaterIds;

    public City(String name, String state, String country) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.state = state;
        this.country = country;
        this.theaterIds = new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public List<String> getTheaterIds() { return Collections.unmodifiableList(theaterIds); }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setState(String state) { this.state = state; }
    public void setCountry(String country) { this.country = country; }

    // Theater management
    public void addTheater(String theaterId) {
        if (!theaterIds.contains(theaterId)) {
            theaterIds.add(theaterId);
        }
    }

    public void removeTheater(String theaterId) {
        theaterIds.remove(theaterId);
    }

    @Override
    public String toString() {
        return String.format("City{id='%s', name='%s', state='%s'}", id, name, state);
    }
}



