package ridesharing.models;

import ridesharing.enums.PaymentMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a passenger who can request rides.
 */
public class Passenger extends User {
    private PaymentMethod preferredPaymentMethod;
    private final List<String> rideHistory;
    private Location savedHomeLocation;
    private Location savedWorkLocation;

    public Passenger(String userId, String name, String email, String phone) {
        super(userId, name, email, phone);
        this.rideHistory = new ArrayList<>();
        this.preferredPaymentMethod = PaymentMethod.CREDIT_CARD;
    }

    public PaymentMethod getPreferredPaymentMethod() {
        return preferredPaymentMethod;
    }

    public void setPreferredPaymentMethod(PaymentMethod preferredPaymentMethod) {
        this.preferredPaymentMethod = preferredPaymentMethod;
    }

    public List<String> getRideHistory() {
        return Collections.unmodifiableList(rideHistory);
    }

    public void addRideToHistory(String rideId) {
        rideHistory.add(rideId);
    }

    public Location getSavedHomeLocation() {
        return savedHomeLocation;
    }

    public void setSavedHomeLocation(Location savedHomeLocation) {
        this.savedHomeLocation = savedHomeLocation;
    }

    public Location getSavedWorkLocation() {
        return savedWorkLocation;
    }

    public void setSavedWorkLocation(Location savedWorkLocation) {
        this.savedWorkLocation = savedWorkLocation;
    }

    @Override
    public String toString() {
        return String.format("Passenger{id='%s', name='%s', rating=%.2f}", userId, name, rating);
    }
}



