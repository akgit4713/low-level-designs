# Tic Tac Toe - Low Level Design

## Table of Contents
1. [Overview](#overview)
2. [Requirements](#requirements)
3. [SOLID Principles Application](#solid-principles-application)
4. [Design Patterns Used](#design-patterns-used)
5. [Class Diagram](#class-diagram)
6. [Class Descriptions](#class-descriptions)
7. [Relationships](#relationships)
8. [Extensibility Points](#extensibility-points)
9. [How to Run](#how-to-run)

---

## Overview

This is a low-level design for a **Tic Tac Toe game** that follows SOLID principles and uses appropriate design patterns. The design is:
- **Extensible**: New winning strategies, player types, and board sizes can be added
- **Loosely Coupled**: Components interact through interfaces
- **Testable**: Dependencies are injected, making unit testing straightforward

---

## Requirements

### Functional Requirements
- Two players take turns placing their symbols (X or O) on a grid
- A player wins by completing a row, column, or diagonal
- Game ends in draw if all cells are filled without a winner
- Support for custom winning strategies
- Support for human and bot players
- Support for variable board sizes

### Non-Functional Requirements
- Clean, maintainable code following SOLID principles
- Extensible architecture for future enhancements
- Separation of concerns between game logic, UI, and player behavior

---

## SOLID Principles Application

### 1. Single Responsibility Principle (SRP)
Each class has one and only one reason to change:

| Class | Responsibility |
|-------|----------------|
| `Cell` | Managing single cell state |
| `Board` | Managing board state and cell access |
| `Move` | Encapsulating move data |
| `Player` | Defining player behavior contract |
| `WinningStrategy` | Checking win conditions |
| `Game` | Orchestrating game flow |
| `GameEventListener` | Handling game events |

### 2. Open/Closed Principle (OCP)
Classes are open for extension but closed for modification:

- **WinningStrategy Interface**: New strategies can be added without modifying existing code
  - `RowWinningStrategy`
  - `ColumnWinningStrategy`
  - `DiagonalWinningStrategy`
  - `CornersWinningStrategy` (custom)
  - `NInARowStrategy` (configurable)

- **Player Abstract Class**: New player types can be added
  - `HumanPlayer`
  - `BotPlayer`

- **BotPlayingStrategy Interface**: New bot behaviors can be added
  - `RandomBotStrategy`
  - `SmartBotStrategy`

### 3. Liskov Substitution Principle (LSP)
Subtypes can be substituted for their base types:

```
Player player = new HumanPlayer("John", Symbol.X);
Player player = new BotPlayer("AI", Symbol.O);
// Both work identically in Game class
```

### 4. Interface Segregation Principle (ISP)
Interfaces are focused and minimal:

- `WinningStrategy`: Single method `checkWin()`
- `BotPlayingStrategy`: Single method `decideMove()`
- `GameEventListener`: Default methods allow partial implementation

### 5. Dependency Inversion Principle (DIP)
High-level modules depend on abstractions:

```java
// Game depends on WinningStrategy interface, not concrete implementations
private final WinningStrategy winningStrategy;

// BotPlayer depends on BotPlayingStrategy interface
private final BotPlayingStrategy playingStrategy;
```

---

## Design Patterns Used

### 1. Strategy Pattern
**Purpose**: Define a family of algorithms, encapsulate each one, and make them interchangeable.

**Application**: 
- `WinningStrategy` interface with multiple implementations
- `BotPlayingStrategy` interface for different AI behaviors

```
┌─────────────────────┐
│ «interface»         │
│ WinningStrategy     │
├─────────────────────┤
│ + checkWin()        │
└─────────────────────┘
          △
          │
    ┌─────┴─────┬─────────────┬────────────────┐
    │           │             │                │
┌───┴───┐  ┌────┴────┐  ┌─────┴─────┐  ┌───────┴───────┐
│  Row  │  │ Column  │  │ Diagonal  │  │   Corners     │
│Strategy│ │Strategy │  │ Strategy  │  │   Strategy    │
└───────┘  └─────────┘  └───────────┘  └───────────────┘
```

### 2. Builder Pattern
**Purpose**: Construct complex objects step by step.

**Application**: `Game.Builder` class for flexible game configuration.

```java
Game game = new Game.Builder()
    .withBoardSize(5)
    .withPlayers(player1, player2)
    .withWinningStrategy(customStrategy)
    .build();
```

### 3. Composite Pattern
**Purpose**: Compose objects into tree structures to represent part-whole hierarchies.

**Application**: `CompositeWinningStrategy` combines multiple strategies.

```java
CompositeWinningStrategy strategy = new CompositeWinningStrategy(
    new RowWinningStrategy(),
    new ColumnWinningStrategy(),
    new DiagonalWinningStrategy(),
    new CornersWinningStrategy()  // Custom addition!
);
```

### 4. Factory Pattern
**Purpose**: Create objects without exposing creation logic.

**Application**: `PlayerFactory` for creating different player types.

```java
Player bot = PlayerFactory.createPlayer(
    PlayerFactory.PlayerType.BOT_HARD, 
    "AI", 
    Symbol.O
);
```

### 5. Observer Pattern
**Purpose**: Define one-to-many dependency between objects.

**Application**: `GameEventListener` for decoupled event handling.

```
┌───────────┐        ┌─────────────────────┐
│   Game    │ ─────> │ «interface»         │
│           │        │ GameEventListener   │
└───────────┘        └─────────────────────┘
                               △
                               │
                     ┌─────────┴─────────┐
                     │                   │
              ┌──────┴──────┐     ┌──────┴──────┐
              │  Console    │     │   Logger    │
              │  Listener   │     │   Listener  │
              └─────────────┘     └─────────────┘
```

### 6. Template Method Pattern
**Purpose**: Define skeleton of algorithm, deferring steps to subclasses.

**Application**: `Player.makeMove()` abstract method.

---

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              TIC TAC TOE - CLASS DIAGRAM                            │
└─────────────────────────────────────────────────────────────────────────────────────┘

                                    ┌─────────────────┐
                                    │     Symbol      │
                                    │    «enum»       │
                                    ├─────────────────┤
                                    │ X               │
                                    │ O               │
                                    │ EMPTY           │
                                    └────────┬────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
                    ▼                        ▼                        ▼
           ┌────────────────┐      ┌─────────────────┐      ┌─────────────────┐
           │      Cell      │      │     Player      │      │      Move       │
           ├────────────────┤      │   «abstract»    │      ├─────────────────┤
           │ - row: int     │      ├─────────────────┤      │ - row: int      │
           │ - col: int     │      │ - name: String  │      │ - col: int      │
           │ - symbol: Symbol│     │ - symbol: Symbol│      │ - player: Player│
           ├────────────────┤      ├─────────────────┤      │ - timestamp:long│
           │ + isEmpty()    │      │ + makeMove()*   │      ├─────────────────┤
           │ + clear()      │      │ + getName()     │      │ + getRow()      │
           └────────────────┘      │ + getSymbol()   │      │ + getCol()      │
                    △              └────────┬────────┘      └─────────────────┘
                    │                       │
           ┌────────┴────────┐     ┌────────┴────────┐
           │                 │     │                 │
    ┌──────┴──────┐   ┌──────┴──────┐        ┌──────┴──────┐
    │    Board    │   │ HumanPlayer │        │  BotPlayer  │
    ├─────────────┤   ├─────────────┤        ├─────────────┤
    │ - size: int │   │ - scanner   │        │ - strategy  │
    │ - grid[][]  │   ├─────────────┤        │ - level     │
    ├─────────────┤   │ + makeMove()│        ├─────────────┤
    │ + getCell() │   └─────────────┘        │ + makeMove()│
    │ + isFull()  │                          └──────┬──────┘
    │ + display() │                                 │
    └─────────────┘                                 │ uses
                                                   ▼
                              ┌─────────────────────────────────────┐
                              │       «interface»                   │
                              │     BotPlayingStrategy              │
                              ├─────────────────────────────────────┤
                              │ + decideMove(board, player): Move   │
                              └──────────────────┬──────────────────┘
                                                 │
                              ┌──────────────────┼──────────────────┐
                              │                  │                  │
                     ┌────────┴────────┐ ┌───────┴───────┐  ┌───────┴───────┐
                     │ RandomBotStrategy│ │SmartBotStrategy│ │ (Extensible)  │
                     └─────────────────┘ └───────────────┘  └───────────────┘


┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           WINNING STRATEGY HIERARCHY                                │
└─────────────────────────────────────────────────────────────────────────────────────┘

                              ┌─────────────────────────────────────┐
                              │       «interface»                   │
                              │       WinningStrategy               │
                              ├─────────────────────────────────────┤
                              │ + checkWin(board, symbol): boolean  │
                              └──────────────────┬──────────────────┘
                                                 │
              ┌──────────────┬──────────────────┼────────────────┬──────────────────┐
              │              │                  │                │                  │
     ┌────────┴────────┐ ┌───┴───────┐ ┌───────┴───────┐ ┌──────┴──────┐ ┌─────────┴─────────┐
     │ RowWinStrategy  │ │ ColWin    │ │ DiagonalWin   │ │ Composite   │ │    CornersWin     │
     │                 │ │ Strategy  │ │ Strategy      │ │ Strategy    │ │    Strategy       │
     └─────────────────┘ └───────────┘ └───────────────┘ └─────────────┘ └───────────────────┘
                                                               │
                                                               │ contains
                                                               ▼
                                                    List<WinningStrategy>


┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              GAME ORCHESTRATION                                     │
└─────────────────────────────────────────────────────────────────────────────────────┘

  ┌─────────────────────────────────────────────────────────────────────┐
  │                              Game                                   │
  ├─────────────────────────────────────────────────────────────────────┤
  │ - board: Board                                                      │
  │ - players: List<Player>                                             │
  │ - winningStrategy: WinningStrategy           ◄── Dependency Injection
  │ - moveHistory: List<Move>                                           │
  │ - eventListeners: List<GameEventListener>                           │
  │ - currentPlayerIndex: int                                           │
  │ - gameState: GameState                                              │
  ├─────────────────────────────────────────────────────────────────────┤
  │ + play(): GameResult                                                │
  │ + executeMove(move): boolean                                        │
  │ + getCurrentPlayer(): Player                                        │
  │ + addGameEventListener(listener)                                    │
  └───────────────────────────────────┬─────────────────────────────────┘
                                      │
                                      │ notifies
                                      ▼
                        ┌─────────────────────────────┐
                        │    «interface»              │
                        │   GameEventListener         │
                        ├─────────────────────────────┤
                        │ + onGameStart(game)         │
                        │ + onMoveMade(move)          │
                        │ + onTurnChange(player)      │
                        │ + onGameEnd(result)         │
                        │ + onInvalidMove(...)        │
                        └──────────────┬──────────────┘
                                       │
                                       ▼
                        ┌─────────────────────────────┐
                        │  ConsoleGameEventListener   │
                        └─────────────────────────────┘


  ┌─────────────────────────────────────────────────────────────────────┐
  │                          Game.Builder                               │
  ├─────────────────────────────────────────────────────────────────────┤
  │ - boardSize: int = 3                                                │
  │ - players: List<Player>                                             │
  │ - winningStrategy: WinningStrategy                                  │
  ├─────────────────────────────────────────────────────────────────────┤
  │ + withBoardSize(size): Builder                                      │
  │ + addPlayer(player): Builder                                        │
  │ + withPlayers(p1, p2): Builder                                      │
  │ + withWinningStrategy(strategy): Builder                            │
  │ + build(): Game                                                     │
  └─────────────────────────────────────────────────────────────────────┘
```

---

## Class Descriptions

### Models Package

| Class | Description |
|-------|-------------|
| `Symbol` | Enum representing X, O, or EMPTY cell states |
| `Cell` | Single cell on the board with position and symbol |
| `Board` | NxN grid of cells with display and state management |
| `Move` | Immutable record of a move (row, col, player, timestamp) |

### Players Package

| Class | Description |
|-------|-------------|
| `Player` | Abstract base class defining player contract |
| `HumanPlayer` | Takes move input from console |
| `BotPlayer` | AI player with configurable strategy |
| `PlayerFactory` | Factory for creating player instances |
| `BotPlayingStrategy` | Interface for bot decision-making |
| `RandomBotStrategy` | Random move selection |
| `SmartBotStrategy` | Intelligent move selection (win/block/strategic) |

### Strategies Package

| Class | Description |
|-------|-------------|
| `WinningStrategy` | Interface for win condition checking |
| `RowWinningStrategy` | Checks horizontal wins |
| `ColumnWinningStrategy` | Checks vertical wins |
| `DiagonalWinningStrategy` | Checks both diagonals |
| `CornersWinningStrategy` | Custom: win by owning all corners |
| `NInARowStrategy` | Configurable N-in-a-row for larger boards |
| `CompositeWinningStrategy` | Combines multiple strategies |

### Game Package

| Class | Description |
|-------|-------------|
| `Game` | Main orchestrator, manages game flow |
| `Game.Builder` | Builder for flexible game construction |
| `GameState` | Enum: NOT_STARTED, IN_PROGRESS, DRAW, WIN |
| `GameResult` | Immutable record of game outcome |
| `GameEventListener` | Observer interface for game events |
| `ConsoleGameEventListener` | Console-based event handler |

---

## Relationships

### Association
A "uses" relationship where one class uses another.

```
Player ────uses────> Symbol
Move ────uses────> Player
Board ────uses────> Cell
Game ────uses────> Board, Player, WinningStrategy
```

### Aggregation
"Has-a" relationship where the contained object can exist independently.

```
Game ◇────────> Player (players can exist without game)
Game ◇────────> WinningStrategy (strategies are reusable)
CompositeWinningStrategy ◇────────> WinningStrategy (list of strategies)
```

### Composition
Strong "owns" relationship where contained objects cannot exist without container.

```
Board ◆────────> Cell (cells belong to board)
Game ◆────────> Move (moveHistory belongs to game)
```

### Inheritance (IS-A)
```
HumanPlayer ────extends────> Player
BotPlayer ────extends────> Player
```

### Interface Implementation
```
RowWinningStrategy ────implements────> WinningStrategy
RandomBotStrategy ────implements────> BotPlayingStrategy
ConsoleGameEventListener ────implements────> GameEventListener
```

### Dependency
```
Game ········depends on········> GameState, GameResult
BotPlayer ········depends on········> BotPlayingStrategy
```

---

## Extensibility Points

### 1. Adding New Winning Strategy

```java
public class DiamondWinningStrategy implements WinningStrategy {
    @Override
    public boolean checkWin(Board board, Symbol symbol) {
        // Check if symbol forms a diamond pattern
        // Custom implementation
        return false;
    }
}

// Usage
CompositeWinningStrategy strategy = CompositeWinningStrategy.createDefaultStrategy();
strategy.addStrategy(new DiamondWinningStrategy());
```

### 2. Adding New Player Type

```java
public class NetworkPlayer extends Player {
    private final Socket connection;
    
    @Override
    public Move makeMove(Board board) {
        // Receive move from network
        return receiveMoveFromNetwork();
    }
}
```

### 3. Adding New Bot Strategy

```java
public class MinimaxBotStrategy implements BotPlayingStrategy {
    @Override
    public Move decideMove(Board board, Player player) {
        // Implement minimax algorithm
        return bestMove;
    }
}
```

### 4. Adding Game Event Handler

```java
public class DatabaseLogger implements GameEventListener {
    @Override
    public void onMoveMade(Move move) {
        database.logMove(move);
    }
    
    @Override
    public void onGameEnd(GameResult result) {
        database.saveGameResult(result);
    }
}
```

---

## How to Run

### Compile
```bash
cd tictactoe
javac -d out $(find . -name "*.java")
```

### Run
```bash
java -cp out tictactoe.Main
```

### Game Modes Available
1. **Human vs Human** - Two players on same console
2. **Human vs Bot (Easy)** - Play against random bot
3. **Human vs Bot (Smart)** - Play against intelligent bot
4. **Bot vs Bot** - Watch AI play against AI
5. **Custom Board Size** - 5x5 grid demonstration
6. **Custom Winning Strategy** - Includes corner capture win

---

## Package Structure

```
tictactoe/
├── Main.java                           # Entry point
├── models/
│   ├── Symbol.java                     # X, O, EMPTY enum
│   ├── Cell.java                       # Single cell
│   ├── Board.java                      # Game board
│   └── Move.java                       # Move record
├── players/
│   ├── Player.java                     # Abstract player
│   ├── HumanPlayer.java                # Human input
│   ├── BotPlayer.java                  # AI player
│   ├── PlayerFactory.java              # Player creation
│   └── botstrategies/
│       ├── BotPlayingStrategy.java     # Strategy interface
│       ├── RandomBotStrategy.java      # Random moves
│       └── SmartBotStrategy.java       # Intelligent moves
├── strategies/
│   ├── WinningStrategy.java            # Win check interface
│   ├── RowWinningStrategy.java         # Horizontal win
│   ├── ColumnWinningStrategy.java      # Vertical win
│   ├── DiagonalWinningStrategy.java    # Diagonal win
│   ├── CornersWinningStrategy.java     # Custom: corners win
│   ├── NInARowStrategy.java            # Configurable N-in-row
│   └── CompositeWinningStrategy.java   # Combines strategies
└── game/
    ├── Game.java                       # Game orchestrator + Builder
    ├── GameState.java                  # Game state enum
    ├── GameResult.java                 # Game outcome
    ├── GameEventListener.java          # Observer interface
    └── ConsoleGameEventListener.java   # Console output
```

---

## Summary

This design demonstrates:

| Principle/Pattern | Application |
|-------------------|-------------|
| **SRP** | Each class has single responsibility |
| **OCP** | Strategies allow extension without modification |
| **LSP** | Player subtypes are interchangeable |
| **ISP** | Focused, minimal interfaces |
| **DIP** | Dependency injection via Builder |
| **Strategy** | WinningStrategy, BotPlayingStrategy |
| **Builder** | Game.Builder for configuration |
| **Factory** | PlayerFactory |
| **Composite** | CompositeWinningStrategy |
| **Observer** | GameEventListener |
| **Template Method** | Player.makeMove() |

The design is **production-ready**, **testable**, and **extensible** for future requirements.

