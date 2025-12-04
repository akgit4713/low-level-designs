# Low-Level Design (LLD) Reference Repository

A comprehensive collection of production-quality low-level system designs, implementing industry-standard design patterns and SOLID principles. Built as a reference for system design interviews and architectural decision-making.

## Overview

Each implementation demonstrates:
- **Clean Architecture** — Separation of concerns across models, services, repositories, and strategies
- **SOLID Principles** — Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion
- **Design Patterns** — Factory, Strategy, Observer, State, Builder, Template Method where applicable
- **Production Practices** — Custom exceptions, thread-safety considerations, dependency injection

## Systems Catalog

| Category | Implementations |
|----------|-----------------|
| Booking & Reservations | Airline, BookMyShow, Concert Booking, Car Rental |
| Financial Systems | ATM, Digital Wallet, Splitwise |
| Social Platforms | LinkedIn, Social Network, Stack Overflow |
| E-Commerce | Online Shopping, Food Delivery, Online Auction |
| Management Systems | Hotel, Library, Restaurant, Task Management, Course Registration |
| Media & Entertainment | Music Streaming, Movie Rating, CricInfo |
| Infrastructure | Parking Lot, Elevator System, Traffic Signal, Vending Machine |
| Gaming | Tic-Tac-Toe, Snake & Ladder |
| Core Components | LRU Cache, Pub-Sub, Ride Sharing |

## Project Structure

```
<system>/
├── <SYSTEM>_LLD.md     # Design document with class diagrams and rationale
├── Main.java           # Executable demonstration of core flows
├── models/             # Domain entities and value objects
├── services/           # Business logic layer
├── repositories/       # Data access abstractions
├── strategies/         # Pluggable algorithm implementations
├── observers/          # Event-driven communication
├── factories/          # Object creation patterns
├── states/             # State machine implementations (where applicable)
├── enums/              # Type-safe constants
└── exceptions/         # Domain-specific error handling
```

## Running Examples

**Java** (JDK 11+)
```bash
cd <system>
find . -name "*.java" > sources.txt && javac @sources.txt -d out
java -cp out Main
```

**Python** (elevator_system)
```bash
cd elevator_system
python -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
python example.py
```

## Design Generation Prompt

These implementations were generated using the following prompt template:

<details>
<summary><strong>Click to expand prompt</strong></summary>

```markdown
You are an expert Software Architect and Senior Engineer.

I will describe a problem or feature. Your job is to:
1. Design a clear, extensible LOW-LEVEL DESIGN (LLD).
2. Implement WORKING CODE for it.
3. Follow SOLID principles, clean code practices, and appropriate design patterns.
4. Keep the design loosely coupled and easily testable.

### General Guidelines

- Ask clarifying questions first if any requirement is ambiguous or underspecified.
- Assume the primary implementation language is Java unless overridden.
- Prefer readability, maintainability, and testability over cleverness.
- Use meaningful names, small focused methods, and avoid code smells.

### LLD Requirements

Before coding, produce a structured LLD that includes:

1. **Responsibility Breakdown**
   - List core responsibilities and how they're separated across components.

2. **Key Abstractions**
   - Main classes, interfaces, and data models.
   - For each: 1–2 lines on its purpose.

3. **Relationships & Collaborations**
   - How components interact (e.g., which class depends on which interface).
   - Highlight where you're applying SOLID principles:
     - SRP – Single Responsibility
     - OCP – Open/Closed
     - LSP – Liskov Substitution
     - ISP – Interface Segregation
     - DIP – Dependency Inversion

4. **Design Patterns**
   - Explicitly call out which patterns you're using (e.g., Strategy, Factory, 
     Observer, Builder, Template Method, etc.) and why they fit.

5. **Extension Points**
   - Show how the design can be extended in the future (e.g., adding new types/flows 
     without modifying existing core logic).

### Coding Requirements

After the LLD is agreed, write production-quality code:

1. **Code Structure**
   - Implement the classes and interfaces designed.
   - Apply Dependency Injection (via constructors or DI frameworks if relevant).
   - Avoid tight coupling and long parameter lists.

2. **Error Handling & Edge Cases**
   - Handle invalid inputs, exceptional scenarios, and failures gracefully.
   - Use clear exceptions or error types with helpful messages.

3. **Clean Code & Engineering Practices**
   - No duplicated logic; extract reusable methods/components.
   - Single responsibility per class/module where feasible.
   - Keep functions short and cohesive.

4. **Testing**
   - Provide unit tests for key components and business logic.
   - Use dependency injection / mocks for external dependencies.

5. **Examples / Usage**
   - Provide a short usage example showing how to wire and run the design.

### Output Format

1. Assumptions & Clarifications (if any)
2. LLD Overview (Responsibilities, Classes & Interfaces, Relationships & Patterns, Extension Points)
3. Code Implementation
4. Unit Tests
5. Usage Example / Sample Run
6. Short Design Rationale
```

</details>

## Contributing

1. Follow the existing directory structure and naming conventions
2. Include a comprehensive `*_LLD.md` document with design rationale
3. Provide a runnable `Main.java` demonstrating primary use cases
4. Ensure code adheres to SOLID principles and uses appropriate patterns

## License

MIT License — See individual system directories for any specific licensing notes.

---

*Maintained as a reference for software design interviews and architectural patterns.*
