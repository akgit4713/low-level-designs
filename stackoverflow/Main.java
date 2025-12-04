package stackoverflow;

import stackoverflow.enums.VoteType;
import stackoverflow.models.*;
import stackoverflow.observers.NotificationObserver;
import stackoverflow.observers.StatisticsObserver;

import java.util.Arrays;
import java.util.List;

/**
 * Demonstration of the Stack Overflow System.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         STACK OVERFLOW SYSTEM DEMO                        ║");
        System.out.println("║    Low-Level Design Implementation                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");

        // Reset instance to ensure clean state
        StackOverflow.resetInstance();
        StackOverflow so = StackOverflow.getInstance();

        // Register observers
        StatisticsObserver statsObserver = new StatisticsObserver();
        so.addObserver(new NotificationObserver());
        so.addObserver(statsObserver);

        // ==================== User Registration ====================
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│           USER REGISTRATION              │");
        System.out.println("└─────────────────────────────────────────┘\n");
        
        User alice = so.createUser("alice", "alice@example.com");
        User bob = so.createUser("bob", "bob@example.com");
        User charlie = so.createUser("charlie", "charlie@example.com");
        User diana = so.createUser("diana", "diana@example.com");
        User eve = so.createUser("eve", "eve@example.com");

        // ==================== Posting Questions ====================
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│           POSTING QUESTIONS              │");
        System.out.println("└─────────────────────────────────────────┘\n");
        
        Question q1 = so.postQuestion(alice, 
            "How to implement Singleton pattern in Java?",
            "I want to implement a thread-safe singleton pattern. " +
            "What are the best practices? Should I use double-checked locking or enum?",
            Arrays.asList("java", "design-patterns", "singleton"));

        Question q2 = so.postQuestion(bob,
            "What is the difference between ArrayList and LinkedList?",
            "When should I use ArrayList vs LinkedList in Java? " +
            "I need to understand the performance implications.",
            Arrays.asList("java", "collections", "data-structures"));

        Question q3 = so.postQuestion(charlie,
            "How does HashMap work internally?",
            "Can someone explain the internal working of HashMap in Java? " +
            "Including hashing, buckets, and collision handling.",
            Arrays.asList("java", "collections", "hashmap"));

        Question q4 = so.postQuestion(diana,
            "Best practices for REST API design?",
            "What are the best practices for designing RESTful APIs? " +
            "Including naming conventions, HTTP methods, and error handling.",
            Arrays.asList("rest", "api", "web-development"));

        // ==================== Posting Answers ====================
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│            POSTING ANSWERS               │");
        System.out.println("└─────────────────────────────────────────┘\n");
        
        Answer a1 = so.postAnswer(bob, q1,
            "Use double-checked locking with volatile keyword for thread safety. " +
            "Example: private static volatile Singleton instance; " +
            "This prevents instruction reordering issues.");

        Answer a2 = so.postAnswer(charlie, q1,
            "Consider using enum-based singleton. It's inherently thread-safe, " +
            "handles serialization automatically, and is the recommended approach " +
            "by Joshua Bloch in Effective Java.");

        Answer a3 = so.postAnswer(eve, q1,
            "You can also use the Bill Pugh Singleton Design Pattern which uses " +
            "a static inner helper class. It's lazy-loaded and thread-safe.");

        Answer a4 = so.postAnswer(alice, q2,
            "ArrayList: O(1) random access, amortized O(1) add at end, O(n) insert/delete. " +
            "LinkedList: O(n) random access, O(1) add/remove at ends. " +
            "Use ArrayList for most cases unless you need frequent insertions.");

        Answer a5 = so.postAnswer(diana, q3,
            "HashMap uses array of buckets. Hash code determines bucket index. " +
            "Since Java 8, uses balanced trees instead of linked lists when " +
            "bucket size exceeds threshold (8) for better worst-case performance.");

        Answer a6 = so.postAnswer(alice, q4,
            "Key practices: Use nouns for resources, HTTP verbs for actions, " +
            "proper status codes, versioning, pagination, and HATEOAS for discoverability. " +
            "Always validate input and use consistent error responses.");

        // ==================== Adding Comments ====================
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│            ADDING COMMENTS               │");
        System.out.println("└─────────────────────────────────────────┘\n");
        
        so.addComment(diana, q1, "Great question! This is fundamental to Java development.");
        so.addComment(alice, a1, "Thanks! Could you provide a complete code example?");
        so.addComment(bob, a2, "Enum singleton is indeed cleaner and preferred!");
        so.addComment(charlie, a4, "Don't forget about CopyOnWriteArrayList for concurrent scenarios.");

        // ==================== Voting ====================
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│               VOTING                     │");
        System.out.println("└─────────────────────────────────────────┘\n");
        
        // Vote on questions
        so.voteQuestion(bob, q1, VoteType.UPVOTE);
        so.voteQuestion(charlie, q1, VoteType.UPVOTE);
        so.voteQuestion(diana, q1, VoteType.UPVOTE);
        so.voteQuestion(eve, q1, VoteType.UPVOTE);
        
        so.voteQuestion(alice, q2, VoteType.UPVOTE);
        so.voteQuestion(charlie, q2, VoteType.UPVOTE);
        so.voteQuestion(diana, q2, VoteType.UPVOTE);

        so.voteQuestion(alice, q3, VoteType.UPVOTE);
        so.voteQuestion(bob, q3, VoteType.UPVOTE);

        so.voteQuestion(alice, q4, VoteType.UPVOTE);
        so.voteQuestion(bob, q4, VoteType.UPVOTE);
        so.voteQuestion(charlie, q4, VoteType.UPVOTE);

        // Vote on answers
        so.voteAnswer(alice, a1, VoteType.UPVOTE);
        so.voteAnswer(diana, a1, VoteType.UPVOTE);
        so.voteAnswer(eve, a1, VoteType.UPVOTE);
        
        so.voteAnswer(alice, a2, VoteType.UPVOTE);
        so.voteAnswer(bob, a2, VoteType.UPVOTE);
        so.voteAnswer(diana, a2, VoteType.UPVOTE);
        so.voteAnswer(eve, a2, VoteType.UPVOTE);
        
        so.voteAnswer(bob, a5, VoteType.UPVOTE);
        so.voteAnswer(alice, a5, VoteType.UPVOTE);
        so.voteAnswer(eve, a5, VoteType.UPVOTE);

        so.voteAnswer(bob, a6, VoteType.UPVOTE);
        so.voteAnswer(charlie, a6, VoteType.UPVOTE);

        // ==================== Accept Answer ====================
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│          ACCEPTING ANSWER                │");
        System.out.println("└─────────────────────────────────────────┘\n");
        
        so.acceptAnswer(alice, a2); // Alice accepts Charlie's answer

        // ==================== Search Demonstrations ====================
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│          SEARCH DEMONSTRATIONS           │");
        System.out.println("└─────────────────────────────────────────┘\n");

        // Search by keyword
        System.out.println("📌 Search for 'singleton':");
        List<Question> singletonResults = so.searchQuestions("singleton");
        singletonResults.forEach(q -> System.out.println("   " + q));

        // Search by tag
        System.out.println("\n📌 Questions tagged 'java':");
        List<Question> javaQuestions = so.getQuestionsByTag("java");
        javaQuestions.forEach(q -> System.out.println("   " + q));

        // Search by user
        System.out.println("\n📌 Questions by Alice:");
        List<Question> aliceQuestions = so.getQuestionsByUser(alice);
        aliceQuestions.forEach(q -> System.out.println("   " + q));

        // Advanced search
        System.out.println("\n📌 Advanced Search (keyword='HashMap', tag='collections'):");
        List<Question> advancedResults = so.advancedSearch("HashMap", 
            Arrays.asList("collections"), null);
        advancedResults.forEach(q -> System.out.println("   " + q));

        // Trending questions
        System.out.println("\n📌 Trending Questions (Top 3):");
        List<Question> trending = so.getTrendingQuestions(3);
        trending.forEach(q -> System.out.println("   " + q));

        // Unanswered questions (create one first)
        Question unanswered = so.postQuestion(eve, 
            "How to configure Spring Boot with PostgreSQL?",
            "I need help setting up a Spring Boot application with PostgreSQL database.",
            Arrays.asList("spring-boot", "postgresql", "java"));
        
        System.out.println("\n📌 Unanswered Questions:");
        List<Question> unansweredQuestions = so.getUnansweredQuestions();
        unansweredQuestions.forEach(q -> System.out.println("   " + q));

        // ==================== Display Question Details ====================
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│          QUESTION DETAILS                │");
        System.out.println("└─────────────────────────────────────────┘\n");
        
        displayQuestionDetails(q1);

        // ==================== Statistics ====================
        System.out.println("\n┌─────────────────────────────────────────┐");
        System.out.println("│              STATISTICS                  │");
        System.out.println("└─────────────────────────────────────────┘");
        
        so.displayStats();
        statsObserver.printStatistics();

        // ==================== Top Users ====================
        System.out.println("┌─────────────────────────────────────────┐");
        System.out.println("│            TOP USERS                     │");
        System.out.println("└─────────────────────────────────────────┘\n");
        
        List<User> topUsers = so.getTopUsers(5);
        int rank = 1;
        for (User u : topUsers) {
            System.out.printf("  #%d %s%n", rank++, u);
        }

        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          DEMO COMPLETED SUCCESSFULLY                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    private static void displayQuestionDetails(Question question) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("  Title: " + question.getTitle());
        System.out.println("  Author: " + question.getAuthor().getUsername());
        System.out.println("  Votes: " + question.getVoteCount());
        System.out.println("  Views: " + question.getViewCount());
        System.out.println("  Tags: " + question.getTags());
        System.out.println("  Comments: " + question.getComments().size());
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("  Content: " + question.getContent());
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("  Answers (" + question.getAnswers().size() + "):");
        for (Answer a : question.getAnswers()) {
            String accepted = a.isAccepted() ? " ✓ ACCEPTED" : "";
            System.out.println("    • " + a.getAuthor().getUsername() + accepted + 
                " (votes: " + a.getVoteCount() + ")");
            System.out.println("      " + (a.getContent().length() > 60 ? 
                a.getContent().substring(0, 60) + "..." : a.getContent()));
        }
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }
}
