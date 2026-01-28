package concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class MultiThreadSequence {
    private final int TOTAL_THREADS = 2;
    private final int LIMIT = 10;
    private int counter = 1;
    private final Object lock = new Object();

    class Printer implements Runnable {
        private final int threadId;

        public Printer(int id) {
            this.threadId = id;
        }

        @Override
        public void run() {
            while (counter <= LIMIT) {
                synchronized (lock) {
                    int turn = counter % TOTAL_THREADS;
                    if (turn == 0) turn = TOTAL_THREADS;

                    while (turn != threadId && counter <= LIMIT) {
                        try {
                            lock.wait();
                            // Re-calculate turn after waking up
                            turn = counter % TOTAL_THREADS;
                            if (turn == 0) turn = TOTAL_THREADS;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    // Double check limit before printing
                    if (counter <= LIMIT) {
                        System.out.println("Thread " + threadId + ": " + counter);
                        counter++;
                        lock.notifyAll(); // Wake up all threads to check the new counter
                    }
                }
            }
        }
    }

    public void startPrinting() {
        for (int i = 1; i <= TOTAL_THREADS; i++) {
            new Thread(new Printer(i)).start();
        }
    }

    public static void main(String[] args) {
        new MultiThreadSequence().startPrinting();
    }
}


class EvenOddPrinter {
    private final int limit;
    private int counter = 1;
    private final Object lock = new Object();

    public EvenOddPrinter(int limit) {
        this.limit = limit;
    }

    public synchronized void printOdd() {
         {
            while (counter <= limit) {
                // If counter is even, wait for Odd turn
                while (counter % 2 == 0) {
                    //try { lock.wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
                if (counter <= limit) {
                    System.out.println(Thread.currentThread().getName() + ": " + counter);
                    counter++;
                }
                //lock.notify(); // Wake up the Even thread
            }
        }
    }

    public synchronized void printEven() {
            while (counter <= limit) {
                // If counter is odd, wait for Even turn
                while (counter % 2 != 0) {
              //      try { lock.wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
                if (counter <= limit) {
                    System.out.println(Thread.currentThread().getName() + ": " + counter);
                    counter++;
                }
            //    lock.notify(); // Wake up the Odd thread
            }

    }



    class NumberPrinter{
        private Integer limit;
        public void printNumbers(){


        }



    }

    static class NumberPrinter2 implements Runnable{

        private static Integer limit = 50;
        private static Integer current= 1;
        private static Object lock = new Object();
        private final Integer threadOrder;

        public NumberPrinter2(Integer threadOrder){
            this.threadOrder = threadOrder;
        }

        @Override
        public void run() {
            synchronized (lock) {
                while (current <= limit) {
                    while(current %3 !=threadOrder) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    System.out.println(Thread.currentThread().getName() + ": " + current);
                    ++current;
                    lock.notifyAll();
                }
            }
        }

    }

    public static void main(String[] args) {
        NumberPrinter2 numberPrinter20 = new NumberPrinter2(0);
        NumberPrinter2 numberPrinter21 = new NumberPrinter2(1);
        NumberPrinter2 numberPrinter22 = new NumberPrinter2(2);

        Thread t1 = new Thread(numberPrinter20);
        Thread t2 = new Thread(numberPrinter21);
        Thread t3 = new Thread(numberPrinter22);

        t1.start();
        t2.start();
        t3.start();
    }



    public static class Solution {
        private final int n; // Total number of sequence
        private int current; // Current number being processed
        private final Lock lock = new ReentrantLock(); // Lock for synchronization
        private final Condition cond = lock.newCondition(); // Condition variable for coordination between threads

        public Solution(int n) {
            this.n = n;
            this.current = 1;
        }

        public void fizz() throws InterruptedException {
            while (true) {
                lock.lock();
                try {
                    while (current <= n && !(current % 3 == 0 && current % 5 != 0)) {
                        cond.await();
                    }
                    if (current > n) break;
                    System.out.println("fizz");
                    current++;
                    cond.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        }

        public void buzz() throws InterruptedException {
            while (true) {
                lock.lock();
                try {
                    while (current <= n && !(current % 5 == 0 && current % 3 != 0)) {
                        cond.await();
                    }
                    if (current > n) break;
                    System.out.println("buzz");
                    current++;
                    cond.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        }

        public void Solution() throws InterruptedException {
            while (true) {
                lock.lock();
                try {
                    while (current <= n && !(current % 15 == 0)) {
                        cond.await();
                    }
                    if (current > n) break;
                    System.out.println("Solution");
                    current++;
                    cond.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        }

        public void number() throws InterruptedException {
            while (true) {
                lock.lock();
                try {
                    while (current <= n && !(current % 3 != 0 && current % 5 != 0)) {
                        cond.await();
                    }
                    if (current > n) break;
                    System.out.println(current);
                    current++;
                    cond.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        }

        public static void main(String[] args) {
            int n = 20;
            Solution fb = new Solution(n);

            Thread t1 = new Thread(() -> {
                try {
                    fb.fizz();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            Thread t2 = new Thread(() -> {
                try {
                    fb.buzz();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            Thread t3 = new Thread(() -> {
                try {
                    fb.Solution();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            Thread t4 = new Thread(() -> {
                try {
                    fb.number();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            t1.start();
            t2.start();
            t3.start();
            t4.start();
        }
    }

}



