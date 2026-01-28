package concurrency.evenodd;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;

/**
 * ================================================================================
 * PROBLEM 5: PRINT N NUMBERS USING DIFFERENT EVEN-ODD THREADS
 * ================================================================================
 * 
 * Requirements:
 * 1. Print numbers 1 to N in order
 * 2. One thread prints only odd numbers (1, 3, 5, ...)
 * 3. Another thread prints only even numbers (2, 4, 6, ...)
 * 4. Numbers must appear in correct sequence: 1, 2, 3, 4, ...
 * 
 * Key Approaches:
 * 1. wait/notify with synchronized - Classic approach
 * 2. Lock/Condition - More control, explicit signaling
 * 3. Semaphores - Elegant signaling between threads
 * 4. AtomicInteger with spin-wait - Lock-free (for low contention)
 * 
 * Interview Tips:
 * - Always use while() loop for wait conditions (spurious wakeups)
 * - Ensure proper signaling to avoid deadlocks
 * - Handle edge cases (N=0, N=1)
 */

// ==================== APPROACH 1: synchronized + wait/notify ====================

public class EvenOddPrinter {
    
    private final int limit;
    private int current = 1;
    private final Object lock = new Object();
    
    public EvenOddPrinter(int limit) {
        this.limit = limit;
    }
    
    /**
     * Called by the odd-number thread.
     * Prints 1, 3, 5, 7, ...
     */
    public void printOdd() {
        synchronized (lock) {
            while (current <= limit) {
                // Wait if current is even (not our turn)
                while (current % 2 == 0 && current <= limit) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                
                if (current <= limit) {
                    System.out.println(Thread.currentThread().getName() + ": " + current);
                    current++;
                    lock.notify();  // Wake up even thread
                }
            }
        }
    }
    
    /**
     * Called by the even-number thread.
     * Prints 2, 4, 6, 8, ...
     */
    public void printEven() {
        synchronized (lock) {
            while (current <= limit) {
                // Wait if current is odd (not our turn)
                while (current % 2 != 0 && current <= limit) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                
                if (current <= limit) {
                    System.out.println(Thread.currentThread().getName() + ": " + current);
                    current++;
                    lock.notify();  // Wake up odd thread
                }
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Approach 1: synchronized + wait/notify ===\n");
        
        EvenOddPrinter printer = new EvenOddPrinter(10);
        
        Thread oddThread = new Thread(printer::printOdd, "OddThread");
        Thread evenThread = new Thread(printer::printEven, "EvenThread");
        
        oddThread.start();
        evenThread.start();
        
        oddThread.join();
        evenThread.join();
        
        System.out.println("\nDone!");
    }
}

// ==================== APPROACH 2: Lock + Condition ====================

class EvenOddWithLockCondition {
    
    private final int limit;
    private int current = 1;
    private final Lock lock = new ReentrantLock();
    private final Condition oddTurn = lock.newCondition();
    private final Condition evenTurn = lock.newCondition();
    
    public EvenOddWithLockCondition(int limit) {
        this.limit = limit;
    }
    
    public void printOdd() {
        while (true) {
            lock.lock();
            try {
                // Wait while it's even's turn
                while (current % 2 == 0 && current <= limit) {
                    try {
                        oddTurn.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                
                if (current > limit) break;
                
                System.out.println(Thread.currentThread().getName() + ": " + current);
                current++;
                
                // Signal even thread
                evenTurn.signal();
                
            } finally {
                lock.unlock();
            }
        }
    }
    
    public void printEven() {
        while (true) {
            lock.lock();
            try {
                // Wait while it's odd's turn
                while (current % 2 != 0 && current <= limit) {
                    try {
                        evenTurn.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                
                if (current > limit) {
                    oddTurn.signal();  // Wake up odd thread so it can exit
                    break;
                }
                
                System.out.println(Thread.currentThread().getName() + ": " + current);
                current++;
                
                // Signal odd thread
                oddTurn.signal();
                
            } finally {
                lock.unlock();
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\n=== Approach 2: Lock + Condition ===\n");
        
        EvenOddWithLockCondition printer = new EvenOddWithLockCondition(10);
        
        Thread oddThread = new Thread(printer::printOdd, "OddThread");
        Thread evenThread = new Thread(printer::printEven, "EvenThread");
        
        oddThread.start();
        evenThread.start();
        
        oddThread.join();
        evenThread.join();
        
        System.out.println("\nDone!");
    }
}

// ==================== APPROACH 3: Semaphores ====================

class EvenOddWithSemaphore {
    
    private final int limit;
    private int current = 1;
    
    // oddSemaphore: starts with 1 permit (odd goes first)
    private final Semaphore oddSemaphore = new Semaphore(1);
    
    // evenSemaphore: starts with 0 permits (even waits initially)
    private final Semaphore evenSemaphore = new Semaphore(0);
    
    public EvenOddWithSemaphore(int limit) {
        this.limit = limit;
    }
    
    public void printOdd() {
        while (current <= limit) {
            try {
                // Wait for our turn
                oddSemaphore.acquire();
                
                if (current <= limit) {
                    System.out.println(Thread.currentThread().getName() + ": " + current);
                    current++;
                }
                
                // Signal even thread
                evenSemaphore.release();
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
    
    public void printEven() {
        while (current <= limit) {
            try {
                // Wait for our turn
                evenSemaphore.acquire();
                
                if (current <= limit) {
                    System.out.println(Thread.currentThread().getName() + ": " + current);
                    current++;
                }
                
                // Signal odd thread
                oddSemaphore.release();
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\n=== Approach 3: Semaphores ===\n");
        
        EvenOddWithSemaphore printer = new EvenOddWithSemaphore(10);
        
        Thread oddThread = new Thread(printer::printOdd, "OddThread");
        Thread evenThread = new Thread(printer::printEven, "EvenThread");
        
        oddThread.start();
        evenThread.start();
        
        oddThread.join();
        evenThread.join();
        
        System.out.println("\nDone!");
    }
}

// ==================== FOLLOW-UP: N Threads Printing in Order ====================

/**
 * Generalization: N threads print numbers in round-robin order.
 * Thread 0: prints 1, N+1, 2N+1, ...
 * Thread 1: prints 2, N+2, 2N+2, ...
 * Thread N-1: prints N, 2N, 3N, ...
 */
class NThreadsPrinter {
    
    private final int numThreads;
    private final int limit;
    private int current = 1;
    private final Object lock = new Object();
    
    public NThreadsPrinter(int numThreads, int limit) {
        this.numThreads = numThreads;
        this.limit = limit;
    }
    
    /**
     * Thread with given ID prints its assigned numbers.
     * 
     * @param threadId 0-indexed thread ID
     */
    public void print(int threadId) {
        synchronized (lock) {
            while (current <= limit) {
                // Calculate whose turn it is (1-indexed current maps to 0-indexed thread)
                int expectedThread = (current - 1) % numThreads;
                
                // Wait if not our turn
                while (expectedThread != threadId && current <= limit) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    expectedThread = (current - 1) % numThreads;
                }
                
                if (current <= limit) {
                    System.out.println("Thread-" + threadId + ": " + current);
                    current++;
                    lock.notifyAll();  // Wake all threads to check their turn
                }
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\n=== Follow-up: N Threads (3 threads) ===\n");
        
        int numThreads = 3;
        int limit = 15;
        NThreadsPrinter printer = new NThreadsPrinter(numThreads, limit);
        
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> printer.print(threadId), "Thread-" + i);
        }
        
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        
        System.out.println("\nDone!");
    }
}

// ==================== FOLLOW-UP: FizzBuzz with Multiple Threads ====================

/**
 * Classic FizzBuzz but with 4 threads:
 * - Thread 1: prints "fizz" for numbers divisible by 3 only
 * - Thread 2: prints "buzz" for numbers divisible by 5 only
 * - Thread 3: prints "fizzbuzz" for numbers divisible by both
 * - Thread 4: prints numbers not divisible by 3 or 5
 */
class FizzBuzzMultiThreaded {
    
    private final int n;
    private int current = 1;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    
    public FizzBuzzMultiThreaded(int n) {
        this.n = n;
    }
    
    // Thread 1: prints "fizz" for multiples of 3 (but not 5)
    public void fizz(Runnable printFizz) throws InterruptedException {
        while (true) {
            lock.lock();
            try {
                while (current <= n && !(current % 3 == 0 && current % 5 != 0)) {
                    condition.await();
                }
                if (current > n) break;
                
                printFizz.run();
                current++;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
    
    // Thread 2: prints "buzz" for multiples of 5 (but not 3)
    public void buzz(Runnable printBuzz) throws InterruptedException {
        while (true) {
            lock.lock();
            try {
                while (current <= n && !(current % 5 == 0 && current % 3 != 0)) {
                    condition.await();
                }
                if (current > n) break;
                
                printBuzz.run();
                current++;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
    
    // Thread 3: prints "fizzbuzz" for multiples of both 3 and 5
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while (true) {
            lock.lock();
            try {
                while (current <= n && !(current % 15 == 0)) {
                    condition.await();
                }
                if (current > n) break;
                
                printFizzBuzz.run();
                current++;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
    
    // Thread 4: prints numbers not divisible by 3 or 5
    public void number(java.util.function.IntConsumer printNumber) throws InterruptedException {
        while (true) {
            lock.lock();
            try {
                while (current <= n && !(current % 3 != 0 && current % 5 != 0)) {
                    condition.await();
                }
                if (current > n) break;
                
                printNumber.accept(current);
                current++;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("\n=== Follow-up: FizzBuzz Multi-Threaded ===\n");
        
        FizzBuzzMultiThreaded fb = new FizzBuzzMultiThreaded(20);
        
        Thread t1 = new Thread(() -> {
            try { fb.fizz(() -> System.out.print("fizz ")); } 
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        
        Thread t2 = new Thread(() -> {
            try { fb.buzz(() -> System.out.print("buzz ")); } 
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        
        Thread t3 = new Thread(() -> {
            try { fb.fizzbuzz(() -> System.out.print("fizzbuzz ")); } 
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        
        Thread t4 = new Thread(() -> {
            try { fb.number(n -> System.out.print(n + " ")); } 
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        
        t1.start(); t2.start(); t3.start(); t4.start();
        t1.join(); t2.join(); t3.join(); t4.join();
        
        System.out.println("\n\nDone!");
    }
}

// ==================== Master Test Class ====================

class EvenOddAllApproachesTest {
    
    public static void main(String[] args) throws InterruptedException {
        // Run all approaches
        EvenOddPrinter.main(args);
        EvenOddWithLockCondition.main(args);
        EvenOddWithSemaphore.main(args);
        NThreadsPrinter.main(args);
        FizzBuzzMultiThreaded.main(args);
    }
}
