# 📊 Database Design & SQL Interview Guide for SDE-2

> A comprehensive guide for FAANG and startup interviews covering DB modeling, schema design, and SQL queries.

---

## Table of Contents

1. [Database Modeling Framework](#1-database-modeling-framework)
2. [Schema Design Patterns](#2-schema-design-patterns)
3. [How to Approach SQL Interview Questions](#3-how-to-approach-sql-interview-questions)
4. [Essential SQL Queries by Category](#4-essential-sql-queries-by-category)
5. [Window Functions Deep Dive](#5-window-functions-deep-dive)
6. [Common Interview Patterns](#6-common-interview-patterns)
7. [Performance & Optimization](#7-performance--optimization)
8. [Interview Tips & Checklist](#8-interview-tips--checklist)

---

## 1. Database Modeling Framework

### Step-by-Step Approach for LLD Interviews

```
┌─────────────────────────────────────────────────────────────┐
│  Step 1: CLARIFY REQUIREMENTS                               │
│  • What entities exist in the system?                       │
│  • What are the read/write patterns?                        │
│  • What's the expected scale?                               │
├─────────────────────────────────────────────────────────────┤
│  Step 2: IDENTIFY ENTITIES                                  │
│  • List all nouns from requirements                         │
│  • Determine which become tables                            │
├─────────────────────────────────────────────────────────────┤
│  Step 3: DEFINE RELATIONSHIPS                               │
│  • 1:1 (User ↔ Profile)                                     │
│  • 1:N (User → Orders)                                      │
│  • M:N (Products ↔ Categories)                              │
├─────────────────────────────────────────────────────────────┤
│  Step 4: NORMALIZE (typically to 3NF)                       │
│  • 1NF: No repeating groups                                 │
│  • 2NF: No partial dependencies                             │
│  • 3NF: No transitive dependencies                          │
├─────────────────────────────────────────────────────────────┤
│  Step 5: ADD INDEXES                                        │
│  • Primary keys (automatic)                                 │
│  • Foreign keys                                             │
│  • Frequently queried columns                               │
├─────────────────────────────────────────────────────────────┤
│  Step 6: DEFINE CONSTRAINTS                                 │
│  • NOT NULL, UNIQUE, CHECK                                  │
│  • Foreign key actions (CASCADE, SET NULL)                  │
└─────────────────────────────────────────────────────────────┘
```

### Data Type Selection Guide

| Use Case | Recommended Type | Why |
|----------|-----------------|-----|
| IDs | `BIGINT` | Scale-proof, auto-increment |
| Money | `DECIMAL(10,2)` | Precise, no floating-point errors |
| Short text | `VARCHAR(n)` | Variable length, efficient |
| Long text | `TEXT` | No length limit |
| Boolean | `BOOLEAN` / `TINYINT(1)` | Storage efficient |
| Timestamps | `TIMESTAMP` | Auto timezone handling |
| Fixed options | `ENUM` | Type safety, storage efficient |
| Coordinates | `DECIMAL(10,8)` | Precise lat/lng |

---

## 2. Schema Design Patterns

### Pattern A: E-Commerce System

```sql
-- ═══════════════════════════════════════════════════════════
-- USERS TABLE
-- ═══════════════════════════════════════════════════════════
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_email (email),
    INDEX idx_created_at (created_at)
);

-- ═══════════════════════════════════════════════════════════
-- ADDRESSES (1:N with Users)
-- ═══════════════════════════════════════════════════════════
CREATE TABLE addresses (
    address_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    address_type ENUM('HOME', 'OFFICE', 'OTHER') DEFAULT 'HOME',
    street_address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- ═══════════════════════════════════════════════════════════
-- CATEGORIES (Self-referencing hierarchy)
-- ═══════════════════════════════════════════════════════════
CREATE TABLE categories (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    parent_category_id BIGINT NULL,
    description TEXT,
    
    FOREIGN KEY (parent_category_id) REFERENCES categories(category_id),
    INDEX idx_parent (parent_category_id)
);

-- ═══════════════════════════════════════════════════════════
-- PRODUCTS
-- ═══════════════════════════════════════════════════════════
CREATE TABLE products (
    product_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    discount_percentage DECIMAL(5, 2) DEFAULT 0,
    category_id BIGINT,
    seller_id BIGINT NOT NULL,
    stock_quantity INT DEFAULT 0,
    sku VARCHAR(100) UNIQUE,
    status ENUM('ACTIVE', 'INACTIVE', 'OUT_OF_STOCK') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    FOREIGN KEY (seller_id) REFERENCES users(user_id),
    INDEX idx_category (category_id),
    INDEX idx_seller (seller_id),
    INDEX idx_price (price),
    FULLTEXT INDEX idx_search (name, description)
);

-- ═══════════════════════════════════════════════════════════
-- ORDERS
-- ═══════════════════════════════════════════════════════════
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    tax_amount DECIMAL(10, 2) DEFAULT 0,
    status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'RETURNED') DEFAULT 'PENDING',
    payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (address_id) REFERENCES addresses(address_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- ═══════════════════════════════════════════════════════════
-- ORDER_ITEMS (Junction table: Orders ↔ Products)
-- ═══════════════════════════════════════════════════════════
CREATE TABLE order_items (
    order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id)
);

-- ═══════════════════════════════════════════════════════════
-- REVIEWS
-- ═══════════════════════════════════════════════════════════
CREATE TABLE reviews (
    review_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    title VARCHAR(255),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE KEY unique_user_product (user_id, product_id),
    INDEX idx_product_rating (product_id, rating)
);
```

### Pattern B: Social Media System

```sql
-- ═══════════════════════════════════════════════════════════
-- USERS
-- ═══════════════════════════════════════════════════════════
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    bio TEXT,
    profile_image_url VARCHAR(500),
    is_verified BOOLEAN DEFAULT FALSE,
    follower_count INT DEFAULT 0,
    following_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ═══════════════════════════════════════════════════════════
-- FOLLOWS (Self-referencing M:N)
-- ═══════════════════════════════════════════════════════════
CREATE TABLE follows (
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (follower_id, following_id),
    FOREIGN KEY (follower_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (following_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_following (following_id)
);

-- ═══════════════════════════════════════════════════════════
-- POSTS
-- ═══════════════════════════════════════════════════════════
CREATE TABLE posts (
    post_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content TEXT,
    media_url VARCHAR(500),
    media_type ENUM('IMAGE', 'VIDEO', 'TEXT') DEFAULT 'TEXT',
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
);

-- ═══════════════════════════════════════════════════════════
-- LIKES (M:N between Users and Posts)
-- ═══════════════════════════════════════════════════════════
CREATE TABLE likes (
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    INDEX idx_post_id (post_id)
);

-- ═══════════════════════════════════════════════════════════
-- COMMENTS (Self-referencing for nested replies)
-- ═══════════════════════════════════════════════════════════
CREATE TABLE comments (
    comment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_comment_id BIGINT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE,
    INDEX idx_post_id (post_id)
);

-- ═══════════════════════════════════════════════════════════
-- HASHTAGS & POST_HASHTAGS (M:N)
-- ═══════════════════════════════════════════════════════════
CREATE TABLE hashtags (
    hashtag_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tag VARCHAR(100) UNIQUE NOT NULL,
    post_count INT DEFAULT 0
);

CREATE TABLE post_hashtags (
    post_id BIGINT NOT NULL,
    hashtag_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, hashtag_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
    FOREIGN KEY (hashtag_id) REFERENCES hashtags(hashtag_id) ON DELETE CASCADE
);
```

### Pattern C: Ride-Sharing System

```sql
-- Users (Riders & Drivers)
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    user_type ENUM('RIDER', 'DRIVER', 'BOTH') NOT NULL,
    rating DECIMAL(3, 2) DEFAULT 5.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Driver-specific details
CREATE TABLE driver_details (
    driver_id BIGINT PRIMARY KEY,
    license_number VARCHAR(100) UNIQUE NOT NULL,
    vehicle_type ENUM('BIKE', 'AUTO', 'SEDAN', 'SUV', 'PREMIUM') NOT NULL,
    vehicle_number VARCHAR(50) NOT NULL,
    is_available BOOLEAN DEFAULT FALSE,
    current_lat DECIMAL(10, 8),
    current_lng DECIMAL(11, 8),
    FOREIGN KEY (driver_id) REFERENCES users(user_id)
);

-- Rides
CREATE TABLE rides (
    ride_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rider_id BIGINT NOT NULL,
    driver_id BIGINT,
    pickup_lat DECIMAL(10, 8) NOT NULL,
    pickup_lng DECIMAL(11, 8) NOT NULL,
    dropoff_lat DECIMAL(10, 8) NOT NULL,
    dropoff_lng DECIMAL(11, 8) NOT NULL,
    distance_km DECIMAL(10, 2),
    estimated_fare DECIMAL(10, 2),
    actual_fare DECIMAL(10, 2),
    status ENUM('REQUESTED', 'DRIVER_ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'),
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (rider_id) REFERENCES users(user_id),
    FOREIGN KEY (driver_id) REFERENCES users(user_id),
    INDEX idx_status (status),
    INDEX idx_requested_at (requested_at)
);
```

---

## 3. How to Approach SQL Interview Questions

### 🧠 The FRAMEWORK Method

When you see an SQL problem in an interview, follow this systematic approach:

```
F - FOCUS on what's being asked (output columns)
R - RECOGNIZE the tables involved
A - ANALYZE relationships (JOINs needed)
M - MAP out aggregations (GROUP BY, HAVING)
E - EVALUATE filtering (WHERE conditions)
W - WINDOW functions if ranking/running totals needed
O - ORDER and LIMIT the results
R - REVIEW for edge cases (NULL handling)
K - KICKSTART with simple version, then optimize
```

### Step-by-Step Example

**Question:** "Find the top 3 customers by total spending in each category"

```
Step 1: FOCUS - Output needs: customer_name, category_name, total_spent, rank

Step 2: RECOGNIZE tables: users, orders, order_items, products, categories

Step 3: ANALYZE JOINs needed:
        orders → users (get customer name)
        orders → order_items (get items)
        order_items → products (get product info)
        products → categories (get category)

Step 4: MAP aggregations: SUM(quantity * unit_price) GROUP BY user, category

Step 5: EVALUATE filters: Only DELIVERED orders

Step 6: WINDOW function: RANK() OVER (PARTITION BY category ORDER BY total DESC)

Step 7: ORDER: By category, then rank

Step 8: REVIEW: Handle NULLs, filter rank <= 3
```

**Solution:**

```sql
WITH customer_category_spending AS (
    SELECT 
        u.user_id,
        u.first_name AS customer_name,
        c.category_id,
        c.name AS category_name,
        SUM(oi.quantity * oi.unit_price) AS total_spent
    FROM users u
    INNER JOIN orders o ON u.user_id = o.user_id
    INNER JOIN order_items oi ON o.order_id = oi.order_id
    INNER JOIN products p ON oi.product_id = p.product_id
    INNER JOIN categories c ON p.category_id = c.category_id
    WHERE o.status = 'DELIVERED'
    GROUP BY u.user_id, u.first_name, c.category_id, c.name
),
ranked_customers AS (
    SELECT 
        *,
        RANK() OVER (
            PARTITION BY category_id 
            ORDER BY total_spent DESC
        ) AS spending_rank
    FROM customer_category_spending
)
SELECT 
    customer_name,
    category_name,
    total_spent,
    spending_rank
FROM ranked_customers
WHERE spending_rank <= 3
ORDER BY category_name, spending_rank;
```

### 📝 Query Writing Checklist

Before submitting your query, verify:

- [ ] **SELECT**: All required columns present?
- [ ] **FROM/JOIN**: All necessary tables joined?
- [ ] **JOIN type**: INNER vs LEFT (will rows be excluded?)
- [ ] **WHERE**: Filters applied before aggregation?
- [ ] **GROUP BY**: All non-aggregated columns included?
- [ ] **HAVING**: Filters on aggregated values?
- [ ] **ORDER BY**: Correct sort direction (ASC/DESC)?
- [ ] **NULL handling**: COALESCE or IFNULL where needed?
- [ ] **Edge cases**: Empty tables, duplicates?

---

## 4. Essential SQL Queries by Category

### 4.1 Basic CRUD & Filtering

```sql
-- ✅ Find active users registered in last 30 days
SELECT * FROM users 
WHERE is_active = TRUE 
AND created_at >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 30 DAY);

-- ✅ Products in price range with stock
SELECT product_id, name, price, stock_quantity
FROM products
WHERE price BETWEEN 100 AND 500
AND stock_quantity > 0
ORDER BY price ASC;

-- ✅ Case-insensitive search
SELECT * FROM products
WHERE LOWER(name) LIKE '%wireless%';
```

### 4.2 JOINs (Most Important!)

```sql
-- ✅ INNER JOIN: Order details with user and product info
SELECT 
    o.order_id,
    u.first_name,
    u.email,
    p.name AS product_name,
    oi.quantity,
    oi.unit_price,
    (oi.quantity * oi.unit_price) AS item_total
FROM orders o
INNER JOIN users u ON o.user_id = u.user_id
INNER JOIN order_items oi ON o.order_id = oi.order_id
INNER JOIN products p ON oi.product_id = p.product_id
WHERE o.order_id = 12345;

-- ✅ LEFT JOIN: Users who never placed an order
SELECT u.user_id, u.email, u.first_name
FROM users u
LEFT JOIN orders o ON u.user_id = o.user_id
WHERE o.order_id IS NULL;

-- ✅ Self JOIN: Get all followers of a user
SELECT 
    u1.username AS follower_username,
    u2.username AS following_username,
    f.created_at AS followed_on
FROM follows f
INNER JOIN users u1 ON f.follower_id = u1.user_id
INNER JOIN users u2 ON f.following_id = u2.user_id
WHERE u2.user_id = 100;

-- ✅ Find mutual followers (bidirectional follows)
SELECT 
    u1.username AS user1,
    u2.username AS user2
FROM follows f1
INNER JOIN follows f2 
    ON f1.follower_id = f2.following_id 
    AND f1.following_id = f2.follower_id
INNER JOIN users u1 ON f1.follower_id = u1.user_id
INNER JOIN users u2 ON f1.following_id = u2.user_id
WHERE f1.follower_id < f1.following_id;  -- Avoid duplicates
```

### 4.3 Aggregations & GROUP BY

```sql
-- ✅ Total revenue per seller
SELECT 
    p.seller_id,
    u.first_name AS seller_name,
    COUNT(DISTINCT o.order_id) AS total_orders,
    SUM(oi.quantity) AS total_items_sold,
    SUM(oi.quantity * oi.unit_price) AS total_revenue
FROM order_items oi
INNER JOIN orders o ON oi.order_id = o.order_id
INNER JOIN products p ON oi.product_id = p.product_id
INNER JOIN users u ON p.seller_id = u.user_id
WHERE o.status = 'DELIVERED'
GROUP BY p.seller_id, u.first_name
ORDER BY total_revenue DESC;

-- ✅ Average rating with minimum review count
SELECT 
    p.product_id,
    p.name,
    COUNT(r.review_id) AS review_count,
    ROUND(AVG(r.rating), 2) AS avg_rating
FROM products p
LEFT JOIN reviews r ON p.product_id = r.product_id
GROUP BY p.product_id, p.name
HAVING COUNT(r.review_id) >= 5
ORDER BY avg_rating DESC;

-- ✅ Monthly revenue report
SELECT 
    YEAR(o.created_at) AS year,
    MONTH(o.created_at) AS month,
    COUNT(DISTINCT o.order_id) AS total_orders,
    SUM(o.total_amount) AS total_revenue,
    ROUND(AVG(o.total_amount), 2) AS avg_order_value
FROM orders o
WHERE o.status = 'DELIVERED'
GROUP BY YEAR(o.created_at), MONTH(o.created_at)
ORDER BY year DESC, month DESC;

-- ✅ Top 5 categories by sales
SELECT 
    c.name AS category_name,
    SUM(oi.quantity * oi.unit_price) AS total_sales
FROM categories c
INNER JOIN products p ON c.category_id = p.category_id
INNER JOIN order_items oi ON p.product_id = oi.product_id
INNER JOIN orders o ON oi.order_id = o.order_id
WHERE o.status = 'DELIVERED'
GROUP BY c.category_id, c.name
ORDER BY total_sales DESC
LIMIT 5;
```

### 4.4 Subqueries

```sql
-- ✅ Products never ordered
SELECT product_id, name, price
FROM products
WHERE product_id NOT IN (
    SELECT DISTINCT product_id FROM order_items
);

-- ✅ Users who spent more than average
SELECT 
    u.user_id,
    u.first_name,
    SUM(o.total_amount) AS total_spent
FROM users u
INNER JOIN orders o ON u.user_id = o.user_id
WHERE o.status = 'DELIVERED'
GROUP BY u.user_id, u.first_name
HAVING SUM(o.total_amount) > (
    SELECT AVG(total_spent) FROM (
        SELECT SUM(total_amount) AS total_spent
        FROM orders
        WHERE status = 'DELIVERED'
        GROUP BY user_id
    ) AS avg_calc
);

-- ✅ Second highest price in each category (correlated subquery)
SELECT p1.*
FROM products p1
WHERE 1 = (
    SELECT COUNT(DISTINCT p2.price)
    FROM products p2
    WHERE p2.category_id = p1.category_id
    AND p2.price > p1.price
);
```

---

## 5. Window Functions Deep Dive

Window functions are **critical** for SDE-2 interviews. Master these patterns:

### 5.1 Ranking Functions

```sql
-- ROW_NUMBER vs RANK vs DENSE_RANK
SELECT 
    product_id,
    name,
    price,
    ROW_NUMBER() OVER (ORDER BY price DESC) AS row_num,    -- 1,2,3,4,5
    RANK() OVER (ORDER BY price DESC) AS rank_num,         -- 1,1,3,4,4 (gaps)
    DENSE_RANK() OVER (ORDER BY price DESC) AS dense_num   -- 1,1,2,3,3 (no gaps)
FROM products;

-- ✅ Rank products within each category
SELECT 
    p.product_id,
    p.name,
    c.name AS category,
    SUM(oi.quantity) AS total_sold,
    RANK() OVER (
        PARTITION BY p.category_id 
        ORDER BY SUM(oi.quantity) DESC
    ) AS category_rank
FROM products p
INNER JOIN categories c ON p.category_id = c.category_id
INNER JOIN order_items oi ON p.product_id = oi.product_id
GROUP BY p.product_id, p.name, c.name, p.category_id;
```

### 5.2 Running Totals & Moving Averages

```sql
-- ✅ Running total of orders per user
SELECT 
    user_id,
    order_id,
    total_amount,
    created_at,
    SUM(total_amount) OVER (
        PARTITION BY user_id 
        ORDER BY created_at 
        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS running_total
FROM orders;

-- ✅ 7-day moving average of daily sales
SELECT 
    DATE(created_at) AS order_date,
    SUM(total_amount) AS daily_sales,
    AVG(SUM(total_amount)) OVER (
        ORDER BY DATE(created_at)
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ) AS moving_avg_7d
FROM orders
WHERE status = 'DELIVERED'
GROUP BY DATE(created_at);
```

### 5.3 LAG/LEAD for Comparisons

```sql
-- ✅ Month-over-month revenue growth
WITH monthly_revenue AS (
    SELECT 
        DATE_FORMAT(created_at, '%Y-%m') AS month,
        SUM(total_amount) AS revenue
    FROM orders
    WHERE status = 'DELIVERED'
    GROUP BY DATE_FORMAT(created_at, '%Y-%m')
)
SELECT 
    month,
    revenue,
    LAG(revenue) OVER (ORDER BY month) AS prev_month,
    ROUND(
        (revenue - LAG(revenue) OVER (ORDER BY month)) / 
        LAG(revenue) OVER (ORDER BY month) * 100, 
        2
    ) AS growth_pct
FROM monthly_revenue;

-- ✅ Days between consecutive orders per user
SELECT 
    user_id,
    order_id,
    created_at,
    LAG(created_at) OVER (PARTITION BY user_id ORDER BY created_at) AS prev_order,
    DATEDIFF(
        created_at, 
        LAG(created_at) OVER (PARTITION BY user_id ORDER BY created_at)
    ) AS days_since_last_order
FROM orders;
```

### 5.4 FIRST_VALUE / LAST_VALUE / NTH_VALUE

```sql
-- ✅ First and most recent order per user
SELECT DISTINCT
    user_id,
    FIRST_VALUE(order_id) OVER w AS first_order_id,
    FIRST_VALUE(created_at) OVER w AS first_order_date,
    LAST_VALUE(order_id) OVER w AS latest_order_id,
    LAST_VALUE(created_at) OVER w AS latest_order_date
FROM orders
WINDOW w AS (
    PARTITION BY user_id 
    ORDER BY created_at
    ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
);
```

### 5.5 Percentiles with NTILE & PERCENT_RANK

```sql
-- ✅ Divide customers into quartiles by spending
SELECT 
    user_id,
    total_spent,
    NTILE(4) OVER (ORDER BY total_spent DESC) AS spending_quartile,
    ROUND(PERCENT_RANK() OVER (ORDER BY total_spent) * 100, 2) AS percentile
FROM (
    SELECT user_id, SUM(total_amount) AS total_spent
    FROM orders
    WHERE status = 'DELIVERED'
    GROUP BY user_id
) AS user_spending;
```

---

## 6. Common Interview Patterns

### Pattern 1: CTEs (Common Table Expressions)

```sql
-- ✅ Multi-step analysis with CTEs
WITH active_users AS (
    SELECT DISTINCT user_id
    FROM orders
    WHERE created_at >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY)
),
user_stats AS (
    SELECT 
        u.user_id,
        u.first_name,
        COUNT(o.order_id) AS order_count,
        SUM(o.total_amount) AS total_spent
    FROM users u
    INNER JOIN orders o ON u.user_id = o.user_id
    GROUP BY u.user_id, u.first_name
)
SELECT us.*
FROM user_stats us
INNER JOIN active_users au ON us.user_id = au.user_id
ORDER BY us.total_spent DESC;
```

### Pattern 2: Recursive CTEs (Hierarchical Data)

```sql
-- ✅ Get full category hierarchy
WITH RECURSIVE category_tree AS (
    -- Base case: root categories
    SELECT 
        category_id,
        name,
        parent_category_id,
        1 AS level,
        CAST(name AS CHAR(1000)) AS path
    FROM categories
    WHERE parent_category_id IS NULL
    
    UNION ALL
    
    -- Recursive case
    SELECT 
        c.category_id,
        c.name,
        c.parent_category_id,
        ct.level + 1,
        CONCAT(ct.path, ' > ', c.name)
    FROM categories c
    INNER JOIN category_tree ct ON c.parent_category_id = ct.category_id
)
SELECT * FROM category_tree ORDER BY path;
```

### Pattern 3: News Feed Query

```sql
-- ✅ Get posts from followed users (Instagram-like feed)
SELECT 
    p.post_id,
    p.content,
    p.like_count,
    p.created_at,
    u.username,
    u.profile_image_url
FROM posts p
INNER JOIN users u ON p.user_id = u.user_id
WHERE p.user_id IN (
    SELECT following_id 
    FROM follows 
    WHERE follower_id = @current_user_id
)
ORDER BY p.created_at DESC
LIMIT 20 OFFSET 0;
```

### Pattern 4: "Also Bought" Recommendation

```sql
-- ✅ Users who bought X also bought Y
SELECT 
    p2.product_id,
    p2.name,
    COUNT(*) AS co_purchase_count
FROM order_items oi1
INNER JOIN order_items oi2 
    ON oi1.order_id = oi2.order_id 
    AND oi1.product_id != oi2.product_id
INNER JOIN products p2 ON oi2.product_id = p2.product_id
WHERE oi1.product_id = @given_product_id
GROUP BY p2.product_id, p2.name
ORDER BY co_purchase_count DESC
LIMIT 5;
```

### Pattern 5: Geospatial Query (Find Nearby)

```sql
-- ✅ Find drivers within 5km radius (Uber-like)
SELECT 
    u.user_id,
    u.name,
    dd.vehicle_type,
    (
        6371 * ACOS(
            COS(RADIANS(@user_lat)) * COS(RADIANS(dd.current_lat)) *
            COS(RADIANS(dd.current_lng) - RADIANS(@user_lng)) +
            SIN(RADIANS(@user_lat)) * SIN(RADIANS(dd.current_lat))
        )
    ) AS distance_km
FROM driver_details dd
INNER JOIN users u ON dd.driver_id = u.user_id
WHERE dd.is_available = TRUE
HAVING distance_km <= 5
ORDER BY distance_km ASC
LIMIT 10;
```

### Pattern 6: Date/Time Analysis

```sql
-- ✅ Orders by day of week
SELECT 
    DAYNAME(created_at) AS day_of_week,
    COUNT(*) AS order_count
FROM orders
GROUP BY DAYNAME(created_at), DAYOFWEEK(created_at)
ORDER BY DAYOFWEEK(created_at);

-- ✅ Inactive users (no order in 90+ days)
SELECT 
    u.user_id,
    u.email,
    MAX(o.created_at) AS last_order_date,
    DATEDIFF(CURRENT_DATE, MAX(o.created_at)) AS days_inactive
FROM users u
LEFT JOIN orders o ON u.user_id = o.user_id
GROUP BY u.user_id, u.email
HAVING MAX(o.created_at) IS NULL 
    OR DATEDIFF(CURRENT_DATE, MAX(o.created_at)) > 90;

-- ✅ Hourly order distribution
SELECT 
    HOUR(created_at) AS hour_of_day,
    COUNT(*) AS order_count
FROM orders
GROUP BY HOUR(created_at)
ORDER BY hour_of_day;
```

---

## 7. Performance & Optimization

### 7.1 Index Strategy

```sql
-- ✅ Create composite index for common query patterns
CREATE INDEX idx_orders_user_status_date 
ON orders(user_id, status, created_at);

-- Query that benefits from this index:
SELECT * FROM orders
WHERE user_id = 100
AND status = 'DELIVERED'
AND created_at >= '2024-01-01';
```

### 7.2 Query Analysis

```sql
-- ✅ Use EXPLAIN to analyze query plan
EXPLAIN ANALYZE
SELECT p.*, c.name AS category_name
FROM products p
INNER JOIN categories c ON p.category_id = c.category_id
WHERE p.price > 100;
```

### 7.3 Pagination Best Practices

```sql
-- ❌ BAD: OFFSET-based (slow for large offsets)
SELECT * FROM posts 
ORDER BY created_at DESC 
LIMIT 20 OFFSET 10000;

-- ✅ GOOD: Cursor-based pagination
SELECT * FROM posts
WHERE created_at < '2024-06-15 10:30:00'  -- Last seen timestamp
ORDER BY created_at DESC
LIMIT 20;
```

### 7.4 UPSERT Pattern

```sql
-- ✅ Insert or Update (MySQL)
INSERT INTO user_preferences (user_id, preference_key, preference_value)
VALUES (100, 'dark_mode', 'true')
ON DUPLICATE KEY UPDATE 
    preference_value = VALUES(preference_value),
    updated_at = CURRENT_TIMESTAMP;

-- PostgreSQL equivalent
INSERT INTO user_preferences (user_id, preference_key, preference_value)
VALUES (100, 'dark_mode', 'true')
ON CONFLICT (user_id, preference_key) 
DO UPDATE SET 
    preference_value = EXCLUDED.preference_value,
    updated_at = CURRENT_TIMESTAMP;
```

---

## 8. Interview Tips & Checklist

### ✅ Schema Design Checklist

| Item | Check |
|------|-------|
| Use BIGINT for IDs | ☐ |
| DECIMAL for money (not FLOAT) | ☐ |
| Add created_at/updated_at timestamps | ☐ |
| Index all foreign keys | ☐ |
| Index frequently filtered columns | ☐ |
| Use ENUM for fixed value sets | ☐ |
| Add appropriate constraints (NOT NULL, UNIQUE) | ☐ |
| Consider soft delete vs hard delete | ☐ |
| Use composite PK for junction tables | ☐ |

### ❌ Common Mistakes to Avoid

| Mistake | Solution |
|---------|----------|
| Using `SELECT *` | Specify needed columns |
| Missing indexes on JOIN columns | Add indexes |
| Using OFFSET for deep pagination | Use cursor-based |
| Not handling NULL in aggregations | Use COALESCE/IFNULL |
| Forgetting GROUP BY non-aggregated columns | Include all columns |
| Using reserved words as column names | Rename or quote |

### 📊 Topics by Priority

| Topic | Priority | Interview Frequency |
|-------|----------|---------------------|
| JOINs (all types) | 🔴 Critical | Every interview |
| GROUP BY + HAVING | 🔴 Critical | Every interview |
| Window Functions | 🔴 Critical | 80% of interviews |
| Subqueries | 🟡 High | 70% of interviews |
| CTEs | 🟡 High | 60% of interviews |
| Indexes & EXPLAIN | 🟡 High | 50% of interviews |
| Date/Time functions | 🟢 Medium | 40% of interviews |
| Recursive CTEs | 🟢 Medium | 20% of interviews |

### 🎯 Practice Resources

1. **LeetCode SQL** - 50 Easy, 50 Medium problems
2. **HackerRank SQL** - Complete all tracks
3. **StrataScratch** - Real company interview questions
4. **SQLZoo** - Interactive tutorials
5. **Mode Analytics SQL Tutorial** - Advanced concepts

### 💡 During the Interview

1. **Clarify first** - Ask about expected output format, edge cases
2. **Start simple** - Write basic query first, then optimize
3. **Verbalize thinking** - Explain your approach out loud
4. **Test with examples** - Walk through with sample data
5. **Discuss trade-offs** - Index vs storage, normalization vs speed

---

## Quick Reference Card

```
┌────────────────────────────────────────────────────────────────┐
│                     SQL QUERY TEMPLATE                         │
├────────────────────────────────────────────────────────────────┤
│  WITH cte_name AS (                                            │
│      SELECT ...                                                │
│  )                                                             │
│  SELECT                                                        │
│      column1,                                                  │
│      AGG_FUNC(column2),                                        │
│      WINDOW_FUNC() OVER (PARTITION BY x ORDER BY y)            │
│  FROM table1                                                   │
│  [INNER|LEFT|RIGHT] JOIN table2 ON ...                         │
│  WHERE condition      -- Filter BEFORE grouping                │
│  GROUP BY column1                                              │
│  HAVING AGG_FUNC(x) > n  -- Filter AFTER grouping              │
│  ORDER BY column1 [ASC|DESC]                                   │
│  LIMIT n OFFSET m;                                             │
├────────────────────────────────────────────────────────────────┤
│  EXECUTION ORDER:                                              │
│  FROM → WHERE → GROUP BY → HAVING → SELECT → ORDER BY → LIMIT  │
└────────────────────────────────────────────────────────────────┘
```

---

*Last Updated: January 2026*

