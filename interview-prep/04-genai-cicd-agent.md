# GenAI Agent for CI/CD Failure Analysis Deep Dive

## 5-Minute Overview Script

"At Zeta, I created a GenAI agent that automatically analyzes CI/CD pipeline failures using RAG (Retrieval-Augmented Generation). When a build fails, the agent parses the error logs, searches our knowledge base of historical RCAs from Confluence and Jira, and generates a likely root cause analysis with citations.

Before this, engineers spent 30-45 minutes triaging each build failure - reading logs, searching Slack history, checking if someone had seen this before. The agent reduced this to under 5 minutes by instantly surfacing relevant historical context. It also auto-creates Jira tickets for known issues, enabling immediate escalation. This reduced build-failure triage time by 85%."

---

## Deep Dive Q&A

### Q1: "Explain the RAG architecture in detail"

**Answer:**
"RAG (Retrieval-Augmented Generation) combines the knowledge retrieval of a search system with the reasoning capabilities of an LLM. Here's our architecture:

**High-level flow:**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        RAG Architecture Overview                             │
│                                                                             │
│  ┌─────────────────┐                                                        │
│  │ CI/CD Pipeline  │                                                        │
│  │    Failure      │                                                        │
│  └────────┬────────┘                                                        │
│           │                                                                 │
│           ▼                                                                 │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                        GenAI Agent                                   │   │
│  │                                                                      │   │
│  │  ┌──────────────────────────────────────────────────────────────┐   │   │
│  │  │ Step 1: Error Extraction                                      │   │   │
│  │  │ - Parse build logs                                            │   │   │
│  │  │ - Extract stack traces, error messages                        │   │   │
│  │  │ - Identify affected services/tests                            │   │   │
│  │  └──────────────────────────────────────────────────────────────┘   │   │
│  │                           │                                          │   │
│  │                           ▼                                          │   │
│  │  ┌──────────────────────────────────────────────────────────────┐   │   │
│  │  │ Step 2: Query Generation                                      │   │   │
│  │  │ - LLM generates semantic search query                         │   │   │
│  │  │ - "NullPointerException in PaymentService.process()"          │   │   │
│  │  │   → "payment service null pointer exception processing"       │   │   │
│  │  └──────────────────────────────────────────────────────────────┘   │   │
│  │                           │                                          │   │
│  │                           ▼                                          │   │
│  │  ┌──────────────────────────────────────────────────────────────┐   │   │
│  │  │ Step 3: Retrieval                                             │   │   │
│  │  │ - Vector similarity search in embeddings DB                   │   │   │
│  │  │ - Keyword search in Confluence/Jira                           │   │   │
│  │  │ - Hybrid ranking (semantic + keyword)                         │   │   │
│  │  │ - Return top-K relevant documents                             │   │   │
│  │  └──────────────────────────────────────────────────────────────┘   │   │
│  │                           │                                          │   │
│  │                           ▼                                          │   │
│  │  ┌──────────────────────────────────────────────────────────────┐   │   │
│  │  │ Step 4: Generation                                            │   │   │
│  │  │ - LLM synthesizes RCA from retrieved context                  │   │   │
│  │  │ - Generates structured output with citations                  │   │   │
│  │  │ - Confidence scoring                                          │   │   │
│  │  └──────────────────────────────────────────────────────────────┘   │   │
│  │                           │                                          │   │
│  │                           ▼                                          │   │
│  │  ┌──────────────────────────────────────────────────────────────┐   │   │
│  │  │ Step 5: Action                                                │   │   │
│  │  │ - If confidence > 0.8: Auto-create Jira ticket               │   │   │
│  │  │ - If confidence < 0.5: Flag for human review                 │   │   │
│  │  │ - Post summary to Slack                                       │   │   │
│  │  └──────────────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Detailed component architecture:**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        Component Architecture                                │
│                                                                             │
│  Knowledge Base Ingestion (Offline)                                         │
│  ─────────────────────────────────                                          │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────┐                │
│  │  Confluence  │────▶│   Chunker    │────▶│  Embeddings  │                │
│  │    Pages     │     │  (semantic)  │     │   (OpenAI)   │                │
│  └──────────────┘     └──────────────┘     └──────┬───────┘                │
│                                                    │                        │
│  ┌──────────────┐     ┌──────────────┐            │                        │
│  │    Jira      │────▶│   Chunker    │────────────┤                        │
│  │   Tickets    │     │  (semantic)  │            │                        │
│  └──────────────┘     └──────────────┘            │                        │
│                                                    ▼                        │
│                                           ┌──────────────┐                  │
│                                           │   Pinecone   │                  │
│                                           │ Vector Store │                  │
│                                           └──────────────┘                  │
│                                                                             │
│  Query Time (Online)                                                        │
│  ───────────────────                                                        │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────┐                │
│  │ Build Failure│────▶│    Agent     │────▶│   GPT-4      │                │
│  │    Webhook   │     │  Orchestrator│◀───│   (OpenAI)   │                │
│  └──────────────┘     └──────┬───────┘     └──────────────┘                │
│                              │                                              │
│                              ▼                                              │
│                       ┌──────────────┐                                      │
│                       │   Pinecone   │                                      │
│                       │    Query     │                                      │
│                       └──────────────┘                                      │
└─────────────────────────────────────────────────────────────────────────────┘
```

**Code implementation:**

```python
class CICDFailureAgent:
    def __init__(self):
        self.embeddings = OpenAIEmbeddings(model="text-embedding-3-small")
        self.vector_store = Pinecone(index_name="cicd-knowledge")
        self.llm = ChatOpenAI(model="gpt-4-turbo", temperature=0)
    
    def analyze_failure(self, build_log: str) -> RCAResult:
        # Step 1: Extract error information
        error_info = self._extract_errors(build_log)
        
        # Step 2: Generate search query
        search_query = self._generate_query(error_info)
        
        # Step 3: Retrieve relevant documents
        docs = self._hybrid_search(search_query, error_info.keywords)
        
        # Step 4: Generate RCA
        rca = self._generate_rca(error_info, docs)
        
        # Step 5: Take action
        self._take_action(rca)
        
        return rca
    
    def _extract_errors(self, build_log: str) -> ErrorInfo:
        prompt = '''
        Extract the following from this build log:
        1. Error type (compilation, test failure, dependency, timeout, etc.)
        2. Error message
        3. Stack trace (if present)
        4. Affected file/service
        5. Key error keywords
        
        Build log:
        {log}
        '''
        
        response = self.llm.invoke(prompt.format(log=build_log[-10000:]))
        return ErrorInfo.parse(response)
    
    def _hybrid_search(self, query: str, keywords: List[str]) -> List[Document]:
        # Semantic search
        query_embedding = self.embeddings.embed_query(query)
        semantic_results = self.vector_store.similarity_search_with_score(
            query_embedding,
            k=10
        )
        
        # Keyword search (BM25-style)
        keyword_results = self._keyword_search(keywords, k=10)
        
        # Reciprocal Rank Fusion
        fused = self._rrf_fusion(semantic_results, keyword_results)
        
        return fused[:5]  # Top 5 after fusion
```"

---

### Q2: "How did you chunk and embed Confluence/Jira documents?"

**Answer:**
"Chunking strategy is critical for RAG quality. We used **semantic chunking** rather than fixed-size:

**Why not fixed-size chunking:**

```
Fixed-size (512 tokens):
┌────────────────────────────────────────────────────┐
│ "## Root Cause Analysis                            │
│                                                    │
│ The payment failure occurred due to a race         │
│ condition in the transaction..."                   │  ← Chunk 1
│ ─────────────────────────────────────────────────  │
│ "...locking mechanism. The solution is to add      │
│ optimistic locking using version columns.          │  ← Chunk 2
│                                                    │
│ ## Steps to Reproduce"                             │
└────────────────────────────────────────────────────┘

Problem: RCA split across chunks, context lost
```

**Semantic chunking approach:**

```python
class SemanticChunker:
    def __init__(self):
        self.min_chunk_size = 200   # tokens
        self.max_chunk_size = 1000  # tokens
        self.embeddings = OpenAIEmbeddings()
    
    def chunk_document(self, document: str) -> List[Chunk]:
        # Step 1: Split by natural boundaries
        sections = self._split_by_headers(document)
        
        chunks = []
        for section in sections:
            if self._token_count(section) <= self.max_chunk_size:
                chunks.append(section)
            else:
                # Split large sections by paragraphs
                paragraphs = section.split('\n\n')
                current_chunk = []
                current_size = 0
                
                for para in paragraphs:
                    para_size = self._token_count(para)
                    if current_size + para_size > self.max_chunk_size:
                        chunks.append('\n\n'.join(current_chunk))
                        current_chunk = [para]
                        current_size = para_size
                    else:
                        current_chunk.append(para)
                        current_size += para_size
                
                if current_chunk:
                    chunks.append('\n\n'.join(current_chunk))
        
        return chunks
    
    def _split_by_headers(self, doc: str) -> List[str]:
        # Split on markdown headers while keeping header with content
        pattern = r'(^#{1,3}\s+.+$)'
        parts = re.split(pattern, doc, flags=re.MULTILINE)
        
        sections = []
        current_section = ''
        for part in parts:
            if re.match(r'^#{1,3}\s+', part):
                if current_section:
                    sections.append(current_section)
                current_section = part + '\n'
            else:
                current_section += part
        
        if current_section:
            sections.append(current_section)
        
        return sections
```

**Document-specific strategies:**

| Document Type | Chunking Strategy |
|---------------|-------------------|
| Confluence RCA pages | By H2/H3 headers (Problem, Cause, Solution) |
| Jira tickets | Entire ticket as one chunk (usually small enough) |
| Runbooks | By step/section |
| Stack traces | Keep entire trace together, add to parent chunk |

**Metadata enrichment:**

```python
def create_chunk_with_metadata(chunk: str, source: dict) -> Document:
    return Document(
        page_content=chunk,
        metadata={
            "source": source["url"],
            "title": source["title"],
            "type": source["type"],  # "confluence", "jira"
            "created_at": source["created_at"],
            "author": source["author"],
            "labels": source.get("labels", []),
            "jira_status": source.get("status"),  # For Jira
            "related_services": extract_services(chunk),
            "chunk_index": chunk_idx,
            "total_chunks": total_chunks
        }
    )
```

**Embedding model choice:**

```python
# We evaluated several models:
#
# | Model                    | Dimensions | Latency | Quality |
# |--------------------------|------------|---------|---------|
# | text-embedding-ada-002   | 1536       | 50ms    | Good    |
# | text-embedding-3-small   | 1536       | 30ms    | Better  |
# | text-embedding-3-large   | 3072       | 60ms    | Best    |
#
# Chose text-embedding-3-small for balance of quality and latency

embeddings = OpenAIEmbeddings(
    model="text-embedding-3-small",
    dimensions=1536  # Can reduce to 512 for faster search
)
```

**Ingestion pipeline:**

```python
class KnowledgeBaseIngester:
    def __init__(self):
        self.confluence_client = ConfluenceClient()
        self.jira_client = JiraClient()
        self.chunker = SemanticChunker()
        self.vector_store = Pinecone(index_name="cicd-knowledge")
    
    async def ingest_all(self):
        # Ingest Confluence
        spaces = ["ENG", "DEVOPS", "PLATFORM"]
        for space in spaces:
            pages = await self.confluence_client.get_pages(space)
            for page in pages:
                chunks = self.chunker.chunk_document(page.content)
                documents = [
                    create_chunk_with_metadata(chunk, page)
                    for chunk in chunks
                ]
                await self.vector_store.add_documents(documents)
        
        # Ingest Jira (RCA tickets only)
        jira_query = 'project = ENG AND labels = "postmortem" AND status = Done'
        tickets = await self.jira_client.search(jira_query)
        for ticket in tickets:
            doc = create_chunk_with_metadata(
                ticket.description + "\n" + ticket.comments,
                ticket
            )
            await self.vector_store.add_documents([doc])
    
    # Run daily to keep knowledge base updated
    @scheduled(cron="0 2 * * *")  # 2 AM daily
    async def incremental_update(self):
        since = datetime.now() - timedelta(days=1)
        # Fetch only recently modified documents
        ...
```"

---

### Q3: "What was your retrieval strategy?"

**Answer:**
"We use **hybrid search** combining semantic and keyword retrieval, then **Reciprocal Rank Fusion** to merge results:

**Why hybrid search:**

| Scenario | Semantic Only | Keyword Only | Hybrid |
|----------|---------------|--------------|--------|
| "database timeout" error | Finds conceptually similar issues | Exact match on "timeout" | Best of both |
| Typo in query | Handles well | Fails | Handles well |
| Specific error code "ORA-00001" | May miss | Exact match | Finds it |
| Conceptual: "slow queries" | Finds "performance issues" | Misses synonyms | Finds all |

**Implementation:**

```python
class HybridRetriever:
    def __init__(self):
        self.vector_store = Pinecone(index_name="cicd-knowledge")
        self.bm25_index = BM25Index()  # Pre-built from documents
        self.embeddings = OpenAIEmbeddings()
    
    def search(self, query: str, k: int = 5) -> List[Document]:
        # 1. Semantic search
        query_embedding = self.embeddings.embed_query(query)
        semantic_results = self.vector_store.similarity_search_with_score(
            query_embedding,
            k=k * 2  # Fetch more for fusion
        )
        
        # 2. Keyword search (BM25)
        keyword_results = self.bm25_index.search(query, k=k * 2)
        
        # 3. Reciprocal Rank Fusion
        fused_results = self._rrf_fusion(
            semantic_results, 
            keyword_results,
            k=60  # RRF parameter
        )
        
        return fused_results[:k]
    
    def _rrf_fusion(
        self, 
        semantic: List[Tuple[Document, float]], 
        keyword: List[Tuple[Document, float]],
        k: int = 60
    ) -> List[Document]:
        """
        RRF Score = sum(1 / (k + rank_i)) for each result list
        """
        scores = {}
        
        # Score from semantic search
        for rank, (doc, _) in enumerate(semantic):
            doc_id = doc.metadata["source"]
            scores[doc_id] = scores.get(doc_id, 0) + 1 / (k + rank + 1)
        
        # Score from keyword search  
        for rank, (doc, _) in enumerate(keyword):
            doc_id = doc.metadata["source"]
            scores[doc_id] = scores.get(doc_id, 0) + 1 / (k + rank + 1)
        
        # Sort by fused score
        sorted_ids = sorted(scores.keys(), key=lambda x: scores[x], reverse=True)
        
        # Return documents in ranked order
        doc_map = {d.metadata["source"]: d for d, _ in semantic + keyword}
        return [doc_map[doc_id] for doc_id in sorted_ids if doc_id in doc_map]
```

**Query expansion for better recall:**

```python
def expand_query(self, original_query: str) -> str:
    prompt = '''
    Given this error description, generate an expanded search query 
    that includes:
    1. Synonyms for technical terms
    2. Common variations of the error
    3. Related concepts
    
    Original: {query}
    
    Expanded query (one line):
    '''
    
    expanded = self.llm.invoke(prompt.format(query=original_query))
    return f"{original_query} {expanded}"

# Example:
# Original: "connection pool exhausted"
# Expanded: "connection pool exhausted database connections 
#           timeout hikari pool size max connections waiting"
```

**Filtering with metadata:**

```python
def search_with_filters(self, query: str, filters: dict) -> List[Document]:
    # Only search recent documents for time-sensitive errors
    if filters.get("recent_only"):
        date_filter = {
            "created_at": {"$gte": datetime.now() - timedelta(days=90)}
        }
    
    # Filter by service if specified
    if filters.get("service"):
        service_filter = {
            "related_services": {"$in": [filters["service"]]}
        }
    
    return self.vector_store.similarity_search(
        query,
        filter={**date_filter, **service_filter},
        k=5
    )
```"

---

### Q4: "How did you handle hallucinations?"

**Answer:**
"Hallucination mitigation was critical for trust. We used multiple strategies:

**1. Grounded generation with citations:**

```python
def generate_rca(self, error_info: ErrorInfo, docs: List[Document]) -> RCAResult:
    context = "\n\n".join([
        f"[Source {i+1}: {doc.metadata['title']}]\n{doc.page_content}"
        for i, doc in enumerate(docs)
    ])
    
    prompt = '''
    You are analyzing a CI/CD build failure. Generate a root cause analysis 
    based ONLY on the provided context. 
    
    RULES:
    1. Only make claims that are directly supported by the context
    2. Cite sources using [Source N] format
    3. If the context doesn't contain relevant information, say "No matching 
       historical data found"
    4. Do not invent error causes or solutions
    5. Express uncertainty when appropriate
    
    Error Information:
    {error_info}
    
    Context from Knowledge Base:
    {context}
    
    Generate a structured RCA with:
    1. Likely Root Cause (with citation)
    2. Confidence Level (high/medium/low)
    3. Suggested Fix (with citation)
    4. Related Past Incidents (with links)
    '''
    
    response = self.llm.invoke(prompt.format(
        error_info=error_info.to_string(),
        context=context
    ))
    
    return self._parse_and_validate(response, docs)
```

**2. Citation verification:**

```python
def _parse_and_validate(self, response: str, docs: List[Document]) -> RCAResult:
    rca = RCAResult.parse(response)
    
    # Verify each citation exists in provided documents
    for citation in rca.citations:
        source_num = int(citation.source_id)
        if source_num > len(docs):
            rca.flags.append("INVALID_CITATION")
            rca.confidence *= 0.5  # Reduce confidence
        else:
            # Verify the cited text exists in source
            cited_text = citation.text
            source_content = docs[source_num - 1].page_content
            if cited_text.lower() not in source_content.lower():
                rca.flags.append("MISQUOTED_CITATION")
                rca.confidence *= 0.7
    
    return rca
```

**3. Confidence scoring:**

```python
def calculate_confidence(self, rca: RCAResult, docs: List[Document]) -> float:
    confidence = 1.0
    
    # Factor 1: Retrieval quality
    if not docs:
        return 0.1  # No relevant docs found
    
    top_doc_score = docs[0].metadata.get("similarity_score", 0)
    if top_doc_score < 0.7:
        confidence *= 0.6  # Weak semantic match
    
    # Factor 2: Recency of sources
    avg_age_days = self._avg_document_age(docs)
    if avg_age_days > 180:
        confidence *= 0.8  # Old information
    
    # Factor 3: Citation coverage
    if len(rca.citations) == 0:
        confidence *= 0.3  # No citations = likely hallucination
    
    # Factor 4: LLM self-assessment
    if "uncertain" in rca.raw_response.lower():
        confidence *= 0.7
    if "no matching" in rca.raw_response.lower():
        confidence *= 0.5
    
    return min(confidence, 1.0)
```

**4. Human-in-the-loop for low confidence:**

```python
def take_action(self, rca: RCAResult):
    if rca.confidence >= 0.8:
        # High confidence: Auto-create ticket
        self.jira_client.create_ticket(
            summary=f"Build Failure: {rca.likely_cause}",
            description=rca.to_markdown(),
            labels=["auto-generated", "ci-failure"]
        )
        self.slack_client.post(
            channel="#ci-failures",
            message=f"✅ RCA auto-generated: {rca.likely_cause}\n{rca.jira_link}"
        )
    
    elif rca.confidence >= 0.5:
        # Medium confidence: Post for review
        self.slack_client.post(
            channel="#ci-failures",
            message=f"⚠️ Possible RCA (needs review): {rca.likely_cause}\n"
                    f"Confidence: {rca.confidence:.0%}\n"
                    f"React with ✅ to create ticket, ❌ to dismiss"
        )
    
    else:
        # Low confidence: Just log
        self.slack_client.post(
            channel="#ci-failures",
            message=f"❓ Build failure needs manual investigation\n"
                    f"No matching historical data found"
        )
```

**5. Feedback loop for improvement:**

```python
class FeedbackCollector:
    def __init__(self):
        self.feedback_db = FeedbackDatabase()
    
    def record_feedback(self, rca_id: str, feedback: Feedback):
        """
        Called when engineer marks RCA as correct/incorrect
        """
        self.feedback_db.save({
            "rca_id": rca_id,
            "was_correct": feedback.was_correct,
            "actual_cause": feedback.actual_cause,
            "timestamp": datetime.now()
        })
        
        if not feedback.was_correct:
            # Add correct RCA to knowledge base
            self.knowledge_base.add_document(
                Document(
                    page_content=f"Error: {feedback.original_error}\n"
                                 f"Actual Cause: {feedback.actual_cause}\n"
                                 f"Solution: {feedback.solution}",
                    metadata={
                        "source": "feedback-correction",
                        "original_rca_id": rca_id
                    }
                )
            )
```

**Hallucination metrics:**

| Metric | Before Mitigations | After |
|--------|-------------------|-------|
| Hallucinated causes | 25% | 5% |
| Invalid citations | 15% | 2% |
| Correct RCAs (verified) | 60% | 85% |
| Human overrides needed | 40% | 15% |"

---

### Q5: "What was the accuracy of RCA identification?"

**Answer:**
"We tracked accuracy through explicit feedback and implicit signals:

**Explicit feedback metrics:**

```python
# Weekly accuracy report
def generate_accuracy_report(self) -> Report:
    last_week = datetime.now() - timedelta(days=7)
    
    rcas = self.rca_db.find({"created_at": {"$gte": last_week}})
    
    # Only count RCAs with feedback
    with_feedback = [r for r in rcas if r.feedback is not None]
    
    correct = sum(1 for r in with_feedback if r.feedback.was_correct)
    incorrect = sum(1 for r in with_feedback if not r.feedback.was_correct)
    
    accuracy = correct / len(with_feedback) if with_feedback else 0
    
    return Report(
        total_rcas=len(rcas),
        with_feedback=len(with_feedback),
        correct=correct,
        incorrect=incorrect,
        accuracy=accuracy,
        feedback_rate=len(with_feedback) / len(rcas)
    )

# Results:
# - Accuracy: 78% (correct RCA identified)
# - Feedback rate: 45% (engineers provide feedback)
# - Auto-ticket accuracy: 85% (higher because only high-confidence)
```

**Implicit signals:**

```python
def track_implicit_signals(self, rca_id: str):
    # Signal 1: Was the Jira ticket resolved quickly?
    ticket = self.jira_client.get_ticket(rca_id)
    if ticket.resolution_time < timedelta(hours=2):
        self.metrics.record("fast_resolution", rca_id)
    
    # Signal 2: Did engineer reopen/reject the ticket?
    if ticket.status == "Rejected":
        self.metrics.record("rejected_rca", rca_id)
    
    # Signal 3: Did the suggested fix actually fix the build?
    next_build = self.ci_client.get_next_build(rca.build_id)
    if next_build.status == "SUCCESS":
        self.metrics.record("fix_worked", rca_id)
```

**Accuracy breakdown by error type:**

| Error Type | Accuracy | Notes |
|------------|----------|-------|
| Dependency issues | 92% | Well-documented in knowledge base |
| Test failures | 75% | Context-dependent, harder to match |
| Compilation errors | 88% | Usually have clear error messages |
| Timeouts | 70% | Many possible causes |
| Flaky tests | 65% | Inherently unpredictable |

**Improvement over time:**

```
Month 1: 65% accuracy (initial deployment)
Month 2: 72% accuracy (feedback loop active)
Month 3: 78% accuracy (knowledge base enriched)
Month 4: 82% accuracy (query expansion added)
```"

---

### Q6: "How did you validate the agent's outputs?"

**Answer:**
"Multi-layer validation approach:

**1. Pre-deployment validation:**

```python
class RCAValidator:
    def __init__(self):
        self.test_cases = load_test_cases("rca_test_cases.json")
    
    def validate_model(self, agent: CICDFailureAgent) -> ValidationReport:
        results = []
        
        for test_case in self.test_cases:
            rca = agent.analyze_failure(test_case.build_log)
            
            result = TestResult(
                test_id=test_case.id,
                expected_cause=test_case.expected_cause,
                actual_cause=rca.likely_cause,
                cause_match=self._semantic_similarity(
                    test_case.expected_cause, 
                    rca.likely_cause
                ) > 0.8,
                citations_valid=self._validate_citations(rca),
                confidence_calibrated=self._check_confidence(
                    rca.confidence, 
                    test_case.difficulty
                )
            )
            results.append(result)
        
        return ValidationReport(results)

# Test cases curated from real incidents
test_cases = [
    {
        "id": "test_001",
        "build_log": "java.lang.OutOfMemoryError: Java heap space...",
        "expected_cause": "Heap memory exceeded during build",
        "expected_fix": "Increase JVM heap size in build config",
        "difficulty": "easy"
    },
    # ... 50+ test cases covering various scenarios
]
```

**2. A/B testing during rollout:**

```python
# 50% of failures go to agent, 50% manual triage
# Compare resolution times
class ABTestHandler:
    def handle_failure(self, build_failure: BuildFailure):
        if hash(build_failure.id) % 2 == 0:
            # Treatment: AI agent
            rca = self.agent.analyze_failure(build_failure.log)
            self.metrics.record("treatment", build_failure.id, {
                "rca_time": rca.generation_time,
                "confidence": rca.confidence
            })
            return rca
        else:
            # Control: Manual triage
            self.metrics.record("control", build_failure.id, {})
            return None  # Engineer handles manually

# After 2 weeks:
# Treatment: Avg resolution time 12 minutes
# Control: Avg resolution time 45 minutes
# Conclusion: Agent reduces triage time by 73%
```

**3. Continuous monitoring:**

```python
# Dashboard metrics
metrics = {
    "rca_accuracy_7d": "78%",
    "avg_confidence": "0.72",
    "auto_ticket_accuracy": "85%",
    "human_override_rate": "15%",
    "avg_triage_time_mins": "4.5",
    "knowledge_base_hits": "92%",  # % of queries with relevant docs
    "hallucination_rate": "5%"
}

# Alerts
alerts = [
    {
        "name": "RCA Accuracy Drop",
        "condition": "accuracy_7d < 0.7",
        "action": "Page on-call, pause auto-tickets"
    },
    {
        "name": "High Hallucination Rate",
        "condition": "hallucination_rate > 0.1",
        "action": "Review recent RCAs, check knowledge base freshness"
    }
]
```"

---

## Common Follow-up Questions

### "What LLM did you use and why?"

"GPT-4-turbo for generation (best reasoning), text-embedding-3-small for embeddings (cost-effective). We evaluated Claude and Gemini too - GPT-4 had best structured output following."

### "How do you handle knowledge base staleness?"

"Daily incremental sync from Confluence/Jira. Documents older than 1 year are deprioritized in ranking. We also track 'relevance decay' - if a document stops being cited, it gets lower weight."

### "What's the latency of the full pipeline?"

"End-to-end: 8-12 seconds
- Error extraction: 2s
- Query generation: 1s  
- Retrieval: 0.5s
- RCA generation: 4-6s
- Action (Jira/Slack): 1s

Acceptable for async failure analysis, runs in background after webhook."

### "How do you handle multi-language codebases?"

"The embedding model handles multiple programming languages well since it's trained on code. We also extract language-specific patterns (Java stack traces vs Python tracebacks) during error extraction."
