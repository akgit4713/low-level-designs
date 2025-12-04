package linkedin;

import linkedin.enums.ApplicationStatus;
import linkedin.enums.ExperienceLevel;
import linkedin.enums.JobType;
import linkedin.models.*;
import linkedin.services.*;
import linkedin.strategies.search.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Main class demonstrating the LinkedIn platform functionality.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("LinkedIn Professional Networking Platform - Demo");
        System.out.println("=".repeat(60));
        
        // Initialize the platform
        LinkedIn linkedin = new LinkedIn();
        
        // Get services
        UserService userService = linkedin.getUserService();
        ConnectionService connectionService = linkedin.getConnectionService();
        MessagingService messagingService = linkedin.getMessagingService();
        CompanyService companyService = linkedin.getCompanyService();
        JobService jobService = linkedin.getJobService();
        SearchService searchService = linkedin.getSearchService();
        NotificationService notificationService = linkedin.getNotificationService();
        
        // === 1. USER REGISTRATION ===
        System.out.println("\n" + "=".repeat(60));
        System.out.println("1. USER REGISTRATION");
        System.out.println("=".repeat(60));
        
        User alice = userService.register("Alice Johnson", "alice@example.com", "password123");
        User bob = userService.register("Bob Smith", "bob@example.com", "password456");
        User charlie = userService.register("Charlie Brown", "charlie@example.com", "password789");
        User diana = userService.register("Diana Prince", "diana@example.com", "password321");
        
        System.out.println("Registered users:");
        System.out.println("  - " + alice);
        System.out.println("  - " + bob);
        System.out.println("  - " + charlie);
        System.out.println("  - " + diana);
        
        // === 2. PROFILE MANAGEMENT ===
        System.out.println("\n" + "=".repeat(60));
        System.out.println("2. PROFILE MANAGEMENT");
        System.out.println("=".repeat(60));
        
        // Update profiles
        userService.updateProfile(alice.getId(), 
            "Senior Software Engineer at TechCorp", 
            "Passionate about building scalable systems",
            "San Francisco, CA",
            "Technology"
        );
        
        userService.updateProfile(bob.getId(), 
            "Product Manager at StartupXYZ", 
            "Building products that users love",
            "New York, NY",
            "Product"
        );
        
        userService.updateProfile(charlie.getId(),
            "HR Manager at TechCorp",
            "Connecting great talent with great opportunities",
            "San Francisco, CA",
            "Human Resources"
        );
        
        userService.updateProfile(diana.getId(),
            "Data Scientist at DataLabs",
            "Turning data into insights",
            "Boston, MA",
            "Data Science"
        );
        
        // Add experience
        Experience exp1 = userService.addExperience(alice.getId(), 
            "Senior Software Engineer", "TechCorp", 
            LocalDate.of(2020, 1, 15),
            "San Francisco, CA",
            "Leading backend development team"
        );
        
        userService.addExperience(alice.getId(),
            "Software Engineer", "StartupABC",
            LocalDate.of(2017, 6, 1),
            "San Francisco, CA",
            "Full-stack development"
        );
        
        // Add education
        userService.addEducation(alice.getId(),
            "Stanford University", "Master's", "Computer Science",
            2015, 2017
        );
        
        // Add skills
        Skill javaSkill = userService.addSkill(alice.getId(), "Java");
        userService.addSkill(alice.getId(), "Python");
        userService.addSkill(alice.getId(), "System Design");
        userService.addSkill(bob.getId(), "Product Strategy");
        userService.addSkill(bob.getId(), "Agile");
        
        System.out.println("Updated Alice's profile: " + alice.getProfile().getHeadline());
        System.out.println("Alice's skills: " + alice.getProfile().getSkills());
        
        // === 3. CONNECTIONS ===
        System.out.println("\n" + "=".repeat(60));
        System.out.println("3. CONNECTIONS");
        System.out.println("=".repeat(60));
        
        // Send connection requests
        Connection conn1 = connectionService.sendConnectionRequest(alice.getId(), bob.getId());
        Connection conn2 = connectionService.sendConnectionRequest(alice.getId(), charlie.getId());
        Connection conn3 = connectionService.sendConnectionRequest(bob.getId(), diana.getId());
        
        System.out.println("\nConnection requests sent.");
        
        // Bob accepts Alice's request
        connectionService.acceptConnectionRequest(conn1.getId(), bob.getId());
        System.out.println("Bob accepted Alice's connection request.");
        
        // Charlie accepts Alice's request
        connectionService.acceptConnectionRequest(conn2.getId(), charlie.getId());
        System.out.println("Charlie accepted Alice's connection request.");
        
        // Diana accepts Bob's request
        connectionService.acceptConnectionRequest(conn3.getId(), diana.getId());
        System.out.println("Diana accepted Bob's connection request.");
        
        // List connections
        List<User> aliceConnections = connectionService.getConnections(alice.getId());
        System.out.println("\nAlice's connections: " + aliceConnections.size());
        aliceConnections.forEach(u -> System.out.println("  - " + u.getName()));
        
        // === 4. MESSAGING ===
        System.out.println("\n" + "=".repeat(60));
        System.out.println("4. MESSAGING");
        System.out.println("=".repeat(60));
        
        // Alice sends message to Bob
        Message msg1 = messagingService.sendMessage(alice.getId(), bob.getId(), 
            "Hi Bob! Great to connect with you. How's the PM role going?");
        
        // Bob replies
        Message msg2 = messagingService.sendMessage(bob.getId(), alice.getId(),
            "Hey Alice! It's going great. Lots of interesting challenges. How about you?");
        
        // Alice sends another message
        Message msg3 = messagingService.sendMessage(alice.getId(), bob.getId(),
            "Same here! We should catch up over coffee sometime.");
        
        System.out.println("Messages exchanged between Alice and Bob.");
        
        // View conversation
        List<Conversation> aliceConversations = messagingService.getConversations(alice.getId());
        System.out.println("Alice has " + aliceConversations.size() + " conversation(s).");
        
        // Check unread messages
        int bobUnread = messagingService.getUnreadCount(bob.getId());
        System.out.println("Bob has " + bobUnread + " unread message(s).");
        
        // Mark as read
        messagingService.markConversationAsRead(aliceConversations.get(0).getId(), bob.getId());
        System.out.println("Bob marked conversation as read.");
        
        // === 5. COMPANIES ===
        System.out.println("\n" + "=".repeat(60));
        System.out.println("5. COMPANIES");
        System.out.println("=".repeat(60));
        
        // Create companies
        Company techCorp = companyService.createCompany("TechCorp", "Technology", alice.getId());
        companyService.updateCompanyDetails(techCorp.getId(),
            "Leading technology company building innovative solutions",
            "https://techcorp.example.com",
            "San Francisco, CA",
            "1001-5000"
        );
        
        Company startupXYZ = companyService.createCompany("StartupXYZ", "Technology", bob.getId());
        companyService.updateCompanyDetails(startupXYZ.getId(),
            "Fast-growing startup disrupting the industry",
            "https://startupxyz.example.com",
            "New York, NY",
            "51-200"
        );
        
        // Add employees
        companyService.addEmployee(techCorp.getId(), charlie.getId());
        
        // Follow companies
        companyService.followCompany(techCorp.getId(), diana.getId());
        companyService.followCompany(startupXYZ.getId(), alice.getId());
        
        System.out.println("Created companies: " + techCorp.getName() + ", " + startupXYZ.getName());
        System.out.println("Diana is following TechCorp.");
        
        // === 6. JOB POSTINGS ===
        System.out.println("\n" + "=".repeat(60));
        System.out.println("6. JOB POSTINGS");
        System.out.println("=".repeat(60));
        
        // Charlie (HR at TechCorp) posts jobs
        JobPosting job1 = jobService.createJobPosting(
            "Senior Backend Engineer",
            "We're looking for a senior backend engineer to join our platform team. " +
            "You'll be working on high-scale distributed systems.",
            techCorp.getId(),
            charlie.getId(),
            JobType.FULL_TIME,
            ExperienceLevel.SENIOR,
            "San Francisco, CA"
        );
        
        JobPosting job2 = jobService.createJobPosting(
            "Product Designer",
            "Join our design team to create beautiful, user-centered experiences.",
            techCorp.getId(),
            charlie.getId(),
            JobType.FULL_TIME,
            ExperienceLevel.MID,
            "San Francisco, CA"
        );
        
        // Bob posts a job at StartupXYZ
        JobPosting job3 = jobService.createJobPosting(
            "Data Analyst",
            "Help us make data-driven decisions. Work with our data team on analytics.",
            startupXYZ.getId(),
            bob.getId(),
            JobType.FULL_TIME,
            ExperienceLevel.ENTRY,
            "New York, NY"
        );
        
        System.out.println("Posted jobs:");
        System.out.println("  - " + job1.getTitle() + " at " + techCorp.getName());
        System.out.println("  - " + job2.getTitle() + " at " + techCorp.getName());
        System.out.println("  - " + job3.getTitle() + " at " + startupXYZ.getName());
        
        // Diana applies for a job
        System.out.println("\nDiana applying for Data Analyst position...");
        JobApplication application = jobService.applyForJob(
            job3.getId(), 
            diana.getId(),
            "https://resume.example.com/diana.pdf",
            "I'm excited to apply for this position. My background in data science makes me a great fit."
        );
        
        System.out.println("Application submitted: " + application);
        
        // Bob reviews and shortlists Diana
        jobService.updateApplicationStatus(application.getId(), bob.getId(), ApplicationStatus.SHORTLISTED);
        System.out.println("Bob shortlisted Diana's application.");
        
        // === 7. SEARCH ===
        System.out.println("\n" + "=".repeat(60));
        System.out.println("7. SEARCH");
        System.out.println("=".repeat(60));
        
        // Search for users
        System.out.println("\nSearching for 'Engineer'...");
        List<SearchResult> searchResults = searchService.searchAll("Engineer", alice.getId());
        System.out.println("Found " + searchResults.size() + " results:");
        searchResults.forEach(r -> System.out.println("  - [" + r.getType() + "] " + r.getTitle() + 
            " (score: " + String.format("%.2f", r.getRelevanceScore()) + ")"));
        
        // Search with different strategy
        System.out.println("\nSearching for 'Data' with location-based ranking...");
        linkedin.setSearchRankingStrategy(new LocationBasedRankingStrategy());
        searchResults = searchService.searchAll("Data", diana.getId());
        System.out.println("Found " + searchResults.size() + " results:");
        searchResults.forEach(r -> System.out.println("  - [" + r.getType() + "] " + r.getTitle()));
        
        // Search jobs only
        System.out.println("\nSearching for jobs in San Francisco...");
        List<JobPosting> sfJobs = jobService.searchJobsByLocation("San Francisco");
        System.out.println("Found " + sfJobs.size() + " job(s):");
        sfJobs.forEach(j -> System.out.println("  - " + j.getTitle()));
        
        // === 8. NOTIFICATIONS ===
        System.out.println("\n" + "=".repeat(60));
        System.out.println("8. NOTIFICATIONS");
        System.out.println("=".repeat(60));
        
        // Check notifications
        int aliceUnreadNotifs = notificationService.getUnreadCount(alice.getId());
        int bobUnreadNotifs = notificationService.getUnreadCount(bob.getId());
        int dianaUnreadNotifs = notificationService.getUnreadCount(diana.getId());
        
        System.out.println("Unread notifications:");
        System.out.println("  - Alice: " + aliceUnreadNotifs);
        System.out.println("  - Bob: " + bobUnreadNotifs);
        System.out.println("  - Diana: " + dianaUnreadNotifs);
        
        System.out.println("\nDiana's notifications:");
        notificationService.getNotificationsForUser(diana.getId()).forEach(n -> 
            System.out.println("  - [" + n.getType() + "] " + n.getContent())
        );
        
        // === 9. SUMMARY ===
        System.out.println("\n" + "=".repeat(60));
        System.out.println("DEMO SUMMARY");
        System.out.println("=".repeat(60));
        
        System.out.println("✓ User registration and authentication");
        System.out.println("✓ Profile management (headline, experience, education, skills)");
        System.out.println("✓ Connection requests (send, accept, decline)");
        System.out.println("✓ Messaging between connections");
        System.out.println("✓ Company creation and management");
        System.out.println("✓ Job postings and applications");
        System.out.println("✓ Search with pluggable ranking strategies");
        System.out.println("✓ Real-time notifications (Observer pattern)");
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Design Patterns Used:");
        System.out.println("=".repeat(60));
        System.out.println("• Observer Pattern - Notification delivery");
        System.out.println("• Strategy Pattern - Search ranking algorithms");
        System.out.println("• Factory Pattern - Notification creation");
        System.out.println("• Builder Pattern - JobPosting, SearchContext");
        System.out.println("• Repository Pattern - Data access abstraction");
        System.out.println("• Facade Pattern - LinkedIn class");
        
        System.out.println("\nDemo completed successfully!");
    }
}



