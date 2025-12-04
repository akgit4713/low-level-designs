package pubsub.impl;

import pubsub.interfaces.MessageDispatcher;
import pubsub.models.Message;
import pubsub.models.Subscription;

import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Asynchronous message dispatcher using a thread pool.
 * Delivers messages to subscribers concurrently without blocking the publisher.
 * 
 * Features:
 * - Configurable thread pool size
 * - Non-blocking message delivery
 * - Graceful shutdown support
 * - Error handling per subscriber
 *
 * @param <T> The type of message payload
 */
public class AsyncMessageDispatcher<T> implements MessageDispatcher<T> {
    
    private final ExecutorService executorService;
    private final AtomicBoolean running;
    private final int deliveryTimeoutSeconds;
    
    /**
     * Creates an async dispatcher with default settings.
     * Uses a cached thread pool for dynamic scaling.
     */
    public AsyncMessageDispatcher() {
        this(Runtime.getRuntime().availableProcessors() * 2, 30);
    }
    
    /**
     * Creates an async dispatcher with specified thread pool size.
     *
     * @param poolSize Number of threads in the pool
     * @param deliveryTimeoutSeconds Timeout for individual message delivery
     */
    public AsyncMessageDispatcher(int poolSize, int deliveryTimeoutSeconds) {
        this.executorService = new ThreadPoolExecutor(
                poolSize,                          // Core pool size
                poolSize * 2,                      // Max pool size
                60L, TimeUnit.SECONDS,             // Keep alive time
                new LinkedBlockingQueue<>(10000),  // Bounded queue to prevent OOM
                new ThreadFactory() {
                    private int counter = 0;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "pubsub-dispatcher-" + counter++);
                        t.setDaemon(true);
                        return t;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // Backpressure: caller runs if queue full
        );
        this.running = new AtomicBoolean(true);
        this.deliveryTimeoutSeconds = deliveryTimeoutSeconds;
    }
    
    /**
     * Creates an async dispatcher with a custom executor service.
     *
     * @param executorService Custom executor service
     * @param deliveryTimeoutSeconds Timeout for individual message delivery
     */
    public AsyncMessageDispatcher(ExecutorService executorService, int deliveryTimeoutSeconds) {
        this.executorService = executorService;
        this.running = new AtomicBoolean(true);
        this.deliveryTimeoutSeconds = deliveryTimeoutSeconds;
    }
    
    @Override
    public void dispatch(Message<T> message, Set<Subscription<T>> subscriptions) {
        if (!running.get()) {
            throw new IllegalStateException("Dispatcher has been shut down");
        }
        
        if (subscriptions == null || subscriptions.isEmpty()) {
            return;
        }
        
        for (Subscription<T> subscription : subscriptions) {
            if (!subscription.isActive()) {
                continue;
            }
            
            executorService.submit(() -> deliverToSubscriber(message, subscription));
        }
    }
    
    private void deliverToSubscriber(Message<T> message, Subscription<T> subscription) {
        try {
            subscription.getSubscriber().onMessage(message);
        } catch (Exception e) {
            // Don't let one subscriber's failure affect others
            try {
                subscription.getSubscriber().onError(message, e);
            } catch (Exception errorHandlerException) {
                // Log if even the error handler fails
                System.err.println("Error handler failed for subscriber " + 
                        subscription.getSubscriber().getId() + ": " + errorHandlerException.getMessage());
            }
        }
    }
    
    @Override
    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    @Override
    public void shutdownNow() {
        if (running.compareAndSet(true, false)) {
            executorService.shutdownNow();
        }
    }
    
    @Override
    public boolean isRunning() {
        return running.get() && !executorService.isShutdown();
    }
}



