# Pub-Sub System - Low Level Design

## Overview

A thread-safe, scalable Publish-Subscribe messaging system implemented in Java that decouples message producers (publishers) from consumers (subscribers) using a central broker.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              PUB-SUB ARCHITECTURE                               │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│   ┌────────────┐                                          ┌────────────┐       │
│   │ Publisher 1│──┐                                   ┌──▶│Subscriber A│       │
│   └────────────┘  │                                   │   └────────────┘       │
│                   │       ┌───────────────────┐       │                        │
│   ┌────────────┐  │       │                   │       │   ┌────────────┐       │
│   │ Publisher 2│──┼──────▶│   PubSubBroker    │───────┼──▶│Subscriber B│       │
│   └────────────┘  │       │                   │       │   └────────────┘       │
│                   │       │  ┌─────────────┐  │       │                        │
│   ┌────────────┐  │       │  │TopicRegistry│  │       │   ┌────────────┐       │
│   │ Publisher N│──┘       │  └─────────────┘  │       └──▶│Subscriber N│       │
│   └────────────┘          │  ┌─────────────┐  │           └────────────┘       │
│                           │  │ Subscription│  │                                │
│   ┌────────────┐          │  │  Manager    │  │           ┌────────────┐       │
│   │   Topic    │          │  └─────────────┘  │           │   Message  │       │
│   │  (sports)  │          │  ┌─────────────┐  │           │  Dispatcher│       │
│   │  (news)    │          │  │  Dispatcher │  │           │  (Async)   │       │
│   │  (weather) │          │  └─────────────┘  │           └────────────┘       │
│   └────────────┘          └───────────────────┘                                │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## Class Diagram

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                               CLASS DIAGRAM                                      │
├──────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  <<interface>>                <<interface>>              <<interface>>           │
│  ┌──────────────┐            ┌────────────────┐         ┌────────────────────┐  │
│  │ Subscriber<T>│            │Publisher<T>    │         │SubscriptionManager │  │
│  ├──────────────┤            ├────────────────┤         │        <T>         │  │
│  │+onMessage()  │            │+publish()      │         ├────────────────────┤  │
│  │+getId()      │            │+getId()        │         │+addSubscription()  │  │
│  │+onError()    │            └───────┬────────┘         │+removeSubscription()│ │
│  └──────┬───────┘                    │                  │+getSubscriptions() │  │
│         │                            │                  └─────────┬──────────┘  │
│         │ implements                 │ implements                 │implements   │
│         │                            │                            │             │
│  ┌──────┴───────┐            ┌───────┴────────┐         ┌─────────┴──────────┐  │
│  │ Logging      │            │DefaultPublisher│         │ Concurrent         │  │
│  │ Subscriber   │            └────────────────┘         │ SubscriptionManager│  │
│  ├──────────────┤                                       └────────────────────┘  │
│  │ Collecting   │                                                               │
│  │ Subscriber   │            <<interface>>                                      │
│  ├──────────────┤            ┌────────────────────┐                             │
│  │ Callback     │            │MessageDispatcher<T>│                             │
│  │ Subscriber   │            ├────────────────────┤                             │
│  ├──────────────┤            │+dispatch()         │                             │
│  │ Filtering    │            │+shutdown()         │                             │
│  │ Subscriber   │            │+isRunning()        │                             │
│  └──────────────┘            └─────────┬──────────┘                             │
│                                        │ implements                             │
│                              ┌─────────┴───────────┐                            │
│                              │                     │                            │
│                    ┌─────────┴──────┐    ┌────────┴───────┐                     │
│                    │Async           │    │Sync            │                     │
│                    │MessageDispatcher│   │MessageDispatcher│                    │
│                    └────────────────┘    └────────────────┘                     │
│                                                                                  │
│  ┌────────────────┐   ┌────────────────┐   ┌────────────────────────────────┐   │
│  │     Topic      │   │   Message<T>   │   │       PubSubBroker<T>          │   │
│  ├────────────────┤   ├────────────────┤   ├────────────────────────────────┤   │
│  │-name: String   │   │-id: String     │   │-topicRegistry: TopicRegistry   │   │
│  ├────────────────┤   │-topic: Topic   │   │-subscriptionManager            │   │
│  │+getName()      │   │-payload: T     │   │-messageDispatcher              │   │
│  │+equals()       │   │-timestamp      │   ├────────────────────────────────┤   │
│  │+hashCode()     │   │-publisherId    │   │+subscribe()                    │   │
│  └────────────────┘   ├────────────────┤   │+unsubscribe()                  │   │
│                       │+builder()      │   │+publish()                      │   │
│  ┌────────────────┐   └────────────────┘   │+createTopic()                  │   │
│  │ Subscription<T>│                        │+shutdown()                     │   │
│  ├────────────────┤                        └────────────────────────────────┘   │
│  │-id: String     │                                                             │
│  │-topic: Topic   │   ┌────────────────┐                                        │
│  │-subscriber     │   │ TopicRegistry  │                                        │
│  │-active: boolean│   ├────────────────┤                                        │
│  ├────────────────┤   │+getOrCreate()  │                                        │
│  │+deactivate()   │   │+exists()       │                                        │
│  │+isActive()     │   │+getAllTopics() │                                        │
│  └────────────────┘   └────────────────┘                                        │
│                                                                                  │
└──────────────────────────────────────────────────────────────────────────────────┘
```

## Core Components

### 1. Models

| Class | Purpose |
|-------|---------|
| `Topic` | Immutable value object representing a named channel |
| `Message<T>` | Immutable message with ID, payload, timestamp, topic reference |
| `Subscription<T>` | Binding between a subscriber and topic with activation state |

### 2. Interfaces

| Interface | Purpose | SOLID Principle |
|-----------|---------|-----------------|
| `Subscriber<T>` | Contract for message receivers | ISP - Single method |
| `Publisher<T>` | Contract for message senders | SRP - Only publishing |
| `MessageDispatcher<T>` | Strategy for delivery mechanism | OCP, DIP |
| `SubscriptionManager<T>` | Abstraction for subscription storage | DIP |

### 3. Implementations

| Class | Purpose |
|-------|---------|
| `PubSubBroker<T>` | Central facade coordinating all operations |
| `ConcurrentSubscriptionManager<T>` | Thread-safe subscription storage using ConcurrentHashMap |
| `AsyncMessageDispatcher<T>` | Non-blocking delivery via thread pool |
| `SyncMessageDispatcher<T>` | Synchronous delivery (for testing) |
| `TopicRegistry` | Manages Topic instances (Flyweight pattern) |
| `DefaultPublisher<T>` | Standard publisher implementation |

### 4. Subscriber Implementations

| Class | Purpose |
|-------|---------|
| `LoggingSubscriber<T>` | Logs messages to console |
| `CollectingSubscriber<T>` | Collects messages for batch processing |
| `CallbackSubscriber<T>` | Invokes lambda/callback for each message |
| `FilteringSubscriber<T>` | Decorator that filters messages |

## Design Patterns Used

| Pattern | Component | Benefit |
|---------|-----------|---------|
| **Observer** | Core pub-sub mechanism | Loose coupling between publishers and subscribers |
| **Mediator** | `PubSubBroker` | Centralizes communication, prevents N:M coupling |
| **Strategy** | `MessageDispatcher` | Swappable delivery algorithms |
| **Decorator** | `FilteringSubscriber` | Extensible message filtering |
| **Builder** | `Message.Builder` | Flexible message construction |
| **Flyweight** | `TopicRegistry` | Memory-efficient topic management |
| **Facade** | `PubSubBroker` | Simplified API for complex subsystem |

## SOLID Principles Application

### Single Responsibility Principle (SRP)
- `SubscriptionManager` - Only manages subscriptions
- `MessageDispatcher` - Only handles delivery
- `TopicRegistry` - Only manages topic instances

### Open/Closed Principle (OCP)
- New dispatchers can be added without modifying `PubSubBroker`
- New subscriber types can be created without changing existing code

### Liskov Substitution Principle (LSP)
- All `Subscriber<T>` implementations are fully interchangeable
- All `MessageDispatcher<T>` implementations work identically

### Interface Segregation Principle (ISP)
- `Subscriber` has only essential methods (`onMessage`, `getId`)
- No fat interfaces forcing unused implementations

### Dependency Inversion Principle (DIP)
- `PubSubBroker` depends on `SubscriptionManager` and `MessageDispatcher` interfaces
- All dependencies are injected via constructor

## Thread Safety

| Component | Mechanism |
|-----------|-----------|
| `ConcurrentSubscriptionManager` | `ConcurrentHashMap` with atomic operations |
| `AsyncMessageDispatcher` | `ThreadPoolExecutor` with bounded queue |
| `Topic`, `Message` | Immutable - inherently thread-safe |
| `Subscription.active` | `volatile` flag |
| Statistics counters | `AtomicLong` |

## Message Flow

```
Publisher                    PubSubBroker                   Subscribers
    │                            │                              │
    │  publish(topic, payload)   │                              │
    │───────────────────────────▶│                              │
    │                            │                              │
    │                            │  getSubscriptions(topic)     │
    │                            │───────────────────────────┐  │
    │                            │                           │  │
    │                            │◀──────────────────────────┘  │
    │                            │  Set<Subscription>           │
    │                            │                              │
    │                            │  dispatch(msg, subscriptions)│
    │                            │──────────────────────────────│
    │                            │     [Async Thread Pool]      │
    │                            │                              │
    │                            │              onMessage(msg)  │
    │                            │─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ▶│
    │                            │              onMessage(msg)  │
    │                            │─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ▶│
    │                            │              onMessage(msg)  │
    │                            │─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ▶│
```

## Extension Points

### Adding New Delivery Strategy

```java
public class PriorityMessageDispatcher<T> implements MessageDispatcher<T> {
    @Override
    public void dispatch(Message<T> message, Set<Subscription<T>> subscriptions) {
        // Custom priority-based delivery
    }
}

// Usage
PubSubBroker<String> broker = new PubSubBroker<>(
    new TopicRegistry(),
    new ConcurrentSubscriptionManager<>(),
    new PriorityMessageDispatcher<>()  // Inject new strategy
);
```

### Adding Message Filtering

```java
// Decorator pattern for filtering
FilteringSubscriber<String> filtered = new FilteringSubscriber<>(
    originalSubscriber,
    msg -> msg.getPayload().contains("important")
);
broker.subscribe("alerts", filtered);
```

### Adding Dead Letter Queue

```java
public class DeadLetterAwareDispatcher<T> implements MessageDispatcher<T> {
    private final MessageDispatcher<T> delegate;
    private final Queue<Message<T>> deadLetterQueue;
    
    @Override
    public void dispatch(Message<T> message, Set<Subscription<T>> subscriptions) {
        try {
            delegate.dispatch(message, subscriptions);
        } catch (Exception e) {
            deadLetterQueue.add(message);
        }
    }
}
```

## Project Structure

```
pubsub/
├── PubSubBroker.java              # Main facade
├── Main.java                      # Usage demonstration
├── models/
│   ├── Topic.java                 # Topic value object
│   ├── Message.java               # Message with builder
│   └── Subscription.java          # Subscription binding
├── interfaces/
│   ├── Subscriber.java            # Subscriber contract
│   ├── Publisher.java             # Publisher contract
│   ├── MessageDispatcher.java     # Delivery strategy
│   └── SubscriptionManager.java   # Subscription management
├── impl/
│   ├── AsyncMessageDispatcher.java    # Async delivery
│   ├── SyncMessageDispatcher.java     # Sync delivery
│   ├── ConcurrentSubscriptionManager.java  # Thread-safe manager
│   ├── TopicRegistry.java             # Topic registry
│   └── DefaultPublisher.java          # Default publisher
├── subscribers/
│   ├── LoggingSubscriber.java         # Logs messages
│   ├── CollectingSubscriber.java      # Collects messages
│   ├── CallbackSubscriber.java        # Lambda-based
│   └── FilteringSubscriber.java       # Decorator filter
├── exceptions/
│   ├── PubSubException.java           # Base exception
│   ├── TopicNotFoundException.java    # Topic not found
│   ├── SubscriptionException.java     # Subscription errors
│   └── MessageDeliveryException.java  # Delivery failures
└── test/
    ├── PubSubBrokerTest.java          # Broker tests
    └── SubscriptionManagerTest.java   # Manager tests
```

## Usage Examples

### Basic Usage

```java
// Create broker
PubSubBroker<String> broker = new PubSubBroker<>();

// Subscribe
broker.subscribe("news", new LoggingSubscriber<>("Reader"));

// Publish
broker.publish("news", "Breaking: Hello World!");

// Shutdown
broker.shutdown();
```

### With Publisher Interface

```java
Publisher<String> publisher = new DefaultPublisher<>("service-1", broker);
publisher.publish("events", "User logged in");
```

### Filtered Subscription

```java
FilteringSubscriber<String> urgent = new FilteringSubscriber<>(
    new LoggingSubscriber<>("Urgent"),
    msg -> msg.getPayload().contains("URGENT")
);
broker.subscribe("logs", urgent);
```

### Callback-based

```java
broker.subscribe("orders", new CallbackSubscriber<>(
    msg -> processOrder(msg.getPayload())
));
```

## Performance Considerations

1. **Async Delivery**: Default `AsyncMessageDispatcher` uses thread pool for non-blocking delivery
2. **Bounded Queue**: Prevents OOM with backpressure (CallerRunsPolicy)
3. **ConcurrentHashMap**: Lock-free reads for subscription lookups
4. **Immutable Objects**: `Topic` and `Message` are thread-safe without locking
5. **Lazy Topic Creation**: Topics created on-demand via `getOrCreate`

## Running Tests

```bash
# Compile
javac -d out pubsub/**/*.java

# Run main demo
java -cp out pubsub.Main

# Run tests
java -cp out pubsub.test.PubSubBrokerTest
java -cp out pubsub.test.SubscriptionManagerTest
```



