package concurrency.singleton;

import java.io.*;

/**
 * ================================================================================
 * PROBLEM 7: THREAD-SAFE SINGLETON DESIGN
 * ================================================================================
 * 
 * Requirements:
 * 1. Only one instance of the class exists
 * 2. Thread-safe initialization
 * 3. Lazy initialization (optional but often required)
 * 
 * Approaches (Ordered by Recommendation):
 * 1. Enum Singleton - BEST: Simple, thread-safe, serialization-safe
 * 2. Bill Pugh (Static Inner Class) - Lazy, thread-safe
 * 3. Double-Checked Locking - Classic, requires volatile
 * 4. Synchronized Method - Simple but slow
 * 5. Eager Initialization - Thread-safe but not lazy
 * 
 * Interview Tips:
 * - Know pros/cons of each approach
 * - Understand why volatile is needed in double-checked locking
 * - Know about serialization and reflection attacks
 */

// ==================== APPROACH 1: ENUM SINGLETON (RECOMMENDED) ====================

/**
 * Enum-based singleton is the best approach because:
 * 1. Thread-safe by JVM guarantee
 * 2. Serialization-safe (prevents multiple instances via deserialization)
 * 3. Reflection-safe (JVM prevents enum instantiation via reflection)
 * 4. Concise and simple
 * 
 * Drawback: Cannot extend other classes (enums implicitly extend Enum)
 */
enum DatabaseConnectionEnum {
    INSTANCE;
    
    private String connectionUrl = "jdbc:mysql://localhost:3306/mydb";
    
    public void connect() {
        System.out.println("Connecting to: " + connectionUrl);
    }
    
    public void query(String sql) {
        System.out.println("Executing: " + sql);
    }
    
    public void setConnectionUrl(String url) {
        this.connectionUrl = url;
    }
}

// ==================== APPROACH 2: BILL PUGH (STATIC INNER CLASS) ====================

/**
 * Bill Pugh Singleton uses a static inner class for lazy initialization.
 * 
 * How it works:
 * - Inner class is not loaded until getInstance() is called
 * - Class loading is thread-safe by JVM specification
 * - No synchronization overhead after initialization
 * 
 * Pros:
 * - Lazy initialization
 * - Thread-safe without synchronization
 * - Simple to understand
 * 
 * Cons:
 * - Not safe against reflection attacks (can be fixed)
 * - Requires readResolve() for serialization safety
 */
class ConfigurationManager {
    
    // Private constructor
    private ConfigurationManager() {
        // Prevent reflection attacks
        if (SingletonHelper.INSTANCE != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance");
        }
        System.out.println("ConfigurationManager initialized");
    }
    
    // Static inner class - not loaded until getInstance() is called
    private static class SingletonHelper {
        // JVM guarantees thread-safe initialization of static final fields
        private static final ConfigurationManager INSTANCE = new ConfigurationManager();
    }
    
    public static ConfigurationManager getInstance() {
        return SingletonHelper.INSTANCE;
    }
    
    // Prevent deserialization from creating new instance
    protected Object readResolve() {
        return getInstance();
    }
    
    // Business methods
    public String getProperty(String key) {
        return System.getProperty(key, "default");
    }
}

// ==================== APPROACH 3: DOUBLE-CHECKED LOCKING ====================

/**
 * Classic double-checked locking pattern.
 * 
 * Why volatile is CRITICAL:
 * Without volatile, thread may see partially constructed object due to:
 * 1. JVM instruction reordering
 * 2. CPU caching
 * 
 * The volatile keyword ensures:
 * 1. Writes happen-before reads (visibility)
 * 2. Prevents instruction reordering
 * 
 * Performance:
 * - Only synchronizes on first access
 * - After initialization, no locking overhead
 */
class CacheManager {
    
    // MUST be volatile to prevent instruction reordering
    private static volatile CacheManager instance;
    
    private CacheManager() {
        System.out.println("CacheManager initialized");
    }
    
    public static CacheManager getInstance() {
        // First check (no locking)
        if (instance == null) {
            // Synchronize only if instance is null
            synchronized (CacheManager.class) {
                // Second check (with locking) - another thread may have initialized
                if (instance == null) {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }
    
    public void put(String key, Object value) {
        System.out.println("Caching: " + key);
    }
    
    public Object get(String key) {
        return null; // Simplified
    }
}

/**
 * Why Double-Checked Locking needs volatile?
 * 
 * Without volatile, the following can happen:
 * 
 * instance = new CacheManager();
 * 
 * This line is NOT atomic. JVM may reorder to:
 * 1. Allocate memory
 * 2. Assign reference to instance (instance != null now!)
 * 3. Call constructor
 * 
 * Another thread at step 2 sees instance != null and returns
 * a partially constructed object!
 * 
 * volatile prevents this reordering by establishing happens-before relationship.
 */

// ==================== APPROACH 4: SYNCHRONIZED METHOD ====================

/**
 * Simple synchronized method approach.
 * 
 * Pros:
 * - Simple to implement
 * - Thread-safe
 * 
 * Cons:
 * - Performance hit: synchronization on EVERY call
 * - Not suitable for high-concurrency scenarios
 */
class LogManager {
    
    private static LogManager instance;
    
    private LogManager() {
        System.out.println("LogManager initialized");
    }
    
    // Synchronized method - every call acquires lock
    public static synchronized LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }
        return instance;
    }
    
    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}

// ==================== APPROACH 5: EAGER INITIALIZATION ====================

/**
 * Eager initialization - instance created at class loading time.
 * 
 * Pros:
 * - Thread-safe (JVM handles class loading)
 * - Simple
 * 
 * Cons:
 * - Not lazy: instance created even if never used
 * - Cannot handle constructor exceptions gracefully
 */
class ApplicationContext {
    
    // Instance created at class loading time
    private static final ApplicationContext INSTANCE = new ApplicationContext();
    
    private ApplicationContext() {
        System.out.println("ApplicationContext initialized");
    }
    
    public static ApplicationContext getInstance() {
        return INSTANCE;
    }
}

// ==================== SERIALIZATION-SAFE SINGLETON ====================

/**
 * Singleton that survives serialization/deserialization.
 * 
 * Problem: Default deserialization creates a new instance
 * Solution: Implement readResolve() to return existing instance
 */
class SerializableSingleton implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private static class SingletonHelper {
        private static final SerializableSingleton INSTANCE = new SerializableSingleton();
    }
    
    private SerializableSingleton() {
        // Prevent reflection attacks
        if (SingletonHelper.INSTANCE != null) {
            throw new RuntimeException("Use getInstance()");
        }
    }
    
    public static SerializableSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }
    
    // This method is called during deserialization
    // Return the existing instance instead of the deserialized one
    protected Object readResolve() {
        return getInstance();
    }
}

// ==================== REFLECTION-SAFE SINGLETON ====================

/**
 * Singleton protected against reflection attacks.
 * 
 * Reflection attack:
 * Constructor<?> constructor = Singleton.class.getDeclaredConstructors()[0];
 * constructor.setAccessible(true);
 * Singleton instance2 = (Singleton) constructor.newInstance();
 */
class ReflectionSafeSingleton {
    
    private static volatile ReflectionSafeSingleton instance;
    private static boolean instanceCreated = false;
    
    private ReflectionSafeSingleton() {
        // Check if instance already exists
        synchronized (ReflectionSafeSingleton.class) {
            if (instanceCreated) {
                throw new RuntimeException("Cannot create second instance via reflection!");
            }
            instanceCreated = true;
        }
        System.out.println("ReflectionSafeSingleton initialized");
    }
    
    public static ReflectionSafeSingleton getInstance() {
        if (instance == null) {
            synchronized (ReflectionSafeSingleton.class) {
                if (instance == null) {
                    instance = new ReflectionSafeSingleton();
                }
            }
        }
        return instance;
    }
}

// ==================== TEST CLASS ====================

public class ThreadSafeSingleton {
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Thread-Safe Singleton Patterns ===\n");
        
        // Test 1: Enum Singleton
        System.out.println("1. Enum Singleton:");
        DatabaseConnectionEnum.INSTANCE.connect();
        DatabaseConnectionEnum.INSTANCE.query("SELECT * FROM users");
        System.out.println("Same instance: " + 
            (DatabaseConnectionEnum.INSTANCE == DatabaseConnectionEnum.INSTANCE));
        
        // Test 2: Bill Pugh Singleton
        System.out.println("\n2. Bill Pugh (Static Inner Class):");
        ConfigurationManager config1 = ConfigurationManager.getInstance();
        ConfigurationManager config2 = ConfigurationManager.getInstance();
        System.out.println("Same instance: " + (config1 == config2));
        
        // Test 3: Double-Checked Locking
        System.out.println("\n3. Double-Checked Locking:");
        CacheManager cache1 = CacheManager.getInstance();
        CacheManager cache2 = CacheManager.getInstance();
        System.out.println("Same instance: " + (cache1 == cache2));
        
        // Test 4: Concurrent access test
        System.out.println("\n4. Concurrent Access Test:");
        testConcurrentAccess();
        
        // Test 5: Serialization test
        System.out.println("\n5. Serialization Test:");
        testSerialization();
        
        System.out.println("\nAll tests completed!");
    }
    
    private static void testConcurrentAccess() throws InterruptedException {
        int numThreads = 100;
        Thread[] threads = new Thread[numThreads];
        CacheManager[] instances = new CacheManager[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instances[index] = CacheManager.getInstance();
            });
        }
        
        // Start all threads simultaneously
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();
        
        // Verify all threads got the same instance
        boolean allSame = true;
        for (int i = 1; i < numThreads; i++) {
            if (instances[i] != instances[0]) {
                allSame = false;
                break;
            }
        }
        System.out.println("All " + numThreads + " threads got same instance: " + allSame);
    }
    
    private static void testSerialization() throws Exception {
        SerializableSingleton instance1 = SerializableSingleton.getInstance();
        
        // Serialize
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(instance1);
        oos.close();
        
        // Deserialize
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        SerializableSingleton instance2 = (SerializableSingleton) ois.readObject();
        ois.close();
        
        System.out.println("Original instance: " + instance1.hashCode());
        System.out.println("Deserialized instance: " + instance2.hashCode());
        System.out.println("Same instance after deserialization: " + (instance1 == instance2));
    }
}

/**
 * ================================================================================
 * COMPARISON TABLE
 * ================================================================================
 * 
 * | Approach              | Lazy | Thread-Safe | Serialization-Safe | Reflection-Safe |
 * |-----------------------|------|-------------|--------------------| ----------------|
 * | Enum                  | No   | Yes         | Yes                | Yes             |
 * | Bill Pugh             | Yes  | Yes         | With readResolve   | No              |
 * | Double-Checked        | Yes  | Yes         | With readResolve   | No              |
 * | Synchronized Method   | Yes  | Yes         | With readResolve   | No              |
 * | Eager                 | No   | Yes         | With readResolve   | No              |
 * 
 * Recommendation Order:
 * 1. Enum Singleton - Use when possible (best overall)
 * 2. Bill Pugh - Use when you need to extend a class
 * 3. Double-Checked Locking - Classic approach, good to know
 * 4. Others - Know for interviews but avoid in production
 */
