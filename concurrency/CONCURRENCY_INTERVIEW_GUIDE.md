# Java Concurrency Interview Guide - FAANG Machine Coding

## Table of Contents
1. [Job Scheduler with Dependencies](#1-job-scheduler-with-dependencies)
2. [Thread-Safe Job Queue](#2-thread-safe-job-queue)
3. [Thread-Safe LRU Cache](#3-thread-safe-lru-cache)
4. [Concurrent Task Scheduler](#4-concurrent-task-scheduler)
5. [Even-Odd Printer](#5-even-odd-printer)
6. [Parallel Matrix Multiplication](#6-parallel-matrix-multiplication)
7. [Thread-Safe Singleton](#7-thread-safe-singleton)
8. [Priority Job Scheduler](#8-priority-job-scheduler)
9. [Distributed Lock Manager](#9-distributed-lock-manager)
10. [Rate Limiter](#10-rate-limiter)

---

## Key Concurrency Concepts Quick Reference

| Concept | Use Case | Thread-Safe? |
|---------|----------|--------------|
| `synchronized` | Simple mutual exclusion | Yes |
| `ReentrantLock` | Advanced locking with conditions | Yes |
| `ReadWriteLock` | Multiple readers, single writer | Yes |
| `Semaphore` | Resource pool limiting | Yes |
| `CountDownLatch` | Wait for N events | Yes |
| `CyclicBarrier` | Sync N threads at barrier | Yes |
| `AtomicInteger` | Lock-free counter | Yes |
| `ConcurrentHashMap` | Thread-safe map | Yes |
| `BlockingQueue` | Producer-consumer | Yes |
| `ExecutorService` | Thread pool management | Yes |

---

## 1. Job Scheduler with Dependencies

### Problem Statement
Design a job scheduler that can schedule and execute multiple jobs concurrently, respecting:
- Job dependencies (Job B can only run after Job A completes)
- Job priorities

### Key Design Decisions
1. **Topological Sort** for dependency resolution
2. **Thread Pool** for concurrent execution
3. **CountDownLatch** for dependency tracking
4. **PriorityQueue** for priority-based scheduling

### Time Complexity
- Submit job: O(log n)
- Execute: O(V + E) for dependency resolution

### Follow-up Questions
1. **How to handle circular dependencies?** 
   - Detect cycles during job submission using DFS
   - Reject jobs that would create cycles

2. **How to handle job failures?**
   - Implement retry mechanism with exponential backoff
   - Cancel dependent jobs or mark them as blocked

3. **How to scale across multiple machines?**
   - Use distributed message queue (Kafka)
   - Implement distributed locking (ZooKeeper/Redis)

---

## 2. Thread-Safe Job Queue

### Problem Statement
Implement a thread-safe job queue supporting:
- Add jobs
- Remove jobs
- Get next job to execute

### Key Design Decisions
1. **ReentrantLock + Conditions** for blocking operations
2. **LinkedList** for O(1) enqueue/dequeue
3. **Separate Conditions** for producers and consumers

### Trade-offs
| Approach | Pros | Cons |
|----------|------|------|
| `synchronized` | Simple | Single wait condition |
| `ReentrantLock` | Fine-grained control | More verbose |
| `BlockingQueue` | Built-in, tested | Less customizable |

### Follow-up Questions
1. **How to implement priority queue variant?**
   - Use `PriorityBlockingQueue` or custom heap with locks

2. **How to handle job cancellation?**
   - Maintain job status map, check before execution

---

## 3. Thread-Safe LRU Cache

### Problem Statement
Implement LRU cache with:
- Concurrent read/write operations
- O(1) get and put operations
- Thread safety

### Key Design Decisions
1. **ReadWriteLock** for concurrent reads
2. **LinkedHashMap** with access-order for LRU
3. **Striped locking** for better concurrency (advanced)

### Time Complexity
- Get: O(1)
- Put: O(1)

### Follow-up Questions
1. **How to improve read concurrency?**
   - Use `ConcurrentHashMap` with segment locking
   - Implement lock striping

2. **How to handle cache warming?**
   - Async background loading
   - Bulk load API

3. **How to implement TTL (Time-To-Live)?**
   - Add timestamp to entries
   - Background cleanup thread

---

## 4. Concurrent Task Scheduler

### Problem Statement
Execute tasks concurrently while limiting maximum parallel executions.

### Key Design Decisions
1. **Semaphore** for limiting concurrency
2. **ExecutorService** for thread management
3. **CompletableFuture** for async composition

### Follow-up Questions
1. **How to implement task timeout?**
   - Use `Future.get(timeout, unit)`
   - `ScheduledExecutorService` for timeout monitoring

2. **How to implement fair scheduling?**
   - Fair semaphore: `new Semaphore(permits, true)`

---

## 5. Even-Odd Printer

### Problem Statement
Two threads print numbers 1-N in order:
- Thread 1: prints odd numbers
- Thread 2: prints even numbers

### Key Approaches
1. **wait/notify** - Classic approach
2. **Lock/Condition** - More control
3. **Semaphores** - Elegant signaling

### Follow-up Questions
1. **How to extend to N threads?**
   - Each thread prints numbers where `num % N == threadId`

2. **How to print in reverse order?**
   - Start from N, decrement counter

---

## 6. Parallel Matrix Multiplication

### Problem Statement
Multiply two matrices using multiple threads.

### Parallelization Strategies
1. **Row-wise** - Each thread handles subset of rows
2. **Block-wise** - Divide matrix into blocks
3. **ForkJoinPool** - Recursive decomposition

### Time Complexity
- Sequential: O(n³)
- Parallel: O(n³/p) where p = number of threads

### Follow-up Questions
1. **How to optimize cache performance?**
   - Block/tile the matrices
   - Transpose second matrix

2. **When does parallelization help?**
   - Large matrices (overhead dominates for small)

---

## 7. Thread-Safe Singleton

### Approaches (Best to Worst)
1. **Enum Singleton** - Best, handles serialization
2. **Bill Pugh (Static Inner Class)** - Lazy, thread-safe
3. **Double-Checked Locking** - Classic approach
4. **Synchronized Method** - Simple but slow

### Follow-up Questions
1. **How does enum prevent reflection attacks?**
   - JVM prevents enum instantiation via reflection

2. **How to make singleton serializable?**
   - Implement `readResolve()` method

---

## 8. Priority Job Scheduler

### Problem Statement
Schedule jobs by priority with dynamic priority changes.

### Key Design Decisions
1. **PriorityBlockingQueue** for priority ordering
2. **ReentrantLock** for priority updates
3. **Comparator** for custom priority logic

### Follow-up Questions
1. **How to prevent starvation of low-priority jobs?**
   - Aging: Increase priority over time
   - Reserved slots for each priority level

---

## 9. Distributed Lock Manager

### Problem Statement
Coordinate job execution across multiple nodes.

### Approaches
1. **Database-based** - Simple, uses DB transactions
2. **Redis-based** - Fast, uses SETNX + TTL
3. **ZooKeeper** - Robust, uses ephemeral nodes

### Key Considerations
- **Fencing tokens** to prevent split-brain
- **TTL** to handle node failures
- **Renewal** for long-running operations

### Follow-up Questions
1. **How to handle network partitions?**
   - Use quorum-based consensus
   - Implement fencing tokens

---

## 10. Rate Limiter

### Algorithms
1. **Token Bucket** - Allows bursts
2. **Sliding Window** - Smooth limiting
3. **Leaky Bucket** - Constant rate

### Key Design Decisions
- **AtomicLong** for lock-free operations
- **ScheduledExecutorService** for token refill
- **ConcurrentHashMap** for per-key limiting

### Follow-up Questions
1. **How to implement distributed rate limiting?**
   - Redis with Lua scripts
   - Sliding window with sorted sets

2. **How to handle bursts?**
   - Token bucket with larger bucket size

---

## Common Interview Tips

### Before Coding
1. Clarify requirements (blocking vs non-blocking, fairness)
2. Discuss trade-offs between approaches
3. Mention potential issues (deadlock, starvation, livelock)

### During Coding
1. Use appropriate synchronization primitives
2. Always release locks in finally blocks
3. Handle InterruptedException properly
4. Use volatile for visibility

### After Coding
1. Discuss testing strategies
2. Mention monitoring/metrics
3. Discuss scalability concerns
