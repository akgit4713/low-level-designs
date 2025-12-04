# Global Server Load Balancing (GSLB): The SDE 2 Guide

> [!IMPORTANT]
> **Target Audience**: SDE 2 / Senior Engineer Candidates
> **Focus**: System Design, Trade-offs, Failure Modes, and High Availability.

## 1. Executive Summary
**Global Server Load Balancing (GSLB)** is the practice of distributing Internet traffic across multiple geographically dispersed data centers to minimize latency and maximize availability. Unlike local load balancers (LBs) that distribute traffic within a single data center, GSLB operates at the DNS or Network layer to route users to the optimal *region*.

**Key Goals:**
*   **Latency Reduction**: Route users to the closest physical point of presence (PoP).
*   **Disaster Recovery (DR)**: Automatic failover if an entire region goes dark.
*   **Load Management**: Shed load from hot regions to cooler ones during spikes.

---

## 2. Core Mechanisms

There are two primary ways to implement GSLB. In a System Design interview, you must know when to choose which.

### A. DNS-Based Load Balancing (GeoDNS)
This is the most common Application Layer approach.
*   **Mechanism**: A specialized DNS server (Authoritative Nameserver) receives a query for `www.example.com`. It looks at the **Source IP** of the resolver (or EDNS Client Subnet) to determine the user's location.
*   **Decision**: Based on the location + server health + configured weights, it returns the IP address of the specific regional VIP (Virtual IP).
*   **Example**:
    *   User in **Paris** queries DNS.
    *   GSLB sees IP from France.
    *   GSLB returns IP `1.2.3.4` (Frankfurt DC).
    *   User in **Tokyo** queries DNS.
    *   GSLB returns IP `5.6.7.8` (Tokyo DC).

### B. Anycast Routing (Network Layer)
This is a Network Layer approach utilizing BGP (Border Gateway Protocol).
*   **Mechanism**: Multiple data centers announce the **same IP address** range via BGP.
*   **Routing**: The internet's routers naturally send packets to the "closest" data center based on BGP metrics (usually AS-path length, not necessarily physical distance).
*   **Example**: Google DNS (`8.8.8.8`). The IP exists in hundreds of locations. You hit the one network-topologically closest to you.

---

## 3. SDE 2 Deep Dive: Architecture & Trade-offs

This section differentiates a Junior engineer from a Senior/SDE 2 candidate.

### 3.1. The "Stale Cache" Problem (DNS)
**The Issue**: DNS records are cached by ISP resolvers and local OSs. If a region fails, the GSLB updates the record, but users might still have the old IP cached for the duration of the **TTL (Time To Live)**.
*   **Trade-off**:
    *   **Low TTL (e.g., 30s)**: Faster failover, but higher load on authoritative DNS servers and slightly higher latency for users (more lookups).
    *   **High TTL (e.g., 1h)**: Better cache hit rate, but slower reaction to outages.
*   **SDE 2 Insight**: "We typically tune TTL to be low (60s-300s) for dynamic services requiring high availability, accepting the higher DNS query volume cost."

### 3.2. BGP Convergence (Anycast)
**The Issue**: Anycast relies on BGP. If a node fails, routes must be withdrawn. BGP is a "gossip" protocol; convergence can take seconds or even minutes globally.
*   **The "Flap" Risk**: If a route flaps (up/down repeatedly), BGP dampening might suppress the route entirely, causing an outage.
*   **SDE 2 Insight**: "Anycast is great for UDP/stateless services (DNS, CDN). For stateful TCP services, route changes can break established connections (TCP reset), so we often use Anycast for the *entry* point (L4) which then tunnels to a specific stable backend."

### 3.3. Health Checks: Active vs. Passive
How does the GSLB know a region is down?
*   **Active Checks (Synthetic)**: The GSLB periodically pings or HTTP GETs the regional LBs.
    *   *Pro*: Simple, explicit.
    *   *Con*: Can generate "dummy" traffic; might not reflect real user experience (e.g., network path to GSLB is fine, but path to user is broken).
*   **Passive Checks (Real User Monitoring - RUM)**: The client (browser/app) reports back performance metrics (latency, errors) to a central collector.
    *   *Pro*: Represents actual user reality.
    *   *Con*: Complex to implement; requires client-side logic.

### 3.4. Load Shedding & Spillover
What if the "closest" data center is at 100% capacity?
*   **Naive GSLB**: Sends user to closest DC -> DC crashes.
*   **Smart GSLB**: Checks `Current_Load` vs `Capacity`. If `Load > Threshold`, it "spills over" traffic to the *next* closest DC.
*   **SDE 2 Insight**: "We must implement **hysteresis** to prevent thrashing (bouncing users back and forth between two DCs)."

---

## 4. Comparison: GeoDNS vs. Anycast

| Feature | DNS-Based (GeoDNS) | Anycast (BGP) |
| :--- | :--- | :--- |
| **OSI Layer** | Application (L7/DNS) | Network (L3) |
| **Granularity** | High (Can target specific cities/ASNs) | Low (Mercy of BGP routing) |
| **Failover Speed** | Slow (Dependent on TTL) | Fast-ish (Dependent on BGP convergence) |
| **Statefulness** | Good (User stays on resolved IP) | Poor (Route change breaks TCP connection) |
| **Cost/Complexity** | Low (Managed services like Route53) | High (Requires own ASN, hardware, BGP tuning) |
| **Best For** | Web Apps, APIs, Database endpoints | DNS, CDNs, DDoS mitigation |

---

## 5. Interview Cheat Sheet (FAANG)

When asked to design a global system (e.g., "Design Netflix" or "Design Instagram"), use these patterns:

1.  **The "Hybrid" Approach**:
    *   Use **Anycast** for the Edge/CDN layer (static content, fast connection setup).
    *   Use **GeoDNS** for the API/Backend layer (stateful connections, sharded databases).

2.  **Split-Brain Scenarios**:
    *   Discuss what happens if the link between US-East and EU-West breaks. GSLB might route users correctly, but if DB replication fails, you have a consistency issue.
    *   *Keyword to drop*: **"CAP Theorem"** - In a partition (P), do we choose Availability (A) via GSLB failover, or Consistency (C) by blocking writes?

3.  **EDNS Client Subnet (ECS)**:
    *   Mention this when discussing DNS limitations. "Standard DNS sees the Resolver's IP, not the User's. ECS fixes this by sending part of the user's IP to the authoritative server for better precision."

4.  **DDoS Mitigation**:
    *   Anycast is a natural DDoS shield. It dilutes the attack traffic across *all* global data centers instead of focusing it on one.

## 6. Summary
GSLB is the traffic cop of the internet. It ensures that a user in Mumbai isn't served by a server in Dallas unless absolutely necessary.
*   **Junior Answer**: "It routes based on IP."
*   **SDE 2 Answer**: "It balances latency, capacity, and health. We choose between GeoDNS for control and Anycast for performance, carefully tuning TTLs and health checks to prevent cascading failures."
