package employeedirectory;

import employeedirectory.enums.Department;
import employeedirectory.enums.EmployeeStatus;
import employeedirectory.enums.TeamRole;
import employeedirectory.models.Employee;
import employeedirectory.models.Team;
import employeedirectory.models.TeamMember;
import employeedirectory.services.CTCCalculatorService;

import java.util.List;

/**
 * Demo application for Employee & Team Directory Management System.
 */
public class Main {
    
    public static void main(String[] args) {
        // Reset instance for clean demo
        EmployeeDirectory.resetInstance();
        EmployeeDirectory directory = EmployeeDirectory.getInstance();

        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║    Employee & Team Directory Management System Demo            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        // ==================== 1. Add Employees ====================
        demoAddEmployees(directory);

        // ==================== 2. Assign Managers ====================
        demoAssignManagers(directory);

        // ==================== 3. Create Teams and Add Members ====================
        demoTeamOperations(directory);

        // ==================== 4. Hierarchical Views ====================
        demoHierarchicalViews(directory);

        // ==================== 5. CTC Calculations ====================
        demoCTCCalculations(directory);

        // ==================== 6. Additional Operations ====================
        demoAdditionalOperations(directory);
    }

    private static void demoAddEmployees(EmployeeDirectory directory) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("1. ADDING EMPLOYEES");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        // CEO
        Employee ceo = directory.addEmployee(
                "E001", "John Smith", "john.smith@company.com",
                "CEO", Department.OPERATIONS, 500000);
        directory.updateCompensation("E001", 500000, 200000, 50000);
        System.out.println("Added: " + ceo);

        // CTO (reports to CEO)
        Employee cto = directory.addEmployee(
                "E002", "Sarah Johnson", "sarah.johnson@company.com",
                "CTO", Department.ENGINEERING, 400000);
        directory.updateCompensation("E002", 400000, 150000, 40000);
        System.out.println("Added: " + cto);

        // CFO (reports to CEO)
        Employee cfo = directory.addEmployee(
                "E003", "Michael Brown", "michael.brown@company.com",
                "CFO", Department.FINANCE, 400000);
        directory.updateCompensation("E003", 400000, 150000, 40000);
        System.out.println("Added: " + cfo);

        // VP Engineering (reports to CTO)
        Employee vpEng = directory.addEmployee(
                "E004", "Emily Davis", "emily.davis@company.com",
                "VP Engineering", Department.ENGINEERING, 300000);
        directory.updateCompensation("E004", 300000, 100000, 30000);
        System.out.println("Added: " + vpEng);

        // Engineering Managers (report to VP Engineering)
        Employee engMgr1 = directory.addEmployee(
                "E005", "David Wilson", "david.wilson@company.com",
                "Engineering Manager", Department.ENGINEERING, 200000);
        directory.updateCompensation("E005", 200000, 50000, 20000);
        System.out.println("Added: " + engMgr1);

        Employee engMgr2 = directory.addEmployee(
                "E006", "Lisa Anderson", "lisa.anderson@company.com",
                "Engineering Manager", Department.ENGINEERING, 200000);
        directory.updateCompensation("E006", 200000, 50000, 20000);
        System.out.println("Added: " + engMgr2);

        // Senior Engineers (report to Engineering Managers)
        Employee seniorEng1 = directory.addEmployee(
                "E007", "Robert Taylor", "robert.taylor@company.com",
                "Senior Software Engineer", Department.ENGINEERING, 150000);
        directory.updateCompensation("E007", 150000, 30000, 15000);
        System.out.println("Added: " + seniorEng1);

        Employee seniorEng2 = directory.addEmployee(
                "E008", "Jennifer Martinez", "jennifer.martinez@company.com",
                "Senior Software Engineer", Department.ENGINEERING, 150000);
        directory.updateCompensation("E008", 150000, 30000, 15000);
        System.out.println("Added: " + seniorEng2);

        Employee seniorEng3 = directory.addEmployee(
                "E009", "James Garcia", "james.garcia@company.com",
                "Senior Software Engineer", Department.ENGINEERING, 145000);
        directory.updateCompensation("E009", 145000, 25000, 15000);
        System.out.println("Added: " + seniorEng3);

        // Software Engineers (report to Senior Engineers)
        Employee eng1 = directory.addEmployee(
                "E010", "Maria Rodriguez", "maria.rodriguez@company.com",
                "Software Engineer", Department.ENGINEERING, 100000);
        directory.updateCompensation("E010", 100000, 15000, 10000);
        System.out.println("Added: " + eng1);

        Employee eng2 = directory.addEmployee(
                "E011", "William Lee", "william.lee@company.com",
                "Software Engineer", Department.ENGINEERING, 95000);
        directory.updateCompensation("E011", 95000, 12000, 10000);
        System.out.println("Added: " + eng2);

        Employee eng3 = directory.addEmployee(
                "E012", "Patricia White", "patricia.white@company.com",
                "Software Engineer", Department.ENGINEERING, 100000);
        directory.updateCompensation("E012", 100000, 15000, 10000);
        System.out.println("Added: " + eng3);

        // Product Team
        Employee productMgr = directory.addEmployee(
                "E013", "Christopher Harris", "christopher.harris@company.com",
                "Product Manager", Department.PRODUCT, 180000);
        directory.updateCompensation("E013", 180000, 40000, 18000);
        System.out.println("Added: " + productMgr);

        Employee productAnalyst = directory.addEmployee(
                "E014", "Amanda Clark", "amanda.clark@company.com",
                "Product Analyst", Department.PRODUCT, 120000);
        directory.updateCompensation("E014", 120000, 20000, 12000);
        System.out.println("Added: " + productAnalyst);

        // Design Team
        Employee designLead = directory.addEmployee(
                "E015", "Daniel Lewis", "daniel.lewis@company.com",
                "Design Lead", Department.DESIGN, 160000);
        directory.updateCompensation("E015", 160000, 35000, 16000);
        System.out.println("Added: " + designLead);

        System.out.println("\nTotal employees added: " + directory.getAllEmployees().size());
        System.out.println();
    }

    private static void demoAssignManagers(EmployeeDirectory directory) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("2. ASSIGNING MANAGERS (Building Hierarchy)");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        // CTO and CFO report to CEO
        directory.assignManager("E002", "E001");  // CTO -> CEO
        System.out.println("Sarah Johnson (CTO) now reports to John Smith (CEO)");
        
        directory.assignManager("E003", "E001");  // CFO -> CEO
        System.out.println("Michael Brown (CFO) now reports to John Smith (CEO)");

        // VP Engineering reports to CTO
        directory.assignManager("E004", "E002");  // VP Eng -> CTO
        System.out.println("Emily Davis (VP Engineering) now reports to Sarah Johnson (CTO)");

        // Product Manager and Design Lead report to CTO
        directory.assignManager("E013", "E002");  // Product Mgr -> CTO
        System.out.println("Christopher Harris (Product Manager) now reports to Sarah Johnson (CTO)");
        
        directory.assignManager("E015", "E002");  // Design Lead -> CTO
        System.out.println("Daniel Lewis (Design Lead) now reports to Sarah Johnson (CTO)");

        // Engineering Managers report to VP Engineering
        directory.assignManager("E005", "E004");  // Eng Mgr 1 -> VP Eng
        System.out.println("David Wilson (Engineering Manager) now reports to Emily Davis (VP Engineering)");
        
        directory.assignManager("E006", "E004");  // Eng Mgr 2 -> VP Eng
        System.out.println("Lisa Anderson (Engineering Manager) now reports to Emily Davis (VP Engineering)");

        // Senior Engineers report to Engineering Managers
        directory.assignManager("E007", "E005");  // Senior Eng 1 -> Eng Mgr 1
        System.out.println("Robert Taylor (Senior Engineer) now reports to David Wilson");
        
        directory.assignManager("E008", "E005");  // Senior Eng 2 -> Eng Mgr 1
        System.out.println("Jennifer Martinez (Senior Engineer) now reports to David Wilson");
        
        directory.assignManager("E009", "E006");  // Senior Eng 3 -> Eng Mgr 2
        System.out.println("James Garcia (Senior Engineer) now reports to Lisa Anderson");

        // Software Engineers report to Senior Engineers
        directory.assignManager("E010", "E007");  // Eng 1 -> Senior Eng 1
        System.out.println("Maria Rodriguez (Engineer) now reports to Robert Taylor");
        
        directory.assignManager("E011", "E007");  // Eng 2 -> Senior Eng 1
        System.out.println("William Lee (Engineer) now reports to Robert Taylor");
        
        directory.assignManager("E012", "E009");  // Eng 3 -> Senior Eng 3
        System.out.println("Patricia White (Engineer) now reports to James Garcia");

        // Product Analyst reports to Product Manager
        directory.assignManager("E014", "E013");  // Product Analyst -> Product Mgr
        System.out.println("Amanda Clark (Product Analyst) now reports to Christopher Harris");

        System.out.println();
    }

    private static void demoTeamOperations(EmployeeDirectory directory) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("3. TEAM OPERATIONS");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        // Create teams
        Team backendTeam = directory.createTeam("T001", "Backend Platform", Department.ENGINEERING);
        System.out.println("Created team: " + backendTeam.getName());

        Team frontendTeam = directory.createTeam("T002", "Frontend Platform", Department.ENGINEERING);
        System.out.println("Created team: " + frontendTeam.getName());

        Team productTeam = directory.createTeam("T003", "Core Product", Department.PRODUCT);
        System.out.println("Created team: " + productTeam.getName());

        // Add members to Backend Team
        System.out.println("\nAdding members to Backend Platform team:");
        directory.addToTeam("T001", "E005", TeamRole.TECH_LEAD);
        System.out.println("  - David Wilson as Tech Lead");
        directory.addToTeam("T001", "E007", TeamRole.MEMBER);
        System.out.println("  - Robert Taylor as Member");
        directory.addToTeam("T001", "E010", TeamRole.MEMBER);
        System.out.println("  - Maria Rodriguez as Member");
        directory.addToTeam("T001", "E011", TeamRole.MEMBER);
        System.out.println("  - William Lee as Member");

        // Add members to Frontend Team
        System.out.println("\nAdding members to Frontend Platform team:");
        directory.addToTeam("T002", "E006", TeamRole.TECH_LEAD);
        System.out.println("  - Lisa Anderson as Tech Lead");
        directory.addToTeam("T002", "E009", TeamRole.MEMBER);
        System.out.println("  - James Garcia as Member");
        directory.addToTeam("T002", "E012", TeamRole.MEMBER);
        System.out.println("  - Patricia White as Member");
        directory.addToTeam("T002", "E015", TeamRole.MEMBER);
        System.out.println("  - Daniel Lewis (Design) as Member");

        // Add members to Product Team
        System.out.println("\nAdding members to Core Product team:");
        directory.addToTeam("T003", "E013", TeamRole.TEAM_LEAD);
        System.out.println("  - Christopher Harris as Team Lead");
        directory.addToTeam("T003", "E014", TeamRole.MEMBER);
        System.out.println("  - Amanda Clark as Member");
        directory.addToTeam("T003", "E008", TeamRole.MEMBER);
        System.out.println("  - Jennifer Martinez (Engineer) as Member");

        // Display team members
        System.out.println("\n--- Team Members ---");
        for (Team team : directory.getAllTeams()) {
            System.out.println("\n" + team.getName() + " (" + team.getSize() + " members):");
            for (TeamMember member : directory.getTeamMembers(team.getTeamId())) {
                System.out.printf("  - %-25s [%s]%n",
                        member.getEmployee().getName(),
                        member.getRole().getDisplayName());
            }
        }
        System.out.println();
    }

    private static void demoHierarchicalViews(EmployeeDirectory directory) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("4. HIERARCHICAL VIEWS");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        // Full organization tree
        System.out.println("--- Complete Organization Tree ---\n");
        System.out.println(directory.getOrganizationTree());

        // Hierarchical view from a specific employee
        System.out.println("--- Hierarchical View from Maria Rodriguez (E010) ---\n");
        System.out.println(directory.getHierarchicalView("E010"));

        // Sub-tree from VP Engineering
        System.out.println("--- Sub-Tree from Emily Davis (VP Engineering) ---\n");
        System.out.println(directory.getSubTree("E004"));

        // Management chain
        System.out.println("--- Management Chain for William Lee (E011) ---");
        List<Employee> chain = directory.getManagementChain("E011");
        for (int i = 0; i < chain.size(); i++) {
            Employee mgr = chain.get(i);
            System.out.printf("  Level %d: %s (%s)%n", i + 1, mgr.getName(), mgr.getDesignation());
        }

        // Direct reports
        System.out.println("\n--- Direct Reports of David Wilson (E005) ---");
        for (Employee report : directory.getDirectReports("E005")) {
            System.out.println("  - " + report.getName() + " (" + report.getDesignation() + ")");
        }

        // All subordinates
        System.out.println("\n--- All Subordinates of Emily Davis (E004) ---");
        for (Employee sub : directory.getAllSubordinates("E004")) {
            System.out.println("  - " + sub.getName() + " (" + sub.getDesignation() + ")");
        }

        // Span of control report
        System.out.println("\n" + directory.getSpanOfControlReport());
    }

    private static void demoCTCCalculations(EmployeeDirectory directory) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("5. CTC CALCULATIONS");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        // Individual employee CTC
        System.out.println("--- Individual Employee CTC ---");
        String[] sampleEmployees = {"E001", "E002", "E005", "E010"};
        for (String empId : sampleEmployees) {
            CTCCalculatorService.CTCBreakdown breakdown = directory.getEmployeeCTCBreakdown(empId);
            System.out.println(breakdown);
        }

        // Team CTC
        System.out.println("\n--- Team CTC ---");
        for (Team team : directory.getAllTeams()) {
            System.out.printf("%s: Total CTC = $%.2f, Average CTC = $%.2f%n",
                    team.getName(),
                    directory.getTeamCTC(team.getTeamId()),
                    team.calculateAverageCTC());
        }

        // Detailed team breakdown
        System.out.println("\n--- Detailed Team CTC Breakdown (Backend Platform) ---");
        CTCCalculatorService.TeamCTCBreakdown teamBreakdown = directory.getTeamCTCBreakdown("T001");
        System.out.println(teamBreakdown);

        // Manager CTC (including subordinates)
        System.out.println("--- Manager CTC (Including Subordinates) ---");
        CTCCalculatorService.ManagerCTCBreakdown mgrBreakdown = directory.getManagerCTCBreakdown("E004");
        System.out.println(mgrBreakdown);

        // Department CTC
        System.out.println("\n--- Department CTC ---");
        for (Department dept : Department.values()) {
            double deptCTC = directory.getDepartmentCTC(dept);
            if (deptCTC > 0) {
                CTCCalculatorService.DepartmentCTCBreakdown deptBreakdown = 
                        directory.getDepartmentCTCBreakdown(dept);
                System.out.println(deptBreakdown);
            }
        }

        // Total organization CTC
        System.out.printf("%n--- Total Organization CTC: $%.2f ---%n", directory.getTotalOrganizationCTC());

        // Full CTC report
        System.out.println("\n" + directory.generateCTCReport());
    }

    private static void demoAdditionalOperations(EmployeeDirectory directory) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("6. ADDITIONAL OPERATIONS");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        // Search employees by name
        System.out.println("--- Search Employees by Name (containing 'David') ---");
        List<Employee> searchResults = directory.searchEmployeesByName("David");
        for (Employee emp : searchResults) {
            System.out.println("  - " + emp);
        }

        // Get employees by department
        System.out.println("\n--- Employees in Engineering Department ---");
        List<Employee> engEmployees = directory.getEmployeesByDepartment(Department.ENGINEERING);
        System.out.println("  Total: " + engEmployees.size() + " employees");
        for (Employee emp : engEmployees.subList(0, Math.min(5, engEmployees.size()))) {
            System.out.println("  - " + emp.getName() + " (" + emp.getDesignation() + ")");
        }
        if (engEmployees.size() > 5) {
            System.out.println("  ... and " + (engEmployees.size() - 5) + " more");
        }

        // Get teams for an employee
        System.out.println("\n--- Teams for Jennifer Martinez (E008) ---");
        List<Team> empTeams = directory.getTeamsForEmployee("E008");
        for (Team team : empTeams) {
            System.out.println("  - " + team.getName());
        }

        // Hierarchy level
        System.out.println("\n--- Hierarchy Levels ---");
        System.out.println("  John Smith (CEO): Level " + directory.getHierarchyLevel("E001"));
        System.out.println("  Sarah Johnson (CTO): Level " + directory.getHierarchyLevel("E002"));
        System.out.println("  Emily Davis (VP Eng): Level " + directory.getHierarchyLevel("E004"));
        System.out.println("  David Wilson (Eng Mgr): Level " + directory.getHierarchyLevel("E005"));
        System.out.println("  Maria Rodriguez (Eng): Level " + directory.getHierarchyLevel("E010"));

        // Change employee status
        System.out.println("\n--- Changing Employee Status ---");
        directory.changeEmployeeStatus("E014", EmployeeStatus.ON_LEAVE);
        Employee amanda = directory.getEmployee("E014");
        System.out.println("Amanda Clark status changed to: " + amanda.getStatus().getDisplayName());

        // Update team member role
        System.out.println("\n--- Updating Team Member Role ---");
        directory.updateTeamMemberRole("T001", "E007", TeamRole.TECH_LEAD);
        System.out.println("Robert Taylor promoted to Tech Lead in Backend Platform team");

        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║              Demo Completed Successfully!                       ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }
}
