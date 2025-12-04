# Movie Rating System - Low Level Design (LLD)

## Table of Contents
1. [Overview](#overview)
2. [Requirements](#requirements)
3. [Design Principles](#design-principles)
4. [Design Patterns Used](#design-patterns-used)
5. [Class Diagram](#class-diagram)
6. [Package Structure](#package-structure)
7. [Core Components](#core-components)
8. [Class Relationships](#class-relationships)
9. [Data Flow](#data-flow)
10. [Extensibility](#extensibility)

---

## Overview

The Movie Rating System is a scalable, extensible platform that allows users of different expertise levels to rate movies. Higher-level users' ratings carry more weight in the aggregated movie score. Users can progress through the hierarchy based on metrics like number of ratings and helpfulness of their reviews.

---

## Requirements

### Functional Requirements
- Users can rate movies (1-5 stars) with optional reviews
- Users have different levels: NOVICE → INTERMEDIATE → PRO → EXPERT → MASTER
- Higher-level users' ratings have more impact on aggregated scores
- Users can be promoted/demoted based on activity metrics
- Users can vote on whether ratings are helpful
- System calculates weighted aggregated ratings for movies

### Non-Functional Requirements
- Extensible design for new rating strategies
- Loosely coupled components
- Thread-safe operations
- Testable architecture

---

## Design Principles

### SOLID Principles Applied

#### 1. Single Responsibility Principle (SRP)
Each class has one reason to change:
- `User` - Only user data management
- `Rating` - Only rating data management
- `RatingServiceImpl` - Only rating operations
- `WeightCalculationStrategy` - Only weight calculation
- `LevelPromotionStrategy` - Only level promotion logic

#### 2. Open/Closed Principle (OCP)
Classes are open for extension but closed for modification:
- New `WeightCalculationStrategy` implementations can be added without changing existing code
- New `LevelPromotionStrategy` implementations extend the system without modifications
- New `RatingAggregationStrategy` implementations can be plugged in

#### 3. Liskov Substitution Principle (LSP)
Derived classes can substitute base classes:
- Any `WeightCalculationStrategy` implementation works with `WeightedAverageStrategy`
- Any `RatingObserver` implementation works with `RatingServiceImpl`
- Service interfaces can be swapped (e.g., `InMemoryUserService` → `DatabaseUserService`)

#### 4. Interface Segregation Principle (ISP)
Interfaces are focused and cohesive:
- `MovieService` - Only movie operations
- `UserService` - Only user operations
- `RatingService` - Only rating operations
- `RatingObserver` - Only rating event handling

#### 5. Dependency Inversion Principle (DIP)
High-level modules depend on abstractions:
- `RatingServiceImpl` depends on `RatingAggregationStrategy` interface
- `WeightedAverageStrategy` depends on `WeightCalculationStrategy` interface
- `UserLevelUpdateObserver` depends on `LevelPromotionStrategy` interface

---

## Design Patterns Used

### 1. Strategy Pattern
**Purpose:** Define a family of algorithms, encapsulate each one, and make them interchangeable.

**Applied to:**
- **Weight Calculation:** `WeightCalculationStrategy` with implementations like `LevelBasedWeightStrategy`, `HelpfulnessWeightStrategy`
- **Rating Aggregation:** `RatingAggregationStrategy` with `SimpleAverageStrategy`, `WeightedAverageStrategy`, `BayesianAverageStrategy`
- **Level Promotion:** `LevelPromotionStrategy` with `RatingCountPromotionStrategy`, `HelpfulnessPromotionStrategy`

```
┌─────────────────────────────────┐
│   WeightCalculationStrategy     │ <<interface>>
├─────────────────────────────────┤
│ + calculateWeight(user, rating) │
└────────────┬────────────────────┘
             │
     ┌───────┴───────┬─────────────────────┐
     ▼               ▼                     ▼
┌─────────────┐ ┌──────────────┐ ┌────────────────────┐
│LevelBased   │ │Helpfulness   │ │CompositeWeight     │
│WeightStrategy││WeightStrategy││Strategy            │
└─────────────┘ └──────────────┘ └────────────────────┘
```

### 2. Observer Pattern
**Purpose:** Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified.

**Applied to:**
- `RatingObserver` interface observed by `UserLevelUpdateObserver` and `StatisticsObserver`
- `RatingServiceImpl` notifies observers on rating events

```
┌─────────────────────────────────┐
│       RatingServiceImpl         │
│     (Subject/Observable)        │
├─────────────────────────────────┤
│ - observers: List<RatingObserver>│
│ + registerObserver()            │
│ + notifyRatingCreated()         │
└────────────┬────────────────────┘
             │ notifies
     ┌───────┴───────┐
     ▼               ▼
┌─────────────────┐ ┌────────────────────┐
│UserLevelUpdate  │ │StatisticsObserver  │
│Observer         │ │                    │
└─────────────────┘ └────────────────────┘
```

### 3. Composite Pattern
**Purpose:** Compose objects into tree structures and treat individual objects and compositions uniformly.

**Applied to:**
- `CompositeWeightStrategy` - Combines multiple weight strategies
- `CompositePromotionStrategy` - Combines multiple promotion strategies

```
┌─────────────────────────────────┐
│   LevelPromotionStrategy        │ <<interface>>
├─────────────────────────────────┤
│ + evaluateLevel(user)           │
│ + shouldPromote(user)           │
└────────────┬────────────────────┘
             │
     ┌───────┴───────┬────────────────────────┐
     ▼               ▼                        ▼
┌─────────────┐ ┌──────────────────┐ ┌───────────────────────┐
│RatingCount  │ │Helpfulness       │ │CompositePromotion     │
│Promotion    │ │Promotion         │ │Strategy               │
│Strategy     │ │Strategy          │ │                       │
└─────────────┘ └──────────────────┘ │- strategies: List<>   │
                                     │+ addStrategy()        │
                                     └───────────────────────┘
```

### 4. Factory Pattern
**Purpose:** Create objects without specifying exact class to be created.

**Applied to:**
- `UserFactory` - Creates User objects
- `MovieFactory` - Creates Movie objects
- `RatingSystemFactory` - Creates complete rating system with all dependencies wired

```
┌─────────────────────────────────┐
│     RatingSystemFactory         │
├─────────────────────────────────┤
│ + createDefaultRatingSystem()   │
│ + createHelpfulnessWeightedSys()│
└─────────────────────────────────┘
        │ creates
        ▼
┌─────────────────────────────────┐
│        RatingSystem             │
├─────────────────────────────────┤
│ - movieService                  │
│ - userService                   │
│ - ratingService                 │
│ - statisticsObserver            │
│ - promotionStrategy             │
└─────────────────────────────────┘
```

---

## Class Diagram

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                              MOVIE RATING SYSTEM - CLASS DIAGRAM                      │
└──────────────────────────────────────────────────────────────────────────────────────┘

╔═══════════════════════════════════════════════════════════════════════════════════════╗
║                                      MODELS                                            ║
╚═══════════════════════════════════════════════════════════════════════════════════════╝

┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
│       User          │     │       Movie         │     │       Rating        │
├─────────────────────┤     ├─────────────────────┤     ├─────────────────────┤
│ - id: String        │     │ - id: String        │     │ - id: String        │
│ - username: String  │     │ - title: String     │     │ - userId: String    │
│ - email: String     │     │ - director: String  │     │ - movieId: String   │
│ - level: UserLevel  │     │ - releaseYear: int  │     │ - ratingValue       │
│ - totalRatingsGiven │     │ - genre: String     │     │ - review: String    │
│ - helpfulVotesRec.  │     │ - description       │     │ - helpfulVotes: int │
├─────────────────────┤     ├─────────────────────┤     │ - notHelpfulVotes   │
│ + incrementRatings()│     │ + getTitle()        │     ├─────────────────────┤
│ + setLevel()        │     │ + getGenre()        │     │ + updateRating()    │
│ + getLevel()        │     └─────────────────────┘     │ + addHelpfulVote()  │
└─────────────────────┘                                 │ + getHelpfulScore() │
         │                                              └─────────────────────┘
         │ has-a                                                │ has-a
         ▼                                                      ▼
┌─────────────────────┐                                 ┌─────────────────────┐
│  <<enum>> UserLevel │                                 │<<enum>> RatingValue │
├─────────────────────┤                                 ├─────────────────────┤
│ NOVICE (weight: 1)  │                                 │ ONE_STAR (1)        │
│ INTERMEDIATE (2)    │                                 │ TWO_STARS (2)       │
│ PRO (3)             │                                 │ THREE_STARS (3)     │
│ EXPERT (4)          │                                 │ FOUR_STARS (4)      │
│ MASTER (5)          │                                 │ FIVE_STARS (5)      │
├─────────────────────┤                                 ├─────────────────────┤
│ + getWeightMultiplier()│                              │ + getValue()        │
│ + getNextLevel()    │                                 │ + fromInt()         │
│ + getPreviousLevel()│                                 └─────────────────────┘
└─────────────────────┘

╔═══════════════════════════════════════════════════════════════════════════════════════╗
║                                     SERVICES                                           ║
╚═══════════════════════════════════════════════════════════════════════════════════════╝

┌─────────────────────────┐   ┌─────────────────────────┐   ┌─────────────────────────┐
│ <<interface>>           │   │ <<interface>>           │   │ <<interface>>           │
│    MovieService         │   │    UserService          │   │    RatingService        │
├─────────────────────────┤   ├─────────────────────────┤   ├─────────────────────────┤
│ + addMovie()            │   │ + registerUser()        │   │ + createRating()        │
│ + getMovieById()        │   │ + getUserById()         │   │ + updateRating()        │
│ + getAllMovies()        │   │ + getAllUsers()         │   │ + getRatingsForMovie()  │
│ + searchByTitle()       │   │ + updateUserLevel()     │   │ + getAggregatedRating() │
│ + deleteMovie()         │   │ + getUsersByLevel()     │   │ + voteOnRating()        │
└───────────┬─────────────┘   └───────────┬─────────────┘   └───────────┬─────────────┘
            │                             │                             │
            ▼                             ▼                             ▼
┌─────────────────────────┐   ┌─────────────────────────┐   ┌─────────────────────────┐
│  InMemoryMovieService   │   │  InMemoryUserService    │   │   RatingServiceImpl     │
├─────────────────────────┤   ├─────────────────────────┤   ├─────────────────────────┤
│ - movies: Map           │   │ - usersById: Map        │   │ - ratingsById: Map      │
│                         │   │ - usersByUsername: Map  │   │ - observers: List       │
│ (implements interface)  │   │                         │   │ - aggregationStrategy   │
└─────────────────────────┘   │ (implements interface)  │   │ - userService           │
                              └─────────────────────────┘   ├─────────────────────────┤
                                                            │ + registerObserver()    │
                                                            │ + notifyRatingCreated() │
                                                            └─────────────────────────┘

╔═══════════════════════════════════════════════════════════════════════════════════════╗
║                                    STRATEGIES                                          ║
╚═══════════════════════════════════════════════════════════════════════════════════════╝

    WEIGHT STRATEGIES                  AGGREGATION STRATEGIES              PROMOTION STRATEGIES
    
┌─────────────────────────┐      ┌─────────────────────────┐      ┌─────────────────────────┐
│ <<interface>>           │      │ <<interface>>           │      │ <<interface>>           │
│WeightCalculationStrategy│      │RatingAggregationStrategy│      │LevelPromotionStrategy   │
├─────────────────────────┤      ├─────────────────────────┤      ├─────────────────────────┤
│+calculateWeight(u,r)    │      │+calculateAggregate()    │      │+evaluateLevel(user)     │
│+getDescription()        │      │+getDescription()        │      │+shouldPromote(user)     │
└───────────┬─────────────┘      └───────────┬─────────────┘      │+shouldDemote(user)      │
            │                                │                    └───────────┬─────────────┘
    ┌───────┼───────┐                ┌───────┼───────┐                ┌───────┼───────┐
    ▼       ▼       ▼                ▼       ▼       ▼                ▼       ▼       ▼
┌───────┐┌───────┐┌───────┐    ┌───────┐┌───────┐┌───────┐    ┌───────┐┌───────┐┌───────┐
│Level  ││Help-  ││Compo- │    │Simple ││Weighted││Bayes- │    │Rating ││Help-  ││Compo- │
│Based  ││fulness││site   │    │Average││Average ││ian    │    │Count  ││fulness││site   │
│Weight ││Weight ││Weight │    │       ││        ││Average│    │Promo  ││Promo  ││Promo  │
└───────┘└───────┘└───────┘    └───────┘└───────┘└───────┘    └───────┘└───────┘└───────┘

╔═══════════════════════════════════════════════════════════════════════════════════════╗
║                                     OBSERVERS                                          ║
╚═══════════════════════════════════════════════════════════════════════════════════════╝

┌─────────────────────────────────────┐
│ <<interface>> RatingObserver        │
├─────────────────────────────────────┤
│ + onRatingCreated(rating, user)     │
│ + onRatingUpdated(rating, user)     │
│ + onRatingDeleted(rating, user)     │
│ + onRatingVoted(rating, author, ...) │
└─────────────────┬───────────────────┘
                  │
          ┌───────┴───────┐
          ▼               ▼
┌─────────────────────┐ ┌─────────────────────┐
│UserLevelUpdate      │ │StatisticsObserver   │
│Observer             │ │                     │
├─────────────────────┤ ├─────────────────────┤
│- promotionStrategy  │ │- totalRatings       │
│- levelChangeListener│ │- totalVotes         │
├─────────────────────┤ │- ratingsPerMovie    │
│+ onRatingCreated()  │ ├─────────────────────┤
│+ onRatingVoted()    │ │+ getTotalRatings()  │
└─────────────────────┘ │+ getStatistics()    │
                        └─────────────────────┘
```

---

## Package Structure

```
movierating/
│
├── Main.java                           # Entry point with demo
│
├── models/                             # Domain entities
│   ├── Movie.java                      # Movie entity
│   ├── User.java                       # User entity with level
│   ├── Rating.java                     # Rating entity
│   ├── UserLevel.java                  # Enum for user levels
│   └── RatingValue.java                # Enum for rating values (1-5)
│
├── services/                           # Business logic interfaces
│   ├── MovieService.java               # Movie operations interface
│   ├── UserService.java                # User operations interface
│   ├── RatingService.java              # Rating operations interface
│   └── impl/                           # Service implementations
│       ├── InMemoryMovieService.java
│       ├── InMemoryUserService.java
│       └── RatingServiceImpl.java
│
├── strategies/                         # Strategy implementations
│   ├── weight/                         # Weight calculation strategies
│   │   ├── WeightCalculationStrategy.java
│   │   ├── LevelBasedWeightStrategy.java
│   │   ├── HelpfulnessWeightStrategy.java
│   │   └── CompositeWeightStrategy.java
│   ├── aggregation/                    # Rating aggregation strategies
│   │   ├── RatingAggregationStrategy.java
│   │   ├── SimpleAverageStrategy.java
│   │   ├── WeightedAverageStrategy.java
│   │   └── BayesianAverageStrategy.java
│   └── promotion/                      # Level promotion strategies
│       ├── LevelPromotionStrategy.java
│       ├── RatingCountPromotionStrategy.java
│       ├── HelpfulnessPromotionStrategy.java
│       └── CompositePromotionStrategy.java
│
├── observers/                          # Event observers
│   ├── RatingObserver.java             # Observer interface
│   ├── UserLevelUpdateObserver.java    # Updates user levels
│   └── StatisticsObserver.java         # Collects statistics
│
└── factories/                          # Object creation
    ├── UserFactory.java
    ├── MovieFactory.java
    └── RatingSystemFactory.java        # Creates complete system
```

---

## Core Components

### 1. Models

| Class | Responsibility | Key Attributes |
|-------|---------------|----------------|
| `User` | Represents a user in the system | id, username, level, totalRatingsGiven, helpfulVotesReceived |
| `Movie` | Represents a movie | id, title, director, releaseYear, genre |
| `Rating` | Represents a user's rating for a movie | id, userId, movieId, ratingValue, review, helpfulVotes |
| `UserLevel` | Enum defining user levels with weight multipliers | NOVICE(1), INTERMEDIATE(2), PRO(3), EXPERT(4), MASTER(5) |
| `RatingValue` | Enum for valid rating values | ONE_STAR to FIVE_STARS |

### 2. Services

| Interface | Implementation | Responsibility |
|-----------|---------------|----------------|
| `MovieService` | `InMemoryMovieService` | CRUD operations for movies |
| `UserService` | `InMemoryUserService` | CRUD operations for users, level management |
| `RatingService` | `RatingServiceImpl` | Rating operations, aggregation, voting |

### 3. Strategies

| Strategy Type | Purpose | Implementations |
|--------------|---------|-----------------|
| `WeightCalculationStrategy` | Calculate weight of a rating | LevelBased, Helpfulness, Composite |
| `RatingAggregationStrategy` | Aggregate movie ratings | SimpleAverage, WeightedAverage, Bayesian |
| `LevelPromotionStrategy` | Determine user level changes | RatingCount, Helpfulness, Composite |

### 4. Observers

| Observer | Trigger | Action |
|----------|---------|--------|
| `UserLevelUpdateObserver` | Rating created, vote received | Evaluates and updates user level |
| `StatisticsObserver` | All rating events | Collects system statistics |

---

## Class Relationships

### Aggregation (Has-A, Weak Ownership)

```
RatingServiceImpl ◇────────> UserService
                  ◇────────> RatingAggregationStrategy
                  ◇────────> List<RatingObserver>

WeightedAverageStrategy ◇────────> WeightCalculationStrategy

CompositeWeightStrategy ◇────────> List<WeightCalculationStrategy>

CompositePromotionStrategy ◇────────> List<LevelPromotionStrategy>
```

### Composition (Has-A, Strong Ownership)

```
User ●────────> UserLevel (enum, value type)

Rating ●────────> RatingValue (enum, value type)

RatingSystem ●────────> MovieService
             ●────────> UserService
             ●────────> RatingService
             ●────────> StatisticsObserver
```

### Association (Uses)

```
RatingServiceImpl ─────> User (via UserService)
                  ─────> Movie (via MovieService)
                  ─────> Rating (manages)

UserLevelUpdateObserver ─────> LevelPromotionStrategy
                        ─────> User (updates level)
```

### Dependency (Creates/Uses Temporarily)

```
RatingSystemFactory ......> RatingServiceImpl (creates)
                    ......> InMemoryUserService (creates)
                    ......> InMemoryMovieService (creates)
                    ......> WeightedAverageStrategy (creates)

UserFactory ......> User (creates)
MovieFactory ......> Movie (creates)
```

### Realization (Implements Interface)

```
InMemoryMovieService ─ ─ ─ ▷ MovieService
InMemoryUserService ─ ─ ─ ▷ UserService
RatingServiceImpl ─ ─ ─ ▷ RatingService

LevelBasedWeightStrategy ─ ─ ─ ▷ WeightCalculationStrategy
HelpfulnessWeightStrategy ─ ─ ─ ▷ WeightCalculationStrategy
CompositeWeightStrategy ─ ─ ─ ▷ WeightCalculationStrategy

SimpleAverageStrategy ─ ─ ─ ▷ RatingAggregationStrategy
WeightedAverageStrategy ─ ─ ─ ▷ RatingAggregationStrategy
BayesianAverageStrategy ─ ─ ─ ▷ RatingAggregationStrategy

RatingCountPromotionStrategy ─ ─ ─ ▷ LevelPromotionStrategy
HelpfulnessPromotionStrategy ─ ─ ─ ▷ LevelPromotionStrategy
CompositePromotionStrategy ─ ─ ─ ▷ LevelPromotionStrategy

UserLevelUpdateObserver ─ ─ ─ ▷ RatingObserver
StatisticsObserver ─ ─ ─ ▷ RatingObserver
```

---

## Data Flow

### Rating Creation Flow

```
1. User calls RatingService.createRating(userId, movieId, value, review)
                    │
                    ▼
2. RatingServiceImpl validates user exists via UserService
                    │
                    ▼
3. Creates Rating object and stores in internal maps
                    │
                    ▼
4. Updates User.totalRatingsGiven via User.incrementRatingsGiven()
                    │
                    ▼
5. Notifies all registered RatingObservers via onRatingCreated()
                    │
        ┌───────────┴───────────┐
        ▼                       ▼
6a. UserLevelUpdate          6b. StatisticsObserver
    Observer                     records metrics
    │
    ▼
7. Evaluates user level via LevelPromotionStrategy.evaluateLevel()
    │
    ▼
8. Updates user level if changed via User.setLevel()
    │
    ▼
9. Notifies LevelChangeListener if provided
```

### Aggregated Rating Calculation Flow

```
1. User calls RatingService.getAggregatedRating(movieId)
                    │
                    ▼
2. RatingServiceImpl retrieves all ratings for movie
                    │
                    ▼
3. Builds user map (userId -> User) for weight calculation
                    │
                    ▼
4. Calls RatingAggregationStrategy.calculateAggregateRating(ratings, userMap)
                    │
                    ▼
5. WeightedAverageStrategy (example):
   For each rating:
   │
   ├─▶ Calls WeightCalculationStrategy.calculateWeight(user, rating)
   │       │
   │       ▼
   │   LevelBasedWeightStrategy returns user.getLevel().getWeightMultiplier()
   │
   └─▶ Multiplies rating value by weight
                    │
                    ▼
6. Returns weighted sum / total weight
```

---

## Extensibility

### Adding a New Weight Strategy

1. Create a new class implementing `WeightCalculationStrategy`:

```java
public class TimeDecayWeightStrategy implements WeightCalculationStrategy {
    @Override
    public double calculateWeight(User user, Rating rating) {
        // Weight decreases as rating gets older
        long daysOld = ChronoUnit.DAYS.between(rating.getCreatedAt(), LocalDateTime.now());
        double decayFactor = Math.exp(-0.01 * daysOld);
        return user.getLevel().getWeightMultiplier() * decayFactor;
    }
    
    @Override
    public String getDescription() {
        return "Time-decay weighted by user level";
    }
}
```

2. Use it by injecting into `WeightedAverageStrategy` or adding to `CompositeWeightStrategy`.

### Adding a New User Level

1. Add to `UserLevel` enum:

```java
public enum UserLevel {
    NOVICE(1, "Novice", 0),
    INTERMEDIATE(2, "Intermediate", 10),
    PRO(3, "Pro", 50),
    EXPERT(4, "Expert", 100),
    MASTER(5, "Master", 200),
    LEGEND(6, "Legend", 500);  // New level
    // ...
}
```

### Adding a New Observer

1. Implement `RatingObserver`:

```java
public class NotificationObserver implements RatingObserver {
    @Override
    public void onRatingCreated(Rating rating, User user) {
        sendNotification("New rating from " + user.getUsername());
    }
    // ... implement other methods
}
```

2. Register with `RatingServiceImpl`:

```java
ratingService.registerObserver(new NotificationObserver());
```

### Switching to Database Storage

1. Create new implementations:

```java
public class DatabaseMovieService implements MovieService {
    private final JdbcTemplate jdbcTemplate;
    // ... implement methods with database operations
}
```

2. Update `RatingSystemFactory` to use new implementations.

---

## Thread Safety Considerations

- All service implementations use `ConcurrentHashMap` for thread-safe storage
- Atomic operations used in `StatisticsObserver` for counters
- User and Rating objects have synchronized-safe update methods

---

## Future Enhancements

1. **Persistence Layer**: Replace in-memory storage with database
2. **Caching**: Add caching layer for aggregated ratings
3. **Rate Limiting**: Prevent rating spam
4. **Machine Learning**: Use ML for spam/fake rating detection
5. **Social Features**: Follow users, see friends' ratings
6. **Recommendation Engine**: Suggest movies based on rating patterns

---

## Running the Application

```bash
cd movierating
javac -d ../out $(find . -name "*.java")
cd ../out
java movierating.Main
```

The demo will:
1. Create sample movies and users with different levels
2. Add ratings from different user levels
3. Show weighted aggregated ratings
4. Demonstrate voting and level progression
5. Provide an interactive menu for testing


