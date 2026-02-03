# Mock Interview Practice Guide

## Overview

This guide provides structured practice formats for resume deep-dive interviews. Practice each project in both quick-overview and deep-dive formats.

---

## Interview Format for Senior SDE

**Typical structure (45-60 minutes):**
```
0-5 min:   Introductions
5-10 min:  "Walk me through your background"
10-35 min: Deep dive into 1-2 projects
35-50 min: Technical follow-ups / What-if scenarios
50-60 min: Your questions for interviewer
```

---

## Practice Session 1: Workflow Engine Redesign

### 5-Minute Overview (Practice saying this out loud)

> "At Zeta, I led the redesign of our workflow engine from a Camunda-based monolithic system to an event-driven microservices architecture using Java, Kafka, and Kubernetes.
>
> **The problem**: Our Camunda engine stored all process state in a single database. Under high load, we experienced row-level lock contention in tables like ACT_RU_EXECUTION, causing 15% transaction failures due to lock timeouts.
>
> **My solution**: I designed an event-driven architecture where each workflow step is an independent microservice communicating through Kafka topics. Each service owns its own data, eliminating shared database locks entirely.
>
> **Key technical decisions**: 
> - Partitioned Kafka topics by workflow instance ID for ordering guarantees
> - Implemented Saga pattern with Outbox for distributed transactions
> - Used idempotency keys for exactly-once processing semantics
>
> **Results**: 40% throughput improvement, P99 latency dropped from 2.5s to 180ms, and zero lock-related failures."

### 15-Minute Deep Dive Practice

**Have a friend ask these questions in order:**

1. "Can you draw the before and after architecture?" (3 min)
   - Practice on whiteboard or paper
   - Show data flow, not just boxes

2. "What specific database locks were the problem?" (2 min)
   - Name the tables: ACT_RU_EXECUTION, ACT_RU_TASK
   - Explain when locks occur

3. "Why Kafka over other message queues?" (2 min)
   - Mention: ordering, durability, replay, consumer groups
   - Acknowledge trade-off: complexity

4. "How did you handle distributed transactions?" (3 min)
   - Explain Saga pattern
   - Show Outbox pattern code/diagram

5. "What was your migration strategy?" (3 min)
   - Strangler Fig pattern
   - Canary deployment percentages
   - Rollback triggers

6. "What would you do differently?" (2 min)
   - Event sourcing from start
   - Schema registry earlier

---

## Practice Session 2: Elasticsearch Search Service

### 5-Minute Overview

> "I built a high-performance search service using Elasticsearch to replace inefficient legacy SQL queries at Zeta.
>
> **The problem**: Our transaction search used complex SQL JOINs across 4 tables with OFFSET-based pagination. With 10 million records, queries took 5+ seconds at P99.
>
> **My solution**: I designed a denormalized Elasticsearch index where all related data is embedded in each transaction document. For sync, I used Debezium CDC to capture database changes and keep Elasticsearch updated in near real-time.
>
> **Key technical decisions**:
> - Denormalization: Embedded user, account, merchant data in each transaction
> - search_after pagination instead of OFFSET for deep pagination
> - Index lifecycle management for time-based data retention
>
> **Results**: Average query latency dropped from 700ms to 48ms - a 93% reduction. P99 went from 5 seconds to 120ms."

### 15-Minute Deep Dive Practice

1. "Show me the problematic SQL query" (2 min)
   - Multiple JOINs
   - OFFSET pagination
   - No covering index possible

2. "How did you model the data in Elasticsearch?" (3 min)
   - Show document structure
   - Explain mapping types (keyword, text, nested)

3. "How do you handle consistency between SQL and ES?" (3 min)
   - CDC with Debezium
   - Acceptable lag SLA
   - What happens when user updates profile?

4. "What sharding strategy for 10M records?" (2 min)
   - 5 shards, sizing rationale
   - Time-based indices
   - Query routing

5. "How did you handle writes?" (2 min)
   - Bulk indexing
   - Refresh interval tuning

6. "What changes for 1 billion records?" (3 min)
   - More shards, time-based
   - Caching layers
   - Query routing optimization

---

## Practice Session 3: Observability Stack

### 5-Minute Overview

> "I designed a centralized observability stack for 12+ microservices at Zeta using OpenTelemetry, Kinesis, and Jaeger.
>
> **The problem**: Debugging production issues was painful. Logs were scattered across services, there was no way to trace a request across the system, and engineers spent hours piecing together what happened during incidents.
>
> **My solution**: I implemented distributed tracing using OpenTelemetry with automatic context propagation. Every request gets a trace ID that flows through all services via W3C Trace Context headers. Logs are correlated with traces via MDC.
>
> **Key technical decisions**:
> - OpenTelemetry for vendor neutrality
> - OTel Collector for aggregation and routing
> - Tail-based sampling to capture interesting traces
> - SLO-based alerting instead of threshold-based
>
> **Results**: Mean Time to Resolution dropped by 60% - from 2 hours to 48 minutes average for production incidents."

### 15-Minute Deep Dive Practice

1. "Why OpenTelemetry over Datadog?" (2 min)
   - Vendor neutrality, cost, control
   - Acknowledge trade-off: more ops work

2. "How do traces flow across services?" (3 min)
   - Draw the flow
   - W3C Trace Context headers
   - Kafka header propagation

3. "How did you reduce MTTR by 60%?" (3 min)
   - Trace-linked alerts
   - Error span attributes
   - Service dependency map

4. "What's your alerting strategy?" (3 min)
   - SLO-based vs threshold
   - Error budget burn rate
   - Multi-window alerts

5. "How do you handle high-cardinality?" (2 min)
   - Don't put user_id in labels
   - Use exemplars
   - Logs for high-cardinality data

6. "What's the infrastructure cost?" (2 min)
   - Know the numbers
   - Compare to SaaS alternatives

---

## Practice Session 4: GenAI CI/CD Agent

### 5-Minute Overview

> "I created a GenAI agent for automated CI/CD failure analysis at Zeta using RAG architecture.
>
> **The problem**: Engineers spent 30-45 minutes triaging each build failure - reading logs, searching Slack, checking if someone had seen the error before. 70% of failures were actually repeat issues with known solutions.
>
> **My solution**: I built a RAG pipeline that parses build logs, searches our knowledge base of historical RCAs from Confluence and Jira, and generates a likely root cause with citations. For high-confidence matches, it auto-creates Jira tickets.
>
> **Key technical decisions**:
> - Semantic chunking of documents (not fixed-size)
> - Hybrid search combining vector similarity and keyword matching
> - Confidence scoring with human-in-the-loop for low scores
> - Prompt engineering for grounded generation with citations
>
> **Results**: Triage time reduced by 85% - from 45 minutes to under 5 minutes. 78% accuracy on RCA suggestions."

### 15-Minute Deep Dive Practice

1. "Explain the RAG architecture" (3 min)
   - Draw the pipeline
   - Each step: extract, embed, retrieve, generate

2. "How did you chunk documents?" (2 min)
   - Semantic vs fixed-size
   - By headers for Confluence
   - Metadata enrichment

3. "What's your retrieval strategy?" (3 min)
   - Hybrid search
   - Reciprocal Rank Fusion

4. "How do you handle hallucinations?" (3 min)
   - Grounded generation with citations
   - Citation verification
   - Confidence scoring

5. "How do you measure accuracy?" (2 min)
   - Explicit feedback
   - Implicit signals (ticket resolution time)

6. "How would you scale 100x?" (2 min)
   - Batching embeddings
   - Caching common failures
   - Cheaper models for classification

---

## Practice Session 5: NoBroker Migration

### 5-Minute Overview

> "At NoBroker, I led the migration from a Java 7 monolith to Spring Boot microservices with Kafka event streaming.
>
> **The problem**: The monolith had become a deployment bottleneck. Any change required full application deployment, and teams were blocking each other. We had weekly releases at best.
>
> **My solution**: I used the Strangler Fig pattern to incrementally extract services. We started with high-change-frequency modules like Communication, added an API gateway for routing, and gradually shifted traffic while maintaining backward compatibility.
>
> **Key technical decisions**:
> - Domain-Driven Design for service boundaries
> - Anti-corruption layer for legacy integration
> - Database views for backward compatibility during transition
> - Quartz clustering with per-service table prefixes
>
> **Results**: Deployment frequency improved from weekly to daily releases. Independent team deployments enabled faster feature delivery."

### 15-Minute Deep Dive Practice

1. "How did you identify service boundaries?" (3 min)
   - Event storming
   - Coupling analysis
   - Change frequency analysis

2. "Explain your Strangler Fig implementation" (3 min)
   - API gateway routing
   - Traffic shifting percentages
   - Anti-corruption layer

3. "How did you handle the shared database?" (3 min)
   - Database views
   - Dual write during transition
   - CDC for sync

4. "What challenges with Quartz?" (3 min)
   - Job ownership
   - Table prefix per service
   - Clustering configuration

5. "What would you do differently?" (3 min)
   - Database split earlier
   - Better integration tests

---

## Mock Interview Checklist

### Before Practice:
- [ ] Set up timer for each section
- [ ] Have whiteboard or paper ready
- [ ] Practice speaking out loud (not just thinking)
- [ ] Review key metrics for each project

### During Practice:
- [ ] Start with context (problem before solution)
- [ ] Use specific numbers (not "improved a lot")
- [ ] Draw diagrams proactively
- [ ] Acknowledge trade-offs
- [ ] Show your decision-making process

### After Practice:
- [ ] Did you stay within time limits?
- [ ] Did you cover all key points?
- [ ] Were you clear about YOUR contribution vs team's?
- [ ] Did you mention what you'd do differently?

---

## Quick Reference: Key Metrics

### Zeta Projects
| Project | Key Metrics |
|---------|-------------|
| Workflow Engine | 40% throughput ↑, P99: 2.5s→180ms, 0 lock failures |
| Elasticsearch | 93% latency ↓ (700ms→48ms), 10M records, <50ms avg |
| Observability | 60% MTTR ↓ (2hr→48min), 12+ services, $1.6K/mo |
| GenAI Agent | 85% triage time ↓ (45min→5min), 78% accuracy |

### NoBroker Projects
| Project | Key Metrics |
|---------|-------------|
| Monolith Migration | Weekly→Daily releases, 6 services extracted |
| WhatsApp Chatbot | 10x conversion (20→200 users/day), 120% revenue ↑ |
| Config Updates | Zero-downtime config changes, @RefreshScope |

---

## Behavioral Story Quick Access

| Question Type | Story to Use |
|---------------|--------------|
| Led an initiative | Workflow Engine Redesign |
| Influenced without authority | Elasticsearch Investment |
| Conflict with colleague | Observability Disagreement |
| Mentored someone | Junior Engineer on Chatbot |
| Handled ambiguity | GenAI Agent Scoping |
| Production crisis | Migration Incident |
| Why software? | Civil to Software Transition |

---

## Practice Schedule Recommendation

### Week 1: Technical Deep Dives
- Day 1-2: Workflow Engine (most complex)
- Day 3-4: Elasticsearch
- Day 5: Observability
- Day 6: GenAI Agent
- Day 7: NoBroker projects

### Week 2: Behavioral + Polish
- Day 1-2: Practice all behavioral stories
- Day 3-4: Mock interviews with friend
- Day 5: Record yourself, review
- Day 6: Focus on weak areas
- Day 7: Rest before interview

### Daily Practice Routine (1 hour)
```
15 min: One 5-minute overview + Q&A
30 min: One deep-dive session
15 min: One behavioral story practice
```

---

## Interview Day Tips

1. **Bring notes** (if virtual)
   - Key metrics on sticky notes
   - Architecture diagrams sketched

2. **Control the narrative**
   - "Would you like me to go deeper on X or move to Y?"
   - "The most interesting challenge was..."

3. **Handle "I don't know"**
   - "I haven't worked with that specifically, but here's how I'd approach it..."
   - "Let me think through this..."

4. **End strong**
   - "Is there anything else you'd like me to clarify?"
   - Have 2-3 genuine questions ready

---

## Sample Questions to Ask Interviewer

**About the role:**
- "What does the first 90 days look like for this role?"
- "What's the biggest technical challenge the team is facing?"

**About the team:**
- "How do you balance new features vs technical debt?"
- "What's the on-call rotation like?"

**About growth:**
- "What does career progression look like for senior engineers here?"
- "How do engineers influence technical direction?"

**Red flag questions (to assess company):**
- "What's your deployment frequency?"
- "Tell me about a recent production incident and how it was handled."
- "How much autonomy do engineers have in choosing technical solutions?"
