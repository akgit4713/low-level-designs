# NoBroker Projects Deep Dive

## Project 1: Monolith to Microservices Migration

### 5-Minute Overview Script

"At NoBroker, I led the migration from a Java 7 monolith to Spring Boot microservices with Kafka event streaming. The monolith had become a deployment bottleneck - any change required full application deployment, and teams were blocked waiting for each other. 

I used the Strangler Fig pattern to incrementally extract services. We started with high-change-frequency modules, created an API gateway for routing, and gradually shifted traffic. The key challenges were identifying service boundaries using Domain-Driven Design, handling the shared database during transition, and managing Quartz scheduler in a distributed environment. The result was improved deployment frequency from weekly to daily releases, and significantly better system maintainability."

---

### Deep Dive Q&A

#### Q1: "How did you identify service boundaries?"

**Answer:**
"We used **Domain-Driven Design (DDD)** principles to identify bounded contexts:

**Step 1: Event Storming sessions**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Event Storming Results                                │
│                                                                             │
│  Property Domain          │  User Domain           │  Payment Domain        │
│  ─────────────────────    │  ────────────────────  │  ─────────────────     │
│  • PropertyListed         │  • UserRegistered      │  • PaymentInitiated    │
│  • PropertyViewed         │  • ProfileUpdated      │  • PaymentCompleted    │
│  • PropertyShortlisted    │  • KYCVerified         │  • PaymentFailed       │
│  • VisitScheduled         │  • PreferencesSet      │  • RefundProcessed     │
│  • VisitCompleted         │                        │                        │
│                           │                        │                        │
│  ─────────────────────────┴────────────────────────┴───────────────────────│
│                                                                             │
│  Communication Domain     │  Notification Domain   │  Search Domain         │
│  ─────────────────────    │  ────────────────────  │  ─────────────────     │
│  • MessageSent            │  • EmailSent           │  • SearchPerformed     │
│  • ChatStarted            │  • SMSSent             │  • FilterApplied       │
│  • CallInitiated          │  • PushSent            │  • ResultsRanked       │
│                           │  • WhatsAppSent        │                        │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Step 2: Analyze coupling and cohesion**

```
Coupling Matrix (how often modules call each other):

                Property  User  Payment  Communication  Notification
Property           -       3      2           5             1
User               2       -      3           4             2
Payment            1       2      -           1             3
Communication      3       3      1           -             2
Notification       0       1      1           1             -

High coupling (>3) = Consider keeping together or clear interface
Low coupling (<2) = Good candidate for separate service
```

**Step 3: Change frequency analysis**

```sql
-- Git commit analysis by package
SELECT 
    REGEXP_EXTRACT(file_path, 'src/main/java/com/nobroker/(.+?)/') as module,
    COUNT(DISTINCT commit_hash) as commits_last_6mo,
    COUNT(DISTINCT author) as contributors
FROM git_commits
WHERE commit_date > DATE_SUB(NOW(), INTERVAL 6 MONTH)
GROUP BY module
ORDER BY commits_last_6mo DESC;

-- Results:
-- communication: 450 commits, 8 contributors ← Extract first
-- property: 380 commits, 6 contributors ← Extract second
-- payment: 120 commits, 3 contributors ← Extract later
-- notification: 80 commits, 2 contributors ← Keep in monolith longer
```

**Final service boundaries:**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Identified Microservices                              │
│                                                                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │ Property Service│  │  User Service   │  │ Payment Service │            │
│  │                 │  │                 │  │                 │            │
│  │ - Listings CRUD │  │ - Registration  │  │ - Payment Init  │            │
│  │ - Search/Filter │  │ - Profile Mgmt  │  │ - Payment Track │            │
│  │ - Visit Mgmt    │  │ - KYC           │  │ - Refunds       │            │
│  │ - Recommendations│ │ - Preferences   │  │ - Reconciliation│            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
│                                                                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │ Communication   │  │ Notification    │  │ Search Service  │            │
│  │ Service         │  │ Service         │  │ (Elasticsearch) │            │
│  │                 │  │                 │  │                 │            │
│  │ - Chat          │  │ - Email         │  │ - Indexing      │            │
│  │ - WhatsApp      │  │ - SMS           │  │ - Query         │            │
│  │ - Voice Calls   │  │ - Push          │  │ - Ranking       │            │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘            │
└─────────────────────────────────────────────────────────────────────────────┘
```"

---

#### Q2: "What was your Strangler Fig pattern implementation?"

**Answer:**
"The Strangler Fig pattern allowed us to migrate incrementally without a big-bang rewrite:

**Phase 1: Add API Gateway (Week 1-2)**

```
Before:
┌────────────┐     ┌───────────────────────────────────────┐
│   Client   │────▶│              Monolith                 │
└────────────┘     │  ┌──────────────────────────────────┐│
                   │  │ All endpoints handled internally  ││
                   │  └──────────────────────────────────┘│
                   └───────────────────────────────────────┘

After:
┌────────────┐     ┌─────────────┐     ┌───────────────────┐
│   Client   │────▶│ API Gateway │────▶│    Monolith       │
└────────────┘     │  (Kong)     │     │ (all traffic still│
                   └─────────────┘     │  goes here)       │
                                       └───────────────────┘
```

**Gateway configuration:**

```yaml
# Kong route configuration
services:
  - name: monolith
    url: http://monolith:8080
    routes:
      - name: all-routes
        paths:
          - /
```

**Phase 2: Extract first service - Communication (Week 3-6)**

```
┌────────────┐     ┌─────────────┐
│   Client   │────▶│ API Gateway │
└────────────┘     └──────┬──────┘
                          │
              ┌───────────┴───────────┐
              │                       │
              ▼                       ▼
    ┌──────────────────┐    ┌──────────────────┐
    │ Communication    │    │    Monolith      │
    │ Service (new)    │    │ (minus comm)     │
    │                  │    │                  │
    │ /api/chat/*      │    │ Everything else  │
    │ /api/whatsapp/*  │    │                  │
    └──────────────────┘    └──────────────────┘
```

**Gradual traffic shifting:**

```yaml
# Kong route with canary
routes:
  - name: communication-canary
    paths:
      - /api/chat
      - /api/whatsapp
    plugins:
      - name: canary
        config:
          percentage: 10  # Start with 10%
          upstream_new: communication-service
          upstream_old: monolith
```

**Phase 3: Anti-Corruption Layer**

```java
// ACL in new service to translate between old and new models
@Service
public class UserAntiCorruptionLayer {
    
    private final MonolithClient monolithClient;
    
    // Old monolith returns flat structure
    // New service uses proper domain model
    public User getUser(String userId) {
        LegacyUserResponse legacy = monolithClient.getUser(userId);
        
        return User.builder()
            .id(userId)
            .name(legacy.getFullName())  // was "full_name" in legacy
            .email(legacy.getEmailAddress())  // was "email_id" in legacy
            .phone(formatPhone(legacy.getMobile()))  // format changed
            .address(convertAddress(legacy))  // nested object now
            .build();
    }
    
    private Address convertAddress(LegacyUserResponse legacy) {
        // Legacy had flat fields: city, state, pincode
        // New model has Address object
        return Address.builder()
            .city(legacy.getCity())
            .state(legacy.getState())
            .pincode(legacy.getPincode())
            .build();
    }
}
```

**Phase 4: Database split (most challenging)**

```
Step 1: Dual write
┌──────────────────┐     ┌──────────────────┐
│ Communication    │────▶│ Monolith DB      │
│ Service          │     │ (chat tables)    │
│                  │──┬─▶│                  │
└──────────────────┘  │  └──────────────────┘
                      │
                      │  ┌──────────────────┐
                      └─▶│ New Chat DB      │
                         │ (PostgreSQL)     │
                         └──────────────────┘

Step 2: Read from new, write to both
Step 3: Write to new only, read from new
Step 4: Retire old tables
```

**Migration timeline:**

| Service | Start | Production | DB Split Complete |
|---------|-------|------------|-------------------|
| Communication | Week 3 | Week 6 | Week 10 |
| Property | Week 8 | Week 14 | Week 18 |
| User | Week 16 | Week 22 | Week 26 |
| Payment | Week 24 | Week 30 | Week 34 |"

---

#### Q3: "How did you handle shared database during migration?"

**Answer:**
"The shared database was the biggest challenge. We used three strategies:

**Strategy 1: Database views for backward compatibility**

```sql
-- New service writes to new schema
CREATE TABLE communication.messages (
    id UUID PRIMARY KEY,
    conversation_id UUID,
    sender_id UUID,
    content TEXT,
    sent_at TIMESTAMP,
    metadata JSONB
);

-- View for monolith to read (backward compatible)
CREATE VIEW public.chat_messages AS
SELECT 
    id::varchar as message_id,  -- Monolith expects varchar
    conversation_id::varchar as thread_id,  -- Different column name
    sender_id::varchar as from_user,
    content as message_text,
    sent_at as created_at,
    metadata->>'read' as is_read  -- Flatten JSON
FROM communication.messages;

-- Monolith code unchanged, reads from view
```

**Strategy 2: Event-driven sync during transition**

```java
// New service publishes events
@Service
public class MessageService {
    
    @Transactional
    public Message sendMessage(SendMessageRequest request) {
        // Write to new DB
        Message message = messageRepository.save(
            Message.from(request)
        );
        
        // Publish event for sync
        kafkaTemplate.send("message-events", 
            new MessageSentEvent(message));
        
        return message;
    }
}

// Sync consumer writes to legacy tables (temporary)
@KafkaListener(topics = "message-events")
public void syncToLegacy(MessageSentEvent event) {
    legacyJdbcTemplate.update(
        "INSERT INTO chat_messages (...) VALUES (...)",
        event.toChatMessageRow()
    );
}
```

**Strategy 3: Change Data Capture for reads**

```yaml
# Debezium connector for monolith tables
# New service subscribes to changes in tables it doesn't own yet
connectors:
  - name: user-table-cdc
    connector.class: io.debezium.connector.mysql.MySqlConnector
    database.hostname: monolith-db
    table.include.list: nobroker.users,nobroker.user_profiles
    topic.prefix: legacy
```

**Database ownership matrix:**

| Table | Owner | Read by | Write by |
|-------|-------|---------|----------|
| users | Monolith → User Service | All | User Service only |
| properties | Monolith → Property Service | All | Property Service only |
| messages | Communication Service | Comm + Monolith | Comm Service only |
| payments | Payment Service | Pay + Monolith | Payment Service only |

**Constraint: Foreign keys**

```sql
-- Problem: Foreign keys across service boundaries
ALTER TABLE messages 
    ADD CONSTRAINT fk_sender 
    FOREIGN KEY (sender_id) REFERENCES users(id);

-- Solution: Remove FK, enforce in application
ALTER TABLE messages DROP CONSTRAINT fk_sender;

-- Validate in service code
@Service
public class MessageService {
    
    public Message sendMessage(SendMessageRequest request) {
        // Validate user exists via API call
        if (!userClient.exists(request.getSenderId())) {
            throw new InvalidSenderException();
        }
        // ... rest of logic
    }
}
```"

---

#### Q4: "What challenges did you face with Quartz in distributed setup?"

**Answer:**
"Quartz Scheduler was tightly coupled to the monolith. Moving to microservices created several challenges:

**Challenge 1: Job ownership confusion**

```
Before: Single Quartz instance, all jobs in one place
After: Which service owns which scheduled job?

Jobs to migrate:
- SendDailyPropertyAlerts → Property Service
- ExpireListings → Property Service
- SendPaymentReminders → Payment Service
- SyncUserData → User Service
- CleanupOldChats → Communication Service
```

**Challenge 2: Quartz clustering**

```java
// Each service runs its own Quartz with JDBC clustering
@Configuration
public class QuartzConfig {
    
    @Bean
    public SchedulerFactoryBean schedulerFactory(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJobFactory(jobFactory);
        
        Properties props = new Properties();
        // Clustering configuration
        props.setProperty("org.quartz.jobStore.class", 
            "org.quartz.impl.jdbcjobstore.JobStoreTX");
        props.setProperty("org.quartz.jobStore.isClustered", "true");
        props.setProperty("org.quartz.jobStore.clusterCheckinInterval", "20000");
        
        // Unique instance ID per pod
        props.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        props.setProperty("org.quartz.scheduler.instanceName", 
            "PropertyServiceScheduler");
        
        factory.setQuartzProperties(props);
        return factory;
    }
}
```

**Challenge 3: Shared job tables**

```sql
-- Problem: All services using same QRTZ_* tables = conflicts
-- Solution: Table prefix per service

-- Property Service
CREATE TABLE property_QRTZ_JOB_DETAILS (...);
CREATE TABLE property_QRTZ_TRIGGERS (...);
-- etc.

-- Payment Service  
CREATE TABLE payment_QRTZ_JOB_DETAILS (...);
CREATE TABLE payment_QRTZ_TRIGGERS (...);
```

```java
// Configuration
props.setProperty("org.quartz.jobStore.tablePrefix", "property_QRTZ_");
```

**Challenge 4: Job migration without gaps**

```java
// Migration strategy: Dual execution with idempotency

// Step 1: Add job to new service (disabled)
@DisallowConcurrentExecution
public class PropertyAlertJob implements Job {
    
    @Override
    public void execute(JobExecutionContext context) {
        if (!featureFlags.isEnabled("property-alerts-new-service")) {
            log.info("Skipping - running in monolith");
            return;
        }
        
        // Job logic with idempotency
        String jobKey = "property-alerts-" + LocalDate.now();
        if (idempotencyStore.wasExecuted(jobKey)) {
            log.info("Already executed today, skipping");
            return;
        }
        
        sendPropertyAlerts();
        idempotencyStore.markExecuted(jobKey);
    }
}

// Step 2: Enable flag in new service
// Step 3: Disable in monolith
// Step 4: Remove from monolith code
```

**Challenge 5: Monitoring distributed schedulers**

```java
// Expose Quartz metrics to Prometheus
@Component
public class QuartzMetricsExporter {
    
    private final Scheduler scheduler;
    private final MeterRegistry registry;
    
    @Scheduled(fixedRate = 60000)
    public void exportMetrics() {
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for (Trigger trigger : triggers) {
                    TriggerState state = scheduler.getTriggerState(trigger.getKey());
                    
                    registry.gauge("quartz_job_state",
                        Tags.of("job", jobKey.getName(), "state", state.name()),
                        state.ordinal());
                    
                    if (trigger.getNextFireTime() != null) {
                        registry.gauge("quartz_next_fire_time",
                            Tags.of("job", jobKey.getName()),
                            trigger.getNextFireTime().getTime());
                    }
                }
            }
        }
    }
}
```"

---

## Project 2: Real-Time Configuration Updates

### Overview Script

"I implemented real-time configuration updates using Spring Boot, Spring Cloud Config, Kafka, and Actuator. This enabled zero-downtime configuration changes in production - previously, any config change required application restart."

### Key Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Real-Time Configuration Architecture                      │
│                                                                             │
│  ┌─────────────────┐                                                        │
│  │ Config Changes  │                                                        │
│  │ (Git/Console)   │                                                        │
│  └────────┬────────┘                                                        │
│           │                                                                 │
│           ▼                                                                 │
│  ┌─────────────────┐                                                        │
│  │ Spring Cloud    │                                                        │
│  │ Config Server   │                                                        │
│  └────────┬────────┘                                                        │
│           │ Webhook on config change                                        │
│           ▼                                                                 │
│  ┌─────────────────┐                                                        │
│  │  Kafka Topic    │                                                        │
│  │  config-refresh │                                                        │
│  └────────┬────────┘                                                        │
│           │ Consumed by all service instances                               │
│           ▼                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐                         │
│  │ Service A   │  │ Service A   │  │ Service B   │                         │
│  │ Instance 1  │  │ Instance 2  │  │ Instance 1  │                         │
│  │             │  │             │  │             │                         │
│  │ @RefreshScope │ @RefreshScope │ @RefreshScope│                         │
│  │ beans reload│  │ beans reload│  │ beans reload│                         │
│  └─────────────┘  └─────────────┘  └─────────────┘                         │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Implementation

```java
// Configuration bean with @RefreshScope
@Configuration
@RefreshScope
public class FeatureConfig {
    
    @Value("${feature.new-search.enabled:false}")
    private boolean newSearchEnabled;
    
    @Value("${feature.payment-v2.enabled:false}")
    private boolean paymentV2Enabled;
    
    @Value("${rate-limit.requests-per-minute:100}")
    private int rateLimitPerMinute;
    
    // Getters...
}

// Kafka listener for config refresh events
@Component
public class ConfigRefreshListener {
    
    private final ContextRefresher contextRefresher;
    
    @KafkaListener(topics = "config-refresh")
    public void onConfigRefresh(ConfigRefreshEvent event) {
        log.info("Received config refresh event: {}", event);
        
        // Only refresh if this service is affected
        if (event.affectsService(serviceName)) {
            Set<String> refreshedKeys = contextRefresher.refresh();
            log.info("Refreshed config keys: {}", refreshedKeys);
            
            // Emit metric
            meterRegistry.counter("config.refresh.count").increment();
        }
    }
}

// Webhook endpoint for config server
@RestController
public class ConfigWebhookController {
    
    private final KafkaTemplate<String, ConfigRefreshEvent> kafkaTemplate;
    
    @PostMapping("/webhook/config-change")
    public ResponseEntity<Void> onConfigChange(@RequestBody GitWebhookPayload payload) {
        // Parse which configs changed
        List<String> changedFiles = payload.getCommits().stream()
            .flatMap(c -> c.getModified().stream())
            .filter(f -> f.endsWith(".yml") || f.endsWith(".properties"))
            .collect(Collectors.toList());
        
        // Notify all services
        kafkaTemplate.send("config-refresh", 
            new ConfigRefreshEvent(changedFiles, Instant.now()));
        
        return ResponseEntity.ok().build();
    }
}
```

---

## Project 3: WhatsApp Chatbot Revamp

### Overview Script

"I revamped the WhatsApp chatbot with Elasticsearch integration, improving search efficiency and increasing tenant conversion from 15-20 to 200-250 daily users - a 10x improvement that drove 120% revenue growth."

### Key Improvements

**Before: Keyword-based property matching**

```java
// Old approach - exact keyword match
public List<Property> findProperties(String userMessage) {
    // Extract keywords manually
    String location = extractLocation(userMessage);  // "Koramangala"
    String bhk = extractBHK(userMessage);  // "2 BHK"
    
    // SQL LIKE query - slow and imprecise
    return jdbcTemplate.query(
        "SELECT * FROM properties WHERE " +
        "locality LIKE ? AND bedrooms = ?",
        location + "%", Integer.parseInt(bhk.charAt(0))
    );
}
```

**After: Elasticsearch fuzzy search with NLP**

```java
// New approach - semantic search
public List<Property> findProperties(ChatMessage message) {
    // NLP extracts intent and entities
    PropertySearchIntent intent = nlpService.parseIntent(message.getText());
    
    // Elasticsearch query with fuzzy matching
    SearchSourceBuilder searchBuilder = new SearchSourceBuilder()
        .query(QueryBuilders.boolQuery()
            .must(QueryBuilders.matchQuery("locality", intent.getLocation())
                .fuzziness(Fuzziness.AUTO))
            .filter(QueryBuilders.rangeQuery("bedrooms")
                .gte(intent.getMinBedrooms())
                .lte(intent.getMaxBedrooms()))
            .filter(QueryBuilders.rangeQuery("rent")
                .gte(intent.getMinBudget())
                .lte(intent.getMaxBudget()))
            .should(QueryBuilders.matchQuery("amenities", 
                String.join(" ", intent.getPreferredAmenities())))
        )
        .sort("_score", SortOrder.DESC)
        .sort("listed_at", SortOrder.DESC)
        .size(10);
    
    return esClient.search(searchRequest);
}
```

**Conversation context management:**

```java
@Service
public class ChatSessionManager {
    
    private final RedisTemplate<String, ChatSession> redis;
    
    public void updateContext(String userId, ChatMessage message, PropertySearchIntent intent) {
        ChatSession session = redis.opsForValue().get("chat:" + userId);
        if (session == null) {
            session = new ChatSession(userId);
        }
        
        // Accumulate preferences across messages
        session.mergeIntent(intent);
        session.addMessage(message);
        
        // TTL of 30 minutes for session
        redis.opsForValue().set("chat:" + userId, session, Duration.ofMinutes(30));
    }
    
    // "I want 2 BHK" → stored
    // "in Koramangala" → merged with previous
    // "under 30k" → merged, now have complete search criteria
}
```

**Conversion metrics:**

| Metric | Before | After |
|--------|--------|-------|
| Daily active chatbot users | 200 | 2,000 |
| Property search success rate | 35% | 78% |
| Tenant conversions (daily) | 15-20 | 200-250 |
| Avg. messages to conversion | 12 | 5 |
| Revenue from chatbot channel | ₹8L/month | ₹17.6L/month (+120%) |
