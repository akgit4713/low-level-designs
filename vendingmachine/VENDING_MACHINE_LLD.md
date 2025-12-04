# Vending Machine - Low-Level Design (LLD)

## Overview

A thread-safe, extensible vending machine implementation using the **State Pattern** as the core design pattern. The system supports multiple products, various currency denominations, change calculation, and administrative operations.

---

## Requirements Addressed

| Requirement | How It's Addressed |
|-------------|-------------------|
| Multiple products with different prices/quantities | `ProductInventory` with `ItemSlot` per product |
| Accept coins and notes | `Coin` and `Note` enums with values |
| Dispense product and return change | `DispensingState` handles both |
| Track product quantities | `ItemSlot` with quantity tracking |
| Handle concurrent transactions | Singleton + synchronized blocks |
| Restocking interface | `VendingMachine.restockProduct()` |
| Money collection interface | `VendingMachine.collectCash()` |
| Handle exceptions | Custom exception hierarchy |

---

## Architecture

### Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           VendingMachine (Singleton)                         │
├─────────────────────────────────────────────────────────────────────────────┤
│ - productInventory: ProductInventory                                         │
│ - cashInventory: CashInventory                                               │
│ - currentState: VendingMachineState                                          │
│ - currentBalance: int                                                        │
│ - selectedProduct: Product                                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│ + insertCoin(Coin): void                                                     │
│ + insertNote(Note): void                                                     │
│ + selectProduct(String): void                                                │
│ + cancelTransaction(): void                                                  │
│ + dispenseProduct(): void                                                    │
│ + addProduct(Product, int): void      [Admin]                                │
│ + restockProduct(String, int): void   [Admin]                                │
│ + collectCash(): int                  [Admin]                                │
└─────────────────────────────────────────────────────────────────────────────┘
                                     │
                                     │ delegates to
                                     ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                     «interface» VendingMachineState                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ + insertCoin(Coin): void                                                     │
│ + insertNote(Note): void                                                     │
│ + selectProduct(String): void                                                │
│ + cancelTransaction(): void                                                  │
│ + dispenseProduct(): void                                                    │
│ + getStateName(): String                                                     │
└─────────────────────────────────────────────────────────────────────────────┘
                    △                    △                    △
                    │                    │                    │
         ┌─────────┴──┐        ┌────────┴────┐       ┌──────┴───────┐
         │ IdleState  │        │HasMoneyState│       │DispensingState│
         └────────────┘        └─────────────┘       └──────────────┘
```

### State Transitions

```
                    ┌──────────────────────┐
                    │                      │
                    ▼                      │
              ┌──────────┐                 │
      ┌──────►│  IDLE    │◄────────────────┘
      │       └────┬─────┘
      │            │ insertCoin/insertNote
      │            ▼
      │       ┌───────────┐
      │       │ HAS_MONEY │◄───────┐
      │       └─────┬─────┘        │
      │             │              │ insertCoin/insertNote
      │             │              │
      │             ├──────────────┘
      │             │ selectProduct (with sufficient funds)
      │             ▼
      │       ┌────────────┐
      │       │ DISPENSING │
      │       └─────┬──────┘
      │             │ dispenseProduct (auto)
      └─────────────┘
      
      cancelTransaction: HAS_MONEY → IDLE (with refund)
```

---

## Component Details

### 1. Enums

| Enum | Purpose |
|------|---------|
| `Coin` | Currency denominations for coins (₹1, ₹2, ₹5, ₹10) |
| `Note` | Currency denominations for notes (₹10, ₹20, ₹50, ₹100, ₹200, ₹500) |

### 2. Models

| Class | Responsibility |
|-------|---------------|
| `Product` | Immutable product representation with code, name, price |
| `ItemSlot` | Mutable slot holding product with quantity management |

### 3. Inventory

| Class | Responsibility |
|-------|---------------|
| `ProductInventory` | Manages all product slots; handles dispense/restock |
| `CashInventory` | Manages coins/notes; handles change calculation |

### 4. States

| State | Description | Valid Operations |
|-------|-------------|-----------------|
| `IdleState` | Waiting for money | `insertCoin`, `insertNote` |
| `HasMoneyState` | Money inserted | `insertCoin`, `insertNote`, `selectProduct`, `cancelTransaction` |
| `DispensingState` | Dispensing product | `dispenseProduct` (auto-triggered) |

### 5. Exceptions

| Exception | When Thrown |
|-----------|-------------|
| `InsufficientFundsException` | Balance < Product Price |
| `OutOfStockException` | Product quantity = 0 |
| `InvalidProductException` | Unknown product code |
| `InsufficientChangeException` | Cannot provide exact change |
| `InvalidStateException` | Operation invalid in current state |

---

## Design Patterns Used

### 1. State Pattern (Primary)

**Where**: `VendingMachineState` interface with `IdleState`, `HasMoneyState`, `DispensingState`

**Why**: 
- Machine behavior varies drastically by state
- Avoids complex switch/if-else chains
- Easy to add new states (e.g., `MaintenanceState`)
- Each state encapsulates its own behavior

### 2. Singleton Pattern

**Where**: `VendingMachine` class

**Why**:
- Single point of access for the machine
- Thread-safe with double-checked locking
- Maintains consistent state across all callers

### 3. Factory Pattern (Implicit)

**Where**: Product creation can be extended with `ProductFactory`

**Why**: Future extension for product variants

---

## SOLID Principles Applied

### Single Responsibility Principle (SRP)
- `ProductInventory` only manages products
- `CashInventory` only manages cash
- Each state only handles its own transitions
- `Product` is immutable and only represents data

### Open/Closed Principle (OCP)
- New states can be added without modifying existing ones
- New product types can be added without changing inventory logic
- New currency denominations by extending enums

### Liskov Substitution Principle (LSP)
- All state implementations are substitutable
- Any state can be assigned to `currentState`

### Interface Segregation Principle (ISP)
- `VendingMachineState` has focused, cohesive methods
- States don't need to implement unused operations

### Dependency Inversion Principle (DIP)
- `VendingMachine` depends on `VendingMachineState` interface
- States don't depend on each other directly

---

## Thread Safety

1. **Singleton**: Double-checked locking for instance creation
2. **Operations**: All public operations are synchronized
3. **Inventories**: Use `ConcurrentHashMap` for internal storage
4. **ItemSlot**: Synchronized `dispense()` and `addStock()` methods

---

## Extension Points

### 1. Adding New States
```java
public class MaintenanceState implements VendingMachineState {
    // All operations throw MaintenanceException
}
```

### 2. Adding New Currency
```java
// In Coin.java
RUPEE_20(20); // Add new denomination
```

### 3. Custom Change Strategy
```java
public interface ChangeStrategy {
    Map<String, Integer> calculateChange(int amount, CashInventory inventory);
}

public class GreedyChangeStrategy implements ChangeStrategy { }
public class DynamicProgrammingChangeStrategy implements ChangeStrategy { }
```

### 4. Product Categories
```java
public enum ProductCategory {
    BEVERAGE, SNACK, CANDY, HEALTH
}
```

### 5. Discount Strategies
```java
public interface DiscountStrategy {
    int applyDiscount(Product product, int originalPrice);
}
```

---

## File Structure

```
vendingmachine/
├── enums/
│   ├── Coin.java              ✅ Implemented
│   └── Note.java              ✅ Implemented
├── exceptions/
│   ├── VendingMachineException.java      ✅ Implemented
│   ├── InsufficientFundsException.java   ✅ Implemented
│   ├── OutOfStockException.java          ✅ Implemented
│   ├── InvalidProductException.java      ✅ Implemented
│   ├── InsufficientChangeException.java  ✅ Implemented
│   └── InvalidStateException.java        ✅ Implemented
├── models/
│   ├── Product.java           ✅ Implemented
│   └── ItemSlot.java          ✅ Implemented
├── inventory/
│   ├── ProductInventory.java  ✅ Implemented
│   └── CashInventory.java     ✅ Implemented
├── states/
│   ├── VendingMachineState.java  ✅ Implemented
│   ├── IdleState.java            ✅ Implemented
│   ├── HasMoneyState.java        ✅ Implemented
│   └── DispensingState.java      ✅ Implemented
├── VendingMachine.java        ✅ Implemented
└── Main.java                  ✅ Implemented
```

---

## Usage Example

```java
// Get machine instance
VendingMachine machine = VendingMachine.getInstance();

// Admin: Setup products
machine.addProduct(new Product("A1", "Coca Cola", 40), 5);
machine.initializeDefaultCash();

// User: Purchase flow
machine.insertNote(Note.RUPEE_50);   // Insert ₹50
machine.selectProduct("A1");          // Select Coca Cola (₹40)
// Machine dispenses product and ₹10 change

// User: Cancel transaction
machine.insertNote(Note.RUPEE_100);
machine.cancelTransaction();          // Refunds ₹100

// Admin: Operations
machine.restockProduct("A1", 10);     // Add 10 units
int cash = machine.collectCash();     // Collect all money
```

---

## Sample Run Output

```
╔═══════════════════════════════════════════════════╗
║         VENDING MACHINE SYSTEM DEMO               ║
╚═══════════════════════════════════════════════════╝

✓ Added product: [A1] Coca Cola - ₹40 (qty: 5)
✓ Added product: [B1] Lays Classic - ₹20 (qty: 6)
✓ Initialized default cash for change

✓ Inserted: ₹50 note
  Current balance: ₹50
✓ Selected: [A1] Coca Cola - ₹40

╔═══════════════════════════════════════╗
║         DISPENSING PRODUCT            ║
╠═══════════════════════════════════════╣
║  Product: Coca Cola                   ║
║  Price:   ₹40                         ║
║  Paid:    ₹50                         ║
╠═══════════════════════════════════════╣
║  Change:  ₹10                         ║
║    ₹10 Coin × 1                       ║
╠═══════════════════════════════════════╣
║  ✓ Please collect your product!      ║
╚═══════════════════════════════════════╝
```

---

## Why This Design?

1. **Extensible**: Add new states, products, or currencies without touching core logic
2. **Loosely Coupled**: Components interact through interfaces
3. **Testable**: Each component can be unit tested in isolation
4. **Thread-Safe**: Supports concurrent operations
5. **Clean**: Follows SOLID principles and clean code practices
6. **Maintainable**: Clear separation of concerns

