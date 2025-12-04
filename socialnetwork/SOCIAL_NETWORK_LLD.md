# Social Network (Facebook-like) - Low Level Design

## Overview

This document describes the Low-Level Design for a social network system similar to Facebook. The design follows SOLID principles, uses appropriate design patterns, and is built for extensibility and maintainability.

## Requirements Covered

1. ✅ **User Registration and Authentication** - Account creation, login/logout with session management
2. ✅ **User Profiles** - Profile picture, bio, interests, privacy settings
3. ✅ **Friend Connections** - Send/accept/decline friend requests, view friends list
4. ✅ **Posts and Newsfeed** - Create posts (text/images/videos), personalized newsfeed
5. ✅ **Likes and Comments** - Like/comment on posts, view interactions
6. ✅ **Privacy and Security** - Visibility controls (public/friends-only/private)
7. ✅ **Notifications** - Real-time notifications via multiple channels
8. ✅ **Scalability** - Thread-safe repositories, extensible strategies

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         SocialNetwork (Facade)                           │
│  Provides unified API for all social network operations                  │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        │                           │                           │
        ▼                           ▼                           ▼
┌───────────────┐         ┌─────────────────┐         ┌─────────────────┐
│  AuthService  │         │  UserService    │         │FriendshipService│
│  - register   │         │  - getUser      │         │  - sendRequest  │
│  - login      │         │  - updateProfile│         │  - acceptRequest│
│  - logout     │         │  - searchUsers  │         │  - getFriends   │
└───────────────┘         └─────────────────┘         └─────────────────┘
        │                           │                           │
        └───────────────────────────┼───────────────────────────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        │                           │                           │
        ▼                           ▼                           ▼
┌───────────────┐         ┌─────────────────┐         ┌─────────────────┐
│  PostService  │         │NewsfeedService  │         │InteractionSvc   │
│  - createPost │         │  - getNewsfeed  │         │  - likePost     │
│  - getPost    │         │  + Strategy     │         │  - addComment   │
│  - deletePost │         │                 │         │  - getComments  │
└───────────────┘         └─────────────────┘         └─────────────────┘
        │                           │                           │
        └───────────────────────────┼───────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                        NotificationService                               │
│  Uses Observer Pattern for multi-channel notifications                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐               │
│  │ InApp        │  │ Email        │  │ Push             │               │
│  │ Observer     │  │ Observer     │  │ Observer         │               │
│  └──────────────┘  └──────────────┘  └──────────────────┘               │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         Repositories (Data Layer)                        │
│  UserRepo │ PostRepo │ CommentRepo │ LikeRepo │ FriendRequestRepo │ ... │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Design Patterns Used

### 1. **Facade Pattern** - `SocialNetwork` class
Provides a unified interface to all subsystems, simplifying client interaction.

```java
SocialNetwork network = new SocialNetwork();
network.register("Alice", "alice@example.com", "password");
network.createTextPost(userId, "Hello World!");
```

### 2. **Strategy Pattern** - `NewsfeedStrategy`
Allows swapping newsfeed algorithms without changing core logic.

```java
// Chronological (default)
network.setNewsfeedStrategy(new ChronologicalNewsfeedStrategy());

// Engagement-based
network.setNewsfeedStrategy(new EngagementBasedNewsfeedStrategy());
```

### 3. **Observer Pattern** - `NotificationObserver`
Enables multiple notification channels without tight coupling.

```java
notificationService.registerObserver(new InAppNotificationObserver(repo));
notificationService.registerObserver(new EmailNotificationObserver(userRepo));
notificationService.registerObserver(new PushNotificationObserver());
```

### 4. **Builder Pattern** - `User`, `Post`, `Notification`
Constructs complex objects with optional parameters.

```java
User user = User.builder()
    .name("Alice")
    .email("alice@example.com")
    .passwordHash(hash)
    .bio("Software Engineer")
    .build();
```

### 5. **Repository Pattern** - All data access
Abstracts data persistence, enabling easy switching between storage implementations.

### 6. **Factory Pattern** - `NotificationFactory`
Creates notifications with consistent formatting.

---

## SOLID Principles Application

| Principle | Application |
|-----------|-------------|
| **SRP** | Each service has a single responsibility (AuthService for auth, PostService for posts) |
| **OCP** | New newsfeed strategies can be added without modifying existing code |
| **LSP** | All strategy implementations are interchangeable |
| **ISP** | Separate interfaces for repositories, services, and observers |
| **DIP** | Services depend on repository interfaces, not implementations |

---

## Class Structure

### Enums
| Enum | Purpose |
|------|---------|
| `FriendshipStatus` | PENDING, ACCEPTED, DECLINED, BLOCKED |
| `PostType` | TEXT, IMAGE, VIDEO, LINK |
| `NotificationType` | FRIEND_REQUEST, LIKE, COMMENT, MENTION, etc. |
| `PrivacyLevel` | PUBLIC, FRIENDS_ONLY, PRIVATE |
| `UserStatus` | ACTIVE, SUSPENDED, DEACTIVATED |

### Models
| Model | Key Fields |
|-------|------------|
| `User` | id, email, passwordHash, name, bio, profilePictureUrl, interests, privacySettings |
| `Post` | id, authorId, content, type, mediaUrl, privacyLevel, likeIds, commentIds |
| `Comment` | id, postId, authorId, content |
| `Like` | id, postId, userId |
| `FriendRequest` | id, senderId, receiverId, status |
| `Notification` | id, userId, actorId, type, message, isRead |
| `Session` | token, userId, expiresAt, isActive |

### Services
| Service | Responsibility |
|---------|---------------|
| `AuthService` | Registration, login/logout, session management |
| `UserService` | Profile management, user search |
| `FriendshipService` | Friend requests, connections |
| `PostService` | Post CRUD with privacy enforcement |
| `NewsfeedService` | Feed generation using strategies |
| `InteractionService` | Likes and comments |
| `NotificationService` | Multi-channel notification delivery |

---

## Key Features

### 1. Privacy Control
```java
// Three levels of privacy for posts and profiles
public enum PrivacyLevel {
    PUBLIC,         // Visible to everyone
    FRIENDS_ONLY,   // Visible to friends only
    PRIVATE         // Visible only to author
}

// Privacy is enforced at service level using PrivacyPolicy
if (!privacyPolicy.canViewPost(post, viewer, friendIds)) {
    throw UnauthorizedException.cannotViewPost();
}
```

### 2. Newsfeed Generation
```java
// Strategy interface allows different algorithms
public interface NewsfeedStrategy {
    List<Post> generateFeed(String userId, List<Post> posts, int limit);
}

// Chronological: Newest first
// Engagement-based: Higher engagement + recency score
```

### 3. Real-time Notifications
```java
// Observer pattern for multi-channel delivery
public interface NotificationObserver {
    void onNotification(Notification notification);
}

// Registered observers receive notifications simultaneously
notificationService.sendNotification(notification);
// → InApp: Saves to database
// → Email: Sends email
// → Push: Sends push notification
```

### 4. Thread Safety
All in-memory repositories use `ConcurrentHashMap` for thread-safe operations.

---

## Extension Points

### Adding New Post Types
1. Add value to `PostType` enum
2. Handle in `PostService.createPost()` if special logic needed

### Adding New Newsfeed Algorithm
1. Implement `NewsfeedStrategy` interface
2. Set via `network.setNewsfeedStrategy(new CustomStrategy())`

### Adding New Notification Channel
1. Implement `NotificationObserver` interface
2. Register via `notificationService.registerObserver(new CustomObserver())`

### Switching to Database Storage
1. Implement repository interfaces (e.g., `JpaUserRepository`)
2. Inject into services via constructor

---

## Usage Example

```java
// Create social network instance
SocialNetwork network = new SocialNetwork();

// Register users
User alice = network.register("Alice", "alice@example.com", "Password123");
User bob = network.register("Bob", "bob@example.com", "SecurePass456");

// Login
Session session = network.login("alice@example.com", "Password123");

// Send friend request
FriendRequest request = network.sendFriendRequest(alice.getId(), bob.getId());
network.acceptFriendRequest(request.getId(), bob.getId());

// Create post
Post post = network.createTextPost(alice.getId(), "Hello everyone!");

// Interact
network.likePost(post.getId(), bob.getId());
network.addComment(post.getId(), bob.getId(), "Great post!");

// View newsfeed
List<Post> feed = network.getNewsfeed(alice.getId());

// Logout
network.logout(session.getToken());
```

---

## File Structure

```
socialnetwork/
├── enums/
│   ├── FriendshipStatus.java
│   ├── NotificationType.java
│   ├── PostType.java
│   ├── PrivacyLevel.java
│   └── UserStatus.java
├── exceptions/
│   ├── AuthenticationException.java
│   ├── FriendshipException.java
│   ├── PostNotFoundException.java
│   ├── SocialNetworkException.java
│   ├── UnauthorizedException.java
│   ├── UserNotFoundException.java
│   └── ValidationException.java
├── factories/
│   └── NotificationFactory.java
├── models/
│   ├── Comment.java
│   ├── FriendRequest.java
│   ├── Like.java
│   ├── Notification.java
│   ├── Post.java
│   ├── Session.java
│   └── User.java
├── observers/
│   ├── EmailNotificationObserver.java
│   ├── InAppNotificationObserver.java
│   ├── NotificationObserver.java
│   └── PushNotificationObserver.java
├── repositories/
│   ├── impl/
│   │   ├── InMemoryCommentRepository.java
│   │   ├── InMemoryFriendRequestRepository.java
│   │   ├── InMemoryLikeRepository.java
│   │   ├── InMemoryNotificationRepository.java
│   │   ├── InMemoryPostRepository.java
│   │   ├── InMemorySessionRepository.java
│   │   └── InMemoryUserRepository.java
│   ├── CommentRepository.java
│   ├── FriendRequestRepository.java
│   ├── LikeRepository.java
│   ├── NotificationRepository.java
│   ├── PostRepository.java
│   ├── SessionRepository.java
│   └── UserRepository.java
├── services/
│   ├── impl/
│   │   ├── AuthServiceImpl.java
│   │   ├── FriendshipServiceImpl.java
│   │   ├── InteractionServiceImpl.java
│   │   ├── NewsfeedServiceImpl.java
│   │   ├── NotificationServiceImpl.java
│   │   ├── PostServiceImpl.java
│   │   └── UserServiceImpl.java
│   ├── AuthService.java
│   ├── FriendshipService.java
│   ├── InteractionService.java
│   ├── NewsfeedService.java
│   ├── NotificationService.java
│   ├── PostService.java
│   └── UserService.java
├── strategies/
│   ├── newsfeed/
│   │   ├── ChronologicalNewsfeedStrategy.java
│   │   ├── EngagementBasedNewsfeedStrategy.java
│   │   └── NewsfeedStrategy.java
│   ├── DefaultPrivacyPolicy.java
│   └── PrivacyPolicy.java
├── Main.java
└── SocialNetwork.java
```

---

## Running the Demo

```bash
cd /path/to/project
javac -d out socialnetwork/**/*.java
java -cp out socialnetwork.Main
```

---

## Design Rationale

1. **Facade Pattern**: Simplifies client code by hiding service complexity
2. **Strategy Pattern**: Enables A/B testing of newsfeed algorithms
3. **Observer Pattern**: Decouples notification delivery from business logic
4. **Repository Pattern**: Enables easy migration to different storage solutions
5. **Builder Pattern**: Clean construction of complex domain objects
6. **In-Memory Implementation**: Fast prototyping, easy testing, can be swapped for DB

The design is **loosely coupled** (services depend on interfaces), **extensible** (new strategies/observers without code changes), and **testable** (dependencies can be mocked).



