# Employee & Team Directory Management System - Low Level Design

## Overview

A comprehensive system for managing employees, teams, organizational hierarchies, and CTC (Cost to Company) calculations.

## Features

1. **Employee Management**
   - Add, update, delete employees
   - Assign and remove managers
   - Track employee status (Active, On Leave, Terminated, etc.)
   - Store compensation details (base salary, bonus, allowances)

2. **Team Management**
   - Create and manage teams
   - Add/remove team members with specific roles
   - Support multiple team memberships per employee

3. **Hierarchical Views**
   - View organizational hierarchy from any employee
   - Display management chain (upward)
   - Display subordinates (downward)
   - Full organization tree view
   - Span of control reports

4. **CTC Calculations**
   - Individual employee CTC
   - Team total and average CTC
   - Manager CTC (including all subordinates)
   - Department-wise CTC breakdown
   - Organization-wide CTC reports

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              EmployeeDirectory                               │
│                           (Singleton Facade)                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│ - employeeService: EmployeeService                                          │
│ - teamService: TeamService                                                  │
│ - hierarchyService: HierarchyService                                        │
│ - ctcCalculatorService: CTCCalculatorService                                │
├─────────────────────────────────────────────────────────────────────────────┤
│ + getInstance(): EmployeeDirectory                                          │
│ + addEmployee(...): Employee                                                │
│ + assignManager(employeeId, managerId): void                                │
│ + addToTeam(teamId, employeeId, role): void                                 │
│ + getHierarchicalView(employeeId): String                                   │
│ + getEmployeeCTC(employeeId): double                                        │
│ + getTeamCTC(teamId): double                                                │
│ + ...                                                                       │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
          ┌───────────────────────────┼───────────────────────────┐
          │                           │                           │
          ▼                           ▼                           ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────────────┐
│ EmployeeService │     │   TeamService   │     │    HierarchyService     │
├─────────────────┤     ├─────────────────┤     ├─────────────────────────┤
│ + addEmployee() │     │ + createTeam()  │     │ + getHierarchicalView() │
│ + assignManager()│    │ + addMember()   │     │ + getOrganizationTree() │
│ + getDirectReports()│ │ + removeMember()│     │ + getSubTree()          │
└─────────────────┘     └─────────────────┘     └─────────────────────────┘
          │                     │                         │
          ▼                     ▼                         ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                           CTCCalculatorService                             │
├───────────────────────────────────────────────────────────────────────────┤
│ + getEmployeeCTC(employeeId): double                                       │
│ + getTeamCTC(teamId): double                                               │
│ + getManagerTotalCTC(managerId): double                                    │
│ + getDepartmentCTC(department): double                                     │
│ + generateCTCReport(): String                                              │
└───────────────────────────────────────────────────────────────────────────┘

┌──────────────────┐          ┌──────────────────┐
│     Employee     │◄─────────│     Employee     │
├──────────────────┤  manager │  (as Manager)    │
│ - employeeId     │          └──────────────────┘
│ - name           │                    │
│ - email          │                    │ directReports
│ - designation    │                    ▼
│ - department     │          ┌──────────────────┐
│ - baseSalary     │          │  List<Employee>  │
│ - bonus          │          └──────────────────┘
│ - allowances     │
│ - status         │
│ - manager        │─────────────────┐
│ - directReports  │                 │
├──────────────────┤                 │
│ + calculateCTC() │                 │
│ + getAllSubordinates()             │
│ + getManagementChain()             │
└──────────────────┘                 │
                                     │
┌──────────────────┐                 │
│       Team       │                 │
├──────────────────┤                 │
│ - teamId         │                 │
│ - name           │       ┌─────────┴──────────┐
│ - department     │       │    TeamMember      │
│ - members        │───────├────────────────────┤
├──────────────────┤       │ - employee         │◄──┘
│ + addMember()    │       │ - role: TeamRole   │
│ + removeMember() │       │ - joinedDate       │
│ + calculateTotalCTC()    └────────────────────┘
│ + calculateAverageCTC()
└──────────────────┘
```

## Package Structure

```
employeedirectory/
├── enums/
│   ├── Department.java        # Department types
│   ├── EmployeeStatus.java    # Employment status
│   └── TeamRole.java          # Role within a team
├── models/
│   ├── Employee.java          # Core employee entity
│   ├── Team.java              # Team entity
│   └── TeamMember.java        # Team membership with role
├── exceptions/
│   ├── EmployeeDirectoryException.java  # Base exception
│   ├── EmployeeNotFoundException.java
│   ├── TeamNotFoundException.java
│   ├── DuplicateEmployeeException.java
│   ├── DuplicateTeamException.java
│   └── InvalidHierarchyException.java
├── repositories/
│   ├── EmployeeRepository.java          # Interface
│   ├── TeamRepository.java              # Interface
│   └── impl/
│       ├── InMemoryEmployeeRepository.java
│       └── InMemoryTeamRepository.java
├── services/
│   ├── EmployeeService.java      # Employee operations
│   ├── TeamService.java          # Team operations
│   ├── HierarchyService.java     # Hierarchy visualization
│   └── CTCCalculatorService.java # CTC calculations
├── EmployeeDirectory.java        # Main facade (Singleton)
├── Main.java                     # Demo application
└── EMPLOYEE_DIRECTORY_LLD.md     # This file
```

## Design Patterns Used

1. **Singleton Pattern**: `EmployeeDirectory` - Single instance for directory management
2. **Facade Pattern**: `EmployeeDirectory` - Unified interface to subsystems
3. **Repository Pattern**: Abstracts data access layer
4. **Composite Pattern**: Employee hierarchy (manager/reports tree structure)
5. **Strategy Pattern Ready**: Can extend for different CTC calculation strategies

## Key Data Structures

### Employee Hierarchy (Tree Structure)
```
                    CEO (E001)
                   /    \
              CTO        CFO
             / | \
     VP Eng  PM  Design Lead
       /  \
  Eng Mgr  Eng Mgr
    /  \      \
 Sr Eng Sr Eng  Sr Eng
   /  \          \
 Eng   Eng       Eng
```

### Team Membership
```
┌────────────────────────┐
│   Backend Platform     │
│  Team Lead: David      │
│  Members: Robert,      │
│    Maria, William      │
└────────────────────────┘
```

## API Examples

### 1. Add Employee
```java
EmployeeDirectory directory = EmployeeDirectory.getInstance();
Employee emp = directory.addEmployee(
    "E001",                    // Employee ID
    "John Smith",              // Name
    "john@company.com",        // Email
    "Software Engineer",       // Designation
    Department.ENGINEERING,    // Department
    100000                     // Base Salary
);
```

### 2. Assign Manager
```java
// E010 now reports to E005
directory.assignManager("E010", "E005");
```

### 3. Add to Team
```java
directory.createTeam("T001", "Backend Team", Department.ENGINEERING);
directory.addToTeam("T001", "E010", TeamRole.MEMBER);
```

### 4. Get Hierarchical View
```java
// Shows management chain (up) and subordinates (down)
String hierarchy = directory.getHierarchicalView("E010");
System.out.println(hierarchy);
```

### 5. Calculate CTC
```java
// Individual employee CTC
double empCTC = directory.getEmployeeCTC("E010");

// Team CTC
double teamCTC = directory.getTeamCTC("T001");

// Manager + all subordinates CTC
double mgrCTC = directory.getManagerTotalCTC("E005");

// Full report
String report = directory.generateCTCReport();
```

## CTC Calculation

**CTC (Cost to Company)** = Base Salary + Bonus + Allowances

```java
public double calculateCTC() {
    return baseSalary + bonus + allowances;
}
```

### Team CTC
Sum of CTC of all team members.

### Manager Total CTC
Sum of manager's CTC + all direct and indirect subordinates' CTC.

## Hierarchy Traversal Algorithms

### Upward Traversal (Management Chain)
```java
public List<Employee> getManagementChain() {
    List<Employee> chain = new ArrayList<>();
    Employee current = this.manager;
    while (current != null) {
        chain.add(current);
        current = current.getManager();
    }
    return chain;
}
```

### Downward Traversal (All Subordinates)
```java
public List<Employee> getAllSubordinates() {
    List<Employee> subordinates = new ArrayList<>();
    collectSubordinates(this, subordinates);
    return subordinates;
}

private void collectSubordinates(Employee emp, List<Employee> list) {
    for (Employee report : emp.getDirectReports()) {
        list.add(report);
        collectSubordinates(report, list);
    }
}
```

## Cycle Detection (Manager Assignment)
Prevents circular hierarchies:
```java
private boolean wouldCreateCycle(Employee employee, Employee potentialManager) {
    Employee current = potentialManager;
    while (current != null) {
        if (current.equals(employee)) {
            return true;
        }
        current = current.getManager();
    }
    return false;
}
```

## Thread Safety

- Repository implementations use `ConcurrentHashMap` for thread-safe operations
- Singleton uses double-checked locking with `volatile`

## Running the Demo

```bash
cd /path/to/low-level-designs
javac employeedirectory/**/*.java
java employeedirectory.Main
```

## Sample Output

```
╔════════════════════════════════════════════════════════════════╗
║    Employee & Team Directory Management System Demo            ║
╚════════════════════════════════════════════════════════════════╝

=== Organization Hierarchy ===

John Smith (CEO, Operations)
├── Sarah Johnson (CTO, Engineering)
│   ├── Emily Davis (VP Engineering, Engineering)
│   │   ├── David Wilson (Engineering Manager, Engineering)
│   │   │   ├── Robert Taylor (Senior Software Engineer, Engineering)
│   │   │   │   ├── Maria Rodriguez (Software Engineer, Engineering)
│   │   │   │   └── William Lee (Software Engineer, Engineering)
│   │   │   └── Jennifer Martinez (Senior Software Engineer, Engineering)
│   │   └── Lisa Anderson (Engineering Manager, Engineering)
│   │       └── James Garcia (Senior Software Engineer, Engineering)
│   │           └── Patricia White (Software Engineer, Engineering)
│   ├── Christopher Harris (Product Manager, Product)
│   │   └── Amanda Clark (Product Analyst, Product)
│   └── Daniel Lewis (Design Lead, Design)
└── Michael Brown (CFO, Finance)
```

## Extensibility

1. **Add new departments**: Simply add to `Department` enum
2. **Custom CTC calculations**: Implement different calculation strategies
3. **Persistent storage**: Implement repository interfaces with database backing
4. **Notifications**: Add observers for employee/team changes
5. **Audit logging**: Add decorators to services
