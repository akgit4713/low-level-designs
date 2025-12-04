package bookmyshow.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents a user of the system.
 */
public class User {
    private final String id;
    private String name;
    private String email;
    private String phone;
    private final List<String> bookingIds;

    public User(String name, String email, String phone) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.bookingIds = new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public List<String> getBookingIds() { return Collections.unmodifiableList(bookingIds); }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }

    // Booking management
    public void addBooking(String bookingId) {
        bookingIds.add(bookingId);
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', email='%s'}", id, name, email);
    }
}



