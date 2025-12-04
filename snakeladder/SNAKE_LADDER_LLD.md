# Snake and Ladder Game - Low Level Design

## Table of Contents
1. [Requirements](#requirements)
2. [LLD Overview](#lld-overview)
3. [Class Diagram](#class-diagram)
4. [Design Patterns](#design-patterns)
5. [SOLID Principles](#solid-principles)
6. [Extension Points](#extension-points)
7. [Usage Examples](#usage-examples)
8. [Testing Strategy](#testing-strategy)

---

## Requirements

### Functional Requirements
1. Board with numbered cells (default: 100 cells, configurable)
2. Predefined snakes and ladders connecting cells
3. Multiple players with unique game pieces
4. Turn-based dice rolling
5. Snake: slide down from head to tail
6. Ladder: climb up from base to top
7. Game ends when a player reaches the final cell
8. Support for concurrent game sessions

### Non-Functional Requirements
- Thread-safe for concurrent games
- Extensible for new rules and features
- Observable for event monitoring
- Clean, testable code

---

## LLD Overview

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                           CLIENT LAYER                               │
│  ┌─────────┐                                                        │
│  │  Main   │ ─────► Uses factories and game manager                 │
│  └─────────┘                                                        │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          SERVICE LAYER                               │
│  ┌─────────────────┐                                                │
│  │   GameManager   │ ─────► Manages concurrent game sessions        │
│  │   (Singleton)   │                                                │
│  └─────────────────┘                                                │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         FACTORY LAYER                                │
│  ┌──────────────┐    ┌──────────────┐                               │
│  │ BoardFactory │    │ GameFactory  │                               │
│  └──────────────┘    └──────────────┘                               │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          CORE LAYER                                  │
│  ┌────────┐  ┌─────────────┐  ┌─────────────────┐                  │
│  │  Game  │──│    Board    │──│  BoardElement   │                  │
│  │        │  │             │  │ (Snake/Ladder)  │                  │
│  └────────┘  └─────────────┘  └─────────────────┘                  │
│       │                                                             │
│       ├──────┬──────────────────────────────────────┐              │
│       ▼      ▼                                      ▼              │
│  ┌────────┐  ┌───────────────┐  ┌──────────────────────┐          │
│  │ Player │  │ DiceStrategy  │  │    GameObserver      │          │
│  └────────┘  │  (Strategy)   │  │    (Observer)        │          │
│              └───────────────┘  └──────────────────────┘          │
└─────────────────────────────────────────────────────────────────────┘
```

### Package Structure

```
snakeladder/
├── enums/
│   ├── GameStatus.java         # NOT_STARTED, IN_PROGRESS, FINISHED
│   ├── GamePiece.java          # RED, BLUE, GREEN, etc.
│   └── MoveResult.java         # NORMAL, SNAKE_BITE, LADDER_CLIMB, WON
├── exceptions/
│   ├── SnakeLadderException.java
│   ├── InvalidBoardConfigException.java
│   ├── InvalidGameStateException.java
│   ├── GameNotFoundException.java
│   ├── PlayerNotFoundException.java
│   └── InvalidMoveException.java
├── models/
│   ├── BoardElement.java       # Interface for snakes/ladders
│   ├── Snake.java              # Slides player down
│   ├── Ladder.java             # Climbs player up
│   ├── Board.java              # Game board with elements
│   ├── Player.java             # Player with position
│   ├── MoveOutcome.java        # Result of a move
│   └── Game.java               # Main game orchestrator
├── strategies/
│   ├── DiceStrategy.java       # Dice rolling interface
│   ├── StandardDiceStrategy.java
│   ├── MultipleDiceStrategy.java
│   ├── BiasedDiceStrategy.java
│   └── CrookedDiceStrategy.java
├── observers/
│   ├── GameObserver.java       # Observer interface
│   ├── ConsoleGameObserver.java
│   └── StatisticsObserver.java
├── factories/
│   ├── BoardFactory.java       # Creates various boards
│   └── GameFactory.java        # Creates game instances
├── services/
│   └── GameManager.java        # Manages concurrent games
└── Main.java                   # Demo application
```

---

## Class Diagram

```
┌───────────────────────────────────────────────────────────────────────────┐
│                              <<interface>>                                │
│                              BoardElement                                 │
├───────────────────────────────────────────────────────────────────────────┤
│ + getStartPosition(): int                                                 │
│ + getEndPosition(): int                                                   │
│ + getPositionDelta(): int                                                 │
│ + getDescription(): String                                                │
│ + getType(): String                                                       │
└───────────────────────────────────────────────────────────────────────────┘
                    △                               △
                    │                               │
        ┌───────────┴───────────┐       ┌──────────┴────────────┐
        │                       │       │                        │
┌───────────────┐       ┌───────────────┐
│     Snake     │       │    Ladder     │
├───────────────┤       ├───────────────┤
│ - head: int   │       │ - base: int   │
│ - tail: int   │       │ - top: int    │
│ - id: String  │       │ - id: String  │
├───────────────┤       ├───────────────┤
│ + getHead()   │       │ + getBase()   │
│ + getTail()   │       │ + getTop()    │
└───────────────┘       └───────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                                  Board                                   │
├─────────────────────────────────────────────────────────────────────────┤
│ - size: int                                                             │
│ - elements: Map<Integer, BoardElement>                                  │
│ - snakes: List<Snake>                                                   │
│ - ladders: List<Ladder>                                                 │
├─────────────────────────────────────────────────────────────────────────┤
│ + addSnake(Snake): void                                                 │
│ + addLadder(Ladder): void                                               │
│ + getElementAt(int): Optional<BoardElement>                             │
│ + getFinalPosition(int): int                                            │
│ + isWinningPosition(int): boolean                                       │
└─────────────────────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                                  Game                                    │
├─────────────────────────────────────────────────────────────────────────┤
│ - id: String                                                            │
│ - board: Board                                                          │
│ - players: List<Player>                                                 │
│ - diceStrategy: DiceStrategy                                            │
│ - observers: List<GameObserver>                                         │
│ - status: GameStatus                                                    │
│ - currentPlayerIndex: int                                               │
│ - winner: Player                                                        │
│ - moveHistory: List<MoveOutcome>                                        │
├─────────────────────────────────────────────────────────────────────────┤
│ + start(): void                                                         │
│ + playTurn(): MoveOutcome                                               │
│ + playToCompletion(): Player                                            │
│ + cancel(): void                                                        │
│ + addObserver(GameObserver): void                                       │
│ + getCurrentPlayer(): Player                                            │
└─────────────────────────────────────────────────────────────────────────┘


┌───────────────────────────────────────────────────────────────────────────┐
│                              <<interface>>                                │
│                              DiceStrategy                                 │
├───────────────────────────────────────────────────────────────────────────┤
│ + roll(): int                                                             │
│ + getMinValue(): int                                                      │
│ + getMaxValue(): int                                                      │
│ + getDiceCount(): int                                                     │
│ + getDescription(): String                                                │
└───────────────────────────────────────────────────────────────────────────┘
         △               △                △                 △
         │               │                │                 │
┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ StandardDice │  │ MultipleDice │  │  BiasedDice  │  │ CrookedDice  │
│   Strategy   │  │   Strategy   │  │   Strategy   │  │   Strategy   │
└──────────────┘  └──────────────┘  └──────────────┘  └──────────────┘


┌───────────────────────────────────────────────────────────────────────────┐
│                              <<interface>>                                │
│                              GameObserver                                 │
├───────────────────────────────────────────────────────────────────────────┤
│ + onGameStart(Game): void                                                 │
│ + onPlayerMove(MoveOutcome): void                                         │
│ + onSnakeEncounter(MoveOutcome): void                                     │
│ + onLadderClimb(MoveOutcome): void                                        │
│ + onPlayerWin(Player, Game): void                                         │
│ + onGameEnd(Game): void                                                   │
│ + onTurnChange(Player, Player): void                                      │
└───────────────────────────────────────────────────────────────────────────┘
                    △                               △
                    │                               │
        ┌───────────┴────────────┐       ┌─────────┴─────────┐
        │                        │       │                   │
┌──────────────────────┐  ┌──────────────────────┐
│ ConsoleGameObserver  │  │ StatisticsObserver   │
├──────────────────────┤  ├──────────────────────┤
│ Prints to console    │  │ Collects game stats  │
└──────────────────────┘  └──────────────────────┘
```

---

## Design Patterns

### 1. Strategy Pattern (DiceStrategy)

**Purpose:** Allows different dice rolling behaviors without changing the Game class.

**Implementation:**
```java
public interface DiceStrategy {
    int roll();
    int getMinValue();
    int getMaxValue();
}

// Implementations
public class StandardDiceStrategy implements DiceStrategy { ... }
public class BiasedDiceStrategy implements DiceStrategy { ... }
public class CrookedDiceStrategy implements DiceStrategy { ... }
```

**Benefits:**
- Easy to add new dice types (e.g., weighted dice, multiple dice)
- Game logic remains unchanged
- Each strategy is independently testable

### 2. Observer Pattern (GameObserver)

**Purpose:** Decouples game event handling from game logic.

**Implementation:**
```java
public interface GameObserver {
    void onGameStart(Game game);
    void onPlayerMove(MoveOutcome outcome);
    void onSnakeEncounter(MoveOutcome outcome);
    void onLadderClimb(MoveOutcome outcome);
    void onPlayerWin(Player player, Game game);
    void onGameEnd(Game game);
}
```

**Benefits:**
- Multiple observers can monitor the same game
- Easy to add logging, analytics, UI updates
- Game doesn't know about its observers

### 3. Factory Pattern (BoardFactory, GameFactory)

**Purpose:** Encapsulates object creation logic.

**Implementation:**
```java
public class BoardFactory {
    public static Board createStandardBoard() { ... }
    public static Board createSmallBoard() { ... }
    public static BoardBuilder customBoard(int size) { ... }
}
```

**Benefits:**
- Centralized creation logic
- Easy to create consistent configurations
- Hides complex initialization

### 4. Builder Pattern (Game.Builder, MoveOutcome.Builder)

**Purpose:** Constructs complex objects step by step.

**Implementation:**
```java
Game game = Game.builder()
    .board(board)
    .addPlayer(player1)
    .addPlayer(player2)
    .diceStrategy(new StandardDiceStrategy())
    .extraTurnOnSix(true)
    .build();
```

**Benefits:**
- Readable object construction
- Optional parameters without constructor overloading
- Immutable objects

### 5. Singleton Pattern (GameManager)

**Purpose:** Single point for managing all game sessions.

**Implementation:**
```java
public class GameManager {
    private static volatile GameManager instance;
    
    public static GameManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }
}
```

**Benefits:**
- Global access point
- Thread-safe implementation
- Manages shared state

---

## SOLID Principles

### Single Responsibility Principle (SRP)

| Class | Single Responsibility |
|-------|----------------------|
| `Board` | Managing board state and element positions |
| `Game` | Orchestrating gameplay and turn management |
| `Player` | Holding player identity and position |
| `Snake/Ladder` | Representing board elements with positions |
| `GameManager` | Managing multiple game sessions |

### Open/Closed Principle (OCP)

The design is open for extension but closed for modification:

- **DiceStrategy:** Add new dice behaviors without modifying Game
- **BoardElement:** Add new board elements (e.g., Teleporter) by implementing interface
- **GameObserver:** Add new observers without touching Game class

### Liskov Substitution Principle (LSP)

All implementations can substitute their interfaces:

```java
// Any DiceStrategy can be used
DiceStrategy dice = new StandardDiceStrategy();
DiceStrategy dice = new BiasedDiceStrategy(Bias.HIGH);
DiceStrategy dice = new CrookedDiceStrategy(CrookedType.EVEN_ONLY);

// Any BoardElement works
BoardElement element = new Snake(50, 25);
BoardElement element = new Ladder(10, 45);
```

### Interface Segregation Principle (ISP)

Small, focused interfaces:

- `BoardElement` - Only element-specific methods
- `DiceStrategy` - Only dice-related methods
- `GameObserver` - Default methods allow partial implementation

### Dependency Inversion Principle (DIP)

High-level modules depend on abstractions:

```java
public class Game {
    private final DiceStrategy diceStrategy;  // Abstraction, not concrete
    private final List<GameObserver> observers;  // Abstraction
}
```

---

## Extension Points

### 1. New Board Elements

Create new elements by implementing `BoardElement`:

```java
public class Teleporter implements BoardElement {
    private final int entry;
    private final int exit;
    
    @Override
    public int getStartPosition() { return entry; }
    
    @Override
    public int getEndPosition() { return exit; }
    
    @Override
    public String getType() { return "TELEPORTER"; }
}
```

### 2. Custom Dice Strategies

Add new dice behaviors:

```java
public class LoadedDiceStrategy implements DiceStrategy {
    private final int[] preferredValues = {4, 5, 6};
    
    @Override
    public int roll() {
        // Always roll high values
        return preferredValues[random.nextInt(preferredValues.length)];
    }
}
```

### 3. New Observers

Add monitoring/analytics:

```java
public class DatabaseObserver implements GameObserver {
    @Override
    public void onGameEnd(Game game) {
        // Save game results to database
        saveGameResult(game);
    }
}
```

### 4. Game Variants

Extend Game for different rules:

```java
public class TimedGame extends Game {
    private final long timeLimitMs;
    
    @Override
    public MoveOutcome playTurn() {
        if (System.currentTimeMillis() > startTime + timeLimitMs) {
            // Time's up - determine winner by position
        }
        return super.playTurn();
    }
}
```

---

## Usage Examples

### Basic Game

```java
// Create a standard game
Game game = GameFactory.createStandardGame("Alice", "Bob");

// Play to completion
Player winner = game.playToCompletion();
System.out.println("Winner: " + winner.getName());
```

### Custom Configuration

```java
// Custom board
Board board = BoardFactory.customBoard(50)
    .addSnake(45, 10)
    .addLadder(5, 35)
    .build();

// Custom dice
DiceStrategy dice = new MultipleDiceStrategy(2);

// Create game
Game game = Game.builder()
    .board(board)
    .addPlayer(new Player("Player1", GamePiece.RED))
    .addPlayer(new Player("Player2", GamePiece.BLUE))
    .diceStrategy(dice)
    .extraTurnOnSix(true)
    .build();

// Add observers
game.addObserver(new ConsoleGameObserver());
game.addObserver(new StatisticsObserver());

game.playToCompletion();
```

### Concurrent Games

```java
GameManager manager = GameManager.getInstance();

// Create multiple games
Game game1 = manager.createGame(Arrays.asList("Alice", "Bob"));
Game game2 = manager.createGame(Arrays.asList("Charlie", "Diana", "Eve"));

// Start and play concurrently (in real app, use threads)
manager.startGame(game1.getId());
manager.startGame(game2.getId());

manager.playToCompletion(game1.getId());
manager.playToCompletion(game2.getId());

manager.printGamesSummary();
```

---

## Testing Strategy

### Unit Tests

1. **Board Tests**
   - Adding snakes/ladders
   - Conflict detection
   - Position lookups

2. **Game Tests**
   - Turn management
   - Win detection
   - Snake/ladder application

3. **Strategy Tests**
   - Dice value ranges
   - Statistical distribution (biased dice)

### Integration Tests

1. **Full Game Flow**
   - Start to finish
   - Multiple players
   - Various board configurations

2. **Observer Tests**
   - Event firing
   - Statistics collection

### Example Test Structure

```java
@Test
void testSnakeBite() {
    Board board = new Board(100);
    board.addSnake(new Snake(50, 10));
    
    Player player = new Player("Test", GamePiece.RED);
    player.setPosition(45);
    
    // Use a fixed dice that returns 5
    DiceStrategy fixedDice = () -> 5;
    
    Game game = Game.builder()
        .board(board)
        .addPlayer(player)
        .addPlayer(new Player("Other", GamePiece.BLUE))
        .diceStrategy(fixedDice)
        .build();
    
    game.start();
    MoveOutcome outcome = game.playTurn();
    
    assertEquals(MoveResult.SNAKE_BITE, outcome.getResult());
    assertEquals(10, player.getPosition());
}
```

---

## Running the Demo

```bash
cd snakeladder
javac *.java **/*.java
java snakeladder.Main
```

Select from various demo modes to see different features in action:
1. Standard 2-Player Game
2. Multi-Player Game
3. Quick Game (Small Board)
4. Custom Dice Strategies
5. Extra Turn on 6 Rule
6. Game Manager (Concurrent Sessions)
7. Board Configuration Demo
8. Statistics Observer Demo

---

## Design Rationale

### Why This Design?

1. **Loose Coupling:** Components interact through interfaces, making changes isolated.

2. **High Cohesion:** Each class has a focused responsibility.

3. **Testability:** Dependency injection allows easy mocking of components.

4. **Extensibility:** New features can be added without modifying existing code.

5. **Thread Safety:** GameManager uses ConcurrentHashMap for safe concurrent access.

### Trade-offs

| Decision | Benefit | Cost |
|----------|---------|------|
| Interface-based design | Flexibility, testability | More files, slight indirection |
| Builder pattern | Readable construction | More code |
| Observer pattern | Decoupled events | Memory for observer list |
| Singleton GameManager | Global access | Testing requires reset |

---

*This design provides a solid foundation for a Snake and Ladder game that is maintainable, extensible, and follows software engineering best practices.*



