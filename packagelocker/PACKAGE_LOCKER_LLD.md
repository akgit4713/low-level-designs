# Package Locker System - Low-Level Design

## Overview
A package locker system that allows carriers to deposit packages and customers to retrieve them using access tokens.

## Requirements

### Functional Requirements
1. Carrier deposits a package by specifying size (small, medium, large)
2. System assigns an available compartment of matching size
3. Returns compartment number and access token, or error if no space
4. Access tokens expire after 7 days
5. User retrieves package by entering access token
6. System validates code and returns compartment number
7. Invalid/expired tokens are rejected with specific error messages

### Out of Scope
- Delivery logistics
- Notification (SMS/email)
- Lockout after failed attempts
- UI layer
- Multiple locker stations
- Payment/pricing

---

## Design

### Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              LockerService                                  │
│  + depositPackage(size: CompartmentSize): DepositResult                    │
│  + retrievePackage(accessCode: String): RetrievalResult                    │
│  + cleanupExpiredPackages(): void                                          │
└─────────────────────┬───────────────────────┬───────────────────────────────┘
                      │                       │
                      ▼                       ▼
┌─────────────────────────────────┐  ┌────────────────────────────────────────┐
│      CompartmentManager         │  │        AccessTokenService              │
│  + allocate(size): Compartment  │  │  + generate(): AccessToken             │
│  + release(id): void            │  │  + validate(code): ValidationResult    │
│  + findByNumber(num): Compart.  │  │  + markAsUsed(code): void              │
└─────────────────┬───────────────┘  └────────────────────────────────────────┘
                  │                                    │
                  ▼                                    ▼
┌─────────────────────────────────┐  ┌────────────────────────────────────────┐
│  <<interface>>                  │  │  <<interface>>                         │
│  CompartmentAllocationStrategy  │  │  AccessTokenGenerator                  │
│  + allocate(compartments, size) │  │  + generate(): String                  │
└─────────────────────────────────┘  └────────────────────────────────────────┘
          ▲                                    ▲
          │                                    │
┌─────────────────────────────────┐  ┌────────────────────────────────────────┐
│  ExactMatchAllocationStrategy   │  │  UUIDAccessTokenGenerator              │
└─────────────────────────────────┘  └────────────────────────────────────────┘
```

### Entity Relationships

```
┌─────────────────┐       1      ┌─────────────────┐
│   Compartment   │◄─────────────│     Package     │
│  - id           │              │  - id           │
│  - number       │              │  - size         │
│  - size         │              │  - compartmentId│
│  - occupied     │              │  - accessToken  │
└─────────────────┘              │  - depositTime  │
                                 └─────────────────┘
                                         │
                                         │ 1
                                         ▼
                                 ┌─────────────────┐
                                 │   AccessToken   │
                                 │  - code         │
                                 │  - expiresAt    │
                                 │  - status       │
                                 └─────────────────┘
```

---

## SOLID Principles Applied

### Single Responsibility Principle (SRP)
- `CompartmentManager`: Only manages compartment allocation/release
- `AccessTokenService`: Only handles token generation/validation
- `LockerService`: Only orchestrates the deposit/retrieve workflow
- `ExpirationManager`: Only handles expiration cleanup

### Open/Closed Principle (OCP)
- `CompartmentAllocationStrategy` interface allows adding new allocation algorithms without modifying existing code
- `AccessTokenGenerator` interface allows different token generation strategies

### Liskov Substitution Principle (LSP)
- All strategy implementations can be substituted without breaking functionality
- `ExactMatchStrategy` and `BestFitStrategy` are interchangeable

### Interface Segregation Principle (ISP)
- Focused interfaces: `CompartmentAllocationStrategy` only has allocation method
- `AccessTokenGenerator` only has generation method

### Dependency Inversion Principle (DIP)
- `CompartmentManager` depends on `CompartmentAllocationStrategy` interface, not concrete implementation
- `AccessTokenService` depends on `AccessTokenGenerator` interface
- All high-level modules depend on abstractions

---

## Design Patterns Used

| Pattern | Where Applied | Benefit |
|---------|---------------|---------|
| Strategy | `CompartmentAllocationStrategy` | Swappable allocation algorithms |
| Repository | `PackageRepository`, `CompartmentRepository` | Decoupled data access |
| Factory Method | `AccessTokenGenerator` | Encapsulated token creation |
| Value Object | `AccessToken`, `DepositResult` | Immutable, thread-safe data |

---

## Extension Points

1. **New Compartment Sizes**: Add to `CompartmentSize` enum
2. **Different Allocation Strategies**: Implement `CompartmentAllocationStrategy`
3. **Custom Token Formats**: Implement `AccessTokenGenerator`
4. **Persistent Storage**: Implement repository interfaces with database backing
5. **Multiple Lockers**: Wrap `LockerService` in a `LockerStationManager`

---

## Exception Hierarchy

```
LockerException (base)
├── NoAvailableCompartmentException
├── InvalidAccessTokenException
├── ExpiredAccessTokenException
└── AlreadyUsedAccessTokenException
```

---

## Usage Example

```java
// Setup
Locker locker = LockerFactory.createLocker(5, 3, 2); // 5 small, 3 medium, 2 large
LockerService service = new LockerService(locker);

// Carrier deposits package
DepositResult result = service.depositPackage(CompartmentSize.MEDIUM);
System.out.println("Compartment: " + result.getCompartmentNumber());
System.out.println("Access Code: " + result.getAccessCode());

// Customer retrieves package
RetrievalResult retrieval = service.retrievePackage(result.getAccessCode());
System.out.println("Collect from: " + retrieval.getCompartmentNumber());
```
