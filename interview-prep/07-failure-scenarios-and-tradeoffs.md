# Failure Scenarios and Technical Trade-offs

## Overview

Senior SDE interviews probe your ability to handle edge cases, failure modes, and make informed trade-offs. This document prepares you for the "what if" questions that test depth of understanding.

---

## Section 1: Distributed Systems Failure Scenarios

### Kafka Failure Scenarios

#### Q: "How would you handle Kafka consumer lag?"

**Answer:**
"Consumer lag indicates consumers can't keep up with producers. My approach:

**Immediate actions:**
1. Check if lag is growing or stable
2. Identify which partitions have highest lag
3. Check consumer group health - any dead consumers?

**Root cause analysis:**
```
Common causes and solutions:

1. Processing too slow
   → Profile consumer code, optimize hot paths
   → Consider async processing for non-critical work

2. Not enough consumers
   → Scale consumers up to partition count
   → Note: More consumers than partitions doesn't help

3. Rebalancing issues
   → Check for frequent rebalances (consumer crashes/slow heartbeats)
   → Tune max.poll.interval.ms and session.timeout.ms

4. Batch size issues
   → Increase max.poll.records for batch efficiency
   → But not so high that processing times out
```

**Monitoring I set up:**
```yaml
alerts:
  - name: KafkaConsumerLag
    expr: kafka_consumergroup_lag_sum > 10000
    for: 5m
    labels:
      severity: warning
  
  - name: KafkaConsumerLagCritical
    expr: kafka_consumergroup_lag_sum > 100000
    for: 2m
    labels:
      severity: critical
```

**Trade-off acknowledged:** Adding more consumers improves throughput but increases cost. I'd analyze if the lag is during peak only (auto-scaling) vs constant (permanent scaling)."

---

#### Q: "What happens if Kafka broker goes down?"

**Answer:**
"Kafka is designed for broker failures. Here's what happens and how I handle it:

**Automatic handling (built-in):**
```
Cluster: 3 brokers, replication factor = 3

Broker 1 (Leader for partition 0) → DOWN
Broker 2 has replica → Becomes new leader (ISR)
Broker 3 has replica → Stays in ISR

Time to failover: ~30 seconds (configurable)
```

**Producer behavior:**
```java
// My producer config for resilience
Properties props = new Properties();
props.put("acks", "all");  // Wait for all replicas
props.put("retries", Integer.MAX_VALUE);  // Retry on failure
props.put("retry.backoff.ms", 100);
props.put("delivery.timeout.ms", 120000);  // 2 min max
props.put("enable.idempotence", true);  // No duplicates on retry
```

**Consumer behavior:**
- Consumers reconnect to new leader automatically
- May see brief pause during rebalance
- With proper config, no messages lost

**What I monitor:**
```promql
# Under-replicated partitions (sign of broker issues)
kafka_server_replicamanager_underreplicatedpartitions > 0

# Offline partitions (severe - no leader)
kafka_controller_kafkacontroller_offlinepartitionscount > 0
```

**Edge case: What if ALL replicas are down?**
- Partition becomes unavailable
- Producers block or fail (depending on config)
- I'd alert immediately, this is P0 incident"

---

### Elasticsearch Failure Scenarios

#### Q: "What happens when Elasticsearch cluster becomes unhealthy?"

**Answer:**
"ES cluster health is: Green (all good) → Yellow (replicas missing) → Red (primary shards missing)

**My handling for each:**

**Yellow (Replicas unavailable):**
```
Cause: Node down, not enough nodes for replicas
Impact: Search works but no redundancy
Action: 
- Alert but not page
- Scale up nodes if persistent
- Check if node is recovering
```

**Red (Primary shards missing):**
```
Cause: Multiple nodes down, data loss possible
Impact: Queries may fail or return partial results
Action:
- Immediate page
- Activate fallback (read-only mode or SQL fallback)
- Don't write to ES until cluster recovers

My circuit breaker pattern:
```

```java
@Service
public class SearchService {
    
    private final CircuitBreaker esCircuitBreaker;
    
    public SearchResult search(SearchRequest request) {
        return esCircuitBreaker.run(
            () -> elasticsearchClient.search(request),
            throwable -> fallbackSearch(request)  // Fallback
        );
    }
    
    private SearchResult fallbackSearch(SearchRequest request) {
        // Option 1: Return cached results
        SearchResult cached = cache.get(request.cacheKey());
        if (cached != null) return cached;
        
        // Option 2: Fall back to SQL (slower but available)
        return sqlSearchService.search(request);
        
        // Option 3: Graceful degradation
        return SearchResult.degraded("Search temporarily unavailable");
    }
}
```

**Recovery strategy:**
```yaml
# ES cluster recovery settings
cluster:
  routing:
    allocation:
      node_concurrent_recoveries: 4  # Parallel recovery
      enable: all
  
  # Don't allocate too fast (overwhelm cluster)
  indices:
    recovery:
      max_bytes_per_sec: 100mb
```"

---

#### Q: "How do you handle split-brain in your microservices?"

**Answer:**
"Split-brain occurs when network partition causes parts of the system to operate independently. Prevention and detection:

**For databases (PostgreSQL with replication):**
```
Primary → Replica
    ↓ network partition
Both think they're primary = split-brain

Prevention:
1. Quorum-based leader election
2. STONITH (Shoot The Other Node In The Head)
3. Use managed services (AWS RDS handles this)
```

**For microservices:**
```
Service A (Region 1) cannot reach Service A (Region 2)
Each might have different view of data

My approach:
1. Single source of truth for writes (primary region)
2. Reads can be local (eventual consistency acceptable)
3. For critical operations, always route to primary
```

**For distributed caches (Redis):**
```
Using Redis Sentinel for failover:
- Majority of sentinels must agree on failover
- Prevents both old and new primary accepting writes
- min-replicas-to-write: 1 ensures writes go to at least one replica
```

**Application-level handling:**
```java
public class ConsistentOperationService {
    
    public void performCriticalOperation(Operation op) {
        // 1. Acquire distributed lock
        Lock lock = redissonClient.getLock("critical-op-" + op.getEntityId());
        
        try {
            if (!lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                throw new ConcurrentModificationException();
            }
            
            // 2. Read current state
            Entity entity = repository.findById(op.getEntityId());
            
            // 3. Optimistic locking check
            if (entity.getVersion() != op.getExpectedVersion()) {
                throw new OptimisticLockException();
            }
            
            // 4. Perform operation
            entity.apply(op);
            repository.save(entity);
            
        } finally {
            lock.unlock();
        }
    }
}
```"

---

## Section 2: Scalability Questions

### Q: "Your search service works for 10M records. What changes for 1B?"

**Answer:**
"100x scale requires fundamental changes, not just more servers:

**Data layer changes:**

| Aspect | 10M Records | 1B Records |
|--------|-------------|------------|
| Total Size | ~20GB | ~2TB |
| Shards | 5 | 50-100 |
| Nodes | 3 | 20-30 |
| Architecture | Single cluster | Multiple clusters / Federated |

**Sharding strategy evolution:**
```
10M: Simple sharding
- 5 shards, queries hit all shards
- Search across entire dataset

1B: Time-based + tenant sharding
- transactions-2024-01, transactions-2024-02, ...
- OR: tenant-based: transactions-tenant-A, transactions-tenant-B
- Route queries to specific shards when possible
```

**Query optimization:**
```java
// At 10M: Query all shards is fine
searchRequest.indices("transactions");

// At 1B: Route to specific shards
public SearchRequest buildRequest(SearchQuery query) {
    String[] targetIndices = calculateTargetIndices(query);
    
    // If date range is last 30 days, only search recent indices
    // Reduces shards from 100 to 5
    
    return new SearchRequest(targetIndices)
        .routing(query.getTenantId())  // Route to specific shard
        .source(buildSource(query));
}
```

**Caching becomes critical:**
```
10M: Cache optional, queries fast enough
1B: Multi-layer caching essential

Layer 1: Application cache (Caffeine) - 1 minute TTL
Layer 2: Distributed cache (Redis) - 5 minute TTL
Layer 3: ES query cache - automatic
Layer 4: ES request cache - for aggregations
```

**Infrastructure changes:**
```yaml
# At 1B scale:
elasticsearch:
  cluster:
    name: prod-search
  nodes:
    - role: master  # 3 dedicated masters
      count: 3
    - role: data_hot  # Recent data (SSD)
      count: 10
    - role: data_warm  # Older data (HDD)
      count: 10
    - role: coordinating  # Query routing
      count: 5
```

**Cost consideration:**
At 1B records, infrastructure cost is significant. I'd evaluate:
- Do we need all 1B searchable? Or just last 6 months?
- Can we archive older data to S3?
- Is there a cheaper storage tier (warm/cold nodes)?
"

---

### Q: "How would you scale the GenAI agent for 100x more failures?"

**Answer:**
"Scaling from ~100 failures/day to 10,000 failures/day:

**Bottleneck analysis:**
```
Current architecture:
1. Webhook → 2. Extract errors → 3. Embed query → 4. Vector search → 5. LLM generation

Bottlenecks at 100x:
- OpenAI API rate limits (RPM limits)
- Vector DB query throughput
- Single-threaded processing
```

**Scaling strategy:**

**1. Queue-based processing:**
```java
// Instead of synchronous processing
@Async
@KafkaListener(topics = "build-failures")
public void processFailure(BuildFailure failure) {
    // Async processing with back-pressure
    failureProcessorPool.submit(() -> {
        analyzeAndReport(failure);
    });
}

// Configure pool based on API rate limits
ThreadPoolExecutor pool = new ThreadPoolExecutor(
    10,   // Core threads
    50,   // Max threads
    60L,  // Keep-alive
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(1000)  // Queue size
);
```

**2. Batching embeddings:**
```python
# Instead of one API call per failure
# Batch multiple queries together

class BatchedEmbeddingService:
    def __init__(self):
        self.batch_size = 100
        self.pending = []
        self.lock = threading.Lock()
    
    def embed_async(self, text: str) -> Future[List[float]]:
        future = Future()
        with self.lock:
            self.pending.append((text, future))
            if len(self.pending) >= self.batch_size:
                self._flush_batch()
        return future
    
    @scheduled(interval_ms=100)
    def _flush_batch(self):
        with self.lock:
            batch = self.pending[:self.batch_size]
            self.pending = self.pending[self.batch_size:]
        
        if batch:
            texts = [t for t, _ in batch]
            embeddings = openai.embeddings.create(
                model="text-embedding-3-small",
                input=texts
            )
            for (text, future), emb in zip(batch, embeddings.data):
                future.set_result(emb.embedding)
```

**3. Caching common failures:**
```python
# 70% of failures are repeat patterns
class FailureCache:
    def __init__(self):
        self.cache = LRUCache(maxsize=10000)
    
    def get_cached_rca(self, failure: BuildFailure) -> Optional[RCAResult]:
        # Create signature from error pattern
        signature = self._compute_signature(failure)
        
        cached = self.cache.get(signature)
        if cached and cached.confidence > 0.9:
            return cached.with_note("Cached RCA from similar failure")
        return None
    
    def _compute_signature(self, failure: BuildFailure) -> str:
        # Normalize error message (remove timestamps, line numbers)
        normalized = self._normalize_error(failure.error_message)
        return hashlib.md5(normalized.encode()).hexdigest()
```

**4. LLM optimization:**
```python
# At 100x scale, LLM costs become significant
# $0.01 per failure × 10,000 = $100/day = $3,000/month

Options:
1. Use cheaper models for initial classification
   - GPT-3.5 for "is this a known pattern?" (fast, cheap)
   - GPT-4 only for complex cases (slow, expensive)

2. Fine-tune smaller model
   - Train on historical RCAs
   - 90%+ of cases can use fine-tuned model
   - Only escalate unknowns to GPT-4

3. Reduce token usage
   - Summarize build logs before sending to LLM
   - Use structured prompts, not verbose instructions
```

**Infrastructure scaling:**
```yaml
# Horizontal scaling
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: genai-agent
spec:
  minReplicas: 3
  maxReplicas: 20
  metrics:
    - type: External
      external:
        metric:
          name: kafka_consumer_lag
        target:
          type: Value
          value: "100"
```"

---

## Section 3: Failure Recovery Questions

### Q: "What's your circuit breaker strategy?"

**Answer:**
"I use circuit breakers to prevent cascade failures when downstream services fail:

**Circuit breaker states:**
```
CLOSED → OPEN → HALF_OPEN → CLOSED
   ↑                          │
   └──────────────────────────┘
   
CLOSED: Normal operation, requests go through
OPEN: Service failing, requests fail fast (no network call)
HALF_OPEN: Testing if service recovered
```

**My implementation using Resilience4j:**
```java
@Configuration
public class CircuitBreakerConfig {
    
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)  // Open if 50% fail
            .slowCallRateThreshold(80)  // Open if 80% are slow
            .slowCallDurationThreshold(Duration.ofSeconds(2))
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .permittedNumberOfCallsInHalfOpenState(5)
            .minimumNumberOfCalls(10)  // Need 10 calls before calculating
            .slidingWindowType(SlidingWindowType.TIME_BASED)
            .slidingWindowSize(10)  // 10 seconds
            .build();
        
        return CircuitBreakerRegistry.of(config);
    }
}

@Service
public class PaymentService {
    
    private final CircuitBreaker circuitBreaker;
    
    public PaymentResult processPayment(PaymentRequest request) {
        return circuitBreaker.executeSupplier(() -> {
            return paymentGateway.process(request);
        });
    }
}
```

**Fallback strategies:**
```java
public PaymentResult processPaymentWithFallback(PaymentRequest request) {
    return Try.ofSupplier(
        CircuitBreaker.decorateSupplier(circuitBreaker, 
            () -> paymentGateway.process(request)))
        .recover(CallNotPermittedException.class, e -> {
            // Circuit is OPEN - fail fast
            log.warn("Circuit breaker is open, using fallback");
            return PaymentResult.queued("Payment queued for later processing");
        })
        .recover(TimeoutException.class, e -> {
            // Timeout - maybe retry or queue
            return retryOrQueue(request);
        })
        .get();
}
```

**Monitoring circuit breaker state:**
```yaml
# Prometheus metrics
resilience4j_circuitbreaker_state{name="paymentGateway"} 
# 0=CLOSED, 1=OPEN, 2=HALF_OPEN

resilience4j_circuitbreaker_failure_rate{name="paymentGateway"}

# Alert when circuit opens
- alert: CircuitBreakerOpen
  expr: resilience4j_circuitbreaker_state == 1
  for: 1m
  labels:
    severity: warning
```"

---

### Q: "How do you handle partial failures in distributed transactions?"

**Answer:**
"Partial failures are the norm in distributed systems. I use Saga pattern with compensation:

**Example: User Registration Flow**
```
Step 1: Create user in User Service
Step 2: Initialize wallet in Wallet Service
Step 3: Send welcome email in Notification Service
Step 4: Create activity log in Analytics Service

If Step 3 fails:
- Step 4 never runs
- Must compensate Step 1 and Step 2
```

**Saga implementation:**
```java
public class UserRegistrationSaga {
    
    private final StateMachine<State, Event> stateMachine;
    
    public void execute(UserRegistration request) {
        SagaState saga = new SagaState(request);
        
        try {
            // Step 1
            User user = userService.createUser(request);
            saga.setUserId(user.getId());
            saga.setCompensation(1, () -> userService.deleteUser(user.getId()));
            
            // Step 2
            Wallet wallet = walletService.createWallet(user.getId());
            saga.setWalletId(wallet.getId());
            saga.setCompensation(2, () -> walletService.deleteWallet(wallet.getId()));
            
            // Step 3
            notificationService.sendWelcomeEmail(user.getEmail());
            // No compensation needed - email already sent
            
            // Step 4
            analyticsService.logRegistration(user.getId());
            
            saga.complete();
            
        } catch (Exception e) {
            saga.compensate();  // Run compensations in reverse order
            throw new RegistrationFailedException(e);
        }
    }
}

class SagaState {
    private Map<Integer, Runnable> compensations = new LinkedHashMap<>();
    
    public void compensate() {
        // Execute in reverse order
        List<Integer> steps = new ArrayList<>(compensations.keySet());
        Collections.reverse(steps);
        
        for (Integer step : steps) {
            try {
                compensations.get(step).run();
            } catch (Exception e) {
                // Log but continue - best effort compensation
                log.error("Compensation failed for step {}", step, e);
                alertService.alert("Compensation failure - manual intervention needed");
            }
        }
    }
}
```

**Handling non-compensatable actions:**
```java
// Some actions can't be undone (email sent, money transferred)
// Strategy: Make them idempotent and retriable

public void sendWelcomeEmail(String email) {
    String idempotencyKey = "welcome-email-" + email;
    
    if (idempotencyStore.wasProcessed(idempotencyKey)) {
        log.info("Email already sent, skipping");
        return;
    }
    
    emailService.send(email, welcomeTemplate);
    idempotencyStore.markProcessed(idempotencyKey);
}
```

**Eventual consistency handling:**
```java
// For non-critical steps, use async processing with retry
@Transactional
public User createUser(UserRegistration request) {
    User user = userRepository.save(new User(request));
    
    // Critical: Wallet creation (must succeed or rollback)
    walletService.createWallet(user.getId());
    
    // Non-critical: Async with retry
    eventPublisher.publish(new UserCreatedEvent(user.getId()));
    // Listeners handle: welcome email, analytics, etc.
    
    return user;
}

@RetryableTopic(
    attempts = 5,
    backoff = @Backoff(delay = 1000, multiplier = 2)
)
@KafkaListener(topics = "user-created")
public void handleUserCreated(UserCreatedEvent event) {
    notificationService.sendWelcomeEmail(event.getUserId());
}
```"

---

### Q: "What's your disaster recovery plan?"

**Answer:**
"DR planning covers: RTO (Recovery Time Objective) and RPO (Recovery Point Objective).

**Our SLAs:**
- RTO: 1 hour (system back online)
- RPO: 5 minutes (max data loss)

**Database DR:**
```yaml
# PostgreSQL with cross-region replication
Primary: ap-south-1 (Mumbai)
Standby: ap-southeast-1 (Singapore)

Replication: Synchronous for critical tables, async for others
Failover: Automated with AWS RDS Multi-AZ

RPO: 0 for critical data (sync replication)
RTO: ~10 minutes (automated failover)
```

**Application DR:**
```yaml
# Multi-region Kubernetes deployment
Regions:
  Primary: ap-south-1
    - All services active
    - Handles 100% traffic
  
  Secondary: ap-southeast-1
    - All services deployed but scaled down
    - Receives replicated data
    - Can scale up in 10 minutes

Traffic failover: AWS Route 53 health checks
Failover trigger: 
  - Primary health check fails for 30 seconds
  - OR manual trigger via runbook
```

**Data backup strategy:**
```yaml
Databases:
  - Automated snapshots: Every 6 hours
  - Retained: 30 days
  - Cross-region copy: Daily
  - Test restore: Monthly

Kafka:
  - Topics replicated to S3 (Kafka Connect)
  - Retained: 7 days
  - Used for replay if needed

Elasticsearch:
  - Snapshots to S3: Every 6 hours
  - Retained: 14 days
  - Note: ES is derived data, can be rebuilt from source DB
```

**Runbook for failover:**
```markdown
## DR Failover Runbook

### Pre-conditions
- [ ] Primary region health check failing > 5 min
- [ ] Secondary region health check passing
- [ ] On-call lead approval

### Steps
1. Scale up secondary region services
   ```
   kubectl --context=secondary scale deployment --all --replicas=5
   ```

2. Promote secondary database to primary
   ```
   aws rds promote-read-replica --db-instance-identifier prod-secondary
   ```

3. Update DNS to point to secondary
   ```
   aws route53 change-resource-record-sets ...
   ```

4. Verify traffic flowing to secondary
   ```
   Check Grafana dashboard: Traffic by region
   ```

5. Communicate to stakeholders
   - Post in #incidents
   - Send status page update
```

**DR testing:**
- Quarterly: Full failover test (during maintenance window)
- Monthly: Backup restore test
- Weekly: Health check verification"

---

## Section 4: Trade-off Discussions

### Q: "Consistency vs Availability in your Elasticsearch sync?"

**Answer:**
"We chose **availability over strong consistency** for the search use case. Here's why:

**The CAP theorem reality:**
```
Network partitions WILL happen
Must choose: Consistency OR Availability

For search:
- Missing 1 recent transaction in results = Acceptable
- Search being completely down = Not acceptable
```

**Our design decisions:**

| Decision | Trade-off |
|----------|-----------|
| Async CDC sync | May lag up to 5 seconds |
| No two-phase commit | Transaction might exist in DB but not yet in ES |
| Retry on failure | Some writes may retry (idempotent handling) |

**How we mitigate consistency gaps:**

```java
// For critical "must be up-to-date" queries
public Transaction getTransaction(String txnId) {
    // Primary read from source of truth
    return transactionRepository.findById(txnId);  // SQL
}

// For search/filter (can be slightly stale)
public List<Transaction> searchTransactions(SearchQuery query) {
    return elasticsearchClient.search(query);  // ES
}

// UI shows disclaimer
"Search results may take a few seconds to reflect recent transactions"
```

**Monitoring the consistency gap:**
```promql
# Track sync lag
kafka_consumer_group_lag{topic="db-cdc"} 

# Alert if lag too high
- alert: ESyncLagHigh
  expr: kafka_consumer_group_lag{topic="db-cdc"} > 10000
  for: 5m
  labels:
    severity: warning
```

**When we DO need consistency:**
```java
// After user creates transaction, they view it
// Use "read your writes" pattern

public Transaction createTransaction(CreateRequest request) {
    Transaction txn = transactionRepository.save(request.toTransaction());
    
    // Sync write to ES (blocking, for this user's session only)
    elasticsearchClient.index(txn);
    
    // Also publish to CDC for other consumers
    kafkaTemplate.send("transaction-created", txn);
    
    return txn;
}
```"

---

### Q: "Why was eventual consistency acceptable?"

**Answer:**
"It was acceptable because of the use case characteristics and our mitigations:

**Use case analysis:**
```
Search use case:
- User searching for transactions from last month
- A 5-second delay in seeing a just-created transaction = No impact
- Most searches are for historical data anyway

Dashboard use case:
- Aggregations (total transactions this month)
- 5-second lag on a monthly total = Negligible

Real-time use case:
- User views their own just-created transaction
- This DOES need consistency
- Solved with read-your-writes pattern
```

**Business agreement:**
- Discussed with Product: 'Is 5-second delay acceptable?'
- Answer: 'Yes, for search. No, for user's own recent activity.'
- Designed accordingly

**SLA documentation:**
```markdown
## Search Service SLA

### Consistency
- Search results may lag behind source of truth by up to 30 seconds
- For time-sensitive queries, use the Transaction API directly

### Availability
- Target: 99.95% uptime
- Search remains available even during database issues
```

**Trade-off was worth it because:**
1. 10x better query performance (async indexing)
2. Higher availability (ES can operate independently)
3. Simpler architecture (no distributed transactions)
4. Lower cost (no synchronous replication overhead)"

---

### Q: "What's the cost of your observability infrastructure?"

**Answer:**
"Transparency about costs is important for senior roles:

**Our observability stack costs:**

| Component | Monthly Cost | Notes |
|-----------|--------------|-------|
| OTel Collectors (3 pods) | ~$200 | 2 CPU, 4GB each |
| Jaeger (with Cassandra) | ~$600 | 8 CPU, 32GB, 500GB SSD |
| Prometheus + Thanos | ~$300 | 4 CPU, 16GB |
| Kinesis (logs) | ~$200 | Based on throughput |
| OpenSearch (log storage) | ~$300 | 3-node cluster |
| Grafana | ~$0 | Self-hosted |
| **Total** | **~$1,600/month** | |

**Comparison to SaaS alternatives:**

| Solution | Monthly Cost | Notes |
|----------|--------------|-------|
| Our stack | $1,600 | Self-managed |
| Datadog | ~$15,000 | Per-host pricing at our scale |
| New Relic | ~$12,000 | Similar to Datadog |
| Elastic Cloud | ~$5,000 | Managed ES + APM |

**ROI calculation:**
```
Cost savings: $15,000 - $1,600 = $13,400/month
Annual savings: $160,800

MTTR improvement: 2 hours → 48 minutes
Incidents/month: ~10
Time saved: 10 × 72 min = 12 hours/month
Engineer cost: $100/hour
Value: $1,200/month

Total annual value: $160,800 + $14,400 = $175,200

Engineering investment: 2 person-months = ~$40,000
Payback period: ~3 months
```

**Cost optimization strategies:**
1. Sampling traces at 10% for routine traffic
2. 7-day retention for detailed logs, 30-day for aggregates
3. Downsampling old metrics
4. Using spot instances for Prometheus/Thanos

**Trade-off acknowledged:**
More operational burden than SaaS, but:
- 10x cost savings
- Full control over data
- No vendor lock-in
- Custom integrations possible"
