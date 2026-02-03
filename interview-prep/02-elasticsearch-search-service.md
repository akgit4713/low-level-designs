# Elasticsearch Search Service Deep Dive

## 5-Minute Overview Script

"At Zeta, I built a high-performance search service using Elasticsearch to replace inefficient legacy SQL queries. The legacy system used complex JOINs across multiple tables with OFFSET-based pagination, which became increasingly slow as data grew to 10 million+ records.

I designed a denormalized Elasticsearch index with CDC-based synchronization from the source database. The new system reduced average query latency from 700ms to under 50ms - a 93% improvement. The key design decisions were: proper denormalization strategy, using search_after for pagination, and implementing a robust sync mechanism using Debezium for change data capture."

---

## Deep Dive Q&A

### Q1: "What was the legacy SQL query structure that was inefficient?"

**Answer:**
"The legacy queries had three main problems:

**Problem 1: Multiple JOINs**

```sql
SELECT 
    t.id, t.amount, t.status, t.created_at,
    u.name, u.email, u.phone,
    a.account_number, a.type as account_type,
    m.name as merchant_name, m.category
FROM transactions t
JOIN users u ON t.user_id = u.id
JOIN accounts a ON t.account_id = a.id
LEFT JOIN merchants m ON t.merchant_id = m.id
WHERE t.status IN ('COMPLETED', 'PENDING')
  AND t.created_at BETWEEN '2024-01-01' AND '2024-01-31'
  AND u.region = 'SOUTH'
  AND a.type = 'SAVINGS'
  AND (t.amount >= 1000 OR m.category = 'UTILITY')
ORDER BY t.created_at DESC
LIMIT 50 OFFSET 10000;
```

**Execution plan problems:**
- 4 table JOINs = nested loop joins
- No covering index possible for all filter combinations
- Full table scans on large tables

**Problem 2: OFFSET pagination**

```sql
-- Page 1: Fast (0.1s)
LIMIT 50 OFFSET 0

-- Page 100: Slow (2.5s)
LIMIT 50 OFFSET 5000

-- Page 1000: Very slow (15s+)
LIMIT 50 OFFSET 50000
```

OFFSET requires scanning and discarding N rows before returning results.

**Problem 3: Filter combinations explosion**

Users could filter by:
- Date range
- Status (5 values)
- Region (10 values)
- Account type (4 values)
- Amount range
- Merchant category (50+ values)

Creating composite indexes for all combinations was impractical:
- 5 × 10 × 4 × 50 = 10,000 possible filter combinations
- Index maintenance overhead
- Storage costs

**Latency breakdown:**

| Operation | Time |
|-----------|------|
| JOIN execution | 400ms |
| Filter application | 150ms |
| OFFSET scan | 100-5000ms |
| Sort | 50ms |
| **Total P50** | **700ms** |
| **Total P99** | **5,000ms** |"

---

### Q2: "How did you model the data in Elasticsearch?"

**Answer:**
"I used **denormalization** - embedding related entity data directly in the transaction document:

**Elasticsearch document structure:**

```json
{
  "transaction_id": "txn_12345",
  "amount": 15000.00,
  "currency": "INR",
  "status": "COMPLETED",
  "created_at": "2024-01-15T10:30:00Z",
  "updated_at": "2024-01-15T10:30:05Z",
  
  "user": {
    "id": "user_789",
    "name": "Anurag Kumar",
    "email": "anurag@example.com",
    "phone": "+91-9876543210",
    "region": "SOUTH",
    "tier": "PREMIUM"
  },
  
  "account": {
    "id": "acc_456",
    "account_number": "XXXX1234",
    "type": "SAVINGS",
    "bank_name": "HDFC Bank"
  },
  
  "merchant": {
    "id": "merch_111",
    "name": "Electricity Board",
    "category": "UTILITY",
    "mcc_code": "4900"
  },
  
  "metadata": {
    "channel": "MOBILE_APP",
    "device_id": "device_xyz",
    "ip_address": "192.168.1.1"
  }
}
```

**Index mappings:**

```json
{
  "mappings": {
    "properties": {
      "transaction_id": { "type": "keyword" },
      "amount": { "type": "scaled_float", "scaling_factor": 100 },
      "status": { "type": "keyword" },
      "created_at": { "type": "date" },
      
      "user": {
        "properties": {
          "id": { "type": "keyword" },
          "name": { 
            "type": "text",
            "fields": {
              "keyword": { "type": "keyword" }
            }
          },
          "region": { "type": "keyword" },
          "tier": { "type": "keyword" }
        }
      },
      
      "account": {
        "properties": {
          "type": { "type": "keyword" }
        }
      },
      
      "merchant": {
        "properties": {
          "name": { "type": "text" },
          "category": { "type": "keyword" }
        }
      }
    }
  }
}
```

**Why this structure:**

1. **No JOINs needed**: All data in single document
2. **Fast filtering**: `keyword` type for exact matches uses inverted index
3. **Text search**: `text` type for fuzzy/partial matching on names
4. **Efficient date ranges**: Native date type with range queries
5. **Multi-field mapping**: `name` searchable as both text and exact keyword

**Trade-offs accepted:**

| Aspect | Trade-off |
|--------|-----------|
| Storage | ~3x more than normalized (acceptable for search performance) |
| Updates | Must update transaction when user/merchant changes |
| Consistency | Eventual consistency (acceptable for search use case) |"

---

### Q3: "How do you handle consistency between SQL and Elasticsearch?"

**Answer:**
"We use **Change Data Capture (CDC) with Debezium** for near real-time sync:

**Architecture:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Sync Architecture                                 │
│                                                                         │
│  ┌─────────────┐                                                        │
│  │  MySQL DB   │                                                        │
│  │  (Source)   │                                                        │
│  │             │                                                        │
│  │  ┌───────┐  │     ┌─────────────┐     ┌─────────────────────────┐   │
│  │  │binlog │──┼────▶│  Debezium   │────▶│     Kafka Topics        │   │
│  │  └───────┘  │     │  Connector  │     │  ┌─────────────────┐    │   │
│  └─────────────┘     └─────────────┘     │  │ mysql.transactions│   │   │
│                                          │  │ mysql.users       │   │   │
│                                          │  │ mysql.merchants   │   │   │
│                                          │  └─────────────────┘    │   │
│                                          └───────────┬─────────────┘   │
│                                                      │                  │
│                                                      ▼                  │
│                                          ┌─────────────────────────┐   │
│                                          │    Sync Service         │   │
│                                          │  ┌───────────────────┐  │   │
│                                          │  │ 1. Consume CDC    │  │   │
│                                          │  │ 2. Denormalize    │  │   │
│                                          │  │ 3. Write to ES    │  │   │
│                                          │  └───────────────────┘  │   │
│                                          └───────────┬─────────────┘   │
│                                                      │                  │
│                                                      ▼                  │
│                                          ┌─────────────────────────┐   │
│                                          │    Elasticsearch        │   │
│                                          │    transactions index   │   │
│                                          └─────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
```

**Sync service logic:**

```java
@KafkaListener(topics = "mysql.transactions")
public void handleTransactionChange(DebeziumEvent event) {
    if (event.getOperation() == Operation.DELETE) {
        esClient.delete("transactions", event.getBefore().getId());
        return;
    }
    
    Transaction txn = event.getAfter();
    
    // Fetch related entities (cached with TTL)
    User user = userCache.get(txn.getUserId());
    Account account = accountCache.get(txn.getAccountId());
    Merchant merchant = merchantCache.get(txn.getMerchantId());
    
    // Build denormalized document
    TransactionDocument doc = TransactionDocument.builder()
        .transactionId(txn.getId())
        .amount(txn.getAmount())
        .status(txn.getStatus())
        .createdAt(txn.getCreatedAt())
        .user(UserEmbedded.from(user))
        .account(AccountEmbedded.from(account))
        .merchant(MerchantEmbedded.from(merchant))
        .build();
    
    // Upsert to Elasticsearch
    esClient.index("transactions", doc.getTransactionId(), doc);
}

@KafkaListener(topics = "mysql.users")
public void handleUserChange(DebeziumEvent event) {
    if (event.getOperation() == Operation.DELETE) return;
    
    User user = event.getAfter();
    
    // Invalidate cache
    userCache.invalidate(user.getId());
    
    // Update all transactions for this user
    // Using bulk update with script
    UpdateByQueryRequest request = new UpdateByQueryRequest("transactions")
        .setQuery(QueryBuilders.termQuery("user.id", user.getId()))
        .setScript(new Script(
            ScriptType.INLINE,
            "painless",
            "ctx._source.user = params.user",
            Map.of("user", UserEmbedded.from(user))
        ));
    
    esClient.updateByQuery(request);
}
```

**Handling eventual consistency:**

1. **SLA definition**: Max lag of 5 seconds acceptable for search
2. **Monitoring**: Kafka consumer lag alerts at 10 seconds
3. **User expectation**: UI shows "Search results may be slightly delayed"
4. **Critical paths**: For real-time needs, query source DB directly

**Consistency guarantees:**

| Scenario | Handling |
|----------|----------|
| Transaction created | Visible in ES within 2-3 seconds |
| User profile updated | Transactions updated within 10 seconds |
| Transaction deleted | Removed from ES within 2 seconds |
| Source DB failure | CDC continues from last offset on recovery |
| ES failure | Events buffered in Kafka, replayed on recovery |"

---

### Q4: "What sharding strategy did you use for 10M+ records?"

**Answer:**
"We designed for 100M records (10x growth) with the following strategy:

**Index configuration:**

```json
{
  "settings": {
    "number_of_shards": 5,
    "number_of_replicas": 1,
    "refresh_interval": "1s",
    "index": {
      "sort.field": ["created_at"],
      "sort.order": ["desc"]
    }
  }
}
```

**Shard sizing rationale:**

- Current: 10M documents × 2KB average = 20GB
- Target: 100M documents = 200GB
- Optimal shard size: 30-50GB
- 200GB / 40GB = 5 shards

**Time-based indexing for retention:**

```
transactions-2024-01    (January 2024)
transactions-2024-02    (February 2024)
...
transactions-2024-12    (December 2024)

Alias: transactions → transactions-2024-*
```

**Index lifecycle management:**

```json
{
  "policy": {
    "phases": {
      "hot": {
        "actions": {
          "rollover": {
            "max_size": "50gb",
            "max_age": "30d"
          }
        }
      },
      "warm": {
        "min_age": "30d",
        "actions": {
          "shrink": { "number_of_shards": 1 },
          "forcemerge": { "max_num_segments": 1 }
        }
      },
      "cold": {
        "min_age": "90d",
        "actions": {
          "freeze": {}
        }
      },
      "delete": {
        "min_age": "365d",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}
```

**Query routing:**

```java
// Most searches are recent data
// Route to specific month indices when date filter provided
public SearchResponse search(SearchRequest request) {
    String[] indices = calculateTargetIndices(request.getDateRange());
    
    // If date range is last 30 days, only search current month
    // Reduces shards to scan from 60 (yearly) to 5 (monthly)
    
    return esClient.search(request, indices);
}
```

**Scaling considerations:**

| Data Size | Shards | Replicas | Nodes |
|-----------|--------|----------|-------|
| 10M (current) | 5 | 1 | 3 |
| 50M | 10 | 1 | 5 |
| 100M | 15 | 1 | 7 |
| 500M | Time-based + 5/month | 1 | 10+ |"

---

### Q5: "How did you measure 93% latency reduction?"

**Answer:**
"We used a comprehensive measurement approach:

**Before/After comparison:**

| Metric | Before (SQL) | After (ES) | Improvement |
|--------|--------------|------------|-------------|
| P50 latency | 700ms | 35ms | 95% |
| P99 latency | 5,000ms | 120ms | 97.6% |
| Average latency | 850ms | 48ms | 94.4% |
| Throughput | 50 QPS | 2,000 QPS | 40x |

**The "93%" claim**: Average latency reduced from 700ms to 48ms = 93.1% reduction

**Measurement methodology:**

1. **Production traffic sampling:**
```java
@Around("execution(* SearchService.search(..))")
public Object measureLatency(ProceedingJoinPoint joinPoint) {
    long start = System.nanoTime();
    try {
        return joinPoint.proceed();
    } finally {
        long duration = System.nanoTime() - start;
        metrics.timer("search.latency").record(duration, TimeUnit.NANOSECONDS);
        
        // Log slow queries
        if (duration > 100_000_000) { // > 100ms
            log.warn("Slow query: {} ms, params: {}", 
                duration / 1_000_000,
                joinPoint.getArgs());
        }
    }
}
```

2. **A/B testing during rollout:**
```
10% traffic → new ES system
90% traffic → old SQL system

Compare latency distributions side by side
```

3. **Load testing:**
```yaml
# Gatling test configuration
scenario:
  users: 1000
  duration: 30m
  queries:
    - simple_filter: 40%
    - multi_filter: 35%
    - pagination: 15%
    - full_text: 10%
```

**Dashboards built:**

```
┌─────────────────────────────────────────────────────────┐
│  Search Service Performance Dashboard                    │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐              │
│  │ P50 Latency     │  │ P99 Latency     │              │
│  │     35ms        │  │     120ms       │              │
│  │ ▼ 95% from SQL  │  │ ▼ 97% from SQL  │              │
│  └─────────────────┘  └─────────────────┘              │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │ Latency over time (last 24h)                     │   │
│  │ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │ Query types distribution                         │   │
│  │ Filter only: 65% | Pagination: 25% | Search: 10% │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```"

---

### Q6: "What about write-heavy operations?"

**Answer:**
"Our system is read-heavy (95% reads, 5% writes), but we optimized writes too:

**Write optimization strategies:**

1. **Bulk indexing:**
```java
@Scheduled(fixedRate = 1000) // Every second
public void bulkIndex() {
    List<TransactionDocument> batch = buffer.drain(1000);
    if (batch.isEmpty()) return;
    
    BulkRequest bulk = new BulkRequest();
    for (TransactionDocument doc : batch) {
        bulk.add(new IndexRequest("transactions")
            .id(doc.getTransactionId())
            .source(objectMapper.writeValueAsString(doc), XContentType.JSON));
    }
    
    BulkResponse response = esClient.bulk(bulk);
    if (response.hasFailures()) {
        // Retry failed items
        retryQueue.addAll(extractFailedItems(response, batch));
    }
}
```

2. **Refresh interval tuning:**
```json
{
  "settings": {
    "refresh_interval": "5s"  // Default is 1s
  }
}
```
- Trade-off: Data visible in 5s instead of 1s
- Benefit: 5x reduction in refresh overhead

3. **Indexing buffer size:**
```json
{
  "settings": {
    "index.buffer_size": "512mb"
  }
}
```

4. **Translog durability:**
```json
{
  "settings": {
    "translog.durability": "async",
    "translog.sync_interval": "5s"
  }
}
```
- Trade-off: Potential data loss of 5s on crash
- Mitigation: Kafka retains events for replay

**Write throughput achieved:**
- Bulk indexing: 10,000 docs/second
- Single document: 500 docs/second

**During peak write scenarios (batch processing):**

```java
// Temporarily disable refresh for bulk loads
esClient.indices().putSettings(
    new UpdateSettingsRequest("transactions")
        .settings(Settings.builder()
            .put("refresh_interval", "-1")
        )
);

// Bulk load millions of records
bulkLoadTransactions();

// Re-enable refresh and force refresh
esClient.indices().putSettings(
    new UpdateSettingsRequest("transactions")
        .settings(Settings.builder()
            .put("refresh_interval", "1s")
        )
);
esClient.indices().refresh(new RefreshRequest("transactions"));
```"

---

## Pagination: search_after vs OFFSET

**Why search_after:**

```java
// First page
SearchRequest firstPage = new SearchRequest("transactions")
    .source(new SearchSourceBuilder()
        .query(query)
        .sort("created_at", SortOrder.DESC)
        .sort("_id", SortOrder.ASC)  // Tie-breaker
        .size(50)
    );

SearchResponse response = esClient.search(firstPage);

// Get sort values of last hit
Object[] lastSortValues = response.getHits().getHits()[49].getSortValues();
// [1705312200000, "txn_12345"]

// Next page - O(1) instead of O(N)
SearchRequest nextPage = new SearchRequest("transactions")
    .source(new SearchSourceBuilder()
        .query(query)
        .sort("created_at", SortOrder.DESC)
        .sort("_id", SortOrder.ASC)
        .searchAfter(lastSortValues)
        .size(50)
    );
```

**Performance comparison:**

| Page | OFFSET time | search_after time |
|------|-------------|-------------------|
| 1 | 35ms | 35ms |
| 100 | 250ms | 35ms |
| 1000 | 2,500ms | 36ms |
| 10000 | 25,000ms | 37ms |

---

## Common Follow-up Questions

### "What if a user updates their profile? How do you update all their transactions?"

"We use update_by_query with a script:

```java
UpdateByQueryRequest request = new UpdateByQueryRequest("transactions")
    .setQuery(QueryBuilders.termQuery("user.id", userId))
    .setScript(new Script(
        ScriptType.INLINE,
        "painless",
        "ctx._source.user.name = params.name; ctx._source.user.region = params.region",
        Map.of("name", newName, "region", newRegion)
    ))
    .setSlices(5)  // Parallelize across shards
    .setConflicts("proceed");  // Continue on version conflicts
```

For users with millions of transactions, we batch this and run during off-peak hours."

### "How do you handle ES cluster failures?"

"Multi-layer resilience:

1. **Replica shards**: Every shard has 1 replica on different node
2. **Circuit breaker**: If ES is down, return cached results or graceful degradation
3. **Fallback**: Critical queries can fall back to SQL (slower but available)
4. **Kafka buffering**: Write events buffered during outage, replayed on recovery"

### "How do you handle schema changes?"

"Zero-downtime reindexing using aliases:

```
1. Create new index: transactions_v2
2. Set up dual-write: write to both v1 and v2
3. Backfill v2 from v1 using reindex API
4. Verify v2 completeness
5. Swap alias: transactions → transactions_v2
6. Stop writes to v1
7. Delete v1 after grace period
```"
