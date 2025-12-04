# Professional Networking Platform (LinkedIn) - Low-Level Design

## 1. Assumptions & Clarifications

1. **Authentication**: Simple in-memory authentication (not OAuth/JWT for this LLD)
2. **Real-time Notifications**: Simulated via Observer pattern (actual WebSocket implementation would be infrastructure-specific)
3. **Search**: In-memory search with pluggable ranking strategies
4. **Persistence**: In-memory repositories (easily swappable to database implementations)
5. **Company**: Companies are separate entities that can post jobs
6. **User Roles**: Regular users and recruiters/employers (users with company association)

---

## 2. Responsibility Breakdown

| Component | Responsibility |
|-----------|----------------|
| **UserService** | User registration, authentication, profile CRUD operations |
| **ProfileService** | Manage profile sections (experience, education, skills) |
| **ConnectionService** | Handle connection requests, accept/decline, list connections |
| **MessagingService** | Send messages, manage conversations, inbox/sent |
| **JobService** | Post jobs, search jobs, apply to jobs |
| **SearchService** | Unified search across users, jobs, companies with ranking |
| **NotificationService** | Create, store, and broadcast notifications |
| **CompanyService** | Company CRUD, associate users as employees |

---

## 3. Key Abstractions

### 3.1 Core Models

```
User
├── id, email, password (hashed), name
├── Profile (1:1)
├── connections: List<User>
└── notifications: List<Notification>

Profile
├── userId, headline, summary, profilePictureUrl
├── experiences: List<Experience>
├── educations: List<Education>
└── skills: List<Skill>

Experience
├── id, title, company, location
├── startDate, endDate, description
└── isCurrent: boolean

Education
├── id, institution, degree, fieldOfStudy
├── startYear, endYear
└── description

Skill
├── id, name
└── endorsements: int

Connection
├── id, requesterId, receiverId
├── status: ConnectionStatus
└── createdAt, updatedAt

Message
├── id, senderId, receiverId
├── content, timestamp
└── isRead: boolean

Conversation
├── id, participants: List<User>
└── messages: List<Message>

JobPosting
├── id, title, description, requirements
├── location, jobType, experienceLevel
├── companyId, postedBy (recruiterId)
├── createdAt, isActive
└── applications: List<JobApplication>

JobApplication
├── id, jobId, applicantId
├── resumeUrl, coverLetter
├── status: ApplicationStatus
└── appliedAt

Company
├── id, name, description, industry
├── website, logoUrl, location
└── employees: List<User>

Notification
├── id, userId, type, content
├── referenceId, isRead
└── createdAt
```

### 3.2 Enums

```
ConnectionStatus: PENDING, ACCEPTED, DECLINED, BLOCKED
NotificationType: CONNECTION_REQUEST, CONNECTION_ACCEPTED, MESSAGE_RECEIVED, JOB_POSTED, JOB_APPLICATION
JobType: FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, REMOTE
ExperienceLevel: ENTRY, MID, SENIOR, LEAD, EXECUTIVE
ApplicationStatus: PENDING, REVIEWED, SHORTLISTED, REJECTED, HIRED
```

---

## 4. Design Patterns Used

### 4.1 Observer Pattern - Notifications
```
NotificationObserver (Interface)
├── onNotification(Notification)

Implementations:
├── EmailNotificationObserver
├── PushNotificationObserver
├── InAppNotificationObserver

NotificationService (Subject)
└── notifyObservers(notification)
```
**Why**: Decouples notification generation from delivery mechanisms. New channels (SMS, Slack) can be added without modifying core logic.

### 4.2 Strategy Pattern - Search Ranking
```
SearchRankingStrategy (Interface)
├── rankResults(List<SearchResult>, SearchContext): List<SearchResult>

Implementations:
├── RelevanceRankingStrategy (default text matching)
├── ConnectionBasedRankingStrategy (prioritize connections)
├── RecencyRankingStrategy (prioritize recent)
├── HybridRankingStrategy (weighted combination)
```
**Why**: Different search contexts need different ranking. Jobs might prioritize recency, people search might prioritize mutual connections.

### 4.3 Factory Pattern - Notification Creation
```
NotificationFactory
├── createConnectionRequestNotification(sender, receiver)
├── createMessageNotification(message)
├── createJobPostNotification(job, followers)
```
**Why**: Centralizes notification creation logic, ensures consistency in notification content formatting.

### 4.4 Builder Pattern - Complex Objects
```
JobPostingBuilder
├── withTitle(), withDescription()
├── withRequirements(), withLocation()
├── withJobType(), withExperienceLevel()
└── build(): JobPosting

UserBuilder / ProfileBuilder
```
**Why**: JobPosting and Profile have many optional fields. Builder provides fluent, readable object construction.

### 4.5 Repository Pattern - Data Access
```
Repository<T, ID> (Interface)
├── save(T): T
├── findById(ID): Optional<T>
├── findAll(): List<T>
├── delete(ID): void

Implementations:
├── InMemoryUserRepository
├── InMemoryJobRepository
├── InMemoryConnectionRepository
└── ... (easily swappable to JPA/MongoDB implementations)
```
**Why**: Abstracts data storage, allows easy testing with in-memory implementations and production DB implementations.

---

## 5. Class Relationships & SOLID Principles

### 5.1 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        LinkedIn Platform                         │
├─────────────────────────────────────────────────────────────────┤
│  Controllers/Entry Points                                        │
│  └── Main.java (Demo/Usage)                                     │
├─────────────────────────────────────────────────────────────────┤
│  Services (Business Logic)                                       │
│  ├── UserService ──────────┐                                    │
│  ├── ProfileService ───────┤                                    │
│  ├── ConnectionService ────┼──▶ NotificationService             │
│  ├── MessagingService ─────┤        │                           │
│  ├── JobService ───────────┘        ▼                           │
│  └── SearchService ◀── SearchRankingStrategy                    │
│                              │                                   │
│                              ▼                                   │
│                    NotificationObserver(s)                       │
├─────────────────────────────────────────────────────────────────┤
│  Repositories (Data Access)                                      │
│  ├── UserRepository                                             │
│  ├── ConnectionRepository                                        │
│  ├── MessageRepository                                           │
│  ├── JobRepository                                               │
│  └── NotificationRepository                                      │
├─────────────────────────────────────────────────────────────────┤
│  Models / Domain Objects                                         │
│  User, Profile, Connection, Message, JobPosting, Notification   │
└─────────────────────────────────────────────────────────────────┘
```

### 5.2 SOLID Principles Application

| Principle | Application |
|-----------|-------------|
| **SRP** | Each service handles one domain (UserService ≠ JobService). Notification creation separated from delivery. |
| **OCP** | New ranking strategies, notification channels added without modifying existing code |
| **LSP** | All Repository implementations are interchangeable. All NotificationObservers handle notifications uniformly |
| **ISP** | Separate interfaces: `Searchable`, `Notifiable`, `ConnectionHandler` instead of one large interface |
| **DIP** | Services depend on Repository interfaces, not concrete implementations. NotificationService depends on Observer interface |

---

## 6. Extension Points

1. **New Notification Channels**: Implement `NotificationObserver` (e.g., SlackNotificationObserver)
2. **Different Search Algorithms**: Implement `SearchRankingStrategy` (e.g., MLBasedRankingStrategy)
3. **Authentication Methods**: Implement `AuthenticationStrategy` (e.g., OAuth, SAML)
4. **New Entity Types**: Add new searchable entities by implementing `Searchable` interface
5. **Job Matching**: Add `JobMatchingStrategy` for intelligent job recommendations
6. **Premium Features**: Decorator pattern for premium user capabilities

---

## 7. Sequence Diagrams

### 7.1 Send Connection Request
```
User A          ConnectionService       ConnectionRepository    NotificationService
   │                    │                        │                      │
   │──sendRequest(B)───▶│                        │                      │
   │                    │──save(connection)─────▶│                      │
   │                    │◀──────connection───────│                      │
   │                    │──createNotification(B)────────────────────────▶│
   │                    │                        │                      │──notify observers
   │◀──────success──────│                        │                      │
```

### 7.2 Search Users
```
User            SearchService        UserRepository       RankingStrategy
  │                   │                    │                    │
  │──search(query)───▶│                    │                    │
  │                   │──findByName(q)────▶│                    │
  │                   │◀────results────────│                    │
  │                   │──rank(results, ctx)────────────────────▶│
  │                   │◀────rankedResults──────────────────────│
  │◀──rankedResults───│                    │                    │
```

---

## 8. Data Flow

```
Registration Flow:
User Input → UserService.register() → Validate → Hash Password → UserRepository.save() → Create Empty Profile → Success

Login Flow:
Credentials → UserService.authenticate() → Find User → Verify Password → Generate Session → Return User

Connection Flow:
Request → ConnectionService → Check Not Already Connected → Create Pending Connection → Notify Receiver

Messaging Flow:
Message → MessagingService → Verify Connection Exists → Save Message → Update Conversation → Notify Receiver

Job Application Flow:
Application → JobService → Verify Job Active → Verify Not Already Applied → Create Application → Notify Recruiter
```

---

## 9. Error Handling Strategy

- Custom exceptions for each domain: `UserNotFoundException`, `ConnectionException`, `JobNotFoundException`
- Service layer validates business rules and throws domain exceptions
- Repository layer handles data access errors
- All exceptions extend `LinkedInException` base class for unified handling

---

## 10. Testability

- All services accept repository interfaces via constructor injection
- Strategy interfaces allow mock implementations for testing
- Observer pattern allows testing notification logic without actual delivery
- In-memory repositories enable fast unit tests without database



