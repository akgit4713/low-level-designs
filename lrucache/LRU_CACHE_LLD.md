# LRU Cache - Low-Level Design

## Overview

A thread-safe, O(1) LRU (Least Recently Used) Cache implementation with support for pluggable eviction policies.

## Requirements

| Requirement | Implementation |
|-------------|----------------|
| `put(key, value)` | O(1) insertion with automatic eviction at capacity |
| `get(key)` | O(1) lookup with LRU order update |
| Fixed capacity | Configurable at construction time |
| Thread-safe | `ReadWriteLock` for concurrent access |
| O(1) operations | HashMap + Doubly Linked List |

---

## Architecture

### Class Diagram

```
                    ┌─────────────────────────────────────────┐
                    │           <<interface>>                 │
                    │             Cache<K, V>                 │
                    ├─────────────────────────────────────────┤
                    │ + get(key: K): Optional<V>              │
                    │ + put(key: K, value: V): void           │
                    │ + remove(key: K): Optional<V>           │
                    │ + size(): int                           │
                    │ + capacity(): int                       │
                    │ + clear(): void                         │
                    │ + containsKey(key: K): boolean          │
                    └────────────────────┬────────────────────┘
                                         │ implements
                                         ▼
                    ┌─────────────────────────────────────────┐
                    │              LRUCache<K, V>             │
                    ├─────────────────────────────────────────┤
                    │ - capacity: int                         │
                    │ - cache: Map<K, Node<K,V>>              │
                    │ - evictionPolicy: EvictionPolicy<K,V>   │
                    │ - evictionListener: EvictionListener    │
                    │ - lock: ReadWriteLock                   │
                    ├─────────────────────────────────────────┤
                    │ + get(key: K): Optional<V>              │
                    │ + put(key: K, value: V): void           │
                    │ - evictIfNecessary(): void              │
                    └─────────────────────────────────────────┘
                        │                            │
            uses        │                            │ uses
                        ▼                            ▼
┌───────────────────────────────────┐    ┌────────────────────────────────┐
│      <<interface>>                │    │     <<interface>>              │
│    EvictionPolicy<K, V>           │    │   EvictionListener<K, V>       │
├───────────────────────────────────┤    ├────────────────────────────────┤
│ + recordAccess(node): void        │    │ + onEviction(key, value): void │
│ + recordInsertion(node): void     │    └────────────────────────────────┘
│ + getEvictionCandidate(): Node    │
│ + remove(node): void              │
│ + clear(): void                   │
└───────────────────┬───────────────┘
                    │ implements
        ┌───────────┴───────────┐
        ▼                       ▼
┌───────────────────┐   ┌───────────────────┐
│ LRUEvictionPolicy │   │ FIFOEvictionPolicy│
├───────────────────┤   ├───────────────────┤
│ - accessOrder:    │   │ - insertionOrder: │
│   DoublyLinkedList│   │   DoublyLinkedList│
└───────────────────┘   └───────────────────┘
```

### Data Structure

```
HashMap<K, Node>                 Doubly Linked List (Access Order)
┌───────────────┐               ┌──────┐    ┌──────┐    ┌──────┐    ┌──────┐
│ key1 → Node1  │──────────────▶│ HEAD │◄──▶│Node1 │◄──▶│Node2 │◄──▶│ TAIL │
│ key2 → Node2  │──────────────▶│(sent)│    │ MRU  │    │ LRU  │    │(sent)│
└───────────────┘               └──────┘    └──────┘    └──────┘    └──────┘
                                             Most        Least
                                            Recent      Recent
                                            (front)     (back)
```

---

## SOLID Principles

| Principle | Application |
|-----------|-------------|
| **SRP** | `LRUCache` handles cache logic; `EvictionPolicy` handles ordering; `DoublyLinkedList` manages nodes |
| **OCP** | New eviction policies can be added without modifying `LRUCache` |
| **LSP** | All `EvictionPolicy` implementations are interchangeable |
| **ISP** | `Cache` interface is minimal; `EvictionListener` is separate and optional |
| **DIP** | `LRUCache` depends on `EvictionPolicy` abstraction, not concrete implementations |

---

## Design Patterns

### 1. Strategy Pattern
**Usage**: `EvictionPolicy` interface  
**Benefit**: Swap eviction algorithms (LRU, FIFO, LFU) without changing cache code

### 2. Observer Pattern
**Usage**: `EvictionListener` interface  
**Benefit**: Decouple cache from eviction handling (logging, metrics, persistence)

### 3. Builder Pattern
**Usage**: `LRUCache.Builder`  
**Benefit**: Flexible construction with optional parameters

### 4. Sentinel Pattern
**Usage**: Dummy head/tail nodes in `DoublyLinkedList`  
**Benefit**: Eliminates null checks and edge case handling

---

## Time & Space Complexity

| Operation | Time | Space |
|-----------|------|-------|
| `get(key)` | O(1) | - |
| `put(key, value)` | O(1) | O(1) per entry |
| `remove(key)` | O(1) | - |
| Eviction | O(1) | - |
| **Total Space** | - | O(capacity) |

---

## Thread Safety

```java
// ReadWriteLock provides:
// - Multiple concurrent readers
// - Exclusive writer access
// - No starvation

ReadWriteLock lock = new ReentrantReadWriteLock();
Lock readLock = lock.readLock();   // size(), containsKey()
Lock writeLock = lock.writeLock(); // get(), put(), remove(), clear()
```

**Note**: `get()` uses write lock because it updates LRU order.

---

## Package Structure

```
lrucache/
├── cache/
│   ├── Cache.java              # Core interface
│   ├── LRUCache.java           # Main implementation
│   ├── Node.java               # Linked list node
│   └── DoublyLinkedList.java   # Order management
├── policy/
│   ├── EvictionPolicy.java     # Strategy interface
│   ├── LRUEvictionPolicy.java  # LRU implementation
│   └── FIFOEvictionPolicy.java # FIFO implementation
├── listener/
│   └── EvictionListener.java   # Observer interface
├── test/
│   └── LRUCacheTest.java       # Unit tests
└── Main.java                   # Demo/usage examples
```

---

## Usage Examples

### Basic Usage
```java
Cache<String, Integer> cache = LRUCache.create(100);

cache.put("key1", 1);
cache.put("key2", 2);

Optional<Integer> value = cache.get("key1");  // Returns Optional.of(1)
Optional<Integer> missing = cache.get("key3"); // Returns Optional.empty()
```

### With Eviction Listener
```java
LRUCache<String, User> cache = new LRUCache.Builder<String, User>(1000)
    .evictionListener((key, user) -> {
        logger.info("Evicted user: {} ({})", key, user.getName());
        persistToDatabase(user);
    })
    .build();
```

### With Custom Policy (FIFO)
```java
Cache<String, Data> cache = new LRUCache.Builder<String, Data>(500)
    .evictionPolicy(new FIFOEvictionPolicy<>())
    .build();
```

---

## Extension Points

1. **New Eviction Policies**
   - Implement `EvictionPolicy<K, V>` for LFU, TTL, Random eviction

2. **Metrics Collection**
   - Add `CacheMetrics` decorator tracking hit/miss rates

3. **Persistence**
   - Use `EvictionListener` to persist evicted entries

4. **TTL Support**
   - Create `TTLEvictionPolicy` with time-based expiration

5. **Distributed Cache**
   - Extend with replication/sharding logic

---

## Running the Code

```bash
# Compile
cd /path/to/local-cp-chores
javac -d out lrucache/**/*.java

# Run Demo
java -cp out lrucache.Main

# Run Tests
java -cp out lrucache.test.LRUCacheTest
```

---

## Test Coverage

| Category | Tests |
|----------|-------|
| Basic Operations | get, put, remove, clear, containsKey |
| LRU Eviction | eviction at capacity, access order updates |
| Edge Cases | null key, null value, single capacity |
| Thread Safety | concurrent reads, writes, read/write mix |
| Extensibility | FIFO policy, eviction listener |



