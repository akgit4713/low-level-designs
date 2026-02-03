# Java Concurrency Deep-Dive: Complete Interview Guide

> **Purpose**: Comprehensive reference for debugging interviews covering all Java concurrency primitives, patterns, and common bugs.

---

## Table of Contents

1. [Memory Model Fundamentals](#1-memory-model-fundamentals)
2. [Synchronization Primitives](#2-synchronization-primitives)
3. [Lock Types Deep-Dive](#3-lock-types-deep-dive)
4. [Semaphores](#4-semaphores)
5. [Latches and Barriers](#5-latches-and-barriers)
6. [Thread Pools and Executors](#6-thread-pools-and-executors)
7. [Future and CompletableFuture](#7-future-and-completablefuture)
8. [Race Conditions](#8-race-conditions)
9. [Deadlocks](#9-deadlocks)
10. [Common Bug Patterns](#10-common-bug-patterns)
11. [Quick Reference Cheat Sheet](#11-quick-reference-cheat-sheet)

---

## 1. Memory Model Fundamentals

### 1.1 The Problem: Why Do We Need a Memory Model?

```
CPU 1                    CPU 2
┌─────────┐             ┌─────────┐
│ Cache L1│             │ Cache L1│
└────┬────┘             └────┬────┘
     │                       │
┌────┴───────────────────────┴────┐
│           Main Memory           │
│   x = 0    y = 0    flag = false│
└─────────────────────────────────┘
```

**Problem**: Each CPU has its own cache. Without synchronization:
- Thread A writes `x = 1` → stored in CPU1's cache only
- Thread B reads `x` → gets `0` from CPU2's cache (stale!)

### 1.2 volatile Keyword

**What volatile guarantees:**
1. **Visibility**: Writes are immediately flushed to main memory; reads always fetch from main memory
2. **Ordering**: Prevents reordering of instructions around volatile access (memory barrier)

**What volatile does NOT guarantee:**
- **Atomicity**: `count++` is still NOT atomic even with volatile!

```java
// CORRECT use of volatile
private volatile boolean running = true;

public void stop() {
    running = false;  // Immediately visible to other threads
}

public void run() {
    while (running) {  // Always reads fresh value
        doWork();
    }
}
```

```java
// INCORRECT use of volatile - NOT ATOMIC!
private volatile int count = 0;

public void increment() {
    count++;  // BUG! This is: read → increment → write (3 operations)
}
// Two threads can read count=5, both increment to 6, both write 6
// Expected: 7, Actual: 6 (lost update)
```

**When to use volatile:**
| Scenario | Use volatile? |
|----------|---------------|
| Simple flag (true/false) | ✅ Yes |
| Read by many, written by one | ✅ Yes |
| Counter increment/decrement | ❌ No, use AtomicInteger |
| Complex state (multiple fields) | ❌ No, use locks |
| Double-checked locking singleton | ✅ Yes (required!) |

### 1.3 final Variables

**Thread-safety guarantee for final fields:**
Once a constructor completes, final fields are **safely published** - all threads will see the correct value without synchronization.

```java
public class ImmutablePoint {
    private final int x;  // Safe publication guaranteed
    private final int y;
    
    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    // After constructor, any thread reading x,y sees correct values
}
```

**Critical rule**: Don't let `this` escape during construction!

```java
// DANGEROUS - "this" escapes before construction complete
public class Broken {
    private final int value;
    
    public Broken() {
        EventBus.register(this);  // BUG! "this" escapes, value may not be set yet
        value = 42;
    }
}
```

### 1.4 Happens-Before Relationships

If action A **happens-before** action B, then A's effects are visible to B.

**Key happens-before rules:**

| Rule | Description |
|------|-------------|
| **Program order** | Within a thread, earlier statements happen-before later statements |
| **Monitor lock** | unlock() happens-before subsequent lock() on same monitor |
| **volatile** | Write to volatile happens-before subsequent read of same volatile |
| **Thread start** | Thread.start() happens-before any action in started thread |
| **Thread join** | All actions in thread happen-before join() returns |
| **Transitivity** | If A happens-before B, and B happens-before C, then A happens-before C |

```java
// Example: Using volatile for happens-before
class Example {
    private int data;
    private volatile boolean ready = false;
    
    // Thread 1
    void writer() {
        data = 42;         // (1)
        ready = true;      // (2) volatile write
    }
    
    // Thread 2
    void reader() {
        if (ready) {       // (3) volatile read
            print(data);   // (4) Guaranteed to see 42!
        }
    }
}
// Write to volatile (2) happens-before read (3)
// By transitivity: (1) happens-before (4)
```

---

## 2. Synchronization Primitives

### 2.1 Comparison Table

| Primitive | Use Case | Blocking | Reentrant | Fairness | Interruptible |
|-----------|----------|----------|-----------|----------|---------------|
| `synchronized` | Simple mutex | Yes | Yes | No | No |
| `ReentrantLock` | Advanced locking | Yes | Yes | Optional | Yes |
| `ReadWriteLock` | Read-heavy workloads | Yes | Yes | Optional | Yes |
| `StampedLock` | Optimistic reads | Partial | No | No | Partial |
| `Semaphore` | Resource pools | Yes | N/A | Optional | Yes |
| `CountDownLatch` | Wait for N events | Yes | N/A | N/A | Yes |
| `CyclicBarrier` | Sync N threads | Yes | N/A | N/A | Yes |

### 2.2 synchronized Keyword

**Three forms:**

```java
// 1. Instance method - locks on 'this'
public synchronized void method() {
    // Only one thread can execute on this instance
}

// 2. Static method - locks on Class object
public static synchronized void staticMethod() {
    // Only one thread can execute across all instances
}

// 3. Block - locks on specified object
public void method() {
    synchronized (lockObject) {
        // Critical section
    }
}
```

**Internal mechanics:**

```
┌─────────────────────────────────────┐
│         Object Header               │
├─────────────────────────────────────┤
│  Mark Word (lock state, hash, etc.) │
├─────────────────────────────────────┤
│  Class Pointer                      │
└─────────────────────────────────────┘

Lock states (in Mark Word):
- Unlocked (biased to thread) → Biased Locking
- Lightweight locked → CAS on stack
- Heavyweight locked → OS mutex
```

### 2.3 wait(), notify(), notifyAll()

**Must be called inside synchronized block!**

```java
synchronized (lock) {
    while (!condition) {    // ALWAYS use while, not if!
        lock.wait();        // Releases lock, waits
    }
    // Condition is true, proceed
}

synchronized (lock) {
    condition = true;
    lock.notify();          // Wake one waiting thread
    // or lock.notifyAll(); // Wake all waiting threads
}
```

**Why while instead of if?**

```java
// BUG: Using if instead of while
synchronized (lock) {
    if (queue.isEmpty()) {   // WRONG!
        lock.wait();
    }
    // Spurious wakeup can bring us here with empty queue!
    return queue.remove();   // NullPointerException or IndexOutOfBoundsException
}

// CORRECT: Using while
synchronized (lock) {
    while (queue.isEmpty()) {  // Check again after wakeup
        lock.wait();
    }
    return queue.remove();     // Guaranteed queue is not empty
}
```

---

## 3. Lock Types Deep-Dive

### 3.1 Decision Flowchart: Which Lock to Use?

```
Need synchronization?
        │
        ▼
   Simple use case?
   (no timeout, no tryLock)
        │
    ┌───┴───┐
   Yes      No
    │        │
    ▼        ▼
synchronized  Need read/write distinction?
                    │
               ┌────┴────┐
              Yes       No
               │         │
               ▼         ▼
    ReadWriteLock    ReentrantLock
         │
         ▼
    Read-heavy with
    occasional writes?
         │
    ┌────┴────┐
   Yes       No
    │         │
    ▼         ▼
StampedLock  ReadWriteLock
(optimistic)
```

### 3.2 ReentrantLock

**Advantages over synchronized:**
1. **Timed lock acquisition**: `tryLock(timeout, unit)`
2. **Interruptible**: Can cancel waiting for lock
3. **Fairness**: Optional FIFO ordering
4. **Multiple conditions**: Can have separate wait sets
5. **Non-block-structured**: Lock in one method, unlock in another

```java
private final ReentrantLock lock = new ReentrantLock();
private final Condition notFull = lock.newCondition();
private final Condition notEmpty = lock.newCondition();

// Producer
public void put(E item) throws InterruptedException {
    lock.lock();
    try {
        while (count == capacity) {
            notFull.await();  // Wait on specific condition
        }
        items[putIndex] = item;
        count++;
        notEmpty.signal();    // Signal specific condition
    } finally {
        lock.unlock();        // ALWAYS in finally!
    }
}

// Consumer
public E take() throws InterruptedException {
    lock.lock();
    try {
        while (count == 0) {
            notEmpty.await();
        }
        E item = items[takeIndex];
        count--;
        notFull.signal();
        return item;
    } finally {
        lock.unlock();
    }
}
```

### 3.3 ReadWriteLock

**When to use**: Many reads, few writes (e.g., cache, configuration)

```java
private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
private final Lock readLock = rwLock.readLock();
private final Lock writeLock = rwLock.writeLock();
private Map<String, Object> cache = new HashMap<>();

public Object get(String key) {
    readLock.lock();         // Multiple readers can hold this
    try {
        return cache.get(key);
    } finally {
        readLock.unlock();
    }
}

public void put(String key, Object value) {
    writeLock.lock();        // Exclusive - no readers or writers
    try {
        cache.put(key, value);
    } finally {
        writeLock.unlock();
    }
}
```

**Lock compatibility matrix:**

|              | Read Lock | Write Lock |
|--------------|-----------|------------|
| **Read Lock**  | ✅ Compatible | ❌ Blocked |
| **Write Lock** | ❌ Blocked | ❌ Blocked |

### 3.4 StampedLock (Java 8+)

**Optimistic read** - doesn't actually acquire lock, just checks if write occurred.

```java
private final StampedLock sl = new StampedLock();
private double x, y;

// Optimistic read - very fast, no blocking
public double distanceFromOrigin() {
    long stamp = sl.tryOptimisticRead();  // Non-blocking!
    double currentX = x, currentY = y;
    
    if (!sl.validate(stamp)) {  // Check if write occurred
        // Fallback to pessimistic read
        stamp = sl.readLock();
        try {
            currentX = x;
            currentY = y;
        } finally {
            sl.unlockRead(stamp);
        }
    }
    return Math.sqrt(currentX * currentX + currentY * currentY);
}

// Write lock (exclusive)
public void move(double deltaX, double deltaY) {
    long stamp = sl.writeLock();
    try {
        x += deltaX;
        y += deltaY;
    } finally {
        sl.unlockWrite(stamp);
    }
}
```

**Caution**: StampedLock is NOT reentrant! Don't call stampedLock.writeLock() twice from same thread.

### 3.5 Lock Ordering (Deadlock Prevention)

**Rule**: Always acquire locks in the same global order.

```java
// BAD: Inconsistent lock ordering → DEADLOCK
void transfer1(Account from, Account to, int amount) {
    synchronized (from) {          // Thread 1: locks A first
        synchronized (to) {        // Thread 1: waits for B
            from.debit(amount);
            to.credit(amount);
        }
    }
}
// Thread 2 calls transfer1(B, A, 50) → locks B, waits for A → DEADLOCK!

// GOOD: Consistent ordering using object identity
void transfer2(Account from, Account to, int amount) {
    Account first = (System.identityHashCode(from) < System.identityHashCode(to)) 
                    ? from : to;
    Account second = (first == from) ? to : from;
    
    synchronized (first) {
        synchronized (second) {
            from.debit(amount);
            to.credit(amount);
        }
    }
}
```

---

## 4. Semaphores

### 4.1 Concept

```
Semaphore(permits=3)

┌────────────────────────────────────────┐
│  Available Permits: ████░░░            │
│                     (3)                │
│                                        │
│  acquire() → takes 1 permit            │
│  release() → returns 1 permit          │
│                                        │
│  If 0 permits: acquire() BLOCKS        │
└────────────────────────────────────────┘
```

### 4.2 Types of Semaphores

| Type | Permits | Use Case |
|------|---------|----------|
| **Binary** (permits=1) | 0 or 1 | Mutex (like lock) |
| **Counting** (permits=N) | 0 to N | Resource pool |

### 4.3 Use Cases

**1. Connection Pool Limiting**

```java
public class ConnectionPool {
    private final Semaphore semaphore;
    private final Queue<Connection> pool;
    
    public ConnectionPool(int maxConnections) {
        this.semaphore = new Semaphore(maxConnections, true); // fair
        this.pool = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < maxConnections; i++) {
            pool.add(createConnection());
        }
    }
    
    public Connection acquire() throws InterruptedException {
        semaphore.acquire();  // Blocks if no permits
        return pool.poll();
    }
    
    public void release(Connection conn) {
        pool.offer(conn);
        semaphore.release();  // Return permit
    }
}
```

**2. Rate Limiting (max N concurrent requests)**

```java
public class RateLimitedService {
    private final Semaphore limiter = new Semaphore(10); // Max 10 concurrent
    
    public Response callApi(Request req) throws InterruptedException {
        limiter.acquire();
        try {
            return doActualApiCall(req);
        } finally {
            limiter.release();
        }
    }
}
```

**3. Producer-Consumer (Classic 3-Semaphore Solution)**

```java
class BoundedBuffer {
    private final Semaphore emptySlots;  // Tracks empty slots
    private final Semaphore fullSlots;   // Tracks filled slots  
    private final Semaphore mutex;       // Protects buffer access
    
    public BoundedBuffer(int capacity) {
        emptySlots = new Semaphore(capacity); // All slots empty initially
        fullSlots = new Semaphore(0);         // No filled slots initially
        mutex = new Semaphore(1);             // Binary semaphore for mutex
    }
    
    public void put(Object item) throws InterruptedException {
        emptySlots.acquire();  // Wait for empty slot
        mutex.acquire();       // Enter critical section
        try {
            buffer.add(item);
        } finally {
            mutex.release();   // Exit critical section
        }
        fullSlots.release();   // Signal: one more full slot
    }
    
    public Object take() throws InterruptedException {
        fullSlots.acquire();   // Wait for filled slot
        mutex.acquire();       // Enter critical section
        Object item;
        try {
            item = buffer.remove();
        } finally {
            mutex.release();   // Exit critical section
        }
        emptySlots.release();  // Signal: one more empty slot
        return item;
    }
}
```

### 4.4 Semaphore vs Lock

| Aspect | Lock | Semaphore |
|--------|------|-----------|
| **Ownership** | Thread that locked must unlock | Any thread can release |
| **Reentrant** | Yes (ReentrantLock) | No concept of reentrancy |
| **Permits** | 1 (binary) | N (counting) |
| **Use case** | Mutual exclusion | Resource limiting |

---

## 5. Latches and Barriers

### 5.1 CountDownLatch

**One-time barrier**: Threads wait until count reaches zero.

```
Initial State:         After countDown():      After all countDown():
┌─────────────┐        ┌─────────────┐         ┌─────────────┐
│  Count: 3   │   →    │  Count: 2   │    →    │  Count: 0   │
├─────────────┤        ├─────────────┤         ├─────────────┤
│ Waiting: T1 │        │ Waiting: T1 │         │ Released!   │
│ Waiting: T2 │        │ Waiting: T2 │         │ All proceed │
└─────────────┘        └─────────────┘         └─────────────┘
```

**Use cases:**

```java
// 1. Wait for services to start
CountDownLatch servicesReady = new CountDownLatch(3);

// Each service calls when ready
servicesReady.countDown();  // Database ready
servicesReady.countDown();  // Cache ready  
servicesReady.countDown();  // Queue ready

// Main thread waits
servicesReady.await();
System.out.println("All services ready!");

// 2. Start all threads simultaneously
CountDownLatch startSignal = new CountDownLatch(1);
CountDownLatch doneSignal = new CountDownLatch(numWorkers);

for (int i = 0; i < numWorkers; i++) {
    new Thread(() -> {
        try {
            startSignal.await();  // Wait for start signal
            doWork();
        } finally {
            doneSignal.countDown();
        }
    }).start();
}

startSignal.countDown();  // Let all workers start simultaneously
doneSignal.await();       // Wait for all to complete
```

### 5.2 CyclicBarrier

**Reusable barrier**: N threads wait for each other, then all proceed.

```
Barrier(parties=3)

T1 arrives → waits     T2 arrives → waits     T3 arrives → BARRIER TRIPS!
    │                       │                       │
    ▼                       ▼                       ▼
┌───────┐              ┌───────┐              ┌───────┐
│ T1 ░░ │              │ T1 T2 │              │ T1 T2 │ ← All released!
│       │              │    ░░ │              │ T3    │
└───────┘              └───────┘              └───────┘
```

**Key difference from CountDownLatch:**
- **CountDownLatch**: One-time, any thread can count down
- **CyclicBarrier**: Reusable, waiting threads must reach barrier

```java
// Parallel computation with barrier sync
CyclicBarrier barrier = new CyclicBarrier(numThreads, () -> {
    // Barrier action: runs when all threads arrive
    mergeResults();
});

for (int i = 0; i < numThreads; i++) {
    new Thread(() -> {
        while (!done) {
            computePartialResult();
            barrier.await();  // Wait for all threads
            // All threads proceed together
        }
    }).start();
}
```

### 5.3 Phaser (Advanced)

**Dynamic barrier**: Parties can register/deregister dynamically.

```java
Phaser phaser = new Phaser(1); // Register self

for (int i = 0; i < 3; i++) {
    phaser.register();  // Dynamic registration
    new Thread(() -> {
        phaser.arriveAndAwaitAdvance();  // Phase 0
        doWork();
        phaser.arriveAndAwaitAdvance();  // Phase 1
        phaser.arriveAndDeregister();    // Done, deregister
    }).start();
}

phaser.arriveAndDeregister();  // Deregister self
```

---

## 6. Thread Pools and Executors

### 6.1 Executor Framework Overview

```
┌─────────────────────────────────────────────────────────────┐
│                      Executor Framework                      │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────┐    ┌──────────────────┐    ┌─────────────┐ │
│  │  Executor   │ ←──│ ExecutorService  │ ←──│Scheduled    │ │
│  │ (interface) │    │   (interface)    │    │ExecutorSvc  │ │
│  └─────────────┘    └──────────────────┘    └─────────────┘ │
│         │                    │                     │        │
│         ▼                    ▼                     ▼        │
│  ┌──────────────────────────────────────────────────────┐  │
│  │              ThreadPoolExecutor                       │  │
│  │  (corePoolSize, maxPoolSize, queue, handler)         │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### 6.2 Thread Pool Types

| Pool Type | Method | Behavior | Use Case |
|-----------|--------|----------|----------|
| **Fixed** | `newFixedThreadPool(n)` | N threads, unbounded queue | Known workload |
| **Cached** | `newCachedThreadPool()` | 0→∞ threads, 60s idle timeout | Many short tasks |
| **Single** | `newSingleThreadExecutor()` | 1 thread, unbounded queue | Sequential tasks |
| **Scheduled** | `newScheduledThreadPool(n)` | N threads, delayed/periodic | Timers, cron |
| **WorkStealing** | `newWorkStealingPool()` | ForkJoinPool, work stealing | Parallel streams |

### 6.3 ThreadPoolExecutor Parameters

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    corePoolSize,    // Minimum threads always alive
    maxPoolSize,     // Maximum threads allowed
    keepAliveTime,   // Idle time before non-core threads die
    TimeUnit.SECONDS,
    workQueue,       // Queue for pending tasks
    threadFactory,   // How to create threads (naming, priority)
    rejectionHandler // What to do when queue is full
);
```

**Task submission flow:**

```
Task submitted
      │
      ▼
┌─────────────────┐
│ threads < core? │──Yes──→ Create new thread, run task
└────────┬────────┘
         │ No
         ▼
┌─────────────────┐
│ Queue has space?│──Yes──→ Add task to queue
└────────┬────────┘
         │ No
         ▼
┌─────────────────┐
│ threads < max?  │──Yes──→ Create new thread, run task
└────────┬────────┘
         │ No
         ▼
┌─────────────────┐
│ Reject Handler  │ → AbortPolicy/CallerRunsPolicy/etc
└─────────────────┘
```

### 6.4 Rejection Policies

```java
// 1. AbortPolicy (default) - throws RejectedExecutionException
new ThreadPoolExecutor.AbortPolicy()

// 2. CallerRunsPolicy - caller thread runs the task
new ThreadPoolExecutor.CallerRunsPolicy()

// 3. DiscardPolicy - silently drops the task
new ThreadPoolExecutor.DiscardPolicy()

// 4. DiscardOldestPolicy - drops oldest queued task
new ThreadPoolExecutor.DiscardOldestPolicy()
```

### 6.5 Pool Sizing Formula

```java
// CPU-bound tasks (computation)
int poolSize = Runtime.getRuntime().availableProcessors();

// IO-bound tasks (network, disk)
// More threads to utilize CPU while others wait for IO
int poolSize = Runtime.getRuntime().availableProcessors() 
               * (1 + waitTime/computeTime);

// Example: 4 CPUs, tasks spend 80% waiting for IO
// poolSize = 4 * (1 + 0.8/0.2) = 4 * 5 = 20 threads
```

### 6.6 Proper Shutdown

```java
ExecutorService executor = Executors.newFixedThreadPool(10);

// Submit tasks...

// Graceful shutdown
executor.shutdown();  // Stop accepting new tasks

try {
    // Wait for existing tasks to complete
    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        // Force shutdown if not done
        executor.shutdownNow();
        
        // Wait again
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("Pool did not terminate");
        }
    }
} catch (InterruptedException e) {
    executor.shutdownNow();
    Thread.currentThread().interrupt();
}
```

### 6.7 ForkJoinPool

**Work-stealing algorithm** for recursive parallelism.

```java
// Divide-and-conquer example
class SumTask extends RecursiveTask<Long> {
    private final long[] array;
    private final int start, end;
    private static final int THRESHOLD = 10000;
    
    @Override
    protected Long compute() {
        int length = end - start;
        
        if (length <= THRESHOLD) {
            // Base case: compute directly
            return computeSequentially();
        }
        
        // Recursive case: split
        int mid = start + length / 2;
        SumTask left = new SumTask(array, start, mid);
        SumTask right = new SumTask(array, mid, end);
        
        left.fork();  // Execute asynchronously
        Long rightResult = right.compute();  // Execute in current thread
        Long leftResult = left.join();  // Wait for left
        
        return leftResult + rightResult;
    }
}

ForkJoinPool pool = new ForkJoinPool();
Long result = pool.invoke(new SumTask(array, 0, array.length));
```

---

## 7. Future and CompletableFuture

### 7.1 Future<T> - Basic Async Result

```java
ExecutorService executor = Executors.newFixedThreadPool(10);

// Submit task, get Future
Future<String> future = executor.submit(() -> {
    Thread.sleep(1000);
    return "Hello";
});

// Later... get result (blocks if not ready)
String result = future.get();  // Blocks until complete

// With timeout
String result = future.get(5, TimeUnit.SECONDS);

// Check status
boolean done = future.isDone();
boolean cancelled = future.isCancelled();
```

**Limitations of Future:**
- `get()` blocks - can't compose async operations
- No callbacks - can't "do X when done"
- Can't combine multiple futures easily

### 7.2 CompletableFuture - Composable Async

```
CompletableFuture Pipeline:

supplyAsync() → thenApply() → thenApply() → thenAccept()
     │              │             │              │
     ▼              ▼             ▼              ▼
 [Supplier]    [Function]    [Function]    [Consumer]
  returns T      T → U          U → V        V → void
```

### 7.3 Creating CompletableFutures

```java
// 1. Run async task without result
CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
    System.out.println("Running in: " + Thread.currentThread().getName());
});

// 2. Run async task with result
CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
    return fetchFromDatabase();
});

// 3. With custom executor
ExecutorService myPool = Executors.newFixedThreadPool(10);
CompletableFuture<String> cf = CompletableFuture.supplyAsync(
    () -> fetchFromDatabase(), 
    myPool
);

// 4. Already completed
CompletableFuture<String> cf = CompletableFuture.completedFuture("value");

// 5. Complete manually
CompletableFuture<String> cf = new CompletableFuture<>();
cf.complete("done");  // Or cf.completeExceptionally(ex);
```

### 7.4 Chaining Operations (thenApply, thenCompose, thenCombine)

```java
// thenApply - transform result (map)
CompletableFuture<Integer> cf = CompletableFuture
    .supplyAsync(() -> "Hello")
    .thenApply(s -> s.length());  // String → Integer

// thenCompose - chain another async operation (flatMap)
CompletableFuture<String> cf = CompletableFuture
    .supplyAsync(() -> getUserId())
    .thenCompose(id -> fetchUserAsync(id));  // Returns CompletableFuture

// thenCombine - combine two independent futures
CompletableFuture<String> cf = CompletableFuture
    .supplyAsync(() -> getFirstName())
    .thenCombine(
        CompletableFuture.supplyAsync(() -> getLastName()),
        (first, last) -> first + " " + last
    );

// thenAccept - consume result (no return)
CompletableFuture.supplyAsync(() -> "Hello")
    .thenAccept(s -> System.out.println(s));

// thenRun - run action after completion (no access to result)
CompletableFuture.supplyAsync(() -> "Hello")
    .thenRun(() -> System.out.println("Done!"));
```

### 7.5 Error Handling

```java
// exceptionally - recover from exception
CompletableFuture<String> cf = CompletableFuture
    .supplyAsync(() -> {
        if (random.nextBoolean()) throw new RuntimeException("Oops!");
        return "Success";
    })
    .exceptionally(ex -> {
        System.err.println("Error: " + ex.getMessage());
        return "Default Value";  // Recovery value
    });

// handle - access both result and exception
CompletableFuture<String> cf = CompletableFuture
    .supplyAsync(() -> riskyOperation())
    .handle((result, ex) -> {
        if (ex != null) {
            return "Error: " + ex.getMessage();
        }
        return "Success: " + result;
    });

// whenComplete - side effects, doesn't transform result
CompletableFuture<String> cf = CompletableFuture
    .supplyAsync(() -> riskyOperation())
    .whenComplete((result, ex) -> {
        if (ex != null) {
            logger.error("Failed", ex);
        } else {
            logger.info("Completed: " + result);
        }
    });  // Still propagates original result/exception
```

### 7.6 Combining Multiple Futures

```java
// allOf - wait for all to complete
CompletableFuture<Void> all = CompletableFuture.allOf(cf1, cf2, cf3);
all.thenRun(() -> {
    // All completed, get results
    String r1 = cf1.join();
    String r2 = cf2.join();
    String r3 = cf3.join();
});

// anyOf - first to complete wins
CompletableFuture<Object> any = CompletableFuture.anyOf(cf1, cf2, cf3);
any.thenAccept(result -> {
    System.out.println("First to complete: " + result);
});

// Collect results from multiple futures
List<CompletableFuture<String>> futures = urls.stream()
    .map(url -> CompletableFuture.supplyAsync(() -> fetch(url)))
    .collect(Collectors.toList());

CompletableFuture<List<String>> allResults = CompletableFuture
    .allOf(futures.toArray(new CompletableFuture[0]))
    .thenApply(v -> futures.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toList()));
```

### 7.7 Async Variants

Every chaining method has 3 variants:

```java
// thenApply - runs in completing thread or caller thread
.thenApply(fn)

// thenApplyAsync - runs in ForkJoinPool.commonPool()
.thenApplyAsync(fn)

// thenApplyAsync - runs in specified executor
.thenApplyAsync(fn, executor)
```

### 7.8 CompletionStage Interface

`CompletableFuture` implements `CompletionStage<T>`:

```java
public interface CompletionStage<T> {
    <U> CompletionStage<U> thenApply(Function<? super T, ? extends U> fn);
    <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn);
    <U,V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other, 
                                          BiFunction<? super T,? super U,? extends V> fn);
    // ... more
}
```

Use `CompletionStage` in APIs for flexibility (hide implementation details).

---

## 8. Race Conditions

### 8.1 Definition

**Race condition**: Outcome depends on the relative timing of events (thread scheduling).

### 8.2 Check-Then-Act Race

```java
// BUG: Check-then-act without synchronization
if (!map.containsKey(key)) {     // (1) Check
    map.put(key, computeValue()); // (2) Act
}
// Another thread can put() between (1) and (2)!

// FIX: Use atomic operation
map.computeIfAbsent(key, k -> computeValue());

// Or with synchronization
synchronized (map) {
    if (!map.containsKey(key)) {
        map.put(key, computeValue());
    }
}
```

### 8.3 Read-Modify-Write Race

```java
// BUG: count++ is not atomic
private int count = 0;
public void increment() {
    count++;  // Read → Modify → Write (3 operations)
}
// Two threads: read 5, read 5, write 6, write 6 → Lost update!

// FIX 1: AtomicInteger
private AtomicInteger count = new AtomicInteger(0);
public void increment() {
    count.incrementAndGet();  // Atomic operation
}

// FIX 2: synchronized
private int count = 0;
public synchronized void increment() {
    count++;
}
```

### 8.4 Lazy Initialization Race

```java
// BUG: Lazy init without synchronization
private Helper helper;
public Helper getHelper() {
    if (helper == null) {           // Thread A checks
        helper = new Helper();       // Thread A creates
    }                                // Thread B might also create!
    return helper;
}

// FIX 1: synchronized method
public synchronized Helper getHelper() { ... }

// FIX 2: Double-checked locking with volatile
private volatile Helper helper;
public Helper getHelper() {
    Helper h = helper;  // Local read for performance
    if (h == null) {
        synchronized (this) {
            h = helper;
            if (h == null) {
                helper = h = new Helper();
            }
        }
    }
    return h;
}

// FIX 3: Holder pattern (lazy, thread-safe)
private static class HelperHolder {
    static final Helper INSTANCE = new Helper();
}
public Helper getHelper() {
    return HelperHolder.INSTANCE;
}
```

### 8.5 Publishing Race

```java
// BUG: Unsafe publication
public class Holder {
    private int n;
    public Holder(int n) { this.n = n; }
    public void assertSanity() {
        if (n != n) throw new AssertionError("WTF?!");  // Can actually fail!
    }
}

// Another thread might see:
// - holder reference (not null)
// - but n = 0 (default, not yet written by constructor)

// FIX: Use final
public class SafeHolder {
    private final int n;  // Safe publication via final
    public SafeHolder(int n) { this.n = n; }
}
```

---

## 9. Deadlocks

### 9.1 Four Conditions for Deadlock (All Must Be True)

1. **Mutual Exclusion**: Resources can't be shared
2. **Hold and Wait**: Thread holds resource while waiting for another
3. **No Preemption**: Resources can't be forcibly taken
4. **Circular Wait**: Thread A waits for B, B waits for C, C waits for A

### 9.2 Classic Example

```java
// DEADLOCK: Inconsistent lock ordering
class Account {
    void transfer(Account from, Account to, int amount) {
        synchronized (from) {
            synchronized (to) {
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }
}

// Thread 1: transfer(A, B, 100) → locks A, waits for B
// Thread 2: transfer(B, A, 50)  → locks B, waits for A
// DEADLOCK!
```

### 9.3 Prevention Strategies

**1. Lock Ordering**
```java
void transfer(Account from, Account to, int amount) {
    Account first = System.identityHashCode(from) < System.identityHashCode(to) 
                    ? from : to;
    Account second = (first == from) ? to : from;
    
    synchronized (first) {
        synchronized (second) {
            // Safe - consistent ordering
        }
    }
}
```

**2. Lock Timeout**
```java
boolean success = lock1.tryLock(1, TimeUnit.SECONDS);
if (success) {
    try {
        boolean success2 = lock2.tryLock(1, TimeUnit.SECONDS);
        if (success2) {
            try {
                // Do work
            } finally {
                lock2.unlock();
            }
        }
    } finally {
        lock1.unlock();
    }
}
```

**3. Single Lock**
```java
// Use one lock for all accounts (coarse-grained)
synchronized (Bank.class) {
    from.balance -= amount;
    to.balance += amount;
}
```

### 9.4 Detection: Thread Dump Analysis

```
Found one Java-level deadlock:
=============================
"Thread-1":
  waiting to lock monitor 0x00007f9a1c003f88 (object 0x00000007c0a0c000),
  which is held by "Thread-0"
"Thread-0":
  waiting to lock monitor 0x00007f9a1c006088 (object 0x00000007c0a0c010),
  which is held by "Thread-1"
```

### 9.5 Livelock vs Starvation

**Livelock**: Threads actively try to resolve conflict but make no progress.
```java
// Two polite people in a hallway, each stepping aside for the other
while (true) {
    if (other.isMoving()) {
        stepAside();  // Both step aside at same time, repeat forever
    }
}
```

**Starvation**: Thread never gets resources because higher-priority threads keep taking them.
```java
// Low-priority thread never runs because high-priority threads always ready
```

---

## 10. Common Bug Patterns

### 10.1 Missing volatile in Double-Checked Locking

```java
// BUG: Missing volatile
private static Singleton instance;  // WRONG!

public static Singleton getInstance() {
    if (instance == null) {
        synchronized (Singleton.class) {
            if (instance == null) {
                instance = new Singleton();  // Can be seen partially constructed!
            }
        }
    }
    return instance;
}

// FIX: Add volatile
private static volatile Singleton instance;
```

**Why volatile is needed:**
```
instance = new Singleton();

JVM may reorder to:
1. Allocate memory
2. Assign reference to instance  ← instance != null now!
3. Run constructor               ← But not fully constructed!

Thread B sees instance != null at step 2, returns partially constructed object!
volatile prevents this reordering.
```

### 10.2 Spurious Wakeup Not Handled

```java
// BUG: Using if instead of while
synchronized (lock) {
    if (queue.isEmpty()) {  // WRONG!
        lock.wait();
    }
    // Spurious wakeup: queue might still be empty!
    return queue.remove();  // CRASH!
}

// FIX: Always use while
synchronized (lock) {
    while (queue.isEmpty()) {  // Re-check after wakeup
        lock.wait();
    }
    return queue.remove();
}
```

### 10.3 Missing finally for Lock Unlock

```java
// BUG: No finally block
lock.lock();
doWork();  // If this throws, lock never released!
lock.unlock();

// FIX: Always use finally
lock.lock();
try {
    doWork();
} finally {
    lock.unlock();
}
```

### 10.4 Wrong Condition Signal

```java
// BUG: Signaling wrong condition
public void put(E item) {
    lock.lock();
    try {
        while (count == capacity) {
            notFull.await();
        }
        items[putIndex] = item;
        count++;
        notFull.signal();  // WRONG! Should signal notEmpty
    } finally {
        lock.unlock();
    }
}

// FIX: Signal the right condition
notEmpty.signal();  // Signal consumers that data is available
```

### 10.5 Non-Atomic Check-Then-Act

```java
// BUG: Check-then-act race
if (!file.exists()) {
    file.createNewFile();  // Another thread might create between check and create!
}

// FIX: Use atomic operation
file.createNewFile();  // Returns false if already exists

// Or with locks
synchronized (lockObject) {
    if (!file.exists()) {
        file.createNewFile();
    }
}
```

### 10.6 Lost Wakeup

```java
// BUG: notify() before wait()
// Thread A
synchronized (lock) {
    ready = true;
    lock.notify();  // Notify sent, but no one waiting yet
}

// Thread B (starts later)
synchronized (lock) {
    while (!ready) {  // ready is already true, but...
        lock.wait();   // If timing is off, might wait forever for missed notify
    }
}

// FIX: Always check condition in while loop before waiting
// The while loop protects against this
```

### 10.7 Using notify() Instead of notifyAll()

```java
// BUG: notify() wakes only one thread
synchronized (lock) {
    queue.add(item);
    lock.notify();  // Only wakes ONE waiter
}
// If multiple consumers waiting, only one wakes up

// FIX: Use notifyAll() when multiple threads might need to wake
lock.notifyAll();

// Or use Condition.signalAll() with ReentrantLock
```

### 10.8 Holding Lock During Long Operations

```java
// BUG: Network call while holding lock
synchronized (lock) {
    String data = httpClient.fetch(url);  // 5 second timeout!
    cache.put(key, data);
}
// Other threads blocked for 5 seconds!

// FIX: Minimize critical section
String data = httpClient.fetch(url);  // Do slow work outside lock
synchronized (lock) {
    cache.put(key, data);  // Only lock for quick operation
}
```

---

## 11. Quick Reference Cheat Sheet

### Lock Patterns

```java
// synchronized
synchronized (object) { ... }

// ReentrantLock
lock.lock();
try {
    ...
} finally {
    lock.unlock();
}

// tryLock with timeout
if (lock.tryLock(1, TimeUnit.SECONDS)) {
    try {
        ...
    } finally {
        lock.unlock();
    }
} else {
    // Couldn't acquire lock
}
```

### Wait/Notify Pattern

```java
// Wait
synchronized (lock) {
    while (!condition) {
        lock.wait();
    }
}

// Notify
synchronized (lock) {
    condition = true;
    lock.notify();  // or notifyAll()
}
```

### Condition Pattern

```java
Lock lock = new ReentrantLock();
Condition cond = lock.newCondition();

// Wait
lock.lock();
try {
    while (!condition) {
        cond.await();
    }
} finally {
    lock.unlock();
}

// Signal
lock.lock();
try {
    condition = true;
    cond.signal();  // or signalAll()
} finally {
    lock.unlock();
}
```

### Semaphore Pattern

```java
Semaphore sem = new Semaphore(permits);

sem.acquire();  // or acquire(n)
try {
    ...
} finally {
    sem.release();  // or release(n)
}
```

### CountDownLatch Pattern

```java
CountDownLatch latch = new CountDownLatch(n);

// Worker threads
latch.countDown();

// Main thread
latch.await();  // or await(timeout, unit)
```

### CompletableFuture Patterns

```java
// Chain transformations
CompletableFuture.supplyAsync(() -> getValue())
    .thenApply(v -> transform(v))
    .thenAccept(v -> consume(v))
    .exceptionally(ex -> handleError(ex));

// Combine futures
cf1.thenCombine(cf2, (r1, r2) -> combine(r1, r2));

// Wait for all
CompletableFuture.allOf(cf1, cf2, cf3).join();
```

### Thread Pool Shutdown

```java
executor.shutdown();
if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
    executor.shutdownNow();
}
```

### Debugging Checklist

| Check | Issue |
|-------|-------|
| `volatile` missing on shared flag | Visibility problem |
| `if` instead of `while` with `wait()` | Spurious wakeup |
| No `finally` for `unlock()` | Lock leak |
| Wrong condition signaled | Lost wakeup |
| Lock ordering inconsistent | Deadlock |
| `count++` on shared variable | Race condition |
| Slow operation in critical section | Performance |

---

## Cross-Reference to Code Examples

| Topic | File |
|-------|------|
| Blocking Queue (Lock/Condition) | `BoundedBlockingQueue.java` |
| Producer-Consumer (Semaphore) | `BoundedBlockingQueue.java` |
| Thread-Safe Singleton | `singleton/ThreadSafeSingleton.java` |
| Job Scheduler (CountDownLatch) | `jobscheduler/JobScheduler.java` |
| Task Scheduler (Semaphore + CompletableFuture) | `taskscheduler/ConcurrentTaskScheduler.java` |
| Even-Odd (wait/notify, Semaphore) | `evenodd/EvenOddPrinter.java` |
| Distributed Lock | `distributedlock/DistributedLockManager.java` |
| Rate Limiter (Token Bucket) | `ratelimiter/RateLimiter.java` |
| LRU Cache (ReadWriteLock) | `lrucache/ThreadSafeLRUCache.java` |

---

*Last updated: January 2026*
