# Online Auction System - Low-Level Design

## Overview

This document outlines the Low-Level Design (LLD) for an Online Auction System that allows users to create auctions, place bids, and manage transactions with proper concurrency handling.

---

## Requirements Summary

1. User registration and authentication
2. Create auction listings (item name, description, starting price, duration)
3. Browse and search auctions (by name, category, price range)
4. Place bids on active auctions
5. Automatic highest bid updates with bidder notifications
6. Auction end handling with winner declaration
7. Concurrent access handling with data consistency
8. Extensible architecture for future enhancements

---

## Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              AUCTION SYSTEM (Facade)                          │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐  ┌────────────┐ │
│  │  UserService   │  │ AuctionService │  │   BidService   │  │SearchService│ │
│  └───────┬────────┘  └───────┬────────┘  └───────┬────────┘  └─────┬──────┘ │
│          │                   │                   │                  │        │
├──────────┴───────────────────┴───────────────────┴──────────────────┴────────┤
│                              SERVICE LAYER                                    │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │                         OBSERVER LAYER                                  │ │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────────┐ │ │
│  │  │ OutbidObserver  │  │AuctionEndObserver│ │ NotificationDispatcher  │ │ │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────────────┘ │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
│                                                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│                              STRATEGY LAYER                                   │
│  ┌────────────────────────────────────────────────────────────────────────┐ │
│  │ SearchStrategy │ NotificationStrategy │ BidValidationStrategy          │ │
│  └────────────────────────────────────────────────────────────────────────┘ │
│                                                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│                             REPOSITORY LAYER                                  │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐                 │
│  │ UserRepository │  │AuctionRepository│ │  BidRepository │                 │
│  └────────────────┘  └────────────────┘  └────────────────┘                 │
│                                                                              │
├──────────────────────────────────────────────────────────────────────────────┤
│                               MODEL LAYER                                     │
│  ┌──────┐  ┌────────────────┐  ┌─────┐  ┌──────────┐  ┌──────────────────┐  │
│  │ User │  │ AuctionListing │  │ Bid │  │ Category │  │   AuctionStatus  │  │
│  └──────┘  └────────────────┘  └─────┘  └──────────┘  └──────────────────┘  │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Patterns Used

### 1. Observer Pattern
**Purpose**: Notify users of important events (outbid, auction won, auction ending soon)

```java
interface AuctionObserver {
    void onBidPlaced(AuctionListing auction, Bid bid);
    void onAuctionEnded(AuctionListing auction, User winner);
    void onOutbid(AuctionListing auction, User outbidUser, Bid newHighestBid);
}
```

### 2. Strategy Pattern
**Purpose**: Pluggable algorithms for search, notification, and bid validation

```java
interface SearchStrategy {
    List<AuctionListing> search(List<AuctionListing> auctions, SearchCriteria criteria);
}

interface NotificationStrategy {
    void notify(User user, String message, NotificationType type);
}

interface BidValidationStrategy {
    void validate(AuctionListing auction, Bid bid) throws BidException;
}
```

### 3. Factory Pattern
**Purpose**: Encapsulate object creation with validation

```java
class AuctionFactory {
    AuctionListing createAuction(User seller, String itemName, ...);
}
```

### 4. Repository Pattern
**Purpose**: Abstract data access for testability and flexibility

```java
interface AuctionRepository extends Repository<AuctionListing, String> {
    List<AuctionListing> findByStatus(AuctionStatus status);
    List<AuctionListing> findByCategory(Category category);
}
```

### 5. Facade Pattern
**Purpose**: Simplified interface for auction system operations

```java
class AuctionSystem {
    // Unified entry point for all auction operations
}
```

---

## SOLID Principles Application

### Single Responsibility Principle (SRP)
- `UserService`: Only handles user-related operations
- `BidService`: Only handles bid placement and validation
- `AuctionService`: Only handles auction lifecycle
- Each observer handles one type of notification

### Open/Closed Principle (OCP)
- New search strategies can be added without modifying existing code
- New notification channels (SMS, Push) can be added as new strategies
- New bid validation rules can be plugged in

### Liskov Substitution Principle (LSP)
- All `SearchStrategy` implementations can be used interchangeably
- All `NotificationStrategy` implementations are substitutable
- `InMemoryRepository` can be replaced with `JpaRepository`

### Interface Segregation Principle (ISP)
- `SearchStrategy` is separate from `NotificationStrategy`
- `AuctionObserver` methods can be extended via default methods
- Repository interfaces are fine-grained

### Dependency Inversion Principle (DIP)
- Services depend on `Repository` interfaces, not concrete implementations
- Services depend on `Strategy` interfaces
- High-level modules don't depend on low-level modules

---

## Class Diagram

### Models

```
┌─────────────────────────────────────┐
│              User                   │
├─────────────────────────────────────┤
│ - id: String                        │
│ - username: String                  │
│ - email: String                     │
│ - passwordHash: String              │
│ - createdAt: Instant                │
├─────────────────────────────────────┤
│ + getId(): String                   │
│ + getUsername(): String             │
│ + getEmail(): String                │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│          AuctionListing             │
├─────────────────────────────────────┤
│ - id: String                        │
│ - seller: User                      │
│ - itemName: String                  │
│ - description: String               │
│ - category: Category                │
│ - startingPrice: BigDecimal         │
│ - currentHighestBid: Bid            │
│ - bids: List<Bid>                   │
│ - status: AuctionStatus             │
│ - startTime: Instant                │
│ - endTime: Instant                  │
│ - lock: ReentrantLock               │
├─────────────────────────────────────┤
│ + placeBid(bid: Bid): boolean       │
│ + isActive(): boolean               │
│ + getWinner(): Optional<User>       │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│               Bid                   │
├─────────────────────────────────────┤
│ - id: String                        │
│ - bidder: User                      │
│ - amount: BigDecimal                │
│ - timestamp: Instant                │
├─────────────────────────────────────┤
│ + getId(): String                   │
│ + getBidder(): User                 │
│ + getAmount(): BigDecimal           │
│ + getTimestamp(): Instant           │
└─────────────────────────────────────┘
```

### Enums

```
┌─────────────────────┐    ┌─────────────────────┐
│   AuctionStatus     │    │      Category       │
├─────────────────────┤    ├─────────────────────┤
│ DRAFT               │    │ ELECTRONICS         │
│ ACTIVE              │    │ FASHION             │
│ ENDED               │    │ HOME_GARDEN         │
│ CANCELLED           │    │ SPORTS              │
│ SOLD                │    │ COLLECTIBLES        │
└─────────────────────┘    │ VEHICLES            │
                           │ ART                 │
                           │ OTHER               │
                           └─────────────────────┘
```

---

## Sequence Diagrams

### Place Bid Flow

```
User        BidService      AuctionRepo     BidValidation    Observers
 │              │               │                │               │
 │──placeBid()─▶│               │                │               │
 │              │──findById()──▶│                │               │
 │              │◀─────────────-│                │               │
 │              │──validate()───────────────────▶│               │
 │              │◀──────────────────────────────-│               │
 │              │──auction.placeBid()──▶│        │               │
 │              │    (with lock)        │        │               │
 │              │◀─────────────────────-│        │               │
 │              │──notifyObservers()────────────────────────────▶│
 │              │                       │        │               │
 │◀───result───-│                       │        │               │
```

### Auction End Flow

```
Scheduler   AuctionService   AuctionRepo    Observers      Winner
    │             │               │             │            │
    │──checkEnd()▶│               │             │            │
    │             │──findActive()▶│             │            │
    │             │◀──────────────│             │            │
    │             │──(for each expired)──────▶  │            │
    │             │   markAsEnded()             │            │
    │             │──notifyAuctionEnded()──────▶│            │
    │             │                             │──notify()─▶│
    │◀────────────│                             │            │
```

---

## Concurrency Handling

### Thread-Safe Bid Placement

```java
public class AuctionListing {
    private final ReentrantLock bidLock = new ReentrantLock();
    
    public boolean placeBid(Bid bid) {
        bidLock.lock();
        try {
            if (!isActive()) return false;
            if (currentHighestBid != null && 
                bid.getAmount().compareTo(currentHighestBid.getAmount()) <= 0) {
                return false;
            }
            this.bids.add(bid);
            this.currentHighestBid = bid;
            return true;
        } finally {
            bidLock.unlock();
        }
    }
}
```

### Repository-Level Concurrency

```java
public class InMemoryAuctionRepository {
    private final ConcurrentHashMap<String, AuctionListing> auctions;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public void save(AuctionListing auction) {
        lock.writeLock().lock();
        try {
            auctions.put(auction.getId(), auction);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

---

## Extension Points

### 1. Adding New Auction Types

```java
// Create new auction type
public class DutchAuction extends AuctionListing {
    private BigDecimal priceDecrement;
    private Duration decrementInterval;
    
    @Override
    public BigDecimal getCurrentPrice() {
        // Calculate decreasing price
    }
}
```

### 2. Adding New Search Criteria

```java
// Add new search strategy
public class PriceRangeSearchStrategy implements SearchStrategy {
    @Override
    public List<AuctionListing> search(List<AuctionListing> auctions, 
                                        SearchCriteria criteria) {
        return auctions.stream()
            .filter(a -> a.getCurrentPrice().compareTo(criteria.getMinPrice()) >= 0)
            .filter(a -> a.getCurrentPrice().compareTo(criteria.getMaxPrice()) <= 0)
            .collect(Collectors.toList());
    }
}
```

### 3. Adding New Notification Channels

```java
// Add SMS notification
public class SmsNotificationStrategy implements NotificationStrategy {
    private final SmsGateway gateway;
    
    @Override
    public void notify(User user, String message, NotificationType type) {
        gateway.sendSms(user.getPhoneNumber(), message);
    }
}
```

---

## File Structure

```
onlineauction/
├── enums/
│   ├── AuctionStatus.java
│   ├── Category.java
│   └── NotificationType.java
├── exceptions/
│   ├── AuctionException.java
│   ├── BidException.java
│   └── UserException.java
├── factories/
│   ├── AuctionFactory.java
│   ├── AuctionSystemFactory.java
│   └── UserFactory.java
├── models/
│   ├── AuctionListing.java
│   ├── Bid.java
│   ├── SearchCriteria.java
│   └── User.java
├── observers/
│   ├── AuctionObserver.java
│   ├── ConsoleNotificationObserver.java
│   └── OutbidNotificationObserver.java
├── repositories/
│   ├── AuctionRepository.java
│   ├── BidRepository.java
│   ├── Repository.java
│   ├── UserRepository.java
│   └── impl/
│       ├── InMemoryAuctionRepository.java
│       ├── InMemoryBidRepository.java
│       └── InMemoryUserRepository.java
├── services/
│   ├── AuctionService.java
│   ├── BidService.java
│   ├── SearchService.java
│   ├── UserService.java
│   └── impl/
│       ├── AuctionServiceImpl.java
│       ├── BidServiceImpl.java
│       ├── SearchServiceImpl.java
│       └── UserServiceImpl.java
├── strategies/
│   ├── notification/
│   │   ├── ConsoleNotificationStrategy.java
│   │   ├── EmailNotificationStrategy.java
│   │   └── NotificationStrategy.java
│   ├── search/
│   │   ├── CategorySearchStrategy.java
│   │   ├── CompositeSearchStrategy.java
│   │   ├── KeywordSearchStrategy.java
│   │   ├── PriceRangeSearchStrategy.java
│   │   └── SearchStrategy.java
│   └── validation/
│       ├── BidValidationStrategy.java
│       ├── MinimumIncrementValidation.java
│       └── ActiveAuctionValidation.java
├── AuctionSystem.java
└── Main.java
```

---

## Key Design Decisions

1. **BigDecimal for Money**: Prevents floating-point precision issues
2. **Instant for Time**: UTC-based, timezone-agnostic timestamps
3. **ReentrantLock**: Fine-grained locking for concurrent bid placement
4. **ConcurrentHashMap**: Thread-safe storage with good performance
5. **Optional**: Explicit null handling for winner, highest bid
6. **Immutable Bid**: Bids cannot be modified after creation
7. **Builder Pattern**: For complex object construction (SearchCriteria)

---

## Testing Strategy

1. **Unit Tests**: Test each service in isolation with mocked dependencies
2. **Concurrency Tests**: Simulate multiple bidders placing bids simultaneously
3. **Integration Tests**: Test complete flows from bid to notification
4. **Edge Cases**: Expired auctions, invalid bids, concurrent modifications



