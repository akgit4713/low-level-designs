package lrucache;

import lrucache.cache.Cache;
import lrucache.cache.LRUCache;
import lrucache.policy.FIFOEvictionPolicy;
import lrucache.policy.LRUEvictionPolicy;

import java.util.Optional;

/**
 * Demonstration of the LRU Cache implementation.
 * Shows various usage patterns and features.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║           LRU CACHE DEMONSTRATION                          ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
        
        demonstrateBasicUsage();
        demonstrateLRUEviction();
        demonstrateEvictionListener();
        demonstrateFIFOPolicy();
        demonstrateBuilderPattern();
        
        System.out.println("\n✅ All demonstrations completed successfully!");
    }
    
    /**
     * Demonstrates basic cache operations: put, get, remove.
     */
    private static void demonstrateBasicUsage() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📦 BASIC USAGE");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        // Create cache with capacity 3
        Cache<String, String> cache = LRUCache.create(3);
        
        // Put operations
        cache.put("user:1", "Alice");
        cache.put("user:2", "Bob");
        cache.put("user:3", "Charlie");
        
        System.out.println("Added 3 users to cache (capacity: 3)");
        System.out.println("  Cache size: " + cache.size());
        
        // Get operations
        Optional<String> user1 = cache.get("user:1");
        System.out.println("  get(user:1) = " + user1.orElse("NOT FOUND"));
        
        // Get non-existent key
        Optional<String> user99 = cache.get("user:99");
        System.out.println("  get(user:99) = " + user99.orElse("NOT FOUND"));
        
        // Remove
        Optional<String> removed = cache.remove("user:2");
        System.out.println("  remove(user:2) = " + removed.orElse("NOT FOUND"));
        System.out.println("  Cache size after remove: " + cache.size());
        
        System.out.println();
    }
    
    /**
     * Demonstrates LRU eviction behavior.
     */
    private static void demonstrateLRUEviction() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔄 LRU EVICTION BEHAVIOR");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        Cache<Integer, String> cache = LRUCache.create(3);
        
        // Fill cache
        System.out.println("Step 1: Fill cache with 3 items");
        cache.put(1, "One");
        cache.put(2, "Two");
        cache.put(3, "Three");
        printCacheState(cache, 1, 2, 3);
        
        // Access key 1 (makes it most recently used)
        System.out.println("\nStep 2: Access key 1 (moves to front)");
        cache.get(1);
        System.out.println("  LRU order after get(1): 1 -> 3 -> 2 (2 is now LRU)");
        
        // Add new item (evicts LRU = key 2)
        System.out.println("\nStep 3: Add key 4 (triggers eviction)");
        cache.put(4, "Four");
        printCacheState(cache, 1, 2, 3, 4);
        System.out.println("  Key 2 was evicted (Least Recently Used)");
        
        // Access patterns matter
        System.out.println("\nStep 4: Access key 3, then add key 5");
        cache.get(3);
        cache.put(5, "Five");
        printCacheState(cache, 1, 3, 4, 5);
        System.out.println("  Key 4 was evicted (was LRU after accessing 3)");
        
        System.out.println();
    }
    
    /**
     * Demonstrates eviction listener for monitoring.
     */
    private static void demonstrateEvictionListener() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👂 EVICTION LISTENER (Observer Pattern)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        // Create cache with eviction listener
        LRUCache<String, Integer> cache = new LRUCache.Builder<String, Integer>(2)
                .evictionListener((key, value) -> 
                    System.out.println("  ⚠️  EVICTED: " + key + " -> " + value))
                .build();
        
        System.out.println("Cache created with capacity 2 and eviction listener");
        
        cache.put("item-A", 100);
        System.out.println("  put(item-A, 100)");
        
        cache.put("item-B", 200);
        System.out.println("  put(item-B, 200)");
        
        cache.put("item-C", 300);
        System.out.println("  put(item-C, 300) - triggers eviction:");
        
        cache.put("item-D", 400);
        System.out.println("  put(item-D, 400) - triggers eviction:");
        
        System.out.println();
    }
    
    /**
     * Demonstrates FIFO eviction policy (extensibility).
     */
    private static void demonstrateFIFOPolicy() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔀 CUSTOM EVICTION POLICY: FIFO (Strategy Pattern)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        // Create cache with FIFO policy
        LRUCache<Integer, String> fifoCache = new LRUCache.Builder<Integer, String>(3)
                .evictionPolicy(new FIFOEvictionPolicy<>())
                .evictionListener((key, value) -> 
                    System.out.println("  ⚠️  FIFO EVICTED: " + key))
                .build();
        
        System.out.println("FIFO Cache (First-In-First-Out):");
        System.out.println("  Unlike LRU, access does NOT update eviction order\n");
        
        fifoCache.put(1, "First");
        fifoCache.put(2, "Second");
        fifoCache.put(3, "Third");
        System.out.println("  Added: 1, 2, 3");
        
        // Access key 1 (in LRU this would move it to front, but not in FIFO)
        fifoCache.get(1);
        System.out.println("  Accessed key 1 (does NOT change order in FIFO)");
        
        fifoCache.put(4, "Fourth");
        System.out.println("  Added key 4:");
        
        // Compare with LRU behavior
        System.out.println("\n  In LRU: key 2 would be evicted (1 was recently accessed)");
        System.out.println("  In FIFO: key 1 is evicted (it was first in)");
        
        System.out.println();
    }
    
    /**
     * Demonstrates builder pattern for flexible construction.
     */
    private static void demonstrateBuilderPattern() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🏗️  BUILDER PATTERN");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        // Simple creation
        Cache<String, String> simpleCache = LRUCache.create(100);
        System.out.println("Simple: LRUCache.create(100)");
        System.out.println("  " + simpleCache);
        
        // Fluent builder
        LRUCache<String, Object> configuredCache = new LRUCache.Builder<String, Object>(50)
                .evictionPolicy(new LRUEvictionPolicy<>())
                .evictionListener((k, v) -> System.out.println("Evicted: " + k))
                .build();
        
        System.out.println("\nConfigured: Builder pattern");
        System.out.println("  new LRUCache.Builder<>(50)");
        System.out.println("      .evictionPolicy(new LRUEvictionPolicy<>())");
        System.out.println("      .evictionListener(...)");
        System.out.println("      .build()");
        System.out.println("  " + configuredCache);
        
        System.out.println();
    }
    
    /**
     * Helper to print cache state for specific keys.
     */
    private static void printCacheState(Cache<Integer, String> cache, int... keys) {
        StringBuilder sb = new StringBuilder("  Cache state: {");
        boolean first = true;
        for (int key : keys) {
            Optional<String> value = cache.get(key);
            if (value.isPresent()) {
                if (!first) sb.append(", ");
                sb.append(key).append("=").append(value.get());
                first = false;
            }
        }
        sb.append("}");
        System.out.println(sb);
    }
}



