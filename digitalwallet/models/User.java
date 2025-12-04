package digitalwallet.models;

import digitalwallet.enums.AccountStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a user in the digital wallet system.
 * Uses Builder pattern for construction.
 */
public class User {
    private final String id;
    private final String email;
    private final LocalDateTime createdAt;
    private final ReentrantLock lock = new ReentrantLock();
    
    private volatile String name;
    private volatile String phoneNumber;
    private volatile String hashedPin;
    private volatile AccountStatus status;
    private volatile boolean kycVerified;
    private volatile LocalDateTime lastLoginAt;
    private volatile int failedLoginAttempts;

    private User(Builder builder) {
        this.id = builder.id;
        this.email = builder.email;
        this.name = builder.name;
        this.phoneNumber = builder.phoneNumber;
        this.hashedPin = builder.hashedPin;
        this.status = builder.status;
        this.kycVerified = builder.kycVerified;
        this.createdAt = LocalDateTime.now();
        this.lastLoginAt = null;
        this.failedLoginAttempts = 0;
    }

    // Getters
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getHashedPin() { return hashedPin; }
    public AccountStatus getStatus() { return status; }
    public boolean isKycVerified() { return kycVerified; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }

    // Thread-safe update methods
    public void updateName(String name) {
        lock.lock();
        try {
            this.name = Objects.requireNonNull(name, "Name cannot be null");
        } finally {
            lock.unlock();
        }
    }

    public void updatePhoneNumber(String phoneNumber) {
        lock.lock();
        try {
            this.phoneNumber = phoneNumber;
        } finally {
            lock.unlock();
        }
    }

    public void updatePin(String hashedPin) {
        lock.lock();
        try {
            this.hashedPin = Objects.requireNonNull(hashedPin, "PIN cannot be null");
            this.failedLoginAttempts = 0; // Reset on PIN change
        } finally {
            lock.unlock();
        }
    }

    public void updateStatus(AccountStatus newStatus) {
        lock.lock();
        try {
            this.status = newStatus;
        } finally {
            lock.unlock();
        }
    }

    public void markKycVerified() {
        lock.lock();
        try {
            this.kycVerified = true;
            if (this.status == AccountStatus.PENDING_VERIFICATION) {
                this.status = AccountStatus.ACTIVE;
            }
        } finally {
            lock.unlock();
        }
    }

    public void recordSuccessfulLogin() {
        lock.lock();
        try {
            this.lastLoginAt = LocalDateTime.now();
            this.failedLoginAttempts = 0;
        } finally {
            lock.unlock();
        }
    }

    public void recordFailedLogin() {
        lock.lock();
        try {
            this.failedLoginAttempts++;
            if (this.failedLoginAttempts >= 5) {
                this.status = AccountStatus.SUSPENDED;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean canTransact() {
        return status.canTransact() && kycVerified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', email='%s', status=%s}", 
            id, name, email, status);
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String email;
        private String name;
        private String phoneNumber;
        private String hashedPin;
        private AccountStatus status = AccountStatus.PENDING_VERIFICATION;
        private boolean kycVerified = false;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder hashedPin(String hashedPin) {
            this.hashedPin = hashedPin;
            return this;
        }

        public Builder status(AccountStatus status) {
            this.status = status;
            return this;
        }

        public Builder kycVerified(boolean kycVerified) {
            this.kycVerified = kycVerified;
            return this;
        }

        public User build() {
            Objects.requireNonNull(id, "User ID is required");
            Objects.requireNonNull(email, "Email is required");
            Objects.requireNonNull(name, "Name is required");
            return new User(this);
        }
    }
}



