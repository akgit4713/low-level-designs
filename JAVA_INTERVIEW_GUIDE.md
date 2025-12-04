# Java Interview Guide: Clean Code, SOLID, and Design Patterns

This guide is designed to help you prepare for technical interviews by covering essential software engineering principles with clear Java examples.

---

## 1. SOLID Principles

SOLID is an acronym for five design principles intended to make software designs more understandable, flexible, and maintainable.

### S - Single Responsibility Principle (SRP)
**Definition:** A class should have one, and only one, reason to change. It should have only one job.

#### ❌ Bad Example
```java
public class UserManager {
    public void registerUser(String username, String password) {
        // Logic to save user to database
        System.out.println("User saved to DB");

        // Logic to send welcome email (Violates SRP)
        System.out.println("Sending welcome email to " + username);
    }
}
```

#### ✅ Good Example
```java
// Responsibility 1: User Management
public class UserRepository {
    public void save(String username) {
        System.out.println("User " + username + " saved to DB");
    }
}

// Responsibility 2: Notification
public class EmailService {
    public void sendWelcomeEmail(String username) {
        System.out.println("Sending welcome email to " + username);
    }
}

// Orchestrator
public class UserService {
    private UserRepository userRepository;
    private EmailService emailService;

    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void registerUser(String username) {
        userRepository.save(username);
        emailService.sendWelcomeEmail(username);
    }
}
```

---

### O - Open/Closed Principle (OCP)
**Definition:** Software entities (classes, modules, functions, etc.) should be open for extension, but closed for modification.

#### ❌ Bad Example
```java
public class DiscountCalculator {
    public double calculateDiscount(String type, double price) {
        if (type.equals("Regular")) {
            return price * 0.1;
        } else if (type.equals("VIP")) {
            return price * 0.2;
        }
        // Adding a new type requires modifying this class!
        return 0;
    }
}
```

#### ✅ Good Example
```java
// Abstract strategy
interface DiscountStrategy {
    double getDiscount(double price);
}

class RegularDiscount implements DiscountStrategy {
    public double getDiscount(double price) { return price * 0.1; }
}

class VipDiscount implements DiscountStrategy {
    public double getDiscount(double price) { return price * 0.2; }
}

// Extending functionality by adding a new class, not modifying existing code
class EmployeeDiscount implements DiscountStrategy {
    public double getDiscount(double price) { return price * 0.5; }
}

class DiscountCalculator {
    public double calculate(DiscountStrategy strategy, double price) {
        return strategy.getDiscount(price);
    }
}
```

---

### L - Liskov Substitution Principle (LSP)
**Definition:** Subtypes must be substitutable for their base types without altering the correctness of the program. "If it looks like a duck and quacks like a duck but needs batteries, you probably have the wrong abstraction."

#### ❌ Bad Example
```java
class Bird {
    public void fly() { System.out.println("Flying"); }
}

class Ostrich extends Bird {
    @Override
    public void fly() {
        throw new UnsupportedOperationException("Ostriches can't fly!");
    }
}
// Calling fly() on a Bird reference that holds an Ostrich will break the program.
```

#### ✅ Good Example
```java
interface Bird {}
interface FlyingBird extends Bird { void fly(); }

class Sparrow implements FlyingBird {
    public void fly() { System.out.println("Sparrow flying"); }
}

class Ostrich implements Bird {
    // Ostrich implements Bird but doesn't implement fly(), avoiding the issue.
}
```

---

### I - Interface Segregation Principle (ISP)
**Definition:** Clients should not be forced to depend on interfaces they do not use. Split large interfaces into smaller, specific ones.

#### ❌ Bad Example
```java
interface Worker {
    void work();
    void eat();
}

class Robot implements Worker {
    public void work() { System.out.println("Robot working"); }
    public void eat() { 
        // Robots don't eat! Forced to implement dummy method.
    }
}
```

#### ✅ Good Example
```java
interface Workable { void work(); }
interface Eatable { void eat(); }

class Human implements Workable, Eatable {
    public void work() { System.out.println("Human working"); }
    public void eat() { System.out.println("Human eating"); }
}

class Robot implements Workable {
    public void work() { System.out.println("Robot working"); }
}
```

---

### D - Dependency Inversion Principle (DIP)
**Definition:** High-level modules should not depend on low-level modules. Both should depend on abstractions. Abstractions should not depend on details. Details should depend on abstractions.

#### ❌ Bad Example
```java
class Keyboard { }
class Monitor { }

class WindowsPC {
    private Keyboard keyboard; // Direct dependency on concrete class
    private Monitor monitor;

    public WindowsPC() {
        this.keyboard = new Keyboard(); // Tightly coupled
        this.monitor = new Monitor();
    }
}
```

#### ✅ Good Example
```java
interface InputDevice { }
interface DisplayDevice { }

class Keyboard implements InputDevice { }
class Monitor implements DisplayDevice { }

class WindowsPC {
    private InputDevice input;
    private DisplayDevice display;

    // Dependency Injection via constructor
    public WindowsPC(InputDevice input, DisplayDevice display) {
        this.input = input;
        this.display = display;
    }
}
```

---

## 2. Design Patterns

Design patterns are typical solutions to common problems in software design.

### Creational Patterns
Focus on object creation mechanisms.

#### 1. Singleton Pattern
**Problem:** Ensure a class has only one instance and provide a global point of access to it.
**Solution:** Private constructor + Static instance.

```java
public class DatabaseConnection {
    // Volatile ensures visibility of changes to variables across threads
    private static volatile DatabaseConnection instance;

    private DatabaseConnection() {
        // Private constructor to prevent instantiation
    }

    public static DatabaseConnection getInstance() {
        // Double-checked locking for thread safety and performance
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public void query(String sql) {
        System.out.println("Executing: " + sql);
    }
}
```

#### 2. Factory Method Pattern
**Problem:** Creating objects without specifying the exact class of object that will be created.
**Solution:** Define an interface for creating an object, but let subclasses alter the type of objects that will be created.

```java
interface Transport {
    void deliver();
}

class Truck implements Transport {
    public void deliver() { System.out.println("Deliver by land in a box."); }
}

class Ship implements Transport {
    public void deliver() { System.out.println("Deliver by sea in a container."); }
}

abstract class Logistics {
    // Factory method
    abstract Transport createTransport();

    public void planDelivery() {
        Transport t = createTransport();
        t.deliver();
    }
}

class RoadLogistics extends Logistics {
    @Override
    Transport createTransport() { return new Truck(); }
}

class SeaLogistics extends Logistics {
    @Override
    Transport createTransport() { return new Ship(); }
}
```

#### 3. Builder Pattern
**Problem:** Constructing a complex object step by step.
**Solution:** Separate the construction of a complex object from its representation.

```java
public class Pizza {
    private String dough;
    private String sauce;
    private String topping;

    // Private constructor
    private Pizza(Builder builder) {
        this.dough = builder.dough;
        this.sauce = builder.sauce;
        this.topping = builder.topping;
    }

    public static class Builder {
        private String dough;
        private String sauce;
        private String topping;

        public Builder setDough(String dough) { this.dough = dough; return this; }
        public Builder setSauce(String sauce) { this.sauce = sauce; return this; }
        public Builder setTopping(String topping) { this.topping = topping; return this; }

        public Pizza build() {
            return new Pizza(this);
        }
    }
}

// Usage
Pizza pizza = new Pizza.Builder()
                .setDough("Thin Crust")
                .setSauce("Tomato")
                .setTopping("Cheese")
                .build();
```

### Structural Patterns
Focus on how classes and objects are composed to form larger structures.

#### 1. Adapter Pattern
**Problem:** Incompatible interfaces need to work together.
**Solution:** Create a wrapper that converts the interface of one class into another interface clients expect.

```java
// Existing Interface
interface LightningPhone {
    void recharge();
    void useLightning();
}

interface MicroUsbPhone {
    void recharge();
    void useMicroUsb();
}

class IPhone implements LightningPhone {
    private boolean connector;
    public void useLightning() { connector = true; System.out.println("Lightning connected"); }
    public void recharge() {
        if (connector) { System.out.println("Recharge started"); System.out.println("Recharge finished"); }
        else { System.out.println("Connect Lightning first"); }
    }
}

class Android implements MicroUsbPhone {
    private boolean connector;
    public void useMicroUsb() { connector = true; System.out.println("MicroUsb connected"); }
    public void recharge() {
        if (connector) { System.out.println("Recharge started"); System.out.println("Recharge finished"); }
        else { System.out.println("Connect MicroUsb first"); }
    }
}

// Adapter to use MicroUsb with Lightning
class LightningToMicroUsbAdapter implements MicroUsbPhone {
    private final LightningPhone lightningPhone;

    public LightningToMicroUsbAdapter(LightningPhone lightningPhone) {
        this.lightningPhone = lightningPhone;
    }

    public void useMicroUsb() {
        System.out.println("MicroUsb connected via LightningAdapter");
        lightningPhone.useLightning();
    }

    public void recharge() {
        lightningPhone.recharge();
    }
}
```

#### 2. Decorator Pattern
**Problem:** Add responsibilities to objects dynamically without subclassing.
**Solution:** Place the component inside another object that adds the behavior (wrapper).

```java
interface Coffee {
    String getDescription();
    double getCost();
}

class SimpleCoffee implements Coffee {
    public String getDescription() { return "Simple Coffee"; }
    public double getCost() { return 5.0; }
}

// Abstract Decorator
abstract class CoffeeDecorator implements Coffee {
    protected final Coffee decoratedCoffee;
    public CoffeeDecorator(Coffee c) { this.decoratedCoffee = c; }
    public String getDescription() { return decoratedCoffee.getDescription(); }
    public double getCost() { return decoratedCoffee.getCost(); }
}

class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee c) { super(c); }
    public String getDescription() { return super.getDescription() + ", Milk"; }
    public double getCost() { return super.getCost() + 1.5; }
}

class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee c) { super(c); }
    public String getDescription() { return super.getDescription() + ", Sugar"; }
    public double getCost() { return super.getCost() + 0.5; }
}

// Usage
Coffee myCoffee = new SimpleCoffee();
myCoffee = new MilkDecorator(myCoffee); // Add Milk
myCoffee = new SugarDecorator(myCoffee); // Add Sugar
```

#### 3. Proxy Pattern
**Problem:** Control access to an object (lazy loading, security, logging).
**Solution:** Use a proxy class that implements the same interface as the real object and delegates work to it.

```java
interface Image {
    void display();
}

class RealImage implements Image {
    private String filename;

    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk(filename); // Expensive operation
    }

    private void loadFromDisk(String filename) {
        System.out.println("Loading " + filename);
    }

    public void display() {
        System.out.println("Displaying " + filename);
    }
}

class ProxyImage implements Image {
    private RealImage realImage;
    private String filename;

    public ProxyImage(String filename) {
        this.filename = filename;
    }

    public void display() {
        if (realImage == null) {
            realImage = new RealImage(filename); // Lazy initialization
        }
        realImage.display();
    }
}
```

#### Other Structural Patterns
- **Facade:** Provides a simplified interface to a complex library or framework.
- **Composite:** Composes objects into tree structures to represent part-whole hierarchies (e.g., Files and Folders).
- **Bridge:** Decouples an abstraction from its implementation so the two can vary independently.

### Behavioral Patterns
Focus on communication between objects.

#### 1. Strategy Pattern
**Problem:** Define a family of algorithms, encapsulate each one, and make them interchangeable.
**Solution:** Define a common interface for all algorithms and implement them in separate classes.

```java
interface PaymentStrategy {
    void pay(int amount);
}

class CreditCardPayment implements PaymentStrategy {
    private String name;
    public CreditCardPayment(String name) { this.name = name; }
    public void pay(int amount) { System.out.println(amount + " paid with credit card"); }
}

class PayPalPayment implements PaymentStrategy {
    private String email;
    public PayPalPayment(String email) { this.email = email; }
    public void pay(int amount) { System.out.println(amount + " paid using PayPal"); }
}

class ShoppingCart {
    public void pay(PaymentStrategy paymentMethod) {
        int amount = 100; // simplified
        paymentMethod.pay(amount);
    }
}

// Usage
ShoppingCart cart = new ShoppingCart();
cart.pay(new CreditCardPayment("John Doe"));
cart.pay(new PayPalPayment("john@example.com"));
```

#### 2. Observer Pattern
**Problem:** Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified.
**Solution:** Subject maintains a list of Observers and notifies them.

```java
import java.util.ArrayList;
import java.util.List;

interface Observer {
    void update(String message);
}

class NewsAgency {
    private String news;
    private List<Observer> channels = new ArrayList<>();

    public void addObserver(Observer o) { channels.add(o); }
    public void removeObserver(Observer o) { channels.remove(o); }

    public void setNews(String news) {
        this.news = news;
        for (Observer o : channels) {
            o.update(this.news);
        }
    }
}

class NewsChannel implements Observer {
    private String name;
    public NewsChannel(String name) { this.name = name; }
    
    @Override
    public void update(String news) {
        System.out.println(name + " received news: " + news);
    }
}

// Usage
NewsAgency agency = new NewsAgency();
NewsChannel channel1 = new NewsChannel("Channel 1");
agency.addObserver(channel1);
agency.setNews("Breaking News!");
```

#### 3. Template Method Pattern
**Problem:** Define the skeleton of an algorithm in a superclass but let subclasses override specific steps.
**Solution:** Abstract class with a `final` template method calling abstract methods.

```java
abstract class Game {
    abstract void initialize();
    abstract void startPlay();
    abstract void endPlay();

    // Template method
    public final void play() {
        initialize();
        startPlay();
        endPlay();
    }
}

class Cricket extends Game {
    void initialize() { System.out.println("Cricket Initialized!"); }
    void startPlay() { System.out.println("Cricket Game Started!"); }
    void endPlay() { System.out.println("Cricket Game Finished!"); }
}
```

#### Other Behavioral Patterns
- **Command:** Encapsulates a request as an object, allowing parameterization of clients with queues or log requests.
- **Iterator:** Provides a way to access elements of a collection sequentially without exposing underlying representation.
- **State:** Allows an object to alter its behavior when its internal state changes.
- **Chain of Responsibility:** Passes a request along a chain of handlers (e.g., logging filters).

---

## 3. Clean Code Practices

Writing code that is easy to read, understand, and maintain is just as important as solving the problem.

### 1. Meaningful Naming
- **Classes:** Nouns, PascalCase (e.g., `User`, `RequestParser`).
- **Methods:** Verbs, camelCase (e.g., `getUser`, `calculateTotal`).
- **Variables:** Descriptive, camelCase (e.g., `maxRetryCount`, `isActive`).
- **Avoid:** Single letter names (except loop counters `i`, `j`), abbreviations (`usr`, `msg`), and magic numbers.

#### ❌ Bad
```java
int d; // elapsed time in days
public List<String> get() { ... }
```

#### ✅ Good
```java
int elapsedTimeInDays;
public List<String> getActiveUsers() { ... }
```

### 2. Method Size & Complexity
- **Small Methods:** A method should do one thing and do it well.
- **Lines of Code:** Ideally 10-20 lines. If it's longer, break it down.
- **Arguments:** Fewer arguments are better (0-3 is ideal). If you have more, consider passing an object.

### 3. Comments
- **Don't explain "What":** The code should explain what it does.
- **Explain "Why":** Explain the intent or reason behind a complex decision.
- **Avoid commented-out code:** Delete it. Git remembers history.

### 4. Exception Handling
- **Don't Swallow Exceptions:** Never catch an exception and do nothing.
- **Catch Specific Exceptions:** Avoid catching generic `Exception`.
- **Use Finally / Try-with-Resources:** Ensure resources are closed.

#### ❌ Bad
```java
try {
    file.read();
} catch (Exception e) {
    e.printStackTrace(); // Just logging stack trace is often not enough
}
```

#### ✅ Good
```java
try (BufferedReader br = new BufferedReader(new FileReader(path))) {
    return br.readLine();
} catch (IOException e) {
    throw new RuntimeException("Failed to read config file", e);
}
```

### 5. Consistency
- **Formatting:** Use consistent indentation (4 spaces or tabs).
- **Style:** Follow standard Java coding conventions (Google Java Style Guide).

---

**Good luck with your interview preparation!**
