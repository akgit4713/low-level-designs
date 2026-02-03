# Observability Stack Deep Dive: OpenTelemetry, Kinesis, Jaeger

## 5-Minute Overview Script

"At Zeta, I designed a centralized observability stack for 12+ microservices using OpenTelemetry for instrumentation, Kinesis for log aggregation, and Jaeger for distributed tracing. Before this, debugging production issues was painful - logs were scattered across services, there was no way to correlate a single user request across multiple services, and engineers spent hours manually piecing together what happened.

The new system provides a unified view: every request gets a trace ID that flows through all services, logs are correlated with traces, and we have dashboards that show service dependencies and latency breakdowns. This reduced our Mean Time to Resolution by 60% - from an average of 2 hours to 48 minutes for production incidents."

---

## Deep Dive Q&A

### Q1: "Why OpenTelemetry over vendor-specific solutions?"

**Answer:**
"We evaluated three approaches:

| Criteria | OpenTelemetry | Datadog APM | AWS X-Ray |
|----------|---------------|-------------|-----------|
| Vendor lock-in | None ✓ | High | AWS-specific |
| Cost | OSS (infra only) | $$$/month | Moderate |
| Language support | All major ✓ | All major | Limited |
| Backend flexibility | Any ✓ | Datadog only | X-Ray only |
| Community | Large, active ✓ | Vendor | AWS |
| Kubernetes integration | Native ✓ | Good | Moderate |

**Why OpenTelemetry won:**

1. **Vendor neutrality**: We could use Jaeger today, switch to Tempo tomorrow without code changes.

2. **Single instrumentation**: One SDK for traces, metrics, and logs. Before, we had separate libraries for each.

3. **Auto-instrumentation**: Spring Boot, Kafka, HTTP clients - all instrumented automatically.

```java
// Before: Manual instrumentation everywhere
Span span = tracer.buildSpan("process-payment").start();
try {
    // business logic
} finally {
    span.finish();
}

// After: Zero-code instrumentation via agent
// Just add JVM argument: -javaagent:opentelemetry-javaagent.jar
```

4. **Industry standard**: CNCF graduated project, widely adopted.

5. **Cost control**: We run our own Jaeger and Prometheus, no per-host licensing fees.

**Trade-off acknowledged**: More operational overhead than managed solutions, but we had the Kubernetes expertise."

---

### Q2: "How did you correlate traces across 12+ microservices?"

**Answer:**
"We use **W3C Trace Context** standard for context propagation:

**Trace context headers:**

```
traceparent: 00-0af7651916cd43dd8448eb211c80319c-b7ad6b7169203331-01
             |  |                                |                |
             |  |                                |                └─ Flags (sampled)
             |  |                                └─ Parent span ID
             |  └─ Trace ID (128-bit)
             └─ Version

tracestate: zeta=user_id:12345;priority:high
```

**Propagation flow:**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Request Flow with Trace Context                       │
│                                                                             │
│  Client Request                                                             │
│      │                                                                      │
│      ▼                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ API Gateway                                                          │   │
│  │ Creates: TraceID=abc123, SpanID=span1                               │   │
│  │ Headers: traceparent: 00-abc123-span1-01                            │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│      │                                                                      │
│      ▼                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ User Service                                                         │   │
│  │ Receives: TraceID=abc123, ParentSpanID=span1                        │   │
│  │ Creates: SpanID=span2                                                │   │
│  │ Propagates: traceparent: 00-abc123-span2-01                         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│      │                          │                                          │
│      ▼                          ▼                                          │
│  ┌──────────────────┐   ┌──────────────────┐                              │
│  │ Auth Service     │   │ Profile Service  │                              │
│  │ TraceID=abc123   │   │ TraceID=abc123   │                              │
│  │ SpanID=span3     │   │ SpanID=span4     │                              │
│  └──────────────────┘   └──────────────────┘                              │
│      │                          │                                          │
│      └──────────┬───────────────┘                                          │
│                 ▼                                                           │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ Database calls, Kafka messages - all carry same TraceID             │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Implementation:**

```java
// OpenTelemetry auto-instrumentation handles HTTP propagation
// For Kafka, we use interceptors:

@Configuration
public class KafkaConfig {
    
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // Add tracing interceptor
        config.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, 
            TracingProducerInterceptor.class.getName());
        
        return new DefaultKafkaProducerFactory<>(config);
    }
}

// Custom interceptor for trace propagation
public class TracingProducerInterceptor implements ProducerInterceptor<String, Object> {
    
    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> record) {
        Span currentSpan = Span.current();
        SpanContext context = currentSpan.getSpanContext();
        
        // Inject trace context into Kafka headers
        record.headers().add("traceparent", 
            String.format("00-%s-%s-01", 
                context.getTraceId(), 
                context.getSpanId()).getBytes());
        
        return record;
    }
}
```

**Correlation with logs:**

```java
// MDC (Mapped Diagnostic Context) integration
public class TraceIdLoggingFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        Span span = Span.current();
        MDC.put("traceId", span.getSpanContext().getTraceId());
        MDC.put("spanId", span.getSpanContext().getSpanId());
        
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}

// Log pattern includes trace context
// logback.xml
<pattern>%d{ISO8601} [%thread] %-5level %logger - traceId=%X{traceId} spanId=%X{spanId} - %msg%n</pattern>
```

**Result in Jaeger UI:**

```
Trace: abc123 (User Registration Flow)
├── API Gateway (12ms)
│   └── User Service (45ms)
│       ├── Auth Service (8ms)
│       │   └── Redis Cache (2ms)
│       ├── Profile Service (15ms)
│       │   └── PostgreSQL (10ms)
│       └── Kafka Producer (3ms)
│           └── Notification Consumer (async)
└── Total: 58ms
```"

---

### Q3: "How did you reduce MTTR by 60%?"

**Answer:**
"MTTR reduction came from four key improvements:

**Before: Manual incident investigation**

```
Incident: User registration failing
Time: 2+ hours average

1. Check API Gateway logs (15 min) - "500 error"
2. SSH to user-service pod, grep logs (20 min) - "DB connection failed"  
3. Check database metrics (15 min) - looks fine
4. Realize it's profile-service, not user-service (30 min)
5. SSH to profile-service, find actual error (20 min)
6. Escalate to DBA, wait for response (30+ min)
```

**After: Observability-driven investigation**

```
Incident: User registration failing
Time: ~48 min average

1. Alert fires with trace link (0 min)
2. Click trace in Jaeger - see exact failing span (2 min)
3. Click span - see error message and stack trace (1 min)
4. Correlated logs show root cause (5 min)
5. Fix and deploy (40 min)
```

**Four key improvements:**

**1. Trace-based alerting:**

```yaml
# Prometheus alerting rule
- alert: HighErrorRate
  expr: |
    sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) 
    / 
    sum(rate(http_server_requests_seconds_count[5m])) > 0.01
  labels:
    severity: critical
  annotations:
    summary: "High error rate detected"
    dashboard: "https://grafana/d/errors?service={{ $labels.service }}"
    traces: "https://jaeger/search?service={{ $labels.service }}&tags=error:true"
```

**2. Error span attributes:**

```java
// Automatically capture error details in span
try {
    processPayment(request);
} catch (Exception e) {
    Span.current()
        .setStatus(StatusCode.ERROR, e.getMessage())
        .recordException(e)
        .setAttribute("error.type", e.getClass().getSimpleName())
        .setAttribute("user.id", request.getUserId())
        .setAttribute("payment.amount", request.getAmount());
    throw e;
}
```

**3. Service dependency map:**

```
┌─────────────────────────────────────────────────────────────────┐
│                   Jaeger Service Map                             │
│                                                                 │
│    ┌───────────┐         ┌───────────┐         ┌───────────┐   │
│    │   API GW  │────────▶│   User    │────────▶│   Auth    │   │
│    │  P99:50ms │         │  P99:80ms │         │  P99:20ms │   │
│    └───────────┘         └─────┬─────┘         └───────────┘   │
│                                │                               │
│                                ▼                               │
│                          ┌───────────┐                         │
│                          │  Profile  │                         │
│                          │  P99:45ms │ ◀── Bottleneck!         │
│                          │  Errors:5%│                         │
│                          └───────────┘                         │
└─────────────────────────────────────────────────────────────────┘
```

**4. Runbook integration:**

```yaml
# Alert with runbook link
- alert: ProfileServiceHighLatency
  annotations:
    runbook: "https://wiki/runbooks/profile-service-latency"
    # Runbook includes:
    # 1. Common causes
    # 2. Diagnostic queries
    # 3. Escalation contacts
    # 4. Remediation steps
```

**MTTR metrics tracking:**

```sql
-- MTTR calculation from incident management system
SELECT 
    DATE_TRUNC('month', created_at) as month,
    AVG(EXTRACT(EPOCH FROM (resolved_at - created_at)) / 60) as mttr_minutes
FROM incidents
WHERE severity IN ('P1', 'P2')
GROUP BY 1
ORDER BY 1;

-- Results:
-- Before (Jan-Mar): 120 minutes average
-- After (Apr-Jun): 48 minutes average
-- Improvement: 60%
```"

---

### Q4: "What alerting strategy did you implement?"

**Answer:**
"We moved from threshold-based to **SLO-based alerting**:

**Before: Threshold alerts (noisy)**

```yaml
# Old approach - generates alert fatigue
- alert: HighLatency
  expr: http_request_duration_seconds_p99 > 0.5
  # Fires constantly, many false positives
```

**After: SLO-based alerts (actionable)**

```yaml
# Define SLOs
slos:
  - name: api-availability
    target: 99.9%  # 43.2 min downtime/month allowed
    indicator:
      success: http_requests_total{status!~"5.."}
      total: http_requests_total
  
  - name: api-latency
    target: 95%
    indicator:
      success: http_request_duration_seconds_bucket{le="0.3"}
      total: http_request_duration_seconds_count

# Alert on error budget burn rate
- alert: ErrorBudgetBurn
  expr: |
    (
      1 - (
        sum(rate(http_requests_total{status!~"5.."}[1h]))
        /
        sum(rate(http_requests_total[1h]))
      )
    ) > (1 - 0.999) * 14.4
  labels:
    severity: critical
  annotations:
    summary: "Burning error budget 14.4x faster than sustainable"
    # At this rate, we'll exhaust monthly budget in 2 days
```

**Multi-window burn rate:**

```yaml
# Fast burn - alert immediately
- alert: ErrorBudgetFastBurn
  expr: |
    error_rate_1h > 14.4 * (1 - slo_target)
    AND
    error_rate_5m > 14.4 * (1 - slo_target)
  labels:
    severity: page

# Slow burn - alert for investigation
- alert: ErrorBudgetSlowBurn
  expr: |
    error_rate_6h > 6 * (1 - slo_target)
    AND
    error_rate_30m > 6 * (1 - slo_target)
  labels:
    severity: ticket
```

**Alert routing:**

```yaml
# Alertmanager configuration
route:
  receiver: default
  routes:
    - match:
        severity: page
      receiver: pagerduty
      repeat_interval: 5m
    
    - match:
        severity: ticket
      receiver: slack-and-jira
      repeat_interval: 4h
    
    - match:
        severity: info
      receiver: slack-only

receivers:
  - name: pagerduty
    pagerduty_configs:
      - service_key: xxx
        severity: critical
  
  - name: slack-and-jira
    slack_configs:
      - channel: '#incidents'
    webhook_configs:
      - url: 'http://jira-webhook/create-ticket'
```

**Result:**

| Metric | Before | After |
|--------|--------|-------|
| Alerts/week | 150+ | 20-30 |
| False positives | 60% | 10% |
| Pages (critical) | 25/week | 5/week |
| Alert acknowledgment time | 15 min | 3 min |"

---

### Q5: "How did you handle high-cardinality metrics?"

**Answer:**
"High cardinality is a common problem with observability. We faced it with metrics like:

- `user_id` (millions of unique values)
- `transaction_id` (unbounded)
- `ip_address` (millions)

**Strategies we used:**

**1. Don't put high-cardinality labels in metrics:**

```java
// BAD - creates millions of time series
Counter.builder("transactions_total")
    .tag("user_id", userId)  // Never do this!
    .tag("transaction_id", txnId)  // Never do this!
    .register(registry)
    .increment();

// GOOD - use bounded labels
Counter.builder("transactions_total")
    .tag("status", status)  // ~5 values
    .tag("type", type)  // ~10 values
    .tag("region", region)  // ~20 values
    .register(registry)
    .increment();
```

**2. Use exemplars for trace correlation:**

```java
// Add trace ID as exemplar, not label
Counter counter = Counter.builder("transactions_total")
    .tag("status", status)
    .register(registry);

// Exemplar links metric to specific trace
Exemplar exemplar = Exemplar.of(
    1.0,
    "trace_id", Span.current().getSpanContext().getTraceId()
);
counter.increment(1.0, exemplar);
```

**3. Aggregate then store:**

```yaml
# Recording rules for pre-aggregation
groups:
  - name: transaction-aggregations
    interval: 1m
    rules:
      # Pre-aggregate by region (reduces cardinality)
      - record: transactions:rate5m:by_region
        expr: sum by (region) (rate(transactions_total[5m]))
      
      # Pre-aggregate by status
      - record: transactions:rate5m:by_status
        expr: sum by (status) (rate(transactions_total[5m]))
```

**4. Logs for high-cardinality data:**

```java
// For user-specific debugging, use logs not metrics
log.info("Transaction processed", 
    kv("userId", userId),
    kv("transactionId", txnId),
    kv("amount", amount),
    kv("traceId", traceId));

// Query in log system when needed
// Kinesis → Elasticsearch → Kibana
```

**5. Cardinality limits in Prometheus:**

```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'app'
    relabel_configs:
      # Drop high-cardinality labels at scrape time
      - source_labels: [__name__]
        regex: 'http_request_duration_seconds_bucket'
        action: keep
      - regex: 'user_id|session_id|request_id'
        action: labeldrop
```

**Monitoring cardinality:**

```promql
# Alert on cardinality explosion
- alert: HighCardinalityMetric
  expr: |
    count by (__name__) ({__name__=~".+"}) > 100000
  labels:
    severity: warning
  annotations:
    summary: "Metric {{ $labels.__name__ }} has {{ $value }} series"
```"

---

## OTel Collector Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        OpenTelemetry Collector Pipeline                      │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                          RECEIVERS                                   │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │   │
│  │  │ OTLP (gRPC)  │  │ OTLP (HTTP)  │  │ Prometheus   │              │   │
│  │  │ :4317        │  │ :4318        │  │ :8888        │              │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                  │                                          │
│                                  ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                          PROCESSORS                                  │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐   │   │
│  │  │   Batch    │  │  Memory    │  │ Attributes │  │   Filter   │   │   │
│  │  │ (200, 5s)  │  │  Limiter   │  │  Processor │  │ (sampling) │   │   │
│  │  └────────────┘  └────────────┘  └────────────┘  └────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                  │                                          │
│                                  ▼                                          │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                          EXPORTERS                                   │   │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐              │   │
│  │  │ Jaeger       │  │ Prometheus   │  │ AWS Kinesis  │              │   │
│  │  │ (traces)     │  │ (metrics)    │  │ (logs)       │              │   │
│  │  └──────────────┘  └──────────────┘  └──────────────┘              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Collector configuration:**

```yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317
      http:
        endpoint: 0.0.0.0:4318

processors:
  batch:
    send_batch_size: 200
    timeout: 5s
  
  memory_limiter:
    limit_mib: 1000
    spike_limit_mib: 200
    check_interval: 5s
  
  attributes:
    actions:
      - key: environment
        value: production
        action: upsert
  
  filter:
    traces:
      span:
        # Drop health check spans
        - 'attributes["http.target"] == "/health"'

exporters:
  jaeger:
    endpoint: jaeger-collector:14250
    tls:
      insecure: true
  
  prometheusremotewrite:
    endpoint: http://prometheus:9090/api/v1/write
  
  awskinesis:
    stream_name: logs-stream
    region: ap-south-1

service:
  pipelines:
    traces:
      receivers: [otlp]
      processors: [memory_limiter, batch, attributes, filter]
      exporters: [jaeger]
    
    metrics:
      receivers: [otlp]
      processors: [memory_limiter, batch]
      exporters: [prometheusremotewrite]
    
    logs:
      receivers: [otlp]
      processors: [memory_limiter, batch]
      exporters: [awskinesis]
```

---

## Common Follow-up Questions

### "How do you handle sampling for high-volume traces?"

"We use tail-based sampling in the collector:

```yaml
processors:
  tail_sampling:
    decision_wait: 10s
    policies:
      # Always sample errors
      - name: errors
        type: status_code
        status_code: {status_codes: [ERROR]}
      
      # Always sample slow traces
      - name: slow-traces
        type: latency
        latency: {threshold_ms: 500}
      
      # Sample 10% of successful fast traces
      - name: probabilistic
        type: probabilistic
        probabilistic: {sampling_percentage: 10}
```

This ensures we capture all interesting traces (errors, slow) while sampling routine traffic."

### "What's the infrastructure cost?"

"For 12 services processing ~50K requests/second:

| Component | Resources | Monthly Cost |
|-----------|-----------|--------------|
| OTel Collectors (3 replicas) | 2 CPU, 4GB each | ~$200 |
| Jaeger (Cassandra backend) | 8 CPU, 32GB | ~$600 |
| Prometheus + Thanos | 4 CPU, 16GB | ~$300 |
| Kinesis + OpenSearch | Managed | ~$400 |
| **Total** | | **~$1,500/month** |

Compare to Datadog: ~$15K/month for same scale. 10x cost saving."
