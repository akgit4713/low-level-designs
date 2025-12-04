# Task Management System - Low-Level Design

## Table of Contents
1. [Overview](#overview)
2. [Requirements](#requirements)
3. [Design Principles](#design-principles)
4. [Design Patterns Used](#design-patterns-used)
5. [Class Diagram](#class-diagram)
6. [Package Structure](#package-structure)
7. [Core Components](#core-components)
8. [Class Relationships](#class-relationships)
9. [Data Flow](#data-flow)
10. [Concurrency Handling](#concurrency-handling)
11. [Extensibility](#extensibility)
12. [Usage Example](#usage-example)

---

## Overview

The Task Management System is a scalable, extensible platform that allows users to create, manage, and track tasks. Users can assign tasks to others, set reminders, search/filter tasks based on various criteria, and view their task history. The system is designed for concurrent access with proper data consistency guarantees.

---

## Requirements

### Functional Requirements
- Users can create, update, and delete tasks
- Each task has title, description, due date, priority, and status (pending, in progress, completed)
- Users can assign tasks to other users
- Users can set reminders for tasks
- Search and filter tasks by priority, due date, assigned user, status
- Mark tasks as completed and view task history
- Track task lifecycle changes

### Non-Functional Requirements
- Thread-safe operations with concurrent access support
- Extensible design for new features
- Loosely coupled components
- Testable architecture
- Data consistency guarantees

---

## Design Principles

### SOLID Principles Applied

#### 1. Single Responsibility Principle (SRP)
Each class has one reason to change:
- `Task` - Only task data management
- `TaskServiceImpl` - Only task CRUD operations
- `SearchService` - Only search/filter operations
- `ReminderService` - Only reminder management
- `TaskHistoryService` - Only history tracking

#### 2. Open/Closed Principle (OCP)
Classes are open for extension but closed for modification:
- New `SearchCriteria` implementations can be added without changing existing code
- New `NotificationStrategy` implementations extend notification capabilities
- New `TaskObserver` implementations can be plugged in

#### 3. Liskov Substitution Principle (LSP)
Derived classes can substitute base classes:
- Any `SearchCriteria` implementation works with `SearchService`
- Any `NotificationStrategy` implementation works with `ReminderService`
- Service interfaces can be swapped (e.g., `InMemoryTaskRepository` → `DatabaseTaskRepository`)

#### 4. Interface Segregation Principle (ISP)
Interfaces are focused and cohesive:
- `TaskService` - Only task CRUD operations
- `UserService` - Only user operations
- `SearchService` - Only search/filter operations
- `ReminderService` - Only reminder operations
- `TaskObserver` - Only task event handling

#### 5. Dependency Inversion Principle (DIP)
High-level modules depend on abstractions:
- `TaskServiceImpl` depends on `TaskRepository` interface
- `SearchServiceImpl` depends on `SearchCriteria` interface
- `ReminderServiceImpl` depends on `NotificationStrategy` interface

---

## Design Patterns Used

### 1. Strategy Pattern
**Purpose:** Define a family of algorithms, encapsulate each one, and make them interchangeable.

**Applied to:**
- **Search Criteria:** `SearchCriteria` with implementations like `PrioritySearchCriteria`, `StatusSearchCriteria`, `DueDateSearchCriteria`, `AssigneeSearchCriteria`
- **Notification:** `NotificationStrategy` with `ConsoleNotificationStrategy`, `EmailNotificationStrategy`

```
┌─────────────────────────────────┐
│       SearchCriteria            │ <<interface>>
├─────────────────────────────────┤
│ + matches(task): boolean        │
│ + getDescription(): String      │
└────────────┬────────────────────┘
             │
     ┌───────┴───────┬─────────────────────┬───────────────────┐
     ▼               ▼                     ▼                   ▼
┌─────────────┐ ┌──────────────┐ ┌────────────────────┐ ┌──────────────┐
│Priority     │ │Status        │ │DueDate             │ │Assignee      │
│SearchCrit.  │ │SearchCrit.   │ │SearchCriteria      │ │SearchCriteria│
└─────────────┘ └──────────────┘ └────────────────────┘ └──────────────┘
```

### 2. Observer Pattern
**Purpose:** Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified.

**Applied to:**
- `TaskObserver` interface observed by `TaskHistoryObserver` and `NotificationObserver`
- `TaskServiceImpl` notifies observers on task events

```
┌─────────────────────────────────┐
│       TaskServiceImpl           │
│     (Subject/Observable)        │
├─────────────────────────────────┤
│ - observers: List<TaskObserver> │
│ + registerObserver()            │
│ + notifyTaskCreated()           │
│ + notifyTaskUpdated()           │
└────────────┬────────────────────┘
             │ notifies
     ┌───────┴───────┐
     ▼               ▼
┌─────────────────┐ ┌────────────────────┐
│TaskHistory      │ │Notification        │
│Observer         │ │Observer            │
└─────────────────┘ └────────────────────┘
```

### 3. Composite Pattern
**Purpose:** Compose objects into tree structures and treat individual objects and compositions uniformly.

**Applied to:**
- `CompositeSearchCriteria` - Combines multiple search criteria with AND/OR logic

```
┌─────────────────────────────────┐
│        SearchCriteria           │ <<interface>>
├─────────────────────────────────┤
│ + matches(task)                 │
└────────────┬────────────────────┘
             │
     ┌───────┴───────┬────────────────────────┐
     ▼               ▼                        ▼
┌─────────────┐ ┌──────────────────┐ ┌───────────────────────┐
│Priority     │ │Status            │ │CompositeSearch        │
│Search       │ │Search            │ │Criteria               │
│Criteria     │ │Criteria          │ │                       │
└─────────────┘ └──────────────────┘ │- criteria: List<>     │
                                     │- operator: AND/OR     │
                                     │+ addCriteria()        │
                                     └───────────────────────┘
```

### 4. Builder Pattern
**Purpose:** Create objects without specifying exact class to be created.

**Applied to:**
- `Task.Builder` - Fluent builder for Task objects with validation
- `Reminder.Builder` - Builder for Reminder objects

```java
Task task = Task.builder()
    .title("Complete LLD")
    .description("Design task management system")
    .priority(TaskPriority.HIGH)
    .dueDate(LocalDateTime.now().plusDays(7))
    .createdBy(userId)
    .build();
```

### 5. Factory Pattern
**Purpose:** Create objects without specifying exact class to be created.

**Applied to:**
- `TaskFactory` - Creates Task objects
- `TaskManagementSystemFactory` - Creates complete system with all dependencies wired

```
┌─────────────────────────────────┐
│  TaskManagementSystemFactory    │
├─────────────────────────────────┤
│ + createDefaultSystem()         │
│ + createWithCustomNotification()│
└─────────────────────────────────┘
        │ creates
        ▼
┌─────────────────────────────────┐
│     TaskManagementSystem        │
├─────────────────────────────────┤
│ - taskService                   │
│ - userService                   │
│ - searchService                 │
│ - reminderService               │
│ - historyService                │
└─────────────────────────────────┘
```

### 6. Repository Pattern
**Purpose:** Abstracts data storage from business logic.

**Applied to:**
- `TaskRepository` - Data access for tasks
- `UserRepository` - Data access for users
- `ReminderRepository` - Data access for reminders

---

## Class Diagram

```
┌──────────────────────────────────────────────────────────────────────────────────────┐
│                            TASK MANAGEMENT SYSTEM - CLASS DIAGRAM                     │
└──────────────────────────────────────────────────────────────────────────────────────┘

╔═══════════════════════════════════════════════════════════════════════════════════════╗
║                                      ENUMS                                            ║
╚═══════════════════════════════════════════════════════════════════════════════════════╝

┌─────────────────────┐     ┌─────────────────────┐
│  <<enum>> TaskStatus│     │<<enum>> TaskPriority│
├─────────────────────┤     ├─────────────────────┤
│ PENDING             │     │ LOW (1)             │
│ IN_PROGRESS         │     │ MEDIUM (2)          │
│ COMPLETED           │     │ HIGH (3)            │
│ CANCELLED           │     │ CRITICAL (4)        │
├─────────────────────┤     ├─────────────────────┤
│ + canTransitionTo() │     │ + getLevel()        │
│ + getValidTransit() │     │ + fromLevel()       │
└─────────────────────┘     └─────────────────────┘

╔═══════════════════════════════════════════════════════════════════════════════════════╗
║                                      MODELS                                            ║
╚═══════════════════════════════════════════════════════════════════════════════════════╝

┌─────────────────────────┐     ┌─────────────────────────┐     ┌─────────────────────────┐
│         Task            │     │         User            │     │       Reminder          │
├─────────────────────────┤     ├─────────────────────────┤     ├─────────────────────────┤
│ - id: String            │     │ - id: String            │     │ - id: String            │
│ - title: String         │     │ - username: String      │     │ - taskId: String        │
│ - description: String   │     │ - email: String         │     │ - userId: String        │
│ - dueDate: LocalDateTime│     │ - name: String          │     │ - reminderTime: LDT     │
│ - priority: TaskPriority│     │ - createdAt: LocalDT    │     │ - message: String       │
│ - status: TaskStatus    │     ├─────────────────────────┤     │ - triggered: boolean    │
│ - createdBy: String     │     │ + getUsername()         │     ├─────────────────────────┤
│ - assignedTo: String    │     │ + getEmail()            │     │ + markTriggered()       │
│ - createdAt: LocalDT    │     └─────────────────────────┘     │ + isTriggered()         │
│ - updatedAt: LocalDT    │                                     └─────────────────────────┘
│ - completedAt: LocalDT  │
├─────────────────────────┤     ┌─────────────────────────┐
│ + updateStatus()        │     │     TaskHistory         │
│ + assignTo()            │     ├─────────────────────────┤
│ + isOverdue()           │     │ - id: String            │
│ + static builder()      │     │ - taskId: String        │
└─────────────────────────┘     │ - action: HistoryAction │
                                │ - previousValue: String │
                                │ - newValue: String      │
                                │ - changedBy: String     │
                                │ - timestamp: LocalDT    │
                                └─────────────────────────┘

╔═══════════════════════════════════════════════════════════════════════════════════════╗
║                                     SERVICES                                           ║
╚═══════════════════════════════════════════════════════════════════════════════════════╝

┌─────────────────────────┐   ┌─────────────────────────┐   ┌─────────────────────────┐
│ <<interface>>           │   │ <<interface>>           │   │ <<interface>>           │
│    TaskService          │   │    UserService          │   │    SearchService        │
├─────────────────────────┤   ├─────────────────────────┤   ├─────────────────────────┤
│ + createTask()          │   │ + createUser()          │   │ + search(criteria)      │
│ + updateTask()          │   │ + getUserById()         │   │ + searchByPriority()    │
│ + deleteTask()          │   │ + getUserByUsername()   │   │ + searchByStatus()      │
│ + getTaskById()         │   │ + getAllUsers()         │   │ + searchByDueDate()     │
│ + getTasksByUser()      │   │ + deleteUser()          │   │ + searchByAssignee()    │
│ + assignTask()          │   └───────────┬─────────────┘   └───────────┬─────────────┘
│ + updateStatus()        │               │                             │
│ + completeTask()        │               ▼                             ▼
│ + registerObserver()    │   ┌─────────────────────────┐   ┌─────────────────────────┐
└───────────┬─────────────┘   │  InMemoryUserService    │   │  SearchServiceImpl      │
            │                 ├─────────────────────────┤   ├─────────────────────────┤
            ▼                 │ - userRepository        │   │ - taskRepository        │
┌─────────────────────────┐   │ (implements interface)  │   │ (implements interface)  │
│  TaskServiceImpl        │   └─────────────────────────┘   └─────────────────────────┘
├─────────────────────────┤
│ - taskRepository        │   ┌─────────────────────────┐   ┌─────────────────────────┐
│ - userService           │   │ <<interface>>           │   │ <<interface>>           │
│ - observers: List       │   │   ReminderService       │   │  TaskHistoryService     │
├─────────────────────────┤   ├─────────────────────────┤   ├─────────────────────────┤
│ + registerObserver()    │   │ + createReminder()      │   │ + recordHistory()       │
│ + notifyTaskCreated()   │   │ + deleteReminder()      │   │ + getHistoryForTask()   │
│ + notifyTaskUpdated()   │   │ + getRemindersForTask() │   │ + getHistoryByUser()    │
└─────────────────────────┘   │ + checkAndTrigger()     │   │ + getRecentHistory()    │
                              └───────────┬─────────────┘   └───────────┬─────────────┘
                                          │                             │
                                          ▼                             ▼
                              ┌─────────────────────────┐   ┌─────────────────────────┐
                              │ ReminderServiceImpl     │   │TaskHistoryServiceImpl   │
                              ├─────────────────────────┤   ├─────────────────────────┤
                              │ - reminderRepository    │   │ - historyRepository     │
                              │ - notificationStrategy  │   │ (implements interface)  │
                              │ (implements interface)  │   └─────────────────────────┘
                              └─────────────────────────┘

╔═══════════════════════════════════════════════════════════════════════════════════════╗
║                                    STRATEGIES                                          ║
╚═══════════════════════════════════════════════════════════════════════════════════════╝

       SEARCH CRITERIA                              NOTIFICATION STRATEGIES
    
┌─────────────────────────┐                    ┌─────────────────────────┐
│ <<interface>>           │                    │ <<interface>>           │
│   SearchCriteria        │                    │ NotificationStrategy    │
├─────────────────────────┤                    ├─────────────────────────┤
│ + matches(task)         │                    │ + notify(user, reminder)│
│ + getDescription()      │                    │ + getType()             │
└───────────┬─────────────┘                    └───────────┬─────────────┘
            │                                              │
    ┌───────┼───────┬───────────┬─────────┐       ┌───────┴───────┐
    ▼       ▼       ▼           ▼         ▼       ▼               ▼
┌───────┐┌───────┐┌───────┐┌───────┐┌─────────┐┌───────────┐┌───────────┐
│Priority││Status ││DueDate││Assign.││Composite││Console    ││Email      │
│Search  ││Search ││Search ││Search ││Search   ││Notification││Notification│
│Criteria││Crit.  ││Crit.  ││Crit.  ││Criteria ││Strategy   ││Strategy   │
└────────┘└───────┘└───────┘└───────┘└─────────┘└───────────┘└───────────┘

╔═══════════════════════════════════════════════════════════════════════════════════════╗
║                                     OBSERVERS                                          ║
╚═══════════════════════════════════════════════════════════════════════════════════════╝

┌─────────────────────────────────────┐
│ <<interface>> TaskObserver          │
├─────────────────────────────────────┤
│ + onTaskCreated(task, user)         │
│ + onTaskUpdated(task, field, old)   │
│ + onTaskDeleted(task, user)         │
│ + onTaskAssigned(task, from, to)    │
│ + onTaskCompleted(task, user)       │
└─────────────────┬───────────────────┘
                  │
          ┌───────┴───────┐
          ▼               ▼
┌─────────────────────┐ ┌─────────────────────┐
│TaskHistory          │ │Notification         │
│Observer             │ │Observer             │
├─────────────────────┤ ├─────────────────────┤
│- historyService     │ │- notificationStrategy│
├─────────────────────┤ ├─────────────────────┤
│+ onTaskCreated()    │ │+ onTaskAssigned()   │
│+ onTaskUpdated()    │ │+ onTaskCompleted()  │
│+ onTaskAssigned()   │ └─────────────────────┘
└─────────────────────┘

╔═══════════════════════════════════════════════════════════════════════════════════════╗
║                                   REPOSITORIES                                         ║
╚═══════════════════════════════════════════════════════════════════════════════════════╝

┌─────────────────────────┐
│ <<interface>>           │
│   Repository<T, ID>     │
├─────────────────────────┤
│ + save(entity)          │
│ + findById(id)          │
│ + findAll()             │
│ + delete(id)            │
│ + existsById(id)        │
└───────────┬─────────────┘
            │
    ┌───────┼───────┬───────────┐
    ▼       ▼       ▼           ▼
┌──────────┐┌──────────┐┌──────────┐┌──────────┐
│InMemory  ││InMemory  ││InMemory  ││InMemory  │
│Task      ││User      ││Reminder  ││History   │
│Repository││Repository││Repository││Repository│
└──────────┘└──────────┘└──────────┘└──────────┘
```

---

## Package Structure

```
taskmanagement/
│
├── Main.java                              # Entry point with demo
│
├── enums/                                 # Enumerations
│   ├── TaskStatus.java                    # Task status values with transitions
│   ├── TaskPriority.java                  # Task priority levels
│   └── HistoryAction.java                 # History action types
│
├── models/                                # Domain entities
│   ├── Task.java                          # Task entity with builder
│   ├── User.java                          # User entity
│   ├── Reminder.java                      # Reminder entity
│   └── TaskHistory.java                   # Task history record
│
├── exceptions/                            # Custom exceptions
│   ├── TaskException.java                 # Task-related exceptions
│   ├── UserException.java                 # User-related exceptions
│   └── ReminderException.java             # Reminder-related exceptions
│
├── repositories/                          # Data access interfaces
│   ├── Repository.java                    # Generic repository interface
│   ├── TaskRepository.java                # Task-specific repository interface
│   └── impl/                              # Repository implementations
│       ├── InMemoryTaskRepository.java
│       ├── InMemoryUserRepository.java
│       ├── InMemoryReminderRepository.java
│       └── InMemoryHistoryRepository.java
│
├── services/                              # Business logic interfaces
│   ├── TaskService.java                   # Task operations interface
│   ├── UserService.java                   # User operations interface
│   ├── SearchService.java                 # Search/filter interface
│   ├── ReminderService.java               # Reminder operations interface
│   ├── TaskHistoryService.java            # History operations interface
│   └── impl/                              # Service implementations
│       ├── TaskServiceImpl.java
│       ├── UserServiceImpl.java
│       ├── SearchServiceImpl.java
│       ├── ReminderServiceImpl.java
│       └── TaskHistoryServiceImpl.java
│
├── strategies/                            # Strategy implementations
│   ├── search/                            # Search criteria strategies
│   │   ├── SearchCriteria.java
│   │   ├── PrioritySearchCriteria.java
│   │   ├── StatusSearchCriteria.java
│   │   ├── DueDateSearchCriteria.java
│   │   ├── AssigneeSearchCriteria.java
│   │   └── CompositeSearchCriteria.java
│   └── notification/                      # Notification strategies
│       ├── NotificationStrategy.java
│       ├── ConsoleNotificationStrategy.java
│       └── EmailNotificationStrategy.java
│
├── observers/                             # Event observers
│   ├── TaskObserver.java                  # Observer interface
│   ├── TaskHistoryObserver.java           # Records task history
│   └── NotificationObserver.java          # Sends notifications
│
└── factories/                             # Object creation
    ├── TaskFactory.java
    └── TaskManagementSystemFactory.java   # Creates complete system
```

---

## Core Components

### 1. Models

| Class | Responsibility | Key Attributes |
|-------|---------------|----------------|
| `Task` | Represents a task in the system | id, title, description, dueDate, priority, status, createdBy, assignedTo |
| `User` | Represents a user | id, username, email, name |
| `Reminder` | Represents a reminder for a task | id, taskId, userId, reminderTime, message, triggered |
| `TaskHistory` | Records task changes | id, taskId, action, previousValue, newValue, changedBy, timestamp |

### 2. Services

| Interface | Implementation | Responsibility |
|-----------|---------------|----------------|
| `TaskService` | `TaskServiceImpl` | CRUD operations for tasks, assignment, status changes |
| `UserService` | `UserServiceImpl` | CRUD operations for users |
| `SearchService` | `SearchServiceImpl` | Search and filter tasks by various criteria |
| `ReminderService` | `ReminderServiceImpl` | Manage reminders, trigger notifications |
| `TaskHistoryService` | `TaskHistoryServiceImpl` | Track and query task history |

### 3. Strategies

| Strategy Type | Purpose | Implementations |
|--------------|---------|-----------------|
| `SearchCriteria` | Filter tasks by criteria | Priority, Status, DueDate, Assignee, Composite |
| `NotificationStrategy` | Send notifications | Console, Email |

### 4. Observers

| Observer | Trigger | Action |
|----------|---------|--------|
| `TaskHistoryObserver` | Task created, updated, deleted, assigned, completed | Records history entry |
| `NotificationObserver` | Task assigned, completed | Notifies relevant users |

---

## Class Relationships

### Aggregation (Has-A, Weak Ownership)

```
TaskServiceImpl ◇────────> TaskRepository
                ◇────────> UserService
                ◇────────> List<TaskObserver>

SearchServiceImpl ◇────────> TaskRepository

ReminderServiceImpl ◇────────> ReminderRepository
                    ◇────────> NotificationStrategy

CompositeSearchCriteria ◇────────> List<SearchCriteria>
```

### Composition (Has-A, Strong Ownership)

```
Task ●────────> TaskStatus (enum, value type)
     ●────────> TaskPriority (enum, value type)

TaskHistory ●────────> HistoryAction (enum, value type)

TaskManagementSystem ●────────> TaskService
                     ●────────> UserService
                     ●────────> SearchService
                     ●────────> ReminderService
                     ●────────> TaskHistoryService
```

### Association (Uses)

```
TaskServiceImpl ─────> User (via UserService)
                ─────> Task (manages)

TaskHistoryObserver ─────> TaskHistoryService
                    ─────> TaskHistory (creates)
```

### Realization (Implements Interface)

```
TaskServiceImpl ─ ─ ─ ▷ TaskService
UserServiceImpl ─ ─ ─ ▷ UserService
SearchServiceImpl ─ ─ ─ ▷ SearchService
ReminderServiceImpl ─ ─ ─ ▷ ReminderService

PrioritySearchCriteria ─ ─ ─ ▷ SearchCriteria
StatusSearchCriteria ─ ─ ─ ▷ SearchCriteria
DueDateSearchCriteria ─ ─ ─ ▷ SearchCriteria
AssigneeSearchCriteria ─ ─ ─ ▷ SearchCriteria
CompositeSearchCriteria ─ ─ ─ ▷ SearchCriteria

ConsoleNotificationStrategy ─ ─ ─ ▷ NotificationStrategy
EmailNotificationStrategy ─ ─ ─ ▷ NotificationStrategy

TaskHistoryObserver ─ ─ ─ ▷ TaskObserver
NotificationObserver ─ ─ ─ ▷ TaskObserver
```

---

## Data Flow

### Task Creation Flow

```
1. User calls TaskService.createTask(title, description, priority, dueDate, createdBy)
                    │
                    ▼
2. TaskServiceImpl validates user exists via UserService
                    │
                    ▼
3. Creates Task object using Builder and saves to TaskRepository
                    │
                    ▼
4. Notifies all registered TaskObservers via onTaskCreated()
                    │
        ┌───────────┴───────────┐
        ▼                       ▼
5a. TaskHistoryObserver      5b. NotificationObserver
    records CREATE event         (optional notification)
    │
    ▼
6. TaskHistoryService.recordHistory(taskId, CREATE, null, taskDetails)
    │
    ▼
7. Returns created Task
```

### Task Search Flow

```
1. User calls SearchService.search(criteria)
                    │
                    ▼
2. SearchServiceImpl retrieves all tasks from TaskRepository
                    │
                    ▼
3. For each task:
   │
   ├─▶ Calls criteria.matches(task)
   │       │
   │       ▼
   │   If CompositeSearchCriteria:
   │       - Evaluates all child criteria
   │       - Combines with AND/OR operator
   │
   └─▶ If matches, add to result list
                    │
                    ▼
4. Returns filtered list of tasks
```

### Reminder Trigger Flow

```
1. Scheduler/User calls ReminderService.checkAndTriggerReminders()
                    │
                    ▼
2. ReminderServiceImpl retrieves all untriggered reminders
                    │
                    ▼
3. For each reminder where reminderTime <= now:
   │
   ├─▶ Fetch associated Task and User
   │
   ├─▶ Call NotificationStrategy.notify(user, reminder, task)
   │       │
   │       ▼
   │   ConsoleNotificationStrategy prints to console
   │   (or EmailNotificationStrategy sends email)
   │
   └─▶ Mark reminder as triggered
                    │
                    ▼
4. Returns list of triggered reminders
```

---

## Concurrency Handling

### Thread-Safe Components

```java
// Task uses ReentrantReadWriteLock for status updates
public class Task {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    public void updateStatus(TaskStatus newStatus) {
        lock.writeLock().lock();
        try {
            if (!this.status.canTransitionTo(newStatus)) {
                throw new TaskException("Invalid status transition");
            }
            this.status = newStatus;
            this.updatedAt = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

### Repository Thread Safety

- All repositories use `ConcurrentHashMap` for thread-safe storage
- Atomic operations for ID generation using `AtomicLong`
- Read-write locks for complex operations

### Service Thread Safety

- `TaskServiceImpl` uses synchronized blocks for observer notification
- Search operations use read locks for consistent results
- Status transitions are atomic with validation

---

## Extensibility

### Adding a New Search Criteria

1. Create a new class implementing `SearchCriteria`:

```java
public class TitleSearchCriteria implements SearchCriteria {
    private final String titlePattern;
    
    public TitleSearchCriteria(String titlePattern) {
        this.titlePattern = titlePattern.toLowerCase();
    }
    
    @Override
    public boolean matches(Task task) {
        return task.getTitle().toLowerCase().contains(titlePattern);
    }
    
    @Override
    public String getDescription() {
        return "Tasks with title containing: " + titlePattern;
    }
}
```

2. Use it directly with `SearchService`:

```java
List<Task> tasks = searchService.search(new TitleSearchCriteria("urgent"));
```

### Adding a New Notification Strategy

1. Implement `NotificationStrategy`:

```java
public class SlackNotificationStrategy implements NotificationStrategy {
    private final SlackClient slackClient;
    
    @Override
    public void notify(User user, Reminder reminder, Task task) {
        slackClient.sendMessage(user.getSlackId(), 
            "Reminder: " + reminder.getMessage() + " for task: " + task.getTitle());
    }
    
    @Override
    public String getType() {
        return "SLACK";
    }
}
```

2. Inject into `ReminderServiceImpl`:

```java
ReminderService reminderService = new ReminderServiceImpl(
    reminderRepository, 
    new SlackNotificationStrategy(slackClient)
);
```

### Adding a New Observer

1. Implement `TaskObserver`:

```java
public class AuditObserver implements TaskObserver {
    private final AuditLogger auditLogger;
    
    @Override
    public void onTaskCreated(Task task, User creator) {
        auditLogger.log("TASK_CREATED", task.getId(), creator.getId());
    }
    
    @Override
    public void onTaskDeleted(Task task, User deletedBy) {
        auditLogger.log("TASK_DELETED", task.getId(), deletedBy.getId());
    }
    // ... implement other methods
}
```

2. Register with `TaskService`:

```java
taskService.registerObserver(new AuditObserver(auditLogger));
```

### Switching to Database Storage

1. Create new implementation:

```java
public class JpaTaskRepository implements TaskRepository {
    private final EntityManager entityManager;
    
    @Override
    public Task save(Task task) {
        entityManager.persist(task);
        return task;
    }
    // ... implement other methods
}
```

2. Inject into services via factory or DI container.

---

## Usage Example

```java
// Initialize system using factory
TaskManagementSystem system = TaskManagementSystemFactory.createDefaultSystem();

// Create users
User alice = system.createUser("alice", "alice@example.com", "Alice Johnson");
User bob = system.createUser("bob", "bob@example.com", "Bob Smith");

// Create a task
Task task = system.createTask(
    "Complete LLD Documentation",
    "Write comprehensive LLD for task management system",
    TaskPriority.HIGH,
    LocalDateTime.now().plusDays(7),
    alice.getId()
);

// Assign task to Bob
system.assignTask(task.getId(), bob.getId(), alice.getId());

// Set a reminder
system.createReminder(
    task.getId(),
    bob.getId(),
    LocalDateTime.now().plusDays(6),
    "Task due tomorrow!"
);

// Update task status
system.updateTaskStatus(task.getId(), TaskStatus.IN_PROGRESS, bob.getId());

// Search tasks
List<Task> highPriorityTasks = system.searchByPriority(TaskPriority.HIGH);
List<Task> bobsTasks = system.searchByAssignee(bob.getId());

// Complete task
system.completeTask(task.getId(), bob.getId());

// View task history
List<TaskHistory> history = system.getTaskHistory(task.getId());
history.forEach(h -> System.out.println(
    h.getTimestamp() + ": " + h.getAction() + " - " + h.getNewValue()
));
```

---

## Future Enhancements

- [ ] Database persistence (JPA/Hibernate)
- [ ] REST API layer
- [ ] User authentication and authorization
- [ ] Recurring tasks
- [ ] Task dependencies (prerequisite tasks)
- [ ] File attachments
- [ ] Comments/Discussion threads
- [ ] Real-time notifications (WebSocket)
- [ ] Team/Project grouping
- [ ] Time tracking
- [ ] Kanban board view
- [ ] Calendar integration

---

## Running the Application

```bash
cd taskmanagement
javac -d ../out $(find . -name "*.java")
cd ../out
java taskmanagement.Main
```

The demo will:
1. Create sample users
2. Create tasks with different priorities and due dates
3. Demonstrate task assignment
4. Show search/filtering capabilities
5. Demonstrate reminder functionality
6. Display task history
7. Provide an interactive menu for testing



