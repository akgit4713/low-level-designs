# Chess Game - Low Level Design

## Table of Contents
1. [Overview](#overview)
2. [Requirements](#requirements)
3. [Architecture Overview](#architecture-overview)
4. [Design Patterns Used](#design-patterns-used)
5. [SOLID Principles Application](#solid-principles-application)
6. [Class Diagram](#class-diagram)
7. [Data Models](#data-models)
8. [Core Components](#core-components)
9. [Move Validation & Game Rules](#move-validation--game-rules)
10. [Extension Points](#extension-points)
11. [API Summary](#api-summary)
12. [How to Run](#how-to-run)

---

## Overview

A comprehensive Low-Level Design for a Chess game that follows standard chess rules. The design is:
- **Extensible**: New piece types, movement strategies, and game variants can be added
- **Loosely Coupled**: Components interact through interfaces
- **Testable**: Dependencies are injected, making unit testing straightforward
- **SOLID-Compliant**: Each class follows single responsibility, open for extension

---

## Requirements

### Functional Requirements

| ID | Requirement | Implementation |
|----|-------------|----------------|
| FR1 | Standard 8x8 chess board | `Board` class with Position grid |
| FR2 | Two players (White/Black) | `Player` abstract class, `Color` enum |
| FR3 | 16 pieces per player | `PieceFactory`, piece hierarchy |
| FR4 | Validate legal moves | `MoveValidator`, `MoveStrategy` per piece |
| FR5 | Prevent illegal moves | `MoveValidator.isValidMove()` |
| FR6 | Detect check condition | `CheckDetector.isInCheck()` |
| FR7 | Detect checkmate | `CheckDetector.isCheckmate()` |
| FR8 | Detect stalemate | `CheckDetector.isStalemate()` |
| FR9 | Handle player turns | `Game.play()` alternation |
| FR10 | Special moves (castling, en passant, pawn promotion) | Specialized `MoveStrategy` implementations |

### Non-Functional Requirements

| ID | Requirement | Implementation |
|----|-------------|----------------|
| NFR1 | Clean, maintainable code | SOLID principles |
| NFR2 | Extensible architecture | Strategy pattern, Factory pattern |
| NFR3 | Separation of concerns | Layered architecture |
| NFR4 | Testability | Dependency injection |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                PRESENTATION LAYER                                │
│                              (Main / Game Facade)                                │
└─────────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                  GAME LAYER                                      │
│  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐        │
│  │     Game      │ │ MoveValidator │ │ CheckDetector │ │   GameState   │        │
│  └───────────────┘ └───────────────┘ └───────────────┘ └───────────────┘        │
└─────────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                               STRATEGY LAYER                                     │
│  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐        │
│  │ KingStrategy  │ │QueenStrategy │ │ RookStrategy  │ │BishopStrategy │        │
│  └───────────────┘ └───────────────┘ └───────────────┘ └───────────────┘        │
│  ┌───────────────┐ ┌───────────────┐                                            │
│  │KnightStrategy │ │ PawnStrategy │                                            │
│  └───────────────┘ └───────────────┘                                            │
└─────────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                 MODEL LAYER                                      │
│  ┌───────────────┐ ┌───────────────┐ ┌───────────────┐ ┌───────────────┐        │
│  │     Board     │ │    Piece      │ │   Position    │ │     Move      │        │
│  └───────────────┘ └───────────────┘ └───────────────┘ └───────────────┘        │
└─────────────────────────────────────────────────────────────────────────────────┘
                                        │
                                        ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                OBSERVER LAYER                                    │
│  ┌───────────────────────┐ ┌───────────────────────┐ ┌─────────────────────┐    │
│  │  GameEventListener    │ │ConsoleEventListener   │ │ (Future: Network)   │    │
│  └───────────────────────┘ └───────────────────────┘ └─────────────────────┘    │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Patterns Used

### 1. Strategy Pattern

**Purpose:** Define movement algorithms for different pieces, making them interchangeable.

```
┌─────────────────────────┐
│     «interface»         │
│     MoveStrategy        │
├─────────────────────────┤
│+ getValidMoves(board,   │
│   piece): List<Move>    │
│+ canMove(board, piece,  │
│   from, to): boolean    │
└─────────────────────────┘
            △
            │
    ┌───────┼───────┬────────────┬────────────┬────────────┬────────────┐
    │       │       │            │            │            │            │
┌───┴───┐ ┌─┴──┐ ┌──┴───┐ ┌──────┴──────┐ ┌───┴────┐ ┌─────┴─────┐
│ King  │ │Queen│ │ Rook │ │   Bishop    │ │ Knight │ │   Pawn    │
│Strategy│ │Strat│ │Strat │ │   Strategy  │ │Strategy│ │ Strategy  │
└───────┘ └────┘ └──────┘ └─────────────┘ └────────┘ └───────────┘
```

### 2. Factory Pattern

**Purpose:** Create piece objects without exposing creation logic.

```java
public class PieceFactory {
    public static Piece createPiece(PieceType type, Color color, Position position) {
        return switch (type) {
            case KING -> new King(color, position);
            case QUEEN -> new Queen(color, position);
            case ROOK -> new Rook(color, position);
            case BISHOP -> new Bishop(color, position);
            case KNIGHT -> new Knight(color, position);
            case PAWN -> new Pawn(color, position);
        };
    }
}
```

### 3. Observer Pattern

**Purpose:** Decouple game events from event handlers.

```
┌───────────┐        ┌─────────────────────┐
│   Game    │ ─────> │ «interface»         │
│           │        │ GameEventListener   │
└───────────┘        └─────────────────────┘
                               △
                               │
                 ┌─────────────┴─────────────┐
                 │                           │
          ┌──────┴──────┐             ┌──────┴──────┐
          │  Console    │             │   Logger    │
          │  Listener   │             │   Listener  │
          └─────────────┘             └─────────────┘
```

### 4. Builder Pattern

**Purpose:** Construct Game object with flexible configuration.

```java
Game game = new Game.Builder()
    .withWhitePlayer(new HumanPlayer("Alice", Color.WHITE))
    .withBlackPlayer(new HumanPlayer("Bob", Color.BLACK))
    .withBoard(Board.createStandardBoard())
    .build();
```

### 5. Template Method Pattern

**Purpose:** Define skeleton of move validation, allowing pieces to customize.

```java
// MoveValidator defines the validation skeleton
public boolean isValidMove(Move move) {
    // 1. Basic validation (common)
    if (!isWithinBounds(move)) return false;
    if (!isNotSamePosition(move)) return false;
    
    // 2. Piece-specific validation (delegated to strategy)
    if (!piece.getMoveStrategy().canMove(board, piece, from, to)) return false;
    
    // 3. Check validation (common)
    if (wouldLeaveKingInCheck(move)) return false;
    
    return true;
}
```

### 6. Command Pattern (Implicit)

**Purpose:** Encapsulate moves as objects for history and undo functionality.

```java
public class Move {
    private final Position from;
    private final Position to;
    private final Piece piece;
    private final Piece capturedPiece;
    private final MoveType moveType;
    // Enables undo, replay, and move history
}
```

---

## SOLID Principles Application

### Single Responsibility Principle (SRP)

Each class has one and only one reason to change:

| Class | Single Responsibility |
|-------|----------------------|
| `Board` | Manage board state and piece positions |
| `Piece` | Represent a chess piece with its properties |
| `MoveStrategy` | Calculate valid moves for a piece type |
| `MoveValidator` | Validate if a move is legal |
| `CheckDetector` | Detect check, checkmate, and stalemate |
| `Game` | Orchestrate game flow and turns |
| `Player` | Represent a player and get their moves |

### Open/Closed Principle (OCP)

Classes are open for extension but closed for modification:

```java
// Adding new piece type (e.g., for Fairy Chess)
public class Empress extends Piece {
    public Empress(Color color, Position position) {
        super(color, position, PieceType.EMPRESS, new EmpressMoveStrategy());
    }
}

public class EmpressMoveStrategy implements MoveStrategy {
    // Combines Rook + Knight moves
}
// No modification to existing code needed!
```

### Liskov Substitution Principle (LSP)

All piece subtypes are interchangeable:

```java
Piece piece = new King(Color.WHITE, new Position(0, 4));
Piece piece = new Queen(Color.WHITE, new Position(0, 3));
// Both work identically when calling piece.getMoveStrategy().getValidMoves()
```

### Interface Segregation Principle (ISP)

Interfaces are focused and minimal:

```java
interface MoveStrategy {
    List<Move> getValidMoves(Board board, Piece piece);
    boolean canMove(Board board, Piece piece, Position from, Position to);
}

interface GameEventListener {
    default void onGameStart(Game game) {}
    default void onMoveMade(Move move) {}
    default void onCheck(Color kingColor) {}
    default void onGameEnd(GameResult result) {}
}
```

### Dependency Inversion Principle (DIP)

High-level modules depend on abstractions:

```java
public class Game {
    // Depends on interfaces, not implementations
    private final Player whitePlayer;  // Abstract
    private final Player blackPlayer;  // Abstract
    private final MoveValidator moveValidator;  // Interface
    private final List<GameEventListener> listeners;  // Interface
    
    // Constructor injection
    public Game(Player white, Player black, Board board) {
        this.whitePlayer = white;
        this.blackPlayer = black;
        this.moveValidator = new MoveValidator(board);
    }
}
```

---

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              CHESS GAME - CLASS DIAGRAM                              │
└─────────────────────────────────────────────────────────────────────────────────────┘

                                    ┌─────────────────┐
                                    │     Color       │
                                    │    «enum»       │
                                    ├─────────────────┤
                                    │ WHITE           │
                                    │ BLACK           │
                                    └────────┬────────┘
                                             │
                    ┌────────────────────────┼────────────────────────┐
                    │                        │                        │
                    ▼                        ▼                        ▼
           ┌────────────────┐      ┌─────────────────┐      ┌─────────────────┐
           │    Position    │      │     Player      │      │    PieceType    │
           ├────────────────┤      │   «abstract»    │      │     «enum»      │
           │ - row: int     │      ├─────────────────┤      ├─────────────────┤
           │ - col: int     │      │ - name: String  │      │ KING            │
           ├────────────────┤      │ - color: Color  │      │ QUEEN           │
           │ + isValid()    │      ├─────────────────┤      │ ROOK            │
           │ + toString()   │      │ + makeMove()*   │      │ BISHOP          │
           └────────────────┘      │ + getName()     │      │ KNIGHT          │
                    △              │ + getColor()    │      │ PAWN            │
                    │              └────────┬────────┘      └─────────────────┘
           ┌────────┴────────┐              │                        │
           │                 │     ┌────────┴────────┐              │
    ┌──────┴──────┐   ┌──────┴──────┐              │              │
    │    Board    │   │    Move     │     ┌────────┴────────┐     │
    ├─────────────┤   ├─────────────┤     │   HumanPlayer   │     │
    │ - grid[8][8]│   │ - from      │     ├─────────────────┤     │
    │ - pieces    │   │ - to        │     │ + makeMove()    │     │
    ├─────────────┤   │ - piece     │     └─────────────────┘     │
    │ + getPieceAt│   │ - captured  │                             │
    │ + movePiece │   │ - moveType  │                             │
    │ + isOccupied│   └─────────────┘                             │
    └─────────────┘                                               │
           │                                                      │
           │ contains                                             │
           ▼                                                      │
    ┌─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                    PIECE HIERARCHY                                   │
└─────────────────────────────────────────────────────────────────────────────────────┘

                              ┌─────────────────────┐
                              │       Piece         │
                              │     «abstract»      │
                              ├─────────────────────┤
                              │ - color: Color      │
                              │ - position: Position│
                              │ - type: PieceType   │
                              │ - moveStrategy      │
                              │ - hasMoved: boolean │
                              ├─────────────────────┤
                              │ + getMoveStrategy() │
                              │ + getValidMoves()   │
                              │ + getSymbol()       │
                              └──────────┬──────────┘
                                         │
        ┌──────────┬──────────┬──────────┼──────────┬──────────┬──────────┐
        │          │          │          │          │          │          │
        ▼          ▼          ▼          ▼          ▼          ▼          │
   ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐     │
   │  King  │ │ Queen  │ │  Rook  │ │ Bishop │ │ Knight │ │  Pawn  │     │
   └────────┘ └────────┘ └────────┘ └────────┘ └────────┘ └────────┘     │
                                                                         │
                                                                         │
┌─────────────────────────────────────────────────────────────────────────┘
│
▼
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                               MOVE STRATEGY HIERARCHY                                │
└─────────────────────────────────────────────────────────────────────────────────────┘

                              ┌─────────────────────────────┐
                              │       «interface»           │
                              │       MoveStrategy          │
                              ├─────────────────────────────┤
                              │ + getValidMoves(board,piece)│
                              │ + canMove(board,piece,f,t)  │
                              └──────────────┬──────────────┘
                                             │
        ┌──────────┬──────────┬──────────────┼──────────────┬──────────┬──────────┐
        │          │          │              │              │          │          │
        ▼          ▼          ▼              ▼              ▼          ▼          ▼
   ┌─────────┐┌─────────┐┌─────────┐  ┌───────────┐  ┌─────────┐┌─────────┐┌─────────┐
   │  King   ││  Queen  ││  Rook   │  │  Bishop   │  │ Knight  ││  Pawn   ││Composite│
   │ Strategy││ Strategy││ Strategy│  │  Strategy │  │Strategy ││Strategy ││Strategy │
   └─────────┘└─────────┘└─────────┘  └───────────┘  └─────────┘└─────────┘└─────────┘


┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                 GAME ORCHESTRATION                                   │
└─────────────────────────────────────────────────────────────────────────────────────┘

  ┌─────────────────────────────────────────────────────────────────────┐
  │                              Game                                   │
  ├─────────────────────────────────────────────────────────────────────┤
  │ - board: Board                                                      │
  │ - whitePlayer: Player                                               │
  │ - blackPlayer: Player                                               │
  │ - currentTurn: Color                                                │
  │ - moveHistory: List<Move>                                           │
  │ - gameStatus: GameStatus                                            │
  │ - moveValidator: MoveValidator                                      │
  │ - checkDetector: CheckDetector                                      │
  │ - eventListeners: List<GameEventListener>                           │
  ├─────────────────────────────────────────────────────────────────────┤
  │ + play(): GameResult                                                │
  │ + makeMove(move): boolean                                           │
  │ + getCurrentPlayer(): Player                                        │
  │ + getGameStatus(): GameStatus                                       │
  │ + isCheck(): boolean                                                │
  │ + isCheckmate(): boolean                                            │
  │ + isStalemate(): boolean                                            │
  └───────────────────────────────┬─────────────────────────────────────┘
                                  │
                                  │ notifies
                                  ▼
                    ┌─────────────────────────────┐
                    │    «interface»              │
                    │   GameEventListener         │
                    ├─────────────────────────────┤
                    │ + onGameStart(game)         │
                    │ + onMoveMade(move)          │
                    │ + onCheck(color)            │
                    │ + onCheckmate(winner)       │
                    │ + onStalemate()             │
                    │ + onGameEnd(result)         │
                    │ + onInvalidMove(move)       │
                    └──────────────┬──────────────┘
                                   │
                                   ▼
                    ┌─────────────────────────────┐
                    │  ConsoleGameEventListener   │
                    └─────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                  VALIDATORS                                          │
└─────────────────────────────────────────────────────────────────────────────────────┘

  ┌─────────────────────────────┐           ┌─────────────────────────────┐
  │       MoveValidator         │           │       CheckDetector         │
  ├─────────────────────────────┤           ├─────────────────────────────┤
  │ - board: Board              │           │ - board: Board              │
  ├─────────────────────────────┤           ├─────────────────────────────┤
  │ + isValidMove(move): bool   │           │ + isInCheck(color): bool    │
  │ + getValidMoves(piece): List│           │ + isCheckmate(color): bool  │
  │ + wouldLeaveInCheck(): bool │           │ + isStalemate(color): bool  │
  └─────────────────────────────┘           │ + getAttackers(pos): List   │
                                            └─────────────────────────────┘
```

---

## Data Models

### Enums

| Enum | Values | Purpose |
|------|--------|---------|
| `Color` | WHITE, BLACK | Player/piece color |
| `PieceType` | KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN | Piece types |
| `GameStatus` | NOT_STARTED, IN_PROGRESS, WHITE_WINS, BLACK_WINS, STALEMATE, DRAW | Game state |
| `MoveType` | NORMAL, CAPTURE, CASTLING_KINGSIDE, CASTLING_QUEENSIDE, EN_PASSANT, PAWN_PROMOTION | Move types |

### Key Models

| Model | Key Fields | Purpose |
|-------|------------|---------|
| `Position` | row, col | Board coordinate (0-7 for row/col) |
| `Piece` | color, position, type, moveStrategy, hasMoved | Abstract base for all pieces |
| `Board` | grid[8][8], pieces | 8x8 chess board with piece management |
| `Move` | from, to, piece, capturedPiece, moveType | Encapsulates a chess move |
| `Player` | name, color | Abstract player base class |
| `Game` | board, players, currentTurn, moveHistory, status | Main game orchestrator |

---

## Core Components

### 1. Board Representation

```
     0   1   2   3   4   5   6   7
   ┌───┬───┬───┬───┬───┬───┬───┬───┐
 7 │ r │ n │ b │ q │ k │ b │ n │ r │  Black (row 7)
   ├───┼───┼───┼───┼───┼───┼───┼───┤
 6 │ p │ p │ p │ p │ p │ p │ p │ p │  Black pawns (row 6)
   ├───┼───┼───┼───┼───┼───┼───┼───┤
 5 │   │   │   │   │   │   │   │   │
   ├───┼───┼───┼───┼───┼───┼───┼───┤
 4 │   │   │   │   │   │   │   │   │
   ├───┼───┼───┼───┼───┼───┼───┼───┤
 3 │   │   │   │   │   │   │   │   │
   ├───┼───┼───┼───┼───┼───┼───┼───┤
 2 │   │   │   │   │   │   │   │   │
   ├───┼───┼───┼───┼───┼───┼───┼───┤
 1 │ P │ P │ P │ P │ P │ P │ P │ P │  White pawns (row 1)
   ├───┼───┼───┼───┼───┼───┼───┼───┤
 0 │ R │ N │ B │ Q │ K │ B │ N │ R │  White (row 0)
   └───┴───┴───┴───┴───┴───┴───┴───┘
     a   b   c   d   e   f   g   h
```

### 2. Game Flow

```
┌─────────┐     ┌─────────────┐     ┌───────────────┐     ┌─────────────┐
│  Start  │────▶│ Initialize  │────▶│ White's Turn  │────▶│  Get Move   │
└─────────┘     │   Board     │     │               │     │  from Player│
                └─────────────┘     └───────────────┘     └──────┬──────┘
                                                                 │
                                            ┌────────────────────┘
                                            ▼
                                    ┌───────────────┐
                                    │ Validate Move │
                                    └───────┬───────┘
                                            │
                              ┌─────────────┴─────────────┐
                              │                           │
                        ┌─────▼─────┐               ┌─────▼─────┐
                        │  Invalid  │               │   Valid   │
                        │   Move    │               │   Move    │
                        └─────┬─────┘               └─────┬─────┘
                              │                           │
                              │                    ┌──────▼──────┐
                              │                    │ Execute Move│
                              │                    └──────┬──────┘
                              │                           │
                              │                    ┌──────▼──────┐
                              │                    │ Check Game  │
                              │                    │   Status    │
                              │                    └──────┬──────┘
                              │                           │
                              │         ┌─────────────────┼─────────────────┐
                              │         │                 │                 │
                              │   ┌─────▼─────┐    ┌──────▼─────┐    ┌──────▼─────┐
                              │   │ Checkmate │    │ Stalemate  │    │  Continue  │
                              │   │ Game Over │    │ Game Over  │    │   Game     │
                              │   └───────────┘    └────────────┘    └──────┬─────┘
                              │                                            │
                              │                                     ┌──────▼──────┐
                              └─────────────────────────────────────▶│ Switch Turn │
                                                                    └──────┬──────┘
                                                                           │
                                                                    ┌──────▼──────┐
                                                                    │Black's Turn │
                                                                    └─────────────┘
```

### 3. Move Validation Flow

```java
public boolean isValidMove(Move move) {
    // Step 1: Basic validation
    if (!isWithinBounds(move.getTo())) return false;
    if (move.getFrom().equals(move.getTo())) return false;
    
    // Step 2: Piece at source exists and belongs to current player
    Piece piece = board.getPieceAt(move.getFrom());
    if (piece == null) return false;
    if (piece.getColor() != currentTurn) return false;
    
    // Step 3: Destination is not occupied by own piece
    Piece targetPiece = board.getPieceAt(move.getTo());
    if (targetPiece != null && targetPiece.getColor() == piece.getColor()) return false;
    
    // Step 4: Piece-specific move validation (Strategy Pattern)
    if (!piece.getMoveStrategy().canMove(board, piece, move.getFrom(), move.getTo())) {
        return false;
    }
    
    // Step 5: Move doesn't leave own king in check
    if (wouldLeaveKingInCheck(move)) return false;
    
    return true;
}
```

---

## Move Validation & Game Rules

### Piece Movement Strategies

| Piece | Movement Rules | Special Rules |
|-------|---------------|---------------|
| King | 1 square any direction | Castling (if not moved, not in check, path clear) |
| Queen | Any direction, any distance | Cannot jump over pieces |
| Rook | Horizontal/vertical, any distance | Castling participation |
| Bishop | Diagonal, any distance | Cannot jump over pieces |
| Knight | L-shape (2+1 or 1+2) | Can jump over pieces |
| Pawn | Forward 1 (or 2 from start), diagonal capture | En passant, Promotion |

### Check Detection Algorithm

```java
public boolean isInCheck(Color kingColor) {
    Position kingPosition = findKingPosition(kingColor);
    Color opponentColor = kingColor.opposite();
    
    // Check if any opponent piece can attack the king
    for (Piece piece : board.getPiecesByColor(opponentColor)) {
        MoveStrategy strategy = piece.getMoveStrategy();
        if (strategy.canMove(board, piece, piece.getPosition(), kingPosition)) {
            return true;
        }
    }
    return false;
}
```

### Checkmate Detection Algorithm

```java
public boolean isCheckmate(Color kingColor) {
    if (!isInCheck(kingColor)) return false;
    
    // Try all possible moves for all pieces of kingColor
    for (Piece piece : board.getPiecesByColor(kingColor)) {
        List<Move> validMoves = moveValidator.getValidMoves(piece);
        if (!validMoves.isEmpty()) {
            return false;  // At least one legal move exists
        }
    }
    return true;  // No legal moves and in check = checkmate
}
```

### Stalemate Detection

```java
public boolean isStalemate(Color kingColor) {
    if (isInCheck(kingColor)) return false;
    
    // No legal moves but not in check
    for (Piece piece : board.getPiecesByColor(kingColor)) {
        List<Move> validMoves = moveValidator.getValidMoves(piece);
        if (!validMoves.isEmpty()) {
            return false;
        }
    }
    return true;
}
```

---

## Extension Points

### 1. Adding New Piece Type (Fairy Chess)

```java
// 1. Add to PieceType enum
public enum PieceType {
    KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN,
    EMPRESS, PRINCESS  // New fairy pieces
}

// 2. Create piece class
public class Empress extends Piece {
    public Empress(Color color, Position position) {
        super(color, position, PieceType.EMPRESS, new EmpressMoveStrategy());
    }
    
    @Override
    public char getSymbol() {
        return color == Color.WHITE ? 'E' : 'e';
    }
}

// 3. Create move strategy
public class EmpressMoveStrategy implements MoveStrategy {
    // Combines Rook + Knight moves
    private final RookMoveStrategy rookStrategy = new RookMoveStrategy();
    private final KnightMoveStrategy knightStrategy = new KnightMoveStrategy();
    
    @Override
    public List<Move> getValidMoves(Board board, Piece piece) {
        List<Move> moves = new ArrayList<>();
        moves.addAll(rookStrategy.getValidMoves(board, piece));
        moves.addAll(knightStrategy.getValidMoves(board, piece));
        return moves;
    }
}

// 4. Update factory
public static Piece createPiece(PieceType type, Color color, Position pos) {
    return switch (type) {
        // ... existing
        case EMPRESS -> new Empress(color, pos);
    };
}
```

### 2. Adding New Game Variant (Chess960)

```java
public class Chess960BoardFactory {
    public static Board createRandomizedBoard() {
        Board board = new Board();
        // Randomize back rank according to Chess960 rules
        // King between rooks, bishops on opposite colors
        return board;
    }
}
```

### 3. Adding Time Control

```java
public class TimedGame extends Game {
    private final Duration whiteTime;
    private final Duration blackTime;
    private final Duration increment;
    
    @Override
    protected void beforeMove(Player player) {
        startTimer(player.getColor());
    }
    
    @Override
    protected void afterMove(Player player) {
        stopTimer(player.getColor());
        addIncrement(player.getColor());
    }
}
```

### 4. Network Multiplayer

```java
public class NetworkPlayer extends Player {
    private final Socket socket;
    
    @Override
    public Move makeMove(Board board) {
        // Receive move from network
        return receiveMoveFromSocket();
    }
}

public class NetworkGameEventListener implements GameEventListener {
    @Override
    public void onMoveMade(Move move) {
        // Broadcast move to all connected clients
        broadcastMove(move);
    }
}
```

---

## API Summary

### Game Facade API

| Method | Description |
|--------|-------------|
| `Game.Builder.build()` | Create a new game |
| `game.play()` | Start and run game loop |
| `game.makeMove(from, to)` | Make a move |
| `game.getCurrentPlayer()` | Get current player |
| `game.getGameStatus()` | Get game status |
| `game.isCheck()` | Check if current player is in check |
| `game.getMoveHistory()` | Get all moves made |
| `game.getBoard()` | Get board state |

### Board API

```java
Piece getPieceAt(Position position);
void setPieceAt(Position position, Piece piece);
void movePiece(Position from, Position to);
void removePiece(Position position);
List<Piece> getPiecesByColor(Color color);
Position findKing(Color color);
boolean isOccupied(Position position);
boolean isPathClear(Position from, Position to);
Board copy();  // For simulation
void display();
```

### Move Validator API

```java
boolean isValidMove(Move move);
List<Move> getValidMoves(Piece piece);
List<Move> getAllValidMoves(Color color);
boolean wouldLeaveKingInCheck(Move move);
```

---

## How to Run

### Compile
```bash
cd chess
javac -d ../out $(find . -name "*.java")
```

### Run
```bash
java -cp ../out chess.Main
```

### Game Modes Available
1. **Human vs Human** - Two players on same console
2. **Quick Demo** - Demonstrates Scholar's Mate (4-move checkmate)
3. **Stalemate Demo** - Shows stalemate position
4. **Interactive Game** - Full chess game with move input

---

## Package Structure

```
chess/
├── Main.java                           # Entry point
├── CHESS_LLD.md                        # This document
├── enums/
│   ├── Color.java                      # WHITE, BLACK
│   ├── PieceType.java                  # KING, QUEEN, etc.
│   ├── GameStatus.java                 # Game state
│   └── MoveType.java                   # Move classifications
├── exceptions/
│   ├── InvalidMoveException.java       # Invalid move error
│   ├── InvalidPositionException.java   # Out of bounds
│   └── GameOverException.java          # Game already ended
├── models/
│   ├── Position.java                   # Board coordinate
│   ├── Move.java                       # Move record
│   ├── Board.java                      # Chess board
│   └── Piece.java                      # Abstract piece base
├── pieces/
│   ├── King.java                       # King piece
│   ├── Queen.java                      # Queen piece
│   ├── Rook.java                       # Rook piece
│   ├── Bishop.java                     # Bishop piece
│   ├── Knight.java                     # Knight piece
│   └── Pawn.java                       # Pawn piece
├── strategies/
│   ├── MoveStrategy.java               # Strategy interface
│   ├── KingMoveStrategy.java           # King movement
│   ├── QueenMoveStrategy.java          # Queen movement
│   ├── RookMoveStrategy.java           # Rook movement
│   ├── BishopMoveStrategy.java         # Bishop movement
│   ├── KnightMoveStrategy.java         # Knight movement
│   └── PawnMoveStrategy.java           # Pawn movement
├── validators/
│   ├── MoveValidator.java              # Move validation
│   └── CheckDetector.java              # Check/checkmate detection
├── players/
│   ├── Player.java                     # Abstract player
│   └── HumanPlayer.java                # Human input player
├── factories/
│   ├── PieceFactory.java               # Create pieces
│   └── BoardFactory.java               # Create boards
├── observers/
│   ├── GameEventListener.java          # Observer interface
│   └── ConsoleGameEventListener.java   # Console output
└── game/
    ├── Game.java                       # Game orchestrator + Builder
    ├── GameResult.java                 # Game outcome
    └── ChessGame.java                  # Facade class
```

---

## Design Rationale

This design is:

| Aspect | Reasoning |
|--------|-----------|
| **Extensible** | Strategy pattern allows new piece types/movements without modifying core logic |
| **Loosely Coupled** | Components interact through interfaces; Game doesn't know concrete piece types |
| **SOLID-Compliant** | Each class has single responsibility; open for extension via strategies |
| **Testable** | Dependencies injected; strategies can be mocked; board can be set up in any state |
| **Maintainable** | Clear separation of concerns; well-defined interfaces; minimal coupling |
| **Scalable** | Can extend to network play, AI players, different variants easily |

---

## Summary

| Principle/Pattern | Application |
|-------------------|-------------|
| **SRP** | Each class has single responsibility |
| **OCP** | Strategies allow extension without modification |
| **LSP** | All piece types are interchangeable |
| **ISP** | Focused, minimal interfaces |
| **DIP** | Dependency injection via Builder/Constructor |
| **Strategy** | MoveStrategy for piece movement |
| **Factory** | PieceFactory, BoardFactory |
| **Builder** | Game.Builder for configuration |
| **Observer** | GameEventListener for events |
| **Template Method** | Move validation skeleton |
| **Command** | Move encapsulation |

The design is **production-ready**, **testable**, and **extensible** for future requirements like AI players, network play, and chess variants.


