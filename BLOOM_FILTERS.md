# Bloom Filters: A Comprehensive Guide for SDE-2 Interviews

> [!NOTE]
> **Quick Summary**: A Bloom Filter is a space-efficient probabilistic data structure that tells you if an element is **definitely not** in a set or **probably** in a set. It trades a small amount of accuracy (false positives) for massive memory savings.

---

## 1. Core Concept
- **Type**: Probabilistic Data Structure.
- **Primary Function**: Membership testing.
- **Guarantee**:
  - **No False Negatives**: If the filter says "No", the item is definitely NOT in the set.
  - **Possible False Positives**: If the filter says "Yes", the item MIGHT be in the set (or it might be a collision).

## 2. How It Works
A Bloom filter consists of two main components:
1.  **Bit Array**: A fixed-size array of $m$ bits, initialized to 0.
2.  **Hash Functions**: $k$ independent hash functions, each mapping an input to an index in the range $[0, m-1]$.

### Operations
#### Insertion
1.  Feed the item to each of the $k$ hash functions.
2.  Get $k$ positions (indices).
3.  Set the bits at all these $k$ positions to **1**.

#### Lookup (Query)
1.  Feed the item to the same $k$ hash functions.
2.  Check the bits at the resulting $k$ positions.
    - If **ANY** bit is **0** $\rightarrow$ Item is **Definitely Not** present.
    - If **ALL** bits are **1** $\rightarrow$ Item is **Probably** present.

---

## 3. Complexity Analysis (The "SDE-2" Details)

| Metric | Complexity | Notes |
| :--- | :--- | :--- |
| **Time (Insert)** | $O(k)$ | Independent of the number of items in the set ($n$). |
| **Time (Lookup)** | $O(k)$ | Very fast, effectively constant since $k$ is usually small. |
| **Space** | $O(m)$ | Fixed size, independent of actual data size. |

### Mathematical Formulas (Good for Interviews)
Let:
- $n$ = number of items expected to be inserted.
- $m$ = size of bit array.
- $k$ = number of hash functions.
- $p$ = desired false positive probability.

**1. Optimal Number of Hash Functions ($k$):**
$$k = \frac{m}{n} \ln 2 \approx 0.7 \frac{m}{n}$$

**2. Optimal Bit Array Size ($m$):**
$$m = -\frac{n \ln p}{(\ln 2)^2}$$
*(Rule of thumb: ~10 bits per element for 1% false positive rate)*

---

## 4. Key Use Cases & System Design Patterns

### A. Reducing Database Load (Cache Penetration)
**Problem**: "Cache Miss Storm" or "Cache Penetration". Malicious users or bugs request non-existent keys. The cache misses, and the DB is hammered with useless queries.
**Solution**:
1.  Place a Bloom Filter in front of the Cache/DB.
2.  Check Bloom Filter first.
3.  If "Not Present" $\rightarrow$ Return 404 immediately (DB untouched).
4.  If "Probably Present" $\rightarrow$ Check Cache $\rightarrow$ Check DB.
**Result**: Filters out ~99% of invalid requests.

### B. Optimizing Caches (The "One-Hit-Wonder" Problem)
**Problem**: In CDNs (like Akamai), 75% of items are requested once and never again. Caching them wastes space and disk I/O.
**Solution**:
1.  On first request, check Bloom Filter.
2.  If "Not Present" $\rightarrow$ Do NOT cache. Add to Bloom Filter. Serve from origin.
3.  If "Probably Present" (seen before) $\rightarrow$ Cache it.
**Result**: Only popular items get cached, saving disk writes and space.

### C. Distributed Systems (Reducing Network Calls)
**Problem**: In a distributed DB (e.g., Cassandra/HBase), data is split across many files (SSTables). Checking every file for a key requires expensive disk reads.
**Solution**:
1.  Each SSTable has its own Bloom Filter in memory.
2.  Before reading a file, check its Bloom Filter.
3.  Only read the file if the Bloom Filter says "Maybe".
**Result**: Massive reduction in disk I/O.

---

## 5. Real-World Examples
- **Google Bigtable / Apache HBase / Cassandra**: Use Bloom filters to avoid reading SSTables that don't contain the row.
- **PostgreSQL**: Uses Bloom filters for index scans.
- **Google Chrome**: Formerly used a Bloom filter to check for malicious URLs (Safe Browsing) on the client side before asking the server.
- **Bitcoin**: Uses Bloom filters (SPV wallets) to sync only relevant transactions without downloading the full blockchain.
- **Medium**: Uses Bloom filters to avoid recommending articles a user has already read.

---

## 6. Trade-offs & Limitations

> [!WARNING]
> **No Deletion**: Standard Bloom filters do not support deletion. Removing an item (setting bits to 0) might disturb other items that share those bits.
> *Solution*: Use **Counting Bloom Filters** (store counters instead of bits), but this uses more memory.

> [!IMPORTANT]
> **Fixed Size**: You must define the size ($m$) upfront based on expected elements ($n$). If $n$ grows beyond expectation, the false positive rate skyrockets.
> *Solution*: Scalable Bloom Filters (chaining multiple filters).

---

## 7. Simple Implementation (Java)

```java
import java.util.BitSet;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class SimpleBloomFilter {
    private final BitSet bitSet;
    private final int size;
    private final int[] seeds; // Seeds for simulating multiple hash functions

    public SimpleBloomFilter(int size, int numHashFunctions) {
        this.size = size;
        this.bitSet = new BitSet(size);
        this.seeds = new int[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            seeds[i] = i * 31 + 7; // Simple seed generation
        }
    }

    private int hash(String item, int seed) {
        int result = 0;
        for (char c : item.toCharArray()) {
            result = result * seed + c;
        }
        return Math.abs(result % size);
    }

    public void add(String item) {
        for (int seed : seeds) {
            bitSet.set(hash(item, seed));
        }
    }

    public boolean mightContain(String item) {
        for (int seed : seeds) {
            if (!bitSet.get(hash(item, seed))) {
                return false; // Definitely not present
            }
        }
        return true; // Probably present
    }
}
```

## 8. Mathematical Derivations (Proof)

Here is the step-by-step derivation for the optimal number of hash functions ($k$) and bit array size ($m$).

### Definitions
- $m$: Size of the bit array.
- $n$: Number of elements inserted.
- $k$: Number of hash functions.
- $p$: Probability of a false positive.

### Step 1: Probability of a bit being 0 or 1
1.  **Probability that a specific bit is NOT set by one hash function**:
    Since the hash function selects one of $m$ bits uniformly, the probability that a specific bit is *not* chosen is:
    $$1 - \frac{1}{m}$$

2.  **Probability that a specific bit is NOT set by $k$ hash functions**:
    $$ \left(1 - \frac{1}{m}\right)^k $$

3.  **Probability that a specific bit is NOT set after inserting $n$ elements**:
    After inserting $n$ elements, we have performed $kn$ total hash operations.
    $$ P(\text{bit is 0}) = \left(1 - \frac{1}{m}\right)^{kn} $$

    **Approximation**: Using the limit $\lim_{x \to \infty} (1 - \frac{1}{x})^x = \frac{1}{e}$, we can approximate for large $m$:
    $$ \left(1 - \frac{1}{m}\right)^{kn} = \left( \left(1 - \frac{1}{m}\right)^m \right)^{kn/m} \approx e^{-kn/m} $$

4.  **Probability that a specific bit IS set (is 1)**:
    $$ P(\text{bit is 1}) = 1 - P(\text{bit is 0}) \approx 1 - e^{-kn/m} $$

### Step 2: Probability of a False Positive ($p$)
A false positive occurs when we check an element (that wasn't added) and *all* $k$ of its hash positions happen to be set to 1.
Assuming the probabilities of bits being set are independent (which is true for large $m$):
$$ p = \left( P(\text{bit is 1}) \right)^k \approx \left( 1 - e^{-kn/m} \right)^k $$

### Step 3: Optimal Number of Hash Functions ($k$)
We want to minimize the false positive rate $p$ with respect to $k$.
Let $p = (1 - e^{-kn/m})^k$.
To make differentiation easier, take the natural log of $p$:
$$ \ln p = k \ln \left( 1 - e^{-kn/m} \right) $$

Let $x = e^{-kn/m}$. Then $\ln p = k \ln(1-x)$.
Also note that $k = -\frac{m}{n} \ln x$.
Substituting $k$:
$$ \ln p = -\frac{m}{n} \ln x \cdot \ln(1 - x) $$

To minimize $p$, we minimize $\ln p$. We take the derivative with respect to $k$ (or more simply, minimize with respect to the exponent term).
The minimum occurs when $k = \frac{m}{n} \ln 2$.

**Intuitive Proof**:
The false positive rate is minimized when the bit array is **50% full** (half 0s, half 1s).
$$ P(\text{bit is 1}) = 1 - e^{-kn/m} = \frac{1}{2} $$
$$ e^{-kn/m} = \frac{1}{2} $$
Take $\ln$ on both sides:
$$ -\frac{kn}{m} = \ln \left(\frac{1}{2}\right) = -\ln 2 $$
$$ \frac{kn}{m} = \ln 2 $$
$$ k = \frac{m}{n} \ln 2 \approx 0.7 \frac{m}{n} $$
**(Q.E.D for $k$)**

### Step 4: Optimal Bit Array Size ($m$)
Now substitute the optimal $k = \frac{m}{n} \ln 2$ back into the false positive equation:
$$ p = \left( 1 - e^{-kn/m} \right)^k $$
Since $e^{-kn/m} = \frac{1}{2}$ (from the derivation above):
$$ p = \left( 1 - \frac{1}{2} \right)^k = \left( \frac{1}{2} \right)^k = 2^{-k} $$

Now solve for $m$. Take $\ln$ of both sides:
$$ \ln p = -k \ln 2 $$
Substitute $k = \frac{m}{n} \ln 2$:
$$ \ln p = - \left( \frac{m}{n} \ln 2 \right) \ln 2 $$
$$ \ln p = - \frac{m}{n} (\ln 2)^2 $$

Rearrange to solve for $m$:
$$ m = - \frac{n \ln p}{(\ln 2)^2} $$
**(Q.E.D for $m$)**

---

## 9. Summary for Interview
"A Bloom Filter is a probabilistic data structure used to test set membership. It is highly space-efficient but allows for false positives. It works by hashing elements to multiple positions in a bit array. It is $O(k)$ for operations and is widely used in databases (Cassandra, HBase) to prevent expensive disk seeks and in caching layers to prevent cache penetration."
