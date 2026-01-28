# 🎯 25 Most Common SQL Interview Questions + Data Modeling Techniques

> Complete solutions with explanations for SDE-2 interviews at FAANG and startups

---

## Table of Contents

- [Part A: 25 Most Common SQL Interview Questions](#part-a-25-most-common-sql-interview-questions)
- [Part B: Data Modeling Techniques](#part-b-data-modeling-techniques)

---

# Part A: 25 Most Common SQL Interview Questions

## Setup: Sample Database Schema

```sql
-- We'll use this schema for all questions
CREATE TABLE employees (
    emp_id INT PRIMARY KEY,
    name VARCHAR(100),
    department_id INT,
    manager_id INT,
    salary DECIMAL(10,2),
    hire_date DATE,
    FOREIGN KEY (department_id) REFERENCES departments(dept_id),
    FOREIGN KEY (manager_id) REFERENCES employees(emp_id)
);

CREATE TABLE departments (
    dept_id INT PRIMARY KEY,
    dept_name VARCHAR(100),
    location VARCHAR(100)
);

CREATE TABLE projects (
    project_id INT PRIMARY KEY,
    project_name VARCHAR(100),
    budget DECIMAL(12,2),
    start_date DATE,
    end_date DATE
);

CREATE TABLE employee_projects (
    emp_id INT,
    project_id INT,
    role VARCHAR(50),
    hours_worked INT,
    PRIMARY KEY (emp_id, project_id)
);

CREATE TABLE salaries (
    emp_id INT,
    salary DECIMAL(10,2),
    from_date DATE,
    to_date DATE
);

CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer_id INT,
    order_date DATE,
    total_amount DECIMAL(10,2),
    status VARCHAR(20)
);

CREATE TABLE customers (
    customer_id INT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(255),
    city VARCHAR(100),
    created_at TIMESTAMP
);
```

---

## Question 1: Find the Second Highest Salary

**Difficulty:** ⭐⭐ Medium | **Frequency:** Very High

```sql
-- Method 1: Using LIMIT and OFFSET
SELECT DISTINCT salary
FROM employees
ORDER BY salary DESC
LIMIT 1 OFFSET 1;

-- Method 2: Using Subquery
SELECT MAX(salary) AS second_highest
FROM employees
WHERE salary < (SELECT MAX(salary) FROM employees);

-- Method 3: Using DENSE_RANK (handles ties correctly)
SELECT salary AS second_highest
FROM (
    SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rank_num
    FROM employees
) ranked
WHERE rank_num = 2
LIMIT 1;

-- Method 4: Using CTE (most readable)
WITH ranked_salaries AS (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
    FROM employees
)
SELECT DISTINCT salary AS second_highest
FROM ranked_salaries
WHERE rnk = 2;
```

**💡 Key Insight:** Use `DENSE_RANK()` over `RANK()` when you want to handle ties without gaps.

---

## Question 2: Find Nth Highest Salary

**Difficulty:** ⭐⭐ Medium | **Frequency:** Very High

```sql
-- Generic solution for Nth highest (set @N = desired position)
SET @N = 3;  -- For 3rd highest

-- Method 1: DENSE_RANK
SELECT DISTINCT salary
FROM (
    SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
    FROM employees
) t
WHERE rnk = @N;

-- Method 2: LIMIT OFFSET
SELECT DISTINCT salary
FROM employees
ORDER BY salary DESC
LIMIT 1 OFFSET @N - 1;

-- Method 3: Correlated Subquery (works in all databases)
SELECT DISTINCT salary
FROM employees e1
WHERE @N - 1 = (
    SELECT COUNT(DISTINCT salary)
    FROM employees e2
    WHERE e2.salary > e1.salary
);
```

---

## Question 3: Find Duplicate Emails

**Difficulty:** ⭐ Easy | **Frequency:** Very High

```sql
-- Method 1: GROUP BY with HAVING
SELECT email
FROM customers
GROUP BY email
HAVING COUNT(*) > 1;

-- Method 2: Using Window Function
SELECT DISTINCT email
FROM (
    SELECT email, COUNT(*) OVER (PARTITION BY email) AS cnt
    FROM customers
) t
WHERE cnt > 1;

-- Method 3: Self JOIN
SELECT DISTINCT c1.email
FROM customers c1
INNER JOIN customers c2 
    ON c1.email = c2.email 
    AND c1.customer_id != c2.customer_id;
```

---

## Question 4: Delete Duplicate Rows (Keep One)

**Difficulty:** ⭐⭐ Medium | **Frequency:** High

```sql
-- Method 1: Keep row with minimum ID
DELETE c1
FROM customers c1
INNER JOIN customers c2
    ON c1.email = c2.email
    AND c1.customer_id > c2.customer_id;

-- Method 2: Using ROW_NUMBER (MySQL 8+)
DELETE FROM customers
WHERE customer_id IN (
    SELECT customer_id FROM (
        SELECT customer_id,
               ROW_NUMBER() OVER (PARTITION BY email ORDER BY customer_id) AS rn
        FROM customers
    ) t
    WHERE rn > 1
);

-- Method 3: Using CTE (PostgreSQL, SQL Server)
WITH duplicates AS (
    SELECT customer_id,
           ROW_NUMBER() OVER (PARTITION BY email ORDER BY customer_id) AS rn
    FROM customers
)
DELETE FROM customers
WHERE customer_id IN (SELECT customer_id FROM duplicates WHERE rn > 1);
```

---

## Question 5: Employees Earning More Than Their Managers

**Difficulty:** ⭐⭐ Medium | **Frequency:** Very High

```sql
-- Method 1: Self JOIN
SELECT e.name AS employee, e.salary AS emp_salary,
       m.name AS manager, m.salary AS mgr_salary
FROM employees e
INNER JOIN employees m ON e.manager_id = m.emp_id
WHERE e.salary > m.salary;

-- Method 2: Subquery
SELECT name, salary
FROM employees e
WHERE salary > (
    SELECT salary 
    FROM employees 
    WHERE emp_id = e.manager_id
);

-- Method 3: Using CTE for clarity
WITH emp_mgr AS (
    SELECT 
        e.emp_id,
        e.name AS emp_name,
        e.salary AS emp_salary,
        m.name AS mgr_name,
        m.salary AS mgr_salary
    FROM employees e
    LEFT JOIN employees m ON e.manager_id = m.emp_id
)
SELECT emp_name, emp_salary, mgr_name, mgr_salary
FROM emp_mgr
WHERE emp_salary > mgr_salary;
```

---

## Question 6: Department with Highest Average Salary

**Difficulty:** ⭐⭐ Medium | **Frequency:** High

```sql
-- Method 1: Subquery with LIMIT
SELECT d.dept_name, AVG(e.salary) AS avg_salary
FROM employees e
INNER JOIN departments d ON e.department_id = d.dept_id
GROUP BY d.dept_id, d.dept_name
ORDER BY avg_salary DESC
LIMIT 1;

-- Method 2: Using RANK
WITH dept_avg AS (
    SELECT 
        d.dept_name,
        AVG(e.salary) AS avg_salary,
        RANK() OVER (ORDER BY AVG(e.salary) DESC) AS rnk
    FROM employees e
    INNER JOIN departments d ON e.department_id = d.dept_id
    GROUP BY d.dept_id, d.dept_name
)
SELECT dept_name, avg_salary
FROM dept_avg
WHERE rnk = 1;

-- Method 3: ALL comparison (handles ties)
SELECT d.dept_name, AVG(e.salary) AS avg_salary
FROM employees e
INNER JOIN departments d ON e.department_id = d.dept_id
GROUP BY d.dept_id, d.dept_name
HAVING AVG(e.salary) >= ALL (
    SELECT AVG(salary)
    FROM employees
    GROUP BY department_id
);
```

---

## Question 7: Top 3 Salaries in Each Department

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** Very High

```sql
-- Method 1: DENSE_RANK (recommended)
WITH ranked AS (
    SELECT 
        e.name,
        e.salary,
        d.dept_name,
        DENSE_RANK() OVER (
            PARTITION BY e.department_id 
            ORDER BY e.salary DESC
        ) AS salary_rank
    FROM employees e
    INNER JOIN departments d ON e.department_id = d.dept_id
)
SELECT dept_name, name, salary, salary_rank
FROM ranked
WHERE salary_rank <= 3
ORDER BY dept_name, salary_rank;

-- Method 2: Correlated Subquery (works in older MySQL)
SELECT e1.name, e1.salary, d.dept_name
FROM employees e1
INNER JOIN departments d ON e1.department_id = d.dept_id
WHERE 3 > (
    SELECT COUNT(DISTINCT e2.salary)
    FROM employees e2
    WHERE e2.department_id = e1.department_id
    AND e2.salary > e1.salary
)
ORDER BY d.dept_name, e1.salary DESC;
```

---

## Question 8: Consecutive Days Login / Streak Problems

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** High

```sql
-- Find users with 3+ consecutive login days
CREATE TABLE logins (
    user_id INT,
    login_date DATE
);

-- Method: Using LAG to detect gaps
WITH login_groups AS (
    SELECT 
        user_id,
        login_date,
        login_date - INTERVAL ROW_NUMBER() OVER (
            PARTITION BY user_id ORDER BY login_date
        ) DAY AS grp
    FROM (SELECT DISTINCT user_id, login_date FROM logins) t
),
streaks AS (
    SELECT 
        user_id,
        grp,
        COUNT(*) AS consecutive_days,
        MIN(login_date) AS streak_start,
        MAX(login_date) AS streak_end
    FROM login_groups
    GROUP BY user_id, grp
)
SELECT user_id, consecutive_days, streak_start, streak_end
FROM streaks
WHERE consecutive_days >= 3
ORDER BY user_id, streak_start;
```

**💡 Key Insight:** The trick is `login_date - ROW_NUMBER()`. For consecutive dates, this produces the same value (grp), allowing grouping.

---

## Question 9: Year-over-Year Growth

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** High

```sql
-- Calculate YoY revenue growth
WITH yearly_revenue AS (
    SELECT 
        YEAR(order_date) AS year,
        SUM(total_amount) AS revenue
    FROM orders
    WHERE status = 'COMPLETED'
    GROUP BY YEAR(order_date)
)
SELECT 
    year,
    revenue,
    LAG(revenue) OVER (ORDER BY year) AS prev_year_revenue,
    ROUND(
        (revenue - LAG(revenue) OVER (ORDER BY year)) / 
        LAG(revenue) OVER (ORDER BY year) * 100, 
        2
    ) AS yoy_growth_pct
FROM yearly_revenue
ORDER BY year;

-- Month-over-Month with same logic
WITH monthly_revenue AS (
    SELECT 
        DATE_FORMAT(order_date, '%Y-%m') AS month,
        SUM(total_amount) AS revenue
    FROM orders
    GROUP BY DATE_FORMAT(order_date, '%Y-%m')
)
SELECT 
    month,
    revenue,
    LAG(revenue) OVER (ORDER BY month) AS prev_month,
    ROUND(
        (revenue - LAG(revenue) OVER (ORDER BY month)) * 100.0 / 
        NULLIF(LAG(revenue) OVER (ORDER BY month), 0),
        2
    ) AS mom_growth_pct
FROM monthly_revenue;
```

---

## Question 10: Running Total / Cumulative Sum

**Difficulty:** ⭐⭐ Medium | **Frequency:** Very High

```sql
-- Running total of orders per customer
SELECT 
    customer_id,
    order_id,
    order_date,
    total_amount,
    SUM(total_amount) OVER (
        PARTITION BY customer_id 
        ORDER BY order_date
        ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
    ) AS running_total
FROM orders
ORDER BY customer_id, order_date;

-- Running total for entire table (no partition)
SELECT 
    order_date,
    total_amount,
    SUM(total_amount) OVER (ORDER BY order_date) AS cumulative_revenue
FROM orders;

-- Running average (moving average)
SELECT 
    order_date,
    total_amount,
    AVG(total_amount) OVER (
        ORDER BY order_date
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ) AS moving_avg_7days
FROM orders;
```

---

## Question 11: Find Employees with No Subordinates

**Difficulty:** ⭐⭐ Medium | **Frequency:** Medium

```sql
-- Method 1: NOT IN
SELECT emp_id, name
FROM employees
WHERE emp_id NOT IN (
    SELECT DISTINCT manager_id 
    FROM employees 
    WHERE manager_id IS NOT NULL
);

-- Method 2: NOT EXISTS (more efficient)
SELECT e1.emp_id, e1.name
FROM employees e1
WHERE NOT EXISTS (
    SELECT 1 
    FROM employees e2 
    WHERE e2.manager_id = e1.emp_id
);

-- Method 3: LEFT JOIN
SELECT e1.emp_id, e1.name
FROM employees e1
LEFT JOIN employees e2 ON e1.emp_id = e2.manager_id
WHERE e2.emp_id IS NULL;
```

---

## Question 12: Pivot Table - Rows to Columns

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** High

```sql
-- Sample: Monthly sales by product
CREATE TABLE sales (
    product VARCHAR(50),
    month VARCHAR(20),
    amount DECIMAL(10,2)
);

-- PIVOT using CASE WHEN (works in all databases)
SELECT 
    product,
    SUM(CASE WHEN month = 'January' THEN amount ELSE 0 END) AS January,
    SUM(CASE WHEN month = 'February' THEN amount ELSE 0 END) AS February,
    SUM(CASE WHEN month = 'March' THEN amount ELSE 0 END) AS March,
    SUM(CASE WHEN month = 'April' THEN amount ELSE 0 END) AS April
FROM sales
GROUP BY product;

-- Dynamic Pivot (requires stored procedure in MySQL)
-- SQL Server has native PIVOT:
/*
SELECT *
FROM sales
PIVOT (
    SUM(amount)
    FOR month IN ([January], [February], [March], [April])
) AS pivot_table;
*/
```

---

## Question 13: Unpivot - Columns to Rows

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** Medium

```sql
-- Sample pivoted table
CREATE TABLE quarterly_sales (
    product VARCHAR(50),
    Q1 DECIMAL(10,2),
    Q2 DECIMAL(10,2),
    Q3 DECIMAL(10,2),
    Q4 DECIMAL(10,2)
);

-- UNPIVOT using UNION ALL
SELECT product, 'Q1' AS quarter, Q1 AS sales FROM quarterly_sales
UNION ALL
SELECT product, 'Q2' AS quarter, Q2 AS sales FROM quarterly_sales
UNION ALL
SELECT product, 'Q3' AS quarter, Q3 AS sales FROM quarterly_sales
UNION ALL
SELECT product, 'Q4' AS quarter, Q4 AS sales FROM quarterly_sales
ORDER BY product, quarter;

-- Using CROSS JOIN with VALUES (SQL Server, PostgreSQL)
/*
SELECT q.product, v.quarter, v.sales
FROM quarterly_sales q
CROSS JOIN LATERAL (
    VALUES ('Q1', Q1), ('Q2', Q2), ('Q3', Q3), ('Q4', Q4)
) AS v(quarter, sales);
*/
```

---

## Question 14: Find Gaps in Sequential Data

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** Medium

```sql
-- Find missing order IDs (gaps in sequence)
SELECT 
    curr.order_id + 1 AS gap_start,
    next.order_id - 1 AS gap_end
FROM orders curr
INNER JOIN orders next ON next.order_id = (
    SELECT MIN(order_id) 
    FROM orders 
    WHERE order_id > curr.order_id
)
WHERE next.order_id - curr.order_id > 1;

-- Using LEAD function (cleaner)
WITH order_gaps AS (
    SELECT 
        order_id,
        LEAD(order_id) OVER (ORDER BY order_id) AS next_order_id
    FROM orders
)
SELECT 
    order_id + 1 AS gap_start,
    next_order_id - 1 AS gap_end
FROM order_gaps
WHERE next_order_id - order_id > 1;
```

---

## Question 15: Median Calculation

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** Medium

```sql
-- Method 1: Using ROW_NUMBER (works everywhere)
WITH ordered AS (
    SELECT 
        salary,
        ROW_NUMBER() OVER (ORDER BY salary) AS rn,
        COUNT(*) OVER () AS total
    FROM employees
)
SELECT AVG(salary) AS median
FROM ordered
WHERE rn IN (FLOOR((total + 1) / 2), CEIL((total + 1) / 2));

-- Method 2: Using PERCENTILE_CONT (PostgreSQL, SQL Server)
/*
SELECT PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY salary) AS median
FROM employees;
*/

-- Method 3: For MySQL 8.0+
WITH ranked AS (
    SELECT 
        salary,
        ROW_NUMBER() OVER (ORDER BY salary) AS rn,
        COUNT(*) OVER () AS cnt
    FROM employees
)
SELECT AVG(salary) AS median
FROM ranked
WHERE rn BETWEEN cnt/2.0 AND cnt/2.0 + 1;
```

---

## Question 16: Customers Who Bought All Products

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** Medium

```sql
CREATE TABLE products (product_id INT PRIMARY KEY);
CREATE TABLE customer_purchases (
    customer_id INT,
    product_id INT
);

-- Find customers who bought every product
SELECT customer_id
FROM customer_purchases
GROUP BY customer_id
HAVING COUNT(DISTINCT product_id) = (SELECT COUNT(*) FROM products);

-- Alternative: Using NOT EXISTS (Relational Division)
SELECT DISTINCT cp.customer_id
FROM customer_purchases cp
WHERE NOT EXISTS (
    SELECT p.product_id
    FROM products p
    WHERE NOT EXISTS (
        SELECT 1
        FROM customer_purchases cp2
        WHERE cp2.customer_id = cp.customer_id
        AND cp2.product_id = p.product_id
    )
);
```

---

## Question 17: Recursive Manager Hierarchy

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** High

```sql
-- Get full reporting chain for each employee
WITH RECURSIVE emp_hierarchy AS (
    -- Base case: start from employees
    SELECT 
        emp_id,
        name,
        manager_id,
        1 AS level,
        CAST(name AS CHAR(1000)) AS path
    FROM employees
    WHERE manager_id IS NULL  -- Start from CEO
    
    UNION ALL
    
    -- Recursive case: join with subordinates
    SELECT 
        e.emp_id,
        e.name,
        e.manager_id,
        h.level + 1,
        CONCAT(h.path, ' > ', e.name)
    FROM employees e
    INNER JOIN emp_hierarchy h ON e.manager_id = h.emp_id
)
SELECT emp_id, name, level, path
FROM emp_hierarchy
ORDER BY path;

-- Get all subordinates of a specific manager
WITH RECURSIVE subordinates AS (
    SELECT emp_id, name, manager_id
    FROM employees
    WHERE manager_id = 101  -- Starting manager ID
    
    UNION ALL
    
    SELECT e.emp_id, e.name, e.manager_id
    FROM employees e
    INNER JOIN subordinates s ON e.manager_id = s.emp_id
)
SELECT * FROM subordinates;
```

---

## Question 18: First/Last Value per Group

**Difficulty:** ⭐⭐ Medium | **Frequency:** High

```sql
-- First order per customer
WITH ranked AS (
    SELECT *,
           ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY order_date) AS rn
    FROM orders
)
SELECT order_id, customer_id, order_date, total_amount
FROM ranked
WHERE rn = 1;

-- First and last order per customer in one query
SELECT DISTINCT
    customer_id,
    FIRST_VALUE(order_id) OVER w AS first_order_id,
    FIRST_VALUE(order_date) OVER w AS first_order_date,
    LAST_VALUE(order_id) OVER w AS last_order_id,
    LAST_VALUE(order_date) OVER w AS last_order_date
FROM orders
WINDOW w AS (
    PARTITION BY customer_id 
    ORDER BY order_date
    ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
);

-- Using aggregate functions (simpler)
SELECT 
    customer_id,
    MIN(order_date) AS first_order_date,
    MAX(order_date) AS last_order_date,
    COUNT(*) AS total_orders
FROM orders
GROUP BY customer_id;
```

---

## Question 19: Find Customers with Increasing Orders

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** Medium

```sql
-- Customers whose order amounts are strictly increasing
WITH order_comparison AS (
    SELECT 
        customer_id,
        order_date,
        total_amount,
        LAG(total_amount) OVER (
            PARTITION BY customer_id ORDER BY order_date
        ) AS prev_amount
    FROM orders
)
SELECT DISTINCT customer_id
FROM order_comparison
WHERE customer_id NOT IN (
    SELECT DISTINCT customer_id
    FROM order_comparison
    WHERE prev_amount IS NOT NULL 
    AND total_amount <= prev_amount
);
```

---

## Question 20: Calculate Retention Rate

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** Very High (Product companies)

```sql
-- Monthly retention: Users who were active this month and last month
WITH monthly_active AS (
    SELECT DISTINCT
        customer_id,
        DATE_FORMAT(order_date, '%Y-%m') AS month
    FROM orders
),
retention AS (
    SELECT 
        curr.month,
        COUNT(DISTINCT curr.customer_id) AS active_users,
        COUNT(DISTINCT prev.customer_id) AS retained_users
    FROM monthly_active curr
    LEFT JOIN monthly_active prev 
        ON curr.customer_id = prev.customer_id
        AND prev.month = DATE_FORMAT(
            STR_TO_DATE(CONCAT(curr.month, '-01'), '%Y-%m-%d') - INTERVAL 1 MONTH,
            '%Y-%m'
        )
    GROUP BY curr.month
)
SELECT 
    month,
    active_users,
    retained_users,
    ROUND(retained_users * 100.0 / active_users, 2) AS retention_rate
FROM retention
ORDER BY month;

-- Cohort-based retention analysis
WITH first_purchase AS (
    SELECT 
        customer_id,
        DATE_FORMAT(MIN(order_date), '%Y-%m') AS cohort_month
    FROM orders
    GROUP BY customer_id
),
monthly_activity AS (
    SELECT 
        o.customer_id,
        fp.cohort_month,
        TIMESTAMPDIFF(MONTH, 
            STR_TO_DATE(CONCAT(fp.cohort_month, '-01'), '%Y-%m-%d'),
            o.order_date
        ) AS month_number
    FROM orders o
    INNER JOIN first_purchase fp ON o.customer_id = fp.customer_id
)
SELECT 
    cohort_month,
    month_number,
    COUNT(DISTINCT customer_id) AS users,
    ROUND(
        COUNT(DISTINCT customer_id) * 100.0 / 
        FIRST_VALUE(COUNT(DISTINCT customer_id)) OVER (
            PARTITION BY cohort_month ORDER BY month_number
        ),
        2
    ) AS retention_pct
FROM monthly_activity
GROUP BY cohort_month, month_number
ORDER BY cohort_month, month_number;
```

---

## Question 21: Ranking with Ties Handling

**Difficulty:** ⭐⭐ Medium | **Frequency:** High

```sql
-- Compare ROW_NUMBER, RANK, DENSE_RANK
SELECT 
    name,
    salary,
    ROW_NUMBER() OVER (ORDER BY salary DESC) AS row_num,    -- 1,2,3,4,5
    RANK() OVER (ORDER BY salary DESC) AS rank_with_gaps,    -- 1,1,3,4,4
    DENSE_RANK() OVER (ORDER BY salary DESC) AS dense_rank   -- 1,1,2,3,3
FROM employees;

-- Top N per group with ties included
WITH ranked AS (
    SELECT *,
           DENSE_RANK() OVER (
               PARTITION BY department_id ORDER BY salary DESC
           ) AS salary_rank
    FROM employees
)
SELECT * FROM ranked WHERE salary_rank <= 3;
```

---

## Question 22: Find Overlapping Date Ranges

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** Medium

```sql
-- Find overlapping project assignments
CREATE TABLE assignments (
    emp_id INT,
    project_id INT,
    start_date DATE,
    end_date DATE
);

-- Find employees with overlapping assignments
SELECT DISTINCT
    a1.emp_id,
    a1.project_id AS project1,
    a2.project_id AS project2,
    GREATEST(a1.start_date, a2.start_date) AS overlap_start,
    LEAST(a1.end_date, a2.end_date) AS overlap_end
FROM assignments a1
INNER JOIN assignments a2 
    ON a1.emp_id = a2.emp_id
    AND a1.project_id < a2.project_id  -- Avoid self-join and duplicates
    AND a1.start_date <= a2.end_date   -- Overlap condition
    AND a1.end_date >= a2.start_date;

-- Merge overlapping intervals (advanced)
WITH RECURSIVE merged AS (
    SELECT emp_id, start_date, end_date,
           ROW_NUMBER() OVER (PARTITION BY emp_id ORDER BY start_date) AS rn
    FROM assignments
),
merge_process AS (
    SELECT emp_id, start_date, end_date, rn
    FROM merged WHERE rn = 1
    
    UNION ALL
    
    SELECT 
        m.emp_id,
        CASE WHEN m.start_date <= mp.end_date 
             THEN mp.start_date ELSE m.start_date END,
        GREATEST(m.end_date, mp.end_date),
        m.rn
    FROM merged m
    INNER JOIN merge_process mp 
        ON m.emp_id = mp.emp_id AND m.rn = mp.rn + 1
)
SELECT emp_id, MIN(start_date) AS start_date, MAX(end_date) AS end_date
FROM merge_process
GROUP BY emp_id;
```

---

## Question 23: Moving/Sliding Window Calculations

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** High

```sql
-- 7-day moving average of daily revenue
WITH daily_revenue AS (
    SELECT 
        DATE(order_date) AS order_day,
        SUM(total_amount) AS revenue
    FROM orders
    GROUP BY DATE(order_date)
)
SELECT 
    order_day,
    revenue,
    ROUND(AVG(revenue) OVER (
        ORDER BY order_day
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ), 2) AS moving_avg_7d,
    SUM(revenue) OVER (
        ORDER BY order_day
        ROWS BETWEEN 6 PRECEDING AND CURRENT ROW
    ) AS moving_sum_7d
FROM daily_revenue;

-- Comparing current vs previous period
SELECT 
    order_day,
    revenue,
    LAG(revenue, 7) OVER (ORDER BY order_day) AS revenue_7_days_ago,
    revenue - LAG(revenue, 7) OVER (ORDER BY order_day) AS week_over_week_change
FROM daily_revenue;
```

---

## Question 24: Self-Join for "Same" Problems

**Difficulty:** ⭐⭐ Medium | **Frequency:** High

```sql
-- Find employees in same department with same salary
SELECT DISTINCT
    e1.name AS employee1,
    e2.name AS employee2,
    e1.department_id,
    e1.salary
FROM employees e1
INNER JOIN employees e2 
    ON e1.department_id = e2.department_id
    AND e1.salary = e2.salary
    AND e1.emp_id < e2.emp_id;  -- Avoid duplicates and self-match

-- Find products bought together (market basket)
SELECT 
    oi1.product_id AS product1,
    oi2.product_id AS product2,
    COUNT(*) AS times_bought_together
FROM order_items oi1
INNER JOIN order_items oi2 
    ON oi1.order_id = oi2.order_id
    AND oi1.product_id < oi2.product_id
GROUP BY oi1.product_id, oi2.product_id
ORDER BY times_bought_together DESC
LIMIT 10;
```

---

## Question 25: Complex Conditional Aggregation

**Difficulty:** ⭐⭐⭐ Hard | **Frequency:** High

```sql
-- Multiple conditional counts in one query
SELECT 
    department_id,
    COUNT(*) AS total_employees,
    COUNT(CASE WHEN salary > 100000 THEN 1 END) AS high_earners,
    COUNT(CASE WHEN hire_date >= DATE_SUB(CURRENT_DATE, INTERVAL 1 YEAR) THEN 1 END) AS new_hires,
    SUM(CASE WHEN salary > 100000 THEN salary ELSE 0 END) AS high_earner_payroll,
    ROUND(
        COUNT(CASE WHEN salary > 100000 THEN 1 END) * 100.0 / COUNT(*),
        2
    ) AS high_earner_pct
FROM employees
GROUP BY department_id;

-- Conditional aggregation with FILTER (PostgreSQL)
/*
SELECT 
    department_id,
    COUNT(*) AS total,
    COUNT(*) FILTER (WHERE salary > 100000) AS high_earners
FROM employees
GROUP BY department_id;
*/

-- Status-based summary
SELECT 
    DATE_FORMAT(order_date, '%Y-%m') AS month,
    COUNT(*) AS total_orders,
    SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed,
    SUM(CASE WHEN status = 'CANCELLED' THEN 1 ELSE 0 END) AS cancelled,
    SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) AS pending,
    SUM(CASE WHEN status = 'COMPLETED' THEN total_amount ELSE 0 END) AS completed_revenue
FROM orders
GROUP BY DATE_FORMAT(order_date, '%Y-%m')
ORDER BY month;
```

---

# Part B: Data Modeling Techniques

## Overview of Data Modeling Approaches

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     DATA MODELING SPECTRUM                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  OLTP (Transactional)              →           OLAP (Analytical)        │
│                                                                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │ Normalized  │  │ Partially   │  │    Star     │  │  Snowflake  │    │
│  │  (3NF/BCNF) │  │ Denormalized│  │   Schema    │  │   Schema    │    │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘    │
│        ↑                                                    ↑          │
│   More JOINs                                         Fewer JOINs       │
│   Write-optimized                                  Read-optimized      │
│   Less redundancy                                 More redundancy      │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 1. Normalization (1NF → 3NF → BCNF)

### What is it?
Process of organizing data to reduce redundancy and improve data integrity.

### When to Use
- **OLTP systems** (e-commerce, banking, CRM)
- **Write-heavy** applications
- When **data integrity** is critical
- When **storage cost** matters

### Normal Forms Explained

```
┌────────────────────────────────────────────────────────────────────────┐
│ 1NF: First Normal Form                                                 │
├────────────────────────────────────────────────────────────────────────┤
│ • Eliminate repeating groups                                           │
│ • Each cell contains single value                                      │
│ • Each row is unique (has primary key)                                 │
└────────────────────────────────────────────────────────────────────────┘

❌ BEFORE (Not 1NF):
┌─────────┬────────────────────────┐
│ OrderID │ Products               │
├─────────┼────────────────────────┤
│ 1       │ Apple, Banana, Orange  │  ← Multiple values in one cell
└─────────┴────────────────────────┘

✅ AFTER (1NF):
┌─────────┬──────────┐
│ OrderID │ Product  │
├─────────┼──────────┤
│ 1       │ Apple    │
│ 1       │ Banana   │
│ 1       │ Orange   │
└─────────┴──────────┘
```

```
┌────────────────────────────────────────────────────────────────────────┐
│ 2NF: Second Normal Form                                                │
├────────────────────────────────────────────────────────────────────────┤
│ • Must be in 1NF                                                       │
│ • No partial dependencies (non-key depends on part of composite key)   │
└────────────────────────────────────────────────────────────────────────┘

❌ BEFORE (Not 2NF):
┌─────────┬───────────┬──────────────┬───────┐
│ OrderID │ ProductID │ ProductName  │ Qty   │
├─────────┼───────────┼──────────────┼───────┤
│ 1       │ 101       │ Laptop       │ 2     │  ← ProductName depends only
│ 1       │ 102       │ Mouse        │ 3     │    on ProductID, not full PK
│ 2       │ 101       │ Laptop       │ 1     │
└─────────┴───────────┴──────────────┴───────┘
PK: (OrderID, ProductID)

✅ AFTER (2NF):
ORDER_ITEMS:                    PRODUCTS:
┌─────────┬───────────┬───────┐ ┌───────────┬──────────────┐
│ OrderID │ ProductID │ Qty   │ │ ProductID │ ProductName  │
├─────────┼───────────┼───────┤ ├───────────┼──────────────┤
│ 1       │ 101       │ 2     │ │ 101       │ Laptop       │
│ 1       │ 102       │ 3     │ │ 102       │ Mouse        │
│ 2       │ 101       │ 1     │ └───────────┴──────────────┘
└─────────┴───────────┴───────┘
```

```
┌────────────────────────────────────────────────────────────────────────┐
│ 3NF: Third Normal Form                                                 │
├────────────────────────────────────────────────────────────────────────┤
│ • Must be in 2NF                                                       │
│ • No transitive dependencies (A → B → C)                               │
└────────────────────────────────────────────────────────────────────────┘

❌ BEFORE (Not 3NF):
┌───────┬─────────┬────────────┬──────────────┐
│ EmpID │ DeptID  │ DeptName   │ DeptLocation │
├───────┼─────────┼────────────┼──────────────┤
│ 1     │ D1      │ Engineering│ Building A   │  ← DeptName, DeptLocation
│ 2     │ D1      │ Engineering│ Building A   │    depend on DeptID
│ 3     │ D2      │ Sales      │ Building B   │    (transitive dependency)
└───────┴─────────┴────────────┴──────────────┘
EmpID → DeptID → DeptName, DeptLocation

✅ AFTER (3NF):
EMPLOYEES:                      DEPARTMENTS:
┌───────┬─────────┐            ┌─────────┬────────────┬──────────────┐
│ EmpID │ DeptID  │            │ DeptID  │ DeptName   │ DeptLocation │
├───────┼─────────┤            ├─────────┼────────────┼──────────────┤
│ 1     │ D1      │            │ D1      │ Engineering│ Building A   │
│ 2     │ D1      │            │ D2      │ Sales      │ Building B   │
│ 3     │ D2      │            └─────────┴────────────┴──────────────┘
└───────┴─────────┘
```

### SQL Example: E-Commerce in 3NF

```sql
-- Fully normalized e-commerce schema
CREATE TABLE customers (
    customer_id INT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(100)
);

CREATE TABLE addresses (
    address_id INT PRIMARY KEY,
    customer_id INT REFERENCES customers(customer_id),
    street VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(50),
    zip VARCHAR(20)
);

CREATE TABLE categories (
    category_id INT PRIMARY KEY,
    name VARCHAR(100),
    parent_id INT REFERENCES categories(category_id)
);

CREATE TABLE products (
    product_id INT PRIMARY KEY,
    name VARCHAR(255),
    category_id INT REFERENCES categories(category_id),
    base_price DECIMAL(10,2)
);

CREATE TABLE product_prices (
    product_id INT REFERENCES products(product_id),
    price DECIMAL(10,2),
    effective_from DATE,
    effective_to DATE,
    PRIMARY KEY (product_id, effective_from)
);

CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer_id INT REFERENCES customers(customer_id),
    shipping_address_id INT REFERENCES addresses(address_id),
    order_date TIMESTAMP
);

CREATE TABLE order_items (
    order_id INT REFERENCES orders(order_id),
    product_id INT REFERENCES products(product_id),
    quantity INT,
    unit_price DECIMAL(10,2),  -- Snapshot price at time of order
    PRIMARY KEY (order_id, product_id)
);
```

---

## 2. Denormalization

### What is it?
Intentionally adding redundancy to improve read performance.

### When to Use
- **Read-heavy** applications
- Complex queries with many JOINs
- Caching frequently accessed data
- Reporting dashboards

### Techniques

```sql
-- TECHNIQUE 1: Redundant Columns
-- Store frequently needed data directly in the table

-- Instead of JOINing to get customer name every time:
CREATE TABLE orders_denormalized (
    order_id INT PRIMARY KEY,
    customer_id INT,
    customer_name VARCHAR(100),      -- Redundant but fast
    customer_email VARCHAR(255),     -- Redundant but fast
    order_date TIMESTAMP,
    total_amount DECIMAL(10,2)
);

-- TECHNIQUE 2: Summary Tables
-- Pre-calculate aggregates

CREATE TABLE daily_sales_summary (
    summary_date DATE PRIMARY KEY,
    total_orders INT,
    total_revenue DECIMAL(12,2),
    unique_customers INT,
    avg_order_value DECIMAL(10,2)
);

-- Populate with scheduled job:
INSERT INTO daily_sales_summary
SELECT 
    DATE(order_date),
    COUNT(*),
    SUM(total_amount),
    COUNT(DISTINCT customer_id),
    AVG(total_amount)
FROM orders
WHERE DATE(order_date) = CURRENT_DATE - INTERVAL 1 DAY
GROUP BY DATE(order_date);

-- TECHNIQUE 3: Materialized Views (PostgreSQL)
CREATE MATERIALIZED VIEW mv_product_sales AS
SELECT 
    p.product_id,
    p.name,
    c.name AS category,
    COUNT(oi.order_id) AS total_orders,
    SUM(oi.quantity) AS total_quantity,
    SUM(oi.quantity * oi.unit_price) AS total_revenue
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
LEFT JOIN order_items oi ON p.product_id = oi.product_id
GROUP BY p.product_id, p.name, c.name;

-- Refresh periodically
REFRESH MATERIALIZED VIEW mv_product_sales;

-- TECHNIQUE 4: Counter Caches
-- Keep counts updated in parent table

CREATE TABLE posts (
    post_id INT PRIMARY KEY,
    content TEXT,
    like_count INT DEFAULT 0,      -- Counter cache
    comment_count INT DEFAULT 0    -- Counter cache
);

-- Use triggers to maintain:
CREATE TRIGGER update_like_count
AFTER INSERT ON likes
FOR EACH ROW
UPDATE posts SET like_count = like_count + 1 WHERE post_id = NEW.post_id;
```

---

## 3. Star Schema (Data Warehousing)

### What is it?
Central **fact table** surrounded by **dimension tables**. Optimized for analytical queries.

### When to Use
- **Data warehouses** and **analytics**
- **BI/Reporting** systems (Tableau, PowerBI)
- **OLAP** workloads
- When query simplicity matters

### Structure

```
                    ┌─────────────────┐
                    │  dim_customer   │
                    │─────────────────│
                    │ customer_key PK │
                    │ customer_id     │
                    │ name            │
                    │ email           │
                    │ city            │
                    │ state           │
                    │ country         │
                    └────────┬────────┘
                             │
┌─────────────────┐          │         ┌─────────────────┐
│   dim_product   │          │         │    dim_date     │
│─────────────────│          │         │─────────────────│
│ product_key PK  │          │         │ date_key PK     │
│ product_id      │          │         │ full_date       │
│ name            │          │         │ day             │
│ category        │          │         │ month           │
│ subcategory     │          │         │ quarter         │
│ brand           │          │         │ year            │
│ unit_price      │          │         │ is_weekend      │
└────────┬────────┘          │         │ is_holiday      │
         │                   │         └────────┬────────┘
         │    ┌──────────────┴──────────────┐   │
         │    │        fact_sales           │   │
         │    │─────────────────────────────│   │
         └────┤ customer_key FK             ├───┘
              │ product_key FK              │
              │ date_key FK                 │
              │ store_key FK                ├───┐
              │─────────────────────────────│   │
              │ quantity                    │   │
              │ unit_price                  │   │
              │ discount                    │   │
              │ total_amount                │   │
              └─────────────────────────────┘   │
                                               │
                             ┌─────────────────┘
                             │
                    ┌────────┴────────┐
                    │    dim_store    │
                    │─────────────────│
                    │ store_key PK    │
                    │ store_id        │
                    │ store_name      │
                    │ city            │
                    │ region          │
                    └─────────────────┘
```

### SQL Implementation

```sql
-- DIMENSION TABLES (Descriptive attributes)

CREATE TABLE dim_date (
    date_key INT PRIMARY KEY,
    full_date DATE NOT NULL,
    day_of_week VARCHAR(10),
    day_of_month INT,
    month INT,
    month_name VARCHAR(20),
    quarter INT,
    year INT,
    is_weekend BOOLEAN,
    is_holiday BOOLEAN
);

CREATE TABLE dim_customer (
    customer_key INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT,  -- Natural key from source
    name VARCHAR(100),
    email VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(50),
    country VARCHAR(100),
    customer_segment VARCHAR(50),
    -- SCD Type 2 fields
    effective_from DATE,
    effective_to DATE,
    is_current BOOLEAN
);

CREATE TABLE dim_product (
    product_key INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,  -- Natural key
    name VARCHAR(255),
    category VARCHAR(100),
    subcategory VARCHAR(100),
    brand VARCHAR(100),
    unit_cost DECIMAL(10,2)
);

CREATE TABLE dim_store (
    store_key INT PRIMARY KEY AUTO_INCREMENT,
    store_id INT,
    store_name VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(50),
    region VARCHAR(50),
    store_type VARCHAR(50)
);

-- FACT TABLE (Measures/metrics)

CREATE TABLE fact_sales (
    sale_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date_key INT NOT NULL,
    customer_key INT NOT NULL,
    product_key INT NOT NULL,
    store_key INT NOT NULL,
    -- Measures
    quantity INT,
    unit_price DECIMAL(10,2),
    discount_amount DECIMAL(10,2),
    total_amount DECIMAL(12,2),
    -- Foreign keys
    FOREIGN KEY (date_key) REFERENCES dim_date(date_key),
    FOREIGN KEY (customer_key) REFERENCES dim_customer(customer_key),
    FOREIGN KEY (product_key) REFERENCES dim_product(product_key),
    FOREIGN KEY (store_key) REFERENCES dim_store(store_key),
    -- Indexes for common queries
    INDEX idx_date (date_key),
    INDEX idx_customer (customer_key),
    INDEX idx_product (product_key)
);

-- SAMPLE ANALYTICAL QUERIES

-- Monthly revenue by category
SELECT 
    d.year,
    d.month_name,
    p.category,
    SUM(f.total_amount) AS revenue
FROM fact_sales f
INNER JOIN dim_date d ON f.date_key = d.date_key
INNER JOIN dim_product p ON f.product_key = p.product_key
GROUP BY d.year, d.month, d.month_name, p.category
ORDER BY d.year, d.month;

-- Top customers by region
SELECT 
    s.region,
    c.name AS customer_name,
    SUM(f.total_amount) AS total_spent,
    RANK() OVER (PARTITION BY s.region ORDER BY SUM(f.total_amount) DESC) AS rnk
FROM fact_sales f
INNER JOIN dim_customer c ON f.customer_key = c.customer_key
INNER JOIN dim_store s ON f.store_key = s.store_key
GROUP BY s.region, c.customer_key, c.name
HAVING rnk <= 5;
```

---

## 4. Snowflake Schema

### What is it?
Extended star schema where dimensions are normalized into sub-dimensions.

### When to Use
- When dimensions have **hierarchical data**
- Need to **reduce dimension table size**
- **Storage optimization** is important
- More complex queries are acceptable

### Structure

```
                          ┌─────────────┐
                          │ dim_country │
                          │─────────────│
                          │ country_key │
                          │ country_name│
                          └──────┬──────┘
                                 │
                          ┌──────┴──────┐
                          │  dim_state  │
                          │─────────────│
                          │ state_key   │
                          │ state_name  │
                          │ country_key │
                          └──────┬──────┘
                                 │
┌─────────────┐           ┌──────┴──────┐
│dim_category │           │  dim_city   │
│─────────────│           │─────────────│
│ category_key│           │ city_key    │
│ cat_name    │           │ city_name   │
└──────┬──────┘           │ state_key   │
       │                  └──────┬──────┘
       │                         │
┌──────┴──────┐           ┌──────┴──────┐
│dim_subcateg │           │dim_customer │
│─────────────│           │─────────────│
│ subcat_key  │           │ cust_key    │
│ subcat_name │           │ name        │
│ category_key│           │ city_key    │
└──────┬──────┘           └──────┬──────┘
       │                         │
       │    ┌────────────────────┴─────┐
       │    │       fact_sales         │
       │    │──────────────────────────│
       └────┤ product_key              │
            │ customer_key             ├────┘
            │ date_key                 │
            │──────────────────────────│
            │ quantity                 │
            │ amount                   │
            └──────────────────────────┘
```

### SQL Implementation

```sql
-- Normalized geography dimension
CREATE TABLE dim_country (
    country_key INT PRIMARY KEY,
    country_name VARCHAR(100),
    country_code VARCHAR(3)
);

CREATE TABLE dim_state (
    state_key INT PRIMARY KEY,
    state_name VARCHAR(100),
    state_code VARCHAR(10),
    country_key INT REFERENCES dim_country(country_key)
);

CREATE TABLE dim_city (
    city_key INT PRIMARY KEY,
    city_name VARCHAR(100),
    state_key INT REFERENCES dim_state(state_key)
);

-- Normalized product dimension
CREATE TABLE dim_category (
    category_key INT PRIMARY KEY,
    category_name VARCHAR(100)
);

CREATE TABLE dim_subcategory (
    subcategory_key INT PRIMARY KEY,
    subcategory_name VARCHAR(100),
    category_key INT REFERENCES dim_category(category_key)
);

CREATE TABLE dim_product_snowflake (
    product_key INT PRIMARY KEY,
    product_name VARCHAR(255),
    subcategory_key INT REFERENCES dim_subcategory(subcategory_key),
    brand VARCHAR(100)
);

-- Query requires more JOINs
SELECT 
    cat.category_name,
    sub.subcategory_name,
    SUM(f.amount) AS revenue
FROM fact_sales f
INNER JOIN dim_product_snowflake p ON f.product_key = p.product_key
INNER JOIN dim_subcategory sub ON p.subcategory_key = sub.subcategory_key
INNER JOIN dim_category cat ON sub.category_key = cat.category_key
GROUP BY cat.category_name, sub.subcategory_name;
```

---

## 5. Slowly Changing Dimensions (SCD)

### What is it?
Techniques to handle changes in dimension data over time.

### Types

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SCD TYPES COMPARISON                                 │
├──────────┬─────────────────────────────────────────────────────────────┤
│ Type 0   │ No changes allowed (static/append-only)                     │
├──────────┼─────────────────────────────────────────────────────────────┤
│ Type 1   │ Overwrite old data (no history)                             │
├──────────┼─────────────────────────────────────────────────────────────┤
│ Type 2   │ Add new row with version tracking (full history)            │
├──────────┼─────────────────────────────────────────────────────────────┤
│ Type 3   │ Add column for previous value (limited history)             │
├──────────┼─────────────────────────────────────────────────────────────┤
│ Type 4   │ Separate history table                                      │
├──────────┼─────────────────────────────────────────────────────────────┤
│ Type 6   │ Hybrid of 1, 2, and 3                                       │
└──────────┴─────────────────────────────────────────────────────────────┘
```

### SQL Examples

```sql
-- SCD TYPE 1: Overwrite (No History)
-- Customer changes city from "NYC" to "LA"

UPDATE dim_customer
SET city = 'Los Angeles'
WHERE customer_id = 123;

-- Simple but loses history

-- ══════════════════════════════════════════════════════════

-- SCD TYPE 2: Add New Row (Full History)

CREATE TABLE dim_customer_scd2 (
    customer_key INT PRIMARY KEY AUTO_INCREMENT,  -- Surrogate key
    customer_id INT,                              -- Natural key
    name VARCHAR(100),
    city VARCHAR(100),
    effective_from DATE NOT NULL,
    effective_to DATE,                            -- NULL = current
    is_current BOOLEAN DEFAULT TRUE,
    version INT DEFAULT 1
);

-- Initial record
INSERT INTO dim_customer_scd2 
    (customer_id, name, city, effective_from, is_current)
VALUES 
    (123, 'John Doe', 'New York', '2020-01-01', TRUE);

-- Customer moves to LA - expire old record, insert new
UPDATE dim_customer_scd2
SET effective_to = CURRENT_DATE - INTERVAL 1 DAY,
    is_current = FALSE
WHERE customer_id = 123 AND is_current = TRUE;

INSERT INTO dim_customer_scd2 
    (customer_id, name, city, effective_from, is_current, version)
SELECT 
    customer_id, name, 'Los Angeles', CURRENT_DATE, TRUE, version + 1
FROM dim_customer_scd2
WHERE customer_id = 123
ORDER BY version DESC LIMIT 1;

-- Query for point-in-time analysis
SELECT f.*, c.city
FROM fact_sales f
INNER JOIN dim_customer_scd2 c 
    ON f.customer_key = c.customer_key
    AND f.sale_date BETWEEN c.effective_from AND COALESCE(c.effective_to, '9999-12-31');

-- ══════════════════════════════════════════════════════════

-- SCD TYPE 3: Add Column (Limited History)

CREATE TABLE dim_customer_scd3 (
    customer_key INT PRIMARY KEY,
    customer_id INT,
    name VARCHAR(100),
    current_city VARCHAR(100),
    previous_city VARCHAR(100),
    city_changed_date DATE
);

-- Update when customer moves
UPDATE dim_customer_scd3
SET previous_city = current_city,
    current_city = 'Los Angeles',
    city_changed_date = CURRENT_DATE
WHERE customer_id = 123;

-- ══════════════════════════════════════════════════════════

-- SCD TYPE 4: Separate History Table

CREATE TABLE dim_customer_current (
    customer_key INT PRIMARY KEY,
    customer_id INT,
    name VARCHAR(100),
    city VARCHAR(100),
    last_updated TIMESTAMP
);

CREATE TABLE dim_customer_history (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_key INT,
    customer_id INT,
    name VARCHAR(100),
    city VARCHAR(100),
    valid_from TIMESTAMP,
    valid_to TIMESTAMP
);

-- Move to history before update
INSERT INTO dim_customer_history
SELECT NULL, customer_key, customer_id, name, city, 
       last_updated, CURRENT_TIMESTAMP
FROM dim_customer_current
WHERE customer_id = 123;

-- Update current
UPDATE dim_customer_current
SET city = 'Los Angeles', last_updated = CURRENT_TIMESTAMP
WHERE customer_id = 123;
```

---

## 6. Document/JSON Modeling (Semi-Structured)

### When to Use
- **Flexible schemas** (evolving data)
- **Nested data** structures
- **Sparse attributes** (many NULL columns)
- **API-first** applications

```sql
-- MySQL JSON columns
CREATE TABLE products_flexible (
    product_id INT PRIMARY KEY,
    name VARCHAR(255),
    base_price DECIMAL(10,2),
    attributes JSON  -- Flexible attributes
);

-- Insert varied products
INSERT INTO products_flexible VALUES
(1, 'Laptop', 999.99, '{"brand": "Dell", "ram": "16GB", "storage": "512GB SSD", "screen": "15.6 inch"}'),
(2, 'T-Shirt', 29.99, '{"size": "M", "color": "Blue", "material": "Cotton"}'),
(3, 'Book', 19.99, '{"author": "John Smith", "pages": 300, "genre": "Fiction"}');

-- Query JSON data
SELECT 
    name,
    base_price,
    JSON_EXTRACT(attributes, '$.brand') AS brand,
    JSON_EXTRACT(attributes, '$.ram') AS ram
FROM products_flexible
WHERE JSON_EXTRACT(attributes, '$.brand') = '"Dell"';

-- Using -> operator (MySQL shorthand)
SELECT 
    name,
    attributes->'$.color' AS color,
    attributes->>'$.size' AS size  -- ->> removes quotes
FROM products_flexible
WHERE attributes->>'$.color' = 'Blue';

-- Index on JSON path
CREATE INDEX idx_brand ON products_flexible((CAST(attributes->>'$.brand' AS CHAR(50))));
```

---

## 7. Graph-like Modeling (Adjacency List & Nested Sets)

### When to Use
- **Hierarchical data** (org charts, categories)
- **Network/graph** structures (social, dependencies)
- **Bill of materials** (parts/assemblies)

```sql
-- ADJACENCY LIST (Simple, recursive queries needed)
CREATE TABLE categories_adj (
    category_id INT PRIMARY KEY,
    name VARCHAR(100),
    parent_id INT REFERENCES categories_adj(category_id)
);

-- Query with recursive CTE
WITH RECURSIVE category_tree AS (
    SELECT category_id, name, parent_id, 0 AS level
    FROM categories_adj WHERE parent_id IS NULL
    UNION ALL
    SELECT c.category_id, c.name, c.parent_id, ct.level + 1
    FROM categories_adj c
    INNER JOIN category_tree ct ON c.parent_id = ct.category_id
)
SELECT * FROM category_tree;

-- ══════════════════════════════════════════════════════════

-- NESTED SETS (Fast reads, slow writes)
CREATE TABLE categories_nested (
    category_id INT PRIMARY KEY,
    name VARCHAR(100),
    lft INT NOT NULL,  -- Left boundary
    rgt INT NOT NULL   -- Right boundary
);

/*
    Electronics (1, 14)
    ├── Computers (2, 9)
    │   ├── Laptops (3, 4)
    │   └── Desktops (5, 8)
    │       └── Gaming (6, 7)
    └── Phones (10, 13)
        └── Smartphones (11, 12)
*/

INSERT INTO categories_nested VALUES
(1, 'Electronics', 1, 14),
(2, 'Computers', 2, 9),
(3, 'Laptops', 3, 4),
(4, 'Desktops', 5, 8),
(5, 'Gaming PCs', 6, 7),
(6, 'Phones', 10, 13),
(7, 'Smartphones', 11, 12);

-- Get all descendants of "Computers" (no recursion needed!)
SELECT * FROM categories_nested
WHERE lft > 2 AND rgt < 9;

-- Get full path to "Gaming PCs"
SELECT * FROM categories_nested
WHERE lft < 6 AND rgt > 7
ORDER BY lft;

-- ══════════════════════════════════════════════════════════

-- PATH ENUMERATION (Materialized path)
CREATE TABLE categories_path (
    category_id INT PRIMARY KEY,
    name VARCHAR(100),
    path VARCHAR(500)  -- e.g., '/1/2/5/'
);

INSERT INTO categories_path VALUES
(1, 'Electronics', '/1/'),
(2, 'Computers', '/1/2/'),
(3, 'Laptops', '/1/2/3/'),
(4, 'Desktops', '/1/2/4/'),
(5, 'Gaming PCs', '/1/2/4/5/');

-- Find all ancestors
SELECT * FROM categories_path
WHERE '/1/2/4/5/' LIKE CONCAT(path, '%')
ORDER BY path;

-- Find all descendants
SELECT * FROM categories_path
WHERE path LIKE '/1/2/%';
```

---

## 8. Comparison: When to Use What?

```
┌─────────────────┬─────────────────┬─────────────────┬─────────────────┐
│    Technique    │   Best For      │   Pros          │   Cons          │
├─────────────────┼─────────────────┼─────────────────┼─────────────────┤
│ Normalized      │ OLTP systems    │ Data integrity  │ Complex JOINs   │
│ (3NF)           │ Write-heavy     │ Less storage    │ Slower reads    │
│                 │ Banking, ERP    │ No redundancy   │                 │
├─────────────────┼─────────────────┼─────────────────┼─────────────────┤
│ Denormalized    │ Read-heavy      │ Fast queries    │ Data redundancy │
│                 │ Caching layers  │ Simple SELECTs  │ Update anomalies│
│                 │ Dashboards      │                 │                 │
├─────────────────┼─────────────────┼─────────────────┼─────────────────┤
│ Star Schema     │ Data warehouse  │ Easy to query   │ Data redundancy │
│                 │ BI reporting    │ Good for OLAP   │ ETL complexity  │
│                 │ Analytics       │ Fast aggregates │                 │
├─────────────────┼─────────────────┼─────────────────┼─────────────────┤
│ Snowflake       │ Large dimensions│ Less storage    │ More JOINs      │
│                 │ Deep hierarchies│ Less redundancy │ Slower queries  │
├─────────────────┼─────────────────┼─────────────────┼─────────────────┤
│ SCD Type 2      │ Historical      │ Full audit trail│ Table growth    │
│                 │ analysis        │ Time travel     │ Complex queries │
├─────────────────┼─────────────────┼─────────────────┼─────────────────┤
│ JSON/Document   │ Flexible schema │ Schema evolution│ Query complexity│
│                 │ Sparse data     │ Developer ease  │ No referential  │
│                 │ API backends    │                 │ integrity       │
├─────────────────┼─────────────────┼─────────────────┼─────────────────┤
│ Adjacency List  │ Simple trees    │ Easy to maintain│ Recursive query │
│                 │                 │ Natural model   │ needed          │
├─────────────────┼─────────────────┼─────────────────┼─────────────────┤
│ Nested Sets     │ Read-heavy      │ Fast subtree    │ Slow updates    │
│                 │ hierarchies     │ queries         │ Complex inserts │
└─────────────────┴─────────────────┴─────────────────┴─────────────────┘
```

---

## Quick Decision Guide

```
START
  │
  ▼
┌─────────────────────────────────────┐
│ What's the primary workload?        │
└─────────────────────────────────────┘
          │
    ┌─────┴─────┐
    ▼           ▼
 OLTP        OLAP/Analytics
    │           │
    ▼           ▼
┌────────┐  ┌────────────────────────┐
│3NF     │  │Is dimension hierarchy  │
│Normal- │  │deep and complex?       │
│ized    │  └────────────────────────┘
└────────┘           │
              ┌──────┴──────┐
              ▼             ▼
           Yes           No
              │             │
              ▼             ▼
         ┌────────┐   ┌────────┐
         │Snow-   │   │Star    │
         │flake   │   │Schema  │
         └────────┘   └────────┘

┌─────────────────────────────────────┐
│ Need historical tracking?           │
└─────────────────────────────────────┘
          │
    ┌─────┴─────┐
    ▼           ▼
  Full       Limited
 History     History
    │           │
    ▼           ▼
┌────────┐  ┌────────┐
│SCD     │  │SCD     │
│Type 2  │  │Type 1/3│
└────────┘  └────────┘

┌─────────────────────────────────────┐
│ Working with hierarchies?           │
└─────────────────────────────────────┘
          │
    ┌─────┴─────┬──────────┐
    ▼           ▼          ▼
 Simple     Read-Heavy   Moderate
    │           │          │
    ▼           ▼          ▼
┌────────┐ ┌────────┐ ┌────────┐
│Adjacency│ │Nested  │ │Path    │
│List     │ │Sets    │ │Enum    │
└────────┘ └────────┘ └────────┘
```

---

## Interview Tips for Data Modeling Questions

1. **Always clarify requirements first:**
   - Read vs write ratio?
   - Scale expectations?
   - Historical tracking needed?
   - Reporting requirements?

2. **Start normalized, denormalize with justification:**
   - "I'll start with 3NF for data integrity, but we may denormalize X for performance because..."

3. **Know trade-offs:**
   - Every denormalization = potential update anomaly
   - Every normalization = potential complex JOIN

4. **Consider indexing alongside schema:**
   - "I'd add an index on X because queries will filter by..."

5. **Mention real-world patterns:**
   - "This is similar to how Amazon/Uber/Netflix models..."

---

*Last Updated: January 2026*

