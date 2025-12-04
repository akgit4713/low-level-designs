package stackoverflow.tests;

import stackoverflow.StackOverflow;
import stackoverflow.enums.VoteType;
import stackoverflow.exceptions.*;
import stackoverflow.models.*;
import stackoverflow.observers.StatisticsObserver;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for Stack Overflow system.
 * Simple test framework without external dependencies.
 */
public class StackOverflowTest {
    
    private StackOverflow so;
    private int passed = 0;
    private int failed = 0;

    public static void main(String[] args) {
        StackOverflowTest tests = new StackOverflowTest();
        tests.runAllTests();
    }

    public void runAllTests() {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║     STACK OVERFLOW UNIT TESTS         ║");
        System.out.println("╚═══════════════════════════════════════╝\n");

        // User Tests
        runTest("testUserCreation", this::testUserCreation);
        runTest("testDuplicateUsername", this::testDuplicateUsername);
        runTest("testDuplicateEmail", this::testDuplicateEmail);
        runTest("testUserNotFound", this::testUserNotFound);

        // Question Tests
        runTest("testQuestionCreation", this::testQuestionCreation);
        runTest("testQuestionWithTags", this::testQuestionWithTags);
        runTest("testQuestionNotFound", this::testQuestionNotFound);

        // Answer Tests
        runTest("testAnswerCreation", this::testAnswerCreation);
        runTest("testAcceptAnswer", this::testAcceptAnswer);
        runTest("testOnlyAuthorCanAccept", this::testOnlyAuthorCanAccept);

        // Comment Tests
        runTest("testAddCommentToQuestion", this::testAddCommentToQuestion);
        runTest("testAddCommentToAnswer", this::testAddCommentToAnswer);

        // Voting Tests
        runTest("testUpvoteQuestion", this::testUpvoteQuestion);
        runTest("testDownvoteQuestion", this::testDownvoteQuestion);
        runTest("testUpvoteAnswer", this::testUpvoteAnswer);
        runTest("testCannotVoteOwnContent", this::testCannotVoteOwnContent);
        runTest("testVoteUpdatesReputation", this::testVoteUpdatesReputation);

        // Search Tests
        runTest("testSearchByKeyword", this::testSearchByKeyword);
        runTest("testSearchByTag", this::testSearchByTag);
        runTest("testSearchByUser", this::testSearchByUser);
        runTest("testAdvancedSearch", this::testAdvancedSearch);

        // Observer Tests
        runTest("testObserverNotification", this::testObserverNotification);

        // Concurrency Tests
        runTest("testConcurrentVoting", this::testConcurrentVoting);

        printSummary();
    }

    private void setUp() {
        StackOverflow.resetInstance();
        so = StackOverflow.getInstance();
    }

    private void runTest(String name, Runnable test) {
        setUp();
        try {
            test.run();
            System.out.println("✅ " + name);
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("❌ " + name + ": " + e.getMessage());
            failed++;
        }
    }

    // ==================== User Tests ====================

    private void testUserCreation() {
        User user = so.createUser("testuser", "test@example.com");
        
        assertNotNull(user, "User should not be null");
        assertEquals("testuser", user.getUsername(), "Username should match");
        assertEquals("test@example.com", user.getEmail(), "Email should match");
        assertEquals(1, user.getReputation(), "Initial reputation should be 1");
    }

    private void testDuplicateUsername() {
        so.createUser("duplicate", "user1@example.com");
        
        try {
            so.createUser("duplicate", "user2@example.com");
            fail("Should throw exception for duplicate username");
        } catch (StackOverflowException e) {
            assertTrue(e.getMessage().contains("Username already exists"), 
                "Exception message should mention duplicate username");
        }
    }

    private void testDuplicateEmail() {
        so.createUser("user1", "duplicate@example.com");
        
        try {
            so.createUser("user2", "duplicate@example.com");
            fail("Should throw exception for duplicate email");
        } catch (StackOverflowException e) {
            assertTrue(e.getMessage().contains("Email already registered"), 
                "Exception message should mention duplicate email");
        }
    }

    private void testUserNotFound() {
        try {
            so.getUser("nonexistent-id");
            fail("Should throw exception for non-existent user");
        } catch (UserNotFoundException e) {
            // Expected
        }
    }

    // ==================== Question Tests ====================

    private void testQuestionCreation() {
        User author = so.createUser("author", "author@example.com");
        Question question = so.postQuestion(author, "Test Title", "Test Content", 
            Arrays.asList("java"));
        
        assertNotNull(question, "Question should not be null");
        assertEquals("Test Title", question.getTitle(), "Title should match");
        assertEquals("Test Content", question.getContent(), "Content should match");
        assertEquals(author.getId(), question.getAuthor().getId(), "Author should match");
    }

    private void testQuestionWithTags() {
        User author = so.createUser("author", "author@example.com");
        Question question = so.postQuestion(author, "Tagged Question", "Content", 
            Arrays.asList("java", "spring", "rest"));
        
        assertEquals(3, question.getTags().size(), "Should have 3 tags");
        assertTrue(question.hasTag("java"), "Should have java tag");
        assertTrue(question.hasTag("spring"), "Should have spring tag");
        assertTrue(question.hasTag("rest"), "Should have rest tag");
    }

    private void testQuestionNotFound() {
        try {
            so.getQuestion("nonexistent-id");
            fail("Should throw exception for non-existent question");
        } catch (QuestionNotFoundException e) {
            // Expected
        }
    }

    // ==================== Answer Tests ====================

    private void testAnswerCreation() {
        User author = so.createUser("author", "author@example.com");
        User answerer = so.createUser("answerer", "answerer@example.com");
        Question question = so.postQuestion(author, "Question", "Content", Arrays.asList("java"));
        
        Answer answer = so.postAnswer(answerer, question, "This is my answer");
        
        assertNotNull(answer, "Answer should not be null");
        assertEquals("This is my answer", answer.getContent(), "Content should match");
        assertEquals(answerer.getId(), answer.getAuthor().getId(), "Author should match");
        assertEquals(question.getId(), answer.getQuestion().getId(), "Question should match");
    }

    private void testAcceptAnswer() {
        User author = so.createUser("author", "author@example.com");
        User answerer = so.createUser("answerer", "answerer@example.com");
        Question question = so.postQuestion(author, "Question", "Content", Arrays.asList("java"));
        Answer answer = so.postAnswer(answerer, question, "Answer");
        
        int repBefore = answerer.getReputation();
        so.acceptAnswer(author, answer);
        
        assertTrue(answer.isAccepted(), "Answer should be accepted");
        assertTrue(answerer.getReputation() > repBefore, "Reputation should increase");
    }

    private void testOnlyAuthorCanAccept() {
        User author = so.createUser("author", "author@example.com");
        User other = so.createUser("other", "other@example.com");
        Question question = so.postQuestion(author, "Question", "Content", Arrays.asList("java"));
        Answer answer = so.postAnswer(other, question, "Answer");
        
        try {
            so.acceptAnswer(other, answer);
            fail("Only question author should be able to accept");
        } catch (UnauthorizedException e) {
            // Expected
        }
    }

    // ==================== Comment Tests ====================

    private void testAddCommentToQuestion() {
        User author = so.createUser("author", "author@example.com");
        User commenter = so.createUser("commenter", "commenter@example.com");
        Question question = so.postQuestion(author, "Question", "Content", Arrays.asList("java"));
        
        Comment comment = so.addComment(commenter, question, "Nice question!");
        
        assertNotNull(comment, "Comment should not be null");
        assertEquals(1, question.getComments().size(), "Question should have 1 comment");
    }

    private void testAddCommentToAnswer() {
        User author = so.createUser("author", "author@example.com");
        User answerer = so.createUser("answerer", "answerer@example.com");
        Question question = so.postQuestion(author, "Question", "Content", Arrays.asList("java"));
        Answer answer = so.postAnswer(answerer, question, "Answer");
        
        Comment comment = so.addComment(author, answer, "Thanks for the answer!");
        
        assertNotNull(comment, "Comment should not be null");
        assertEquals(1, answer.getComments().size(), "Answer should have 1 comment");
    }

    // ==================== Voting Tests ====================

    private void testUpvoteQuestion() {
        User author = so.createUser("author", "author@example.com");
        User voter = so.createUser("voter", "voter@example.com");
        Question question = so.postQuestion(author, "Question", "Content", Arrays.asList("java"));
        
        so.voteQuestion(voter, question, VoteType.UPVOTE);
        
        assertEquals(1, question.getVoteCount(), "Vote count should be 1");
    }

    private void testDownvoteQuestion() {
        User author = so.createUser("author", "author@example.com");
        User voter = so.createUser("voter", "voter@example.com");
        Question question = so.postQuestion(author, "Question", "Content", Arrays.asList("java"));
        
        so.voteQuestion(voter, question, VoteType.DOWNVOTE);
        
        assertEquals(-1, question.getVoteCount(), "Vote count should be -1");
    }

    private void testUpvoteAnswer() {
        User author = so.createUser("author", "author@example.com");
        User answerer = so.createUser("answerer", "answerer@example.com");
        Question question = so.postQuestion(author, "Question", "Content", Arrays.asList("java"));
        Answer answer = so.postAnswer(answerer, question, "Answer");
        
        so.voteAnswer(author, answer, VoteType.UPVOTE);
        
        assertEquals(1, answer.getVoteCount(), "Vote count should be 1");
    }

    private void testCannotVoteOwnContent() {
        User author = so.createUser("author", "author@example.com");
        Question question = so.postQuestion(author, "Question", "Content", Arrays.asList("java"));
        
        try {
            so.voteQuestion(author, question, VoteType.UPVOTE);
            fail("Should not be able to vote on own content");
        } catch (VotingException e) {
            // Expected
        }
    }

    private void testVoteUpdatesReputation() {
        User author = so.createUser("author", "author@example.com");
        User voter = so.createUser("voter", "voter@example.com");
        Question question = so.postQuestion(author, "Question", "Content", Arrays.asList("java"));
        
        int repBefore = author.getReputation();
        so.voteQuestion(voter, question, VoteType.UPVOTE);
        
        assertTrue(author.getReputation() > repBefore, "Author reputation should increase");
    }

    // ==================== Search Tests ====================

    private void testSearchByKeyword() {
        User author = so.createUser("author", "author@example.com");
        so.postQuestion(author, "How to use Singleton pattern?", "Content", Arrays.asList("java"));
        so.postQuestion(author, "ArrayList vs LinkedList", "Content", Arrays.asList("java"));
        
        List<Question> results = so.searchQuestions("Singleton");
        
        assertEquals(1, results.size(), "Should find 1 question");
        assertTrue(results.get(0).getTitle().contains("Singleton"), "Should contain keyword");
    }

    private void testSearchByTag() {
        User author = so.createUser("author", "author@example.com");
        so.postQuestion(author, "Java Question", "Content", Arrays.asList("java"));
        so.postQuestion(author, "Python Question", "Content", Arrays.asList("python"));
        
        List<Question> results = so.getQuestionsByTag("java");
        
        assertEquals(1, results.size(), "Should find 1 question");
        assertTrue(results.get(0).hasTag("java"), "Should have java tag");
    }

    private void testSearchByUser() {
        User alice = so.createUser("alice", "alice@example.com");
        User bob = so.createUser("bob", "bob@example.com");
        so.postQuestion(alice, "Alice's Question 1", "Content", Arrays.asList("java"));
        so.postQuestion(alice, "Alice's Question 2", "Content", Arrays.asList("python"));
        so.postQuestion(bob, "Bob's Question", "Content", Arrays.asList("java"));
        
        List<Question> results = so.getQuestionsByUser(alice);
        
        assertEquals(2, results.size(), "Should find 2 questions by Alice");
    }

    private void testAdvancedSearch() {
        User author = so.createUser("author", "author@example.com");
        so.postQuestion(author, "Java HashMap Tutorial", "Content about HashMap", 
            Arrays.asList("java", "collections"));
        so.postQuestion(author, "Python Dictionary", "Content about dict", 
            Arrays.asList("python"));
        so.postQuestion(author, "Java ArrayList Guide", "Content about ArrayList", 
            Arrays.asList("java", "collections"));
        
        List<Question> results = so.advancedSearch("HashMap", 
            Arrays.asList("java"), null);
        
        assertEquals(1, results.size(), "Should find 1 question");
    }

    // ==================== Observer Tests ====================

    private void testObserverNotification() {
        StatisticsObserver stats = new StatisticsObserver();
        so.addObserver(stats);
        
        User user = so.createUser("user", "user@example.com");
        so.postQuestion(user, "Question", "Content", Arrays.asList("java"));
        
        assertEquals(1, stats.getTotalUsers(), "Should have 1 user");
        assertEquals(1, stats.getTotalQuestions(), "Should have 1 question");
    }

    // ==================== Concurrency Tests ====================

    private void testConcurrentVoting() {
        User author = so.createUser("author", "author@example.com");
        Question question = so.postQuestion(author, "Question", "Content", Arrays.asList("java"));
        
        // Create multiple voters
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int index = i;
            User voter = so.createUser("voter" + i, "voter" + i + "@example.com");
            threads[i] = new Thread(() -> {
                so.voteQuestion(voter, question, VoteType.UPVOTE);
            });
        }
        
        // Start all threads
        for (Thread t : threads) {
            t.start();
        }
        
        // Wait for completion
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        assertEquals(10, question.getVoteCount(), "Should have 10 votes");
    }

    // ==================== Utility Methods ====================

    private void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }

    private void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private void fail(String message) {
        throw new AssertionError(message);
    }

    private void printSummary() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("TEST SUMMARY");
        System.out.println("═══════════════════════════════════════");
        System.out.println("✅ Passed: " + passed);
        System.out.println("❌ Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
        System.out.println("═══════════════════════════════════════");
        
        if (failed == 0) {
            System.out.println("🎉 ALL TESTS PASSED!");
        } else {
            System.out.println("⚠️  SOME TESTS FAILED");
        }
    }
}



