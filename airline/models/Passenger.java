package airline.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a passenger with personal details and travel documents.
 */
public class Passenger {
    private final String id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final LocalDate dateOfBirth;
    private final String passportNumber;
    private final String nationality;
    private final List<Baggage> baggage;

    private Passenger(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.phone = builder.phone;
        this.dateOfBirth = builder.dateOfBirth;
        this.passportNumber = builder.passportNumber;
        this.nationality = builder.nationality;
        this.baggage = new ArrayList<>(builder.baggage);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getNationality() {
        return nationality;
    }

    public List<Baggage> getBaggage() {
        return new ArrayList<>(baggage);
    }

    public void addBaggage(Baggage bag) {
        baggage.add(bag);
    }

    public double getTotalBaggageWeight() {
        return baggage.stream().mapToDouble(Baggage::getWeight).sum();
    }

    @Override
    public String toString() {
        return String.format("Passenger[%s | %s %s | %s]",
                id, firstName, lastName, email);
    }

    public static class Builder {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private LocalDate dateOfBirth;
        private String passportNumber;
        private String nationality;
        private List<Baggage> baggage = new ArrayList<>();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder dateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder passportNumber(String passportNumber) {
            this.passportNumber = passportNumber;
            return this;
        }

        public Builder nationality(String nationality) {
            this.nationality = nationality;
            return this;
        }

        public Builder baggage(List<Baggage> baggage) {
            this.baggage = new ArrayList<>(baggage);
            return this;
        }

        public Passenger build() {
            if (id == null || firstName == null || lastName == null || email == null) {
                throw new IllegalStateException("Passenger requires id, firstName, lastName, and email");
            }
            return new Passenger(this);
        }
    }
}



