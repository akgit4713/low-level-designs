# Stack Overflow System - Low Level Design

## Problem Statement
Design a Q&A platform like Stack Overflow where users can post questions, provide answers, comment, vote, and earn reputation based on their contributions.

---

## Requirements

1. Users can post questions, answer questions, and comment on questions and answers
2. Users can vote on questions and answers
3. Questions should have tags associated with them
4. Users can search for questions based on keywords, tags, or user profiles
5. The system should assign reputation score to users based on their activity and contributions
6. The system should handle concurrent access and ensure data consistency

---

## LLD Overview

### 1. Responsibility Breakdown

| Component | Responsibility |
|-----------|---------------|
| `User` | User profile, reputation tracking |
| `Question` | Question content, tags, votes, answers |
| `Answer` | Answer content, votes, acceptance status |
| `Comment` | Comment on questions/answers |
| `Tag` | Category/topic for questions |
| `Vote` | Upvote/downvote tracking |
| `UserRepository` | Persistence for users |
| `QuestionRepository` | Persistence for questions with search |
| `AnswerRepository` | Persistence for answers |
| `UserService` | User management and operations |
| `QuestionService` | Question CRUD and search |
| `AnswerService` | Answer management |
| `VoteService` | Voting logic and reputation updates |
| `SearchService` | Advanced search functionality |
| `ReputationStrategy` | Different reputation calculation strategies |
| `ReputationObserver` | Notify on reputation changes |

### 2. Key Abstractions

#### Enums
- **`VoteType`**: UPVOTE, DOWNVOTE

#### Interfaces
- **`Votable`**: Interface for items that can be voted on (questions, answers)
- **`Commentable`**: Interface for items that can have comments
- **`Searchable`**: Interface for searchable entities
- **`Repository<T>`**: Generic repository interface for CRUD operations
- **`ReputationStrategy`**: Strategy for calculating reputation changes
- **`StackOverflowObserver`**: Observer for system events

#### Models
- **`User`**: id, username, email, reputation, questions, answers
- **`Question`**: id, title, content, author, answers, comments, tags, votes, createdAt
- **`Answer`**: id, content, author, question, comments, votes, isAccepted, createdAt
- **`Comment`**: id, content, author, createdAt
- **`Tag`**: id, name, description
- **`Vote`**: voter, voteType

#### Services
- **`UserService`**: User registration, profile management
- **`QuestionService`**: Question posting, tagging, retrieval
- **`AnswerService`**: Answer posting, acceptance
- **`VoteService`**: Voting operations with reputation updates
- **`SearchService`**: Search by keywords, tags, users
- **`StackOverflow`**: Facade coordinating all services

### 3. SOLID Principles Applied

| Principle | Application |
|-----------|-------------|
| **SRP** | Each service handles one domain concern; repositories handle persistence only |
| **OCP** | ReputationStrategy allows new reputation rules without modifying existing code |
| **LSP** | Question and Answer both implement Votable and Commentable correctly |
| **ISP** | Small focused interfaces: Votable, Commentable, Searchable |
| **DIP** | Services depend on Repository interfaces, not concrete implementations |

### 4. Design Patterns Used

| Pattern | Usage | Location |
|---------|-------|----------|
| **Singleton** | Single system instance | `StackOverflow` |
| **Strategy** | Different reputation calculations | `ReputationStrategy` |
| **Observer** | Notifications for events | `StackOverflowObserver` |
| **Repository** | Data access abstraction | `*Repository` interfaces |
| **Facade** | Simplified API | `StackOverflow` class |
| **Factory** | Object creation | `TagFactory` for tags |

### 5. Reputation System

| Action | Points |
|--------|--------|
| Question upvoted | +5 |
| Question downvoted | -2 |
| Answer upvoted | +10 |
| Answer downvoted | -2 |
| Answer accepted | +15 |
| Downvoting others | -1 |
| Bounty offered | -amount |

### 6. Extension Points

1. **New Reputation Strategies**: Implement `ReputationStrategy` for seasonal multipliers, badges, etc.
2. **New Search Criteria**: Extend `SearchService` with new filters
3. **New Observers**: Add new observers for analytics, email notifications, etc.
4. **New Post Types**: Add bounties, wikis by extending base interfaces

---

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              INTERFACES                                      │
├─────────────────┬─────────────────┬─────────────────┬───────────────────────┤
│   <<interface>> │   <<interface>> │   <<interface>> │     <<interface>>     │
│     Votable     │   Commentable   │   Searchable    │  ReputationStrategy   │
├─────────────────┼─────────────────┼─────────────────┼───────────────────────┤
│ + vote()        │ + addComment()  │ + matches()     │ + calcQuestionVote()  │
│ + getVoteCount()│ + getComments() │ + getKeywords() │ + calcAnswerVote()    │
│ + getAuthor()   │                 │                 │ + calcAccepted()      │
└────────┬────────┴────────┬────────┴─────────────────┴───────────────────────┘
         │                 │
         └────────┬────────┘
                  │
    ┌─────────────┴─────────────┐
    ▼                           ▼
┌───────────────────┐     ┌───────────────────┐
│     Question      │     │      Answer       │
├───────────────────┤     ├───────────────────┤
│ - id              │────>│ - id              │
│ - title           │     │ - content         │
│ - content         │     │ - author          │
│ - author          │     │ - question        │
│ - answers         │     │ - comments        │
│ - comments        │     │ - votes           │
│ - tags            │     │ - isAccepted      │
│ - votes           │     │ - createdAt       │
│ - createdAt       │     └───────────────────┘
│ - viewCount       │
└───────────────────┘

┌───────────────────┐     ┌───────────────────┐     ┌───────────────────┐
│       User        │     │       Tag         │     │      Comment      │
├───────────────────┤     ├───────────────────┤     ├───────────────────┤
│ - id              │     │ - id              │     │ - id              │
│ - username        │     │ - name            │     │ - content         │
│ - email           │     │ - description     │     │ - author          │
│ - reputation      │     └───────────────────┘     │ - createdAt       │
│ - questions       │                               └───────────────────┘
│ - answers         │     ┌───────────────────┐
└───────────────────┘     │       Vote        │
                          ├───────────────────┤
                          │ - voter           │
                          │ - voteType        │
                          └───────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              REPOSITORIES                                    │
├───────────────────────────────────────────────────────────────────────────────┤
│  <<interface>>           <<interface>>           <<interface>>               │
│  Repository<T>           QuestionRepository      UserRepository              │
│  ├─ save()               ├─ findByTag()          ├─ findByUsername()         │
│  ├─ findById()           ├─ searchByKeyword()    └─ findByEmail()            │
│  ├─ findAll()            └─ findByAuthor()                                   │
│  └─ delete()                                                                 │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                              SERVICES                                        │
├─────────────────┬─────────────────┬─────────────────┬───────────────────────┤
│  UserService    │  QuestionService│  AnswerService  │     VoteService       │
├─────────────────┼─────────────────┼─────────────────┼───────────────────────┤
│ + register()    │ + post()        │ + post()        │ + vote()              │
│ + getProfile()  │ + addTag()      │ + accept()      │ + removeVote()        │
│ + updateRep()   │ + search()      │ + addComment()  │ + getVoteCount()      │
└─────────────────┴─────────────────┴─────────────────┴───────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                    StackOverflow (Singleton Facade)                          │
├─────────────────────────────────────────────────────────────────────────────┤
│ - userService: UserService                                                   │
│ - questionService: QuestionService                                           │
│ - answerService: AnswerService                                               │
│ - voteService: VoteService                                                   │
│ - searchService: SearchService                                               │
│ - observers: List<StackOverflowObserver>                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│ + createUser()                                                               │
│ + postQuestion()                                                             │
│ + postAnswer()                                                               │
│ + addComment()                                                               │
│ + voteQuestion()                                                             │
│ + voteAnswer()                                                               │
│ + acceptAnswer()                                                             │
│ + searchQuestions()                                                          │
│ + getQuestionsByTag()                                                        │
│ + getQuestionsByUser()                                                       │
│ + addObserver()                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Concurrency Handling

1. **ConcurrentHashMap** for thread-safe collections
2. **synchronized** blocks for atomic reputation updates
3. **Read-Write Locks** for search operations
4. **Atomic operations** where possible

---

## Sample Usage

```java
StackOverflow so = StackOverflow.getInstance();

// Register users
User alice = so.createUser("alice", "alice@example.com");
User bob = so.createUser("bob", "bob@example.com");

// Post question
Question q = so.postQuestion(alice, 
    "How to implement Singleton?",
    "What are best practices for thread-safe singleton?",
    Arrays.asList("java", "design-patterns"));

// Post answer
Answer a = so.postAnswer(bob, q, 
    "Use enum-based singleton for thread safety.");

// Vote
so.voteQuestion(bob, q, VoteType.UPVOTE);
so.voteAnswer(alice, a, VoteType.UPVOTE);

// Accept answer
so.acceptAnswer(alice, a);

// Search
List<Question> results = so.searchQuestions("singleton");
List<Question> javaQuestions = so.getQuestionsByTag("java");
List<Question> aliceQuestions = so.getQuestionsByUser(alice);
```
