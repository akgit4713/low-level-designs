# CricInfo - Cricket Information System (LLD)

A comprehensive Low-Level Design for a Cricket Information System like CricInfo, implementing real-time match updates, player statistics, and search functionality.

---

## 1. Requirements Covered

| Requirement | Implementation |
|-------------|----------------|
| Match, Team, Player information | Domain models with full attributes |
| Upcoming & completed match schedule | `MatchRepository` with status-based filtering |
| Search for matches, teams, players | Strategy pattern with pluggable search algorithms |
| Detailed match info (scorecard, commentary) | `Scorecard` (Builder pattern) and `Commentary` models |
| Real-time live score updates | Observer pattern with `LiveScoreNotifier` |
| Concurrent access handling | Thread-safe repositories with `ConcurrentHashMap` |
| Scalability | Layered architecture with repository abstraction |
| Extensibility | Interfaces, dependency injection, and design patterns |

---

## 2. Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         CricInfo (Facade)                        │
│              Unified API for all system operations               │
└─────────────────────────────────────────────────────────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        ▼                        ▼                        ▼
┌───────────────┐      ┌───────────────┐      ┌───────────────┐
│  MatchService │      │  ScoreService │      │  TeamService  │
│ PlayerService │      │               │      │               │
└───────────────┘      └───────────────┘      └───────────────┘
        │                        │                        │
        ▼                        ▼                        ▼
┌───────────────┐      ┌───────────────┐      ┌───────────────┐
│  Repositories │      │   Observers   │      │  Strategies   │
│   (Data)      │      │   (Events)    │      │  (Algorithms) │
└───────────────┘      └───────────────┘      └───────────────┘
```

---

## 3. Package Structure

```
cricinfo/
├── enums/
│   ├── MatchStatus.java         # Match states (SCHEDULED, LIVE, COMPLETED, etc.)
│   ├── MatchFormat.java         # T20, ODI, TEST, etc.
│   ├── MatchResult.java         # Win, Tie, Draw, No Result
│   ├── PlayerRole.java          # BATSMAN, BOWLER, ALL_ROUNDER
│   ├── BattingStyle.java        # RIGHT/LEFT_HANDED
│   ├── BowlingStyle.java        # FAST, OFF_BREAK, LEG_BREAK, etc.
│   ├── DismissalType.java       # BOWLED, CAUGHT, LBW, etc.
│   ├── InningsStatus.java       # IN_PROGRESS, ALL_OUT, DECLARED
│   └── BallType.java            # LEGAL, WIDE, NO_BALL, etc.
│
├── exceptions/
│   ├── CricInfoException.java           # Base exception
│   ├── MatchNotFoundException.java
│   ├── TeamNotFoundException.java
│   ├── PlayerNotFoundException.java
│   ├── InvalidMatchStateException.java
│   ├── InningsNotFoundException.java
│   └── InvalidScoreUpdateException.java
│
├── models/
│   ├── Player.java              # Player entity with stats
│   ├── PlayerStats.java         # Batting/bowling statistics
│   ├── Team.java                # Team with squad
│   ├── TeamStats.java           # Team win/loss record
│   ├── Match.java               # Match entity
│   ├── Innings.java             # Innings with overs, scores
│   ├── Over.java                # Collection of balls
│   ├── Ball.java                # Individual delivery
│   ├── BatsmanScore.java        # Batsman's innings score
│   ├── BowlerStats.java         # Bowler's match figures
│   ├── Venue.java               # Stadium information
│   ├── Commentary.java          # Ball-by-ball commentary
│   └── Scorecard.java           # Match scorecard (Builder)
│
├── repositories/
│   ├── MatchRepository.java     # Match data access interface
│   ├── TeamRepository.java      # Team data access interface
│   ├── PlayerRepository.java    # Player data access interface
│   └── impl/
│       ├── InMemoryMatchRepository.java   # Thread-safe implementation
│       ├── InMemoryTeamRepository.java
│       └── InMemoryPlayerRepository.java
│
├── services/
│   ├── MatchService.java        # Match operations interface
│   ├── ScoreService.java        # Scoring operations interface
│   ├── TeamService.java         # Team operations interface
│   ├── PlayerService.java       # Player operations interface
│   └── impl/
│       ├── MatchServiceImpl.java
│       ├── ScoreServiceImpl.java
│       ├── TeamServiceImpl.java
│       └── PlayerServiceImpl.java
│
├── observers/
│   ├── MatchObserver.java       # Observer interface
│   ├── MatchSubject.java        # Subject interface
│   ├── LiveScoreNotifier.java   # Concrete subject
│   ├── ConsoleScoreObserver.java    # Console output observer
│   └── WebSocketScoreObserver.java  # WebSocket push observer
│
├── strategies/
│   ├── search/
│   │   ├── SearchStrategy.java      # Generic search interface
│   │   ├── PlayerSearchStrategy.java
│   │   ├── TeamSearchStrategy.java
│   │   ├── MatchSearchStrategy.java
│   │   └── FuzzySearchStrategy.java # Levenshtein distance search
│   └── scoring/
│       ├── ScoringStrategy.java     # Scoring calculation interface
│       ├── StandardScoringStrategy.java
│       └── DLSScoringStrategy.java  # Duckworth-Lewis-Stern
│
├── factories/
│   ├── MatchFactory.java        # Factory interface
│   ├── T20MatchFactory.java     # T20 match creation
│   ├── ODIMatchFactory.java     # ODI match creation
│   ├── TestMatchFactory.java    # Test match creation
│   └── MatchFactoryProvider.java    # Factory registry
│
├── CricInfo.java                # Main facade (Singleton)
└── Main.java                    # Demo application
```

---

## 4. Design Patterns Used

### 4.1 Singleton Pattern
**Where:** `CricInfo` class
**Why:** Provides a single point of access to the cricket information system, ensuring consistent state across the application.

```java
public static CricInfo getInstance() {
    if (instance == null) {
        synchronized (CricInfo.class) {
            if (instance == null) {
                instance = new CricInfo(...);
            }
        }
    }
    return instance;
}
```

### 4.2 Observer Pattern
**Where:** `MatchObserver`, `MatchSubject`, `LiveScoreNotifier`
**Why:** Enables real-time notifications to multiple subscribers (mobile apps, web clients) when match events occur.

```java
public interface MatchObserver {
    void onMatchStart(Match match);
    void onBallBowled(Match match, Ball ball);
    void onWicket(Match match, Ball ball);
    void onMatchEnd(Match match);
}
```

### 4.3 Strategy Pattern
**Where:** `SearchStrategy`, `ScoringStrategy`
**Why:** Allows pluggable algorithms for search (fuzzy, exact) and scoring calculations (standard, DLS).

```java
public interface SearchStrategy<T> {
    List<T> search(List<T> entities, String query);
}
```

### 4.4 Factory Pattern
**Where:** `MatchFactory`, `MatchFactoryProvider`
**Why:** Encapsulates match creation logic for different formats (T20, ODI, Test).

```java
public static MatchFactory getFactory(MatchFormat format) {
    return factories.get(format);
}
```

### 4.5 Builder Pattern
**Where:** `Scorecard.Builder`
**Why:** Simplifies construction of complex scorecard objects with multiple innings.

```java
Scorecard scorecard = new Scorecard.Builder(matchId)
    .addInningsScorecard(innings1Card)
    .addInningsScorecard(innings2Card)
    .setMatchResult("India won by 5 wickets")
    .build();
```

### 4.6 Repository Pattern
**Where:** `MatchRepository`, `TeamRepository`, `PlayerRepository`
**Why:** Abstracts data access, enabling easy switch between in-memory and database storage.

---

## 5. SOLID Principles Applied

### Single Responsibility Principle (SRP)
- `ScoreService` handles only scoring operations
- `MatchService` handles only match lifecycle
- `Commentary` model only represents commentary data

### Open/Closed Principle (OCP)
- New match formats can be added via `MatchFactory` without modifying existing code
- New search algorithms can be added via `SearchStrategy` implementations
- New observers can be added without modifying `LiveScoreNotifier`

### Liskov Substitution Principle (LSP)
- All `MatchFactory` implementations are interchangeable
- All `MatchObserver` implementations can substitute each other

### Interface Segregation Principle (ISP)
- Separate interfaces for `MatchService`, `ScoreService`, `TeamService`, `PlayerService`
- Clients depend only on the interfaces they need

### Dependency Inversion Principle (DIP)
- Services depend on repository interfaces, not implementations
- `CricInfo` accepts repository implementations via constructor injection

---

## 6. Key Classes & Relationships

### 6.1 Core Domain Model

```
┌─────────────┐     has     ┌─────────────┐
│    Match    │ ◆─────────▶ │   Innings   │
│             │      *      │             │
└─────────────┘             └─────────────┘
      │ has                       │ has
      ▼ 2                         ▼ *
┌─────────────┐             ┌─────────────┐
│    Team     │             │    Over     │
│             │             │             │
└─────────────┘             └─────────────┘
      │ has                       │ has
      ▼ *                         ▼ 1-6
┌─────────────┐             ┌─────────────┐
│   Player    │◀────────────│    Ball     │
│             │   bowler,   │             │
└─────────────┘   batsman   └─────────────┘
```

### 6.2 Observer Relationships

```
┌─────────────────────┐
│  LiveScoreNotifier  │ ──────implements──────▶ MatchSubject
│     (Subject)       │
└─────────────────────┘
         │ notifies
         ▼
┌─────────────────────┐     ┌─────────────────────┐
│ConsoleScoreObserver │     │WebSocketScoreObserver│
└─────────────────────┘     └─────────────────────┘
         │                            │
         └────────both implement──────┘
                      │
                      ▼
              MatchObserver (interface)
```

---

## 7. Thread Safety

### Concurrent Data Access
- `ConcurrentHashMap` used in all in-memory repositories
- `CopyOnWriteArrayList` used in `LiveScoreNotifier` for observers
- Double-checked locking in `CricInfo` singleton

```java
// Thread-safe repository
private final Map<String, Match> matches = new ConcurrentHashMap<>();

// Thread-safe observer list
private final List<MatchObserver> observers = new CopyOnWriteArrayList<>();
```

---

## 8. Extension Points

### 8.1 Adding New Match Format
1. Create new factory implementing `MatchFactory`
2. Register in `MatchFactoryProvider`

```java
MatchFactoryProvider.registerFactory(MatchFormat.T10, new T10MatchFactory());
```

### 8.2 Adding New Search Algorithm
1. Implement `SearchStrategy<T>` interface
2. Inject into service via constructor

```java
new PlayerServiceImpl(playerRepo, new FuzzySearchStrategy());
```

### 8.3 Adding New Observer
1. Implement `MatchObserver` interface
2. Register with `LiveScoreNotifier`

```java
cricInfo.subscribeToLiveScores(new MobileAppObserver());
```

### 8.4 Adding Database Storage
1. Implement repository interfaces (e.g., `JpaMatchRepository`)
2. Inject into `CricInfo` via `createInstance()`

---

## 9. Sample Usage

```java
// Get system instance
CricInfo cricInfo = CricInfo.getInstance();

// Subscribe to live updates
cricInfo.subscribeToLiveScores(new ConsoleScoreObserver("Client1"));

// Create teams and players
Team india = cricInfo.createTeam("India", "India");
Player kohli = cricInfo.createPlayer("Virat Kohli", "India");
cricInfo.addPlayerToTeam(india.getId(), kohli);

// Create match
Match match = cricInfo.createMatch(india, australia, MatchFormat.T20);
cricInfo.startMatch(match.getId());

// Start innings and record balls
cricInfo.startInnings(match, india, australia, rohit, kohli);
cricInfo.recordBall(match, new Ball(1, 1, bumrah, rohit, kohli));

// Get live score
System.out.println(cricInfo.getLiveScore(match));

// Get scorecard
Scorecard card = cricInfo.getScorecard(match.getId());
System.out.println(card);

// Search
List<Player> players = cricInfo.searchPlayers("kohli");
```

---

## 10. Class Diagram Summary

```
┌────────────────────────────────────────────────────────────────┐
│                         <<Facade>>                              │
│                          CricInfo                               │
├────────────────────────────────────────────────────────────────┤
│ - matchService: MatchService                                    │
│ - scoreService: ScoreService                                    │
│ - teamService: TeamService                                      │
│ - playerService: PlayerService                                  │
│ - scoreNotifier: LiveScoreNotifier                              │
├────────────────────────────────────────────────────────────────┤
│ + getInstance(): CricInfo                                       │
│ + createMatch(...): Match                                       │
│ + getLiveMatches(): List<Match>                                 │
│ + recordBall(match, ball): void                                 │
│ + subscribeToLiveScores(observer): void                         │
│ + searchPlayers(query): List<Player>                            │
└────────────────────────────────────────────────────────────────┘
                              │
          ┌───────────────────┼───────────────────┐
          ▼                   ▼                   ▼
   MatchService        ScoreService         TeamService
   (interface)         (interface)          (interface)
          │                   │                   │
          ▼                   ▼                   ▼
   MatchServiceImpl    ScoreServiceImpl    TeamServiceImpl
          │                   │                   │
          ▼                   ▼                   │
   MatchRepository     LiveScoreNotifier    TeamRepository
   (interface)         (Subject)            (interface)
          │                   │                   │
          ▼                   ▼                   ▼
   InMemoryMatchRepo   MatchObserver       InMemoryTeamRepo
                       (interface)
```

---

## 11. Design Rationale

1. **Layered Architecture**: Clear separation between facade, services, and repositories enables independent testing and modification.

2. **Interface-Based Design**: All major components use interfaces, enabling dependency injection and easy mocking for tests.

3. **Pattern Usage**: Each pattern solves a specific problem:
   - Observer for real-time updates without tight coupling
   - Strategy for algorithm flexibility
   - Factory for object creation complexity
   - Builder for complex object construction

4. **Thread Safety**: Critical for a live score system where multiple users access data concurrently.

5. **Extensibility**: The system can grow to support new formats, platforms, and features without modifying core logic.

---

## 12. Files Created

| Category | Files |
|----------|-------|
| Enums | 9 files |
| Exceptions | 7 files |
| Models | 13 files |
| Repositories | 6 files |
| Services | 8 files |
| Observers | 5 files |
| Strategies | 7 files |
| Factories | 5 files |
| Main | 2 files |
| **Total** | **62 files** |



