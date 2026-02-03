# Workflow Engine Redesign: Camunda to Event-Driven Architecture

## 5-Minute Overview Script

"At Zeta, I led the redesign of our workflow engine from a Camunda-based monolithic system to an event-driven microservices architecture. The legacy system had significant database locking bottlenecks - Camunda stores process state in runtime tables like ACT_RU_EXECUTION and ACT_RU_TASK, and under high load, row-level locks caused transaction timeouts and failures.

I designed a new architecture using Kafka as the messaging backbone, decomposing the monolithic workflow into independent microservices, each owning its own data. This eliminated lock contention entirely and allowed us to scale horizontally. The result was a 40% improvement in processing throughput."

---

## Deep Dive Q&A

### Q1: "Walk me through the architecture before and after the redesign"

**Before (Camunda-based):**

```
┌─────────────────────────────────────────────────────────────────┐
│                    Camunda BPMN Engine                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ Onboarding   │  │ KYC Process  │  │ Payment Flow │          │
│  │   Process    │  │              │  │              │          │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘          │
│         │                 │                 │                   │
│         └────────────────┬┴─────────────────┘                   │
│                          │                                      │
│              ┌───────────▼───────────┐                         │
│              │   Camunda Database    │                         │
│              │  ┌─────────────────┐  │                         │
│              │  │ ACT_RU_EXECUTION│  │  ← Row-level locks      │
│              │  │ ACT_RU_TASK     │  │                         │
│              │  │ ACT_RU_VARIABLE │  │                         │
│              │  │ ACT_HI_*        │  │                         │
│              │  └─────────────────┘  │                         │
│              └───────────────────────┘                         │
└─────────────────────────────────────────────────────────────────┘

Problems:
1. Single database = lock contention under high load
2. Vertical scaling only (expensive and limited)
3. Process execution blocked waiting for locks
4. Transaction timeouts during peak hours
5. No isolation between different workflow types
```

**After (Event-Driven Microservices):**

```
┌──────────────────────────────────────────────────────────────────────────┐
│                        Event-Driven Architecture                          │
│                                                                          │
│  ┌────────────────┐   ┌────────────────┐   ┌────────────────┐           │
│  │  Onboarding    │   │  KYC Service   │   │ Payment Service│           │
│  │    Service     │   │                │   │                │           │
│  │  ┌──────────┐  │   │  ┌──────────┐  │   │  ┌──────────┐  │           │
│  │  │ Own DB   │  │   │  │ Own DB   │  │   │  │ Own DB   │  │           │
│  │  └──────────┘  │   │  └──────────┘  │   │  └──────────┘  │           │
│  └───────┬────────┘   └───────┬────────┘   └───────┬────────┘           │
│          │                    │                    │                     │
│          ▼                    ▼                    ▼                     │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │                         Kafka Cluster                              │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐               │  │
│  │  │ onboarding- │  │ kyc-events  │  │ payment-    │               │  │
│  │  │ events      │  │             │  │ events      │               │  │
│  │  └─────────────┘  └─────────────┘  └─────────────┘               │  │
│  └───────────────────────────────────────────────────────────────────┘  │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐    │
│  │                   Workflow Orchestrator                          │    │
│  │  - Tracks workflow state via events                              │    │
│  │  - No shared database locks                                      │    │
│  │  - Horizontal scaling with Kafka partitions                      │    │
│  └─────────────────────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────────────────────┘

Benefits:
1. No lock contention - each service has isolated database
2. Horizontal scaling via Kafka partitions and service replicas
3. Fault isolation - one service failure doesn't block others
4. Independent deployability
5. Better observability with event trails
```

---

### Q2: "What were the specific database locking bottlenecks?"

**Answer:**
"Camunda uses several runtime tables to track process execution:

1. **ACT_RU_EXECUTION**: Stores execution instances. When a process moves between activities, it locks rows in this table.

2. **ACT_RU_TASK**: Stores user tasks. Claiming or completing tasks requires row locks.

3. **ACT_RU_VARIABLE**: Process variables. Reading/writing variables locks the row.

The specific bottleneck pattern was:

```sql
-- When completing a task, Camunda does:
BEGIN TRANSACTION;

-- Lock the execution row
SELECT * FROM ACT_RU_EXECUTION WHERE ID_ = ? FOR UPDATE;

-- Lock task row
SELECT * FROM ACT_RU_TASK WHERE EXECUTION_ID_ = ? FOR UPDATE;

-- Update variables (locks variable rows)
UPDATE ACT_RU_VARIABLE SET ... WHERE EXECUTION_ID_ = ?;

-- Move to next activity
UPDATE ACT_RU_EXECUTION SET ACT_ID_ = ?, ...;

COMMIT;
```

Under high concurrency:
- Transaction A locks execution row for process P1
- Transaction B waits for the same row (if same process instance)
- Transaction C locks execution row for process P2
- If P1 and P2 need to interact (e.g., subprocess), deadlocks occur

We saw lock wait timeouts of 30+ seconds during peak hours, causing 15% transaction failure rate."

---

### Q3: "Why Kafka? Did you evaluate other message queues?"

**Answer:**
"Yes, we evaluated three options:

| Criteria | Kafka | RabbitMQ | AWS SQS |
|----------|-------|----------|---------|
| Ordering | Partition-level ordering ✓ | Queue-level only | FIFO queues (limited) |
| Throughput | Millions/sec | Thousands/sec | Moderate |
| Durability | Replicated log ✓ | Disk-backed | AWS managed |
| Replay | Yes ✓ | No | No |
| Consumer Groups | Native ✓ | Manual | Limited |
| Existing Stack | Already used ✓ | New infra | Cloud lock-in |

**Why Kafka won:**

1. **Ordering guarantee**: Workflow events must be processed in order per workflow instance. Kafka's partition-level ordering matched our needs - we partition by `workflow_instance_id`.

2. **Event replay**: For debugging and recovery, we needed to replay events. Kafka's log-based storage enables this.

3. **Consumer groups**: Multiple services can consume the same events independently without duplication.

4. **Existing expertise**: Team already had Kafka experience from other projects.

5. **Throughput**: We process 100K+ workflow events per second during peak.

**Trade-off acknowledged**: Kafka is more complex to operate than RabbitMQ, but we had existing Kubernetes operators and expertise."

---

### Q4: "How did you handle distributed transactions?"

**Answer:**
"We used the **Saga pattern with choreography** combined with the **Outbox pattern** for reliability.

**Saga Choreography:**

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│ Onboarding  │    │ KYC Service │    │ Account     │    │ Notification│
│   Service   │    │             │    │ Service     │    │   Service   │
└──────┬──────┘    └──────┬──────┘    └──────┬──────┘    └──────┬──────┘
       │                  │                  │                  │
       │ UserCreated      │                  │                  │
       │─────────────────▶│                  │                  │
       │                  │                  │                  │
       │                  │ KYCCompleted     │                  │
       │                  │─────────────────▶│                  │
       │                  │                  │                  │
       │                  │                  │ AccountCreated   │
       │                  │                  │─────────────────▶│
       │                  │                  │                  │
       │                  │                  │                  │ WelcomeEmail
       │                  │                  │                  │────────▶
```

**Compensating transactions for failures:**

```
If KYC fails:
┌─────────────┐    ┌─────────────┐
│ KYC Service │    │ Onboarding  │
└──────┬──────┘    └──────┬──────┘
       │                  │
       │ KYCFailed        │
       │─────────────────▶│
       │                  │
       │                  │ RollbackUser()
       │                  │ (compensating action)
```

**Outbox Pattern for reliability:**

```java
@Transactional
public void completeKYC(String userId, KYCResult result) {
    // 1. Update local state
    kycRepository.save(new KYCRecord(userId, result));
    
    // 2. Write event to outbox table (same transaction)
    outboxRepository.save(new OutboxEvent(
        "kyc-events",
        userId,
        new KYCCompletedEvent(userId, result)
    ));
}

// Separate process polls outbox and publishes to Kafka
@Scheduled(fixedRate = 100)
public void publishOutboxEvents() {
    List<OutboxEvent> events = outboxRepository.findUnpublished();
    for (OutboxEvent event : events) {
        kafkaTemplate.send(event.getTopic(), event.getKey(), event.getPayload());
        event.markPublished();
        outboxRepository.save(event);
    }
}
```

This ensures atomicity between local database write and event publication."

---

### Q5: "How did you ensure exactly-once processing?"

**Answer:**
"Kafka provides at-least-once delivery by default. We achieved effectively exactly-once through **idempotent consumers**:

**1. Idempotency key in every event:**

```java
public class WorkflowEvent {
    private String eventId;        // UUID, globally unique
    private String workflowId;     // For partitioning
    private String eventType;
    private Instant timestamp;
    private Map<String, Object> payload;
}
```

**2. Processed events table:**

```sql
CREATE TABLE processed_events (
    event_id VARCHAR(36) PRIMARY KEY,
    processed_at TIMESTAMP,
    service_name VARCHAR(50)
);
```

**3. Idempotent consumer implementation:**

```java
@KafkaListener(topics = "workflow-events")
@Transactional
public void handleEvent(WorkflowEvent event) {
    // Check if already processed
    if (processedEventRepository.existsById(event.getEventId())) {
        log.info("Skipping duplicate event: {}", event.getEventId());
        return;
    }
    
    // Process the event
    processWorkflowEvent(event);
    
    // Mark as processed (same transaction)
    processedEventRepository.save(new ProcessedEvent(
        event.getEventId(),
        Instant.now(),
        "kyc-service"
    ));
}
```

**4. Kafka consumer configuration:**

```yaml
spring:
  kafka:
    consumer:
      enable-auto-commit: false
      isolation-level: read_committed
    listener:
      ack-mode: manual
```

**Trade-off**: This adds a database lookup per event, but with proper indexing, it's < 1ms overhead."

---

### Q6: "What was your rollback strategy during migration?"

**Answer:**
"We used a **Strangler Fig pattern with feature flags** and **parallel running**:

**Phase 1: Dual Write (2 weeks)**
```
┌─────────────┐     ┌─────────────────────────────────────────┐
│   API       │────▶│  Workflow Router                        │
│   Gateway   │     │  ┌─────────────────────────────────┐   │
└─────────────┘     │  │ if (featureFlag.isNewSystem()) { │   │
                    │  │   newEventDrivenSystem.process();│   │
                    │  │ }                                 │   │
                    │  │ // Always write to Camunda too    │   │
                    │  │ camundaEngine.process();          │   │
                    │  └─────────────────────────────────┘   │
                    └─────────────────────────────────────────┘
```
- Both systems process every workflow
- Compare outputs for correctness
- New system is read-only initially

**Phase 2: Shadow Mode (2 weeks)**
- New system processes and writes to its own database
- Automated comparison of final states
- Alert on any discrepancy

**Phase 3: Canary Release (3 weeks)**
```
Traffic split:
Week 1: 5% → new system
Week 2: 25% → new system
Week 3: 50% → new system
```

**Phase 4: Full Migration**
- 100% traffic to new system
- Camunda kept running for 2 weeks (read-only) for emergency rollback
- Feature flag for instant rollback

**Rollback triggers:**
- Error rate > 1% (auto-rollback)
- Latency P99 > 500ms (alert + manual decision)
- Any data inconsistency (immediate rollback)"

---

### Q7: "How did you measure the 40% throughput improvement?"

**Answer:**
"We established baseline metrics before migration and tracked them throughout:

**Metrics tracked:**

| Metric | Before | After | Improvement |
|--------|--------|-------|-------------|
| Workflows/second | 850 | 1,190 | +40% |
| P50 latency | 120ms | 45ms | -62.5% |
| P99 latency | 2,500ms | 180ms | -92.8% |
| Error rate | 2.3% | 0.15% | -93.5% |
| Lock wait events/hour | 15,000 | 0 | -100% |

**Measurement methodology:**

1. **Load testing**: JMeter tests simulating production traffic patterns
   - 10,000 concurrent users
   - Mix of workflow types matching production distribution

2. **Production metrics**: Prometheus + Grafana dashboards
   ```
   workflow_processed_total{status="success"} rate over 1m
   workflow_processing_duration_seconds histogram
   kafka_consumer_lag_sum
   ```

3. **Database metrics**: 
   - Lock wait time from MySQL performance_schema
   - Connection pool utilization
   - Query latency percentiles

4. **A/B comparison during canary**:
   - Same traffic patterns
   - Direct comparison of latency distributions"

---

## Challenge Question: "What would you do differently?"

**Answer:**
"Three things I'd change:

1. **Start with event sourcing**: We built state-based services first, then added events. If I could redo it, I'd use event sourcing from the start for full audit trail and easier debugging.

2. **Invest in schema registry earlier**: We had breaking changes in event schemas that caused consumer failures. Using Avro with Confluent Schema Registry from day one would have prevented this.

3. **Build a workflow debugger UI**: Debugging distributed workflows is hard. I'd invest earlier in a tool that visualizes the event flow for a specific workflow instance, similar to Temporal's web UI."

---

## Common Follow-up Questions

### "How do you handle out-of-order events?"

"Each service maintains its own state machine. If an event arrives out of order:
1. We check if it's processable in current state
2. If not, we park it in a 'pending' queue
3. A reconciliation job processes pending events periodically
4. We use vector clocks for causal ordering when needed"

### "What about long-running workflows?"

"We have workflows that span days or weeks. We handle this by:
1. Persisting saga state after each step
2. Using scheduled events for timeouts and reminders
3. External systems can query workflow state via a status API"

### "How do you test distributed workflows?"

"Three levels:
1. **Unit tests**: Each service tested in isolation with mocked events
2. **Integration tests**: Testcontainers with Kafka and databases
3. **End-to-end tests**: Dedicated test environment with full workflow execution"
