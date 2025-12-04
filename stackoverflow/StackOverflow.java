package stackoverflow;

import stackoverflow.enums.VoteType;
import stackoverflow.models.*;
import stackoverflow.observers.StackOverflowObserver;
import stackoverflow.repositories.*;
import stackoverflow.repositories.impl.*;
import stackoverflow.services.*;
import stackoverflow.strategies.ReputationStrategy;
import stackoverflow.strategies.StandardReputationStrategy;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Main facade class managing the Stack Overflow system.
 * Singleton pattern ensures single instance.
 * Coordinates all services and handles observer notifications.
 */
public class StackOverflow {
    private static volatile StackOverflow instance;
    
    // Services
    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final VoteService voteService;
    private final SearchService searchService;
    private final CommentService commentService;
    
    // Observers
    private final List<StackOverflowObserver> observers;

    private StackOverflow() {
        this(new StandardReputationStrategy());
    }

    private StackOverflow(ReputationStrategy reputationStrategy) {
        // Initialize repositories
        UserRepository userRepository = new InMemoryUserRepository();
        QuestionRepository questionRepository = new InMemoryQuestionRepository();
        AnswerRepository answerRepository = new InMemoryAnswerRepository();
        TagRepository tagRepository = new InMemoryTagRepository();

        // Initialize services with dependency injection
        this.userService = new UserService(userRepository);
        this.questionService = new QuestionService(questionRepository, tagRepository);
        this.answerService = new AnswerService(answerRepository);
        this.voteService = new VoteService(reputationStrategy);
        this.searchService = new SearchService(questionRepository, userRepository);
        this.commentService = new CommentService();
        
        // Thread-safe observer list
        this.observers = new CopyOnWriteArrayList<>();
    }

    public static StackOverflow getInstance() {
        if (instance == null) {
            synchronized (StackOverflow.class) {
                if (instance == null) {
                    instance = new StackOverflow();
                }
            }
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        instance = null;
    }

    // Observer Management
    public void addObserver(StackOverflowObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(StackOverflowObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(java.util.function.Consumer<StackOverflowObserver> action) {
        observers.forEach(action);
    }

    // ==================== User Operations ====================

    public User createUser(String username, String email) {
        User user = userService.createUser(username, email);
        notifyObservers(o -> o.onUserRegistered(user));
        System.out.println("✓ User created: " + user.getUsername());
        return user;
    }

    public User getUser(String userId) {
        return userService.getUserById(userId);
    }

    public Optional<User> findUserByUsername(String username) {
        return userService.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public List<User> getTopUsers(int limit) {
        return userService.getTopUsersByReputation(limit);
    }

    // ==================== Question Operations ====================

    public Question postQuestion(User author, String title, String content, List<String> tagNames) {
        Question question = questionService.createQuestion(author, title, content, tagNames);
        notifyObservers(o -> o.onQuestionPosted(question));
        System.out.println("✓ Question posted: " + question.getTitle());
        return question;
    }

    public Question getQuestion(String questionId) {
        return questionService.getQuestionById(questionId);
    }

    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    // ==================== Answer Operations ====================

    public Answer postAnswer(User author, Question question, String content) {
        Answer answer = answerService.createAnswer(author, question, content);
        notifyObservers(o -> o.onAnswerPosted(answer));
        System.out.println("✓ Answer posted to: " + question.getTitle());
        return answer;
    }

    public void acceptAnswer(User questionAuthor, Answer answer) {
        answerService.acceptAnswer(questionAuthor, answer);
        voteService.applyAcceptedAnswerReputation(answer);
        notifyObservers(o -> o.onAnswerAccepted(answer));
        System.out.println("✓ Answer accepted!");
    }

    // ==================== Comment Operations ====================

    public Comment addComment(User author, Commentable target, String content) {
        Comment comment = commentService.addComment(author, target, content);
        notifyObservers(o -> o.onCommentAdded(comment, target));
        System.out.println("✓ Comment added by " + author.getUsername());
        return comment;
    }

    // ==================== Voting Operations ====================

    public void voteQuestion(User voter, Question question, VoteType voteType) {
        int oldRep = question.getAuthor().getReputation();
        voteService.voteOnQuestion(voter, question, voteType);
        int newRep = question.getAuthor().getReputation();
        
        notifyObservers(o -> o.onQuestionVoted(question, voter));
        notifyObservers(o -> o.onReputationChanged(question.getAuthor(), oldRep, newRep));
        System.out.println("✓ " + voteType + " on question: " + question.getTitle());
    }

    public void voteAnswer(User voter, Answer answer, VoteType voteType) {
        int oldRep = answer.getAuthor().getReputation();
        voteService.voteOnAnswer(voter, answer, voteType);
        int newRep = answer.getAuthor().getReputation();
        
        notifyObservers(o -> o.onAnswerVoted(answer, voter));
        notifyObservers(o -> o.onReputationChanged(answer.getAuthor(), oldRep, newRep));
        System.out.println("✓ " + voteType + " on answer by: " + answer.getAuthor().getUsername());
    }

    // ==================== Search Operations ====================

    public List<Question> searchQuestions(String keyword) {
        return searchService.searchByKeyword(keyword);
    }

    public List<Question> getQuestionsByTag(String tagName) {
        return searchService.searchByTag(tagName);
    }

    public List<Question> getQuestionsByTags(List<String> tagNames) {
        return searchService.searchByTags(tagNames);
    }

    public List<Question> getQuestionsByUser(User user) {
        return searchService.searchByUser(user);
    }

    public List<Question> getQuestionsByUsername(String username) {
        return searchService.searchByAuthor(username);
    }

    public List<Question> advancedSearch(String keyword, List<String> tags, String username) {
        return searchService.advancedSearch(keyword, tags, username);
    }

    public List<Question> getTrendingQuestions(int limit) {
        return searchService.getTrendingQuestions(limit);
    }

    public List<Question> getUnansweredQuestions() {
        return searchService.getUnansweredQuestions();
    }

    // ==================== Statistics ====================

    public void displayStats() {
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("        STACK OVERFLOW STATS");
        System.out.println("═══════════════════════════════════════");
        System.out.println("Total Users: " + userService.getAllUsers().size());
        System.out.println("Total Questions: " + questionService.getQuestionCount());
        System.out.println("Total Answers: " + answerService.getAnswerCount());
        System.out.println("Reputation Strategy: " + voteService.getReputationStrategy().getName());
        
        System.out.println("\nTop Users by Reputation:");
        userService.getTopUsersByReputation(5)
            .forEach(u -> System.out.println("  " + u));
        System.out.println("═══════════════════════════════════════\n");
    }

    // ==================== Getters for Services (for testing) ====================

    public UserService getUserService() { return userService; }
    public QuestionService getQuestionService() { return questionService; }
    public AnswerService getAnswerService() { return answerService; }
    public VoteService getVoteService() { return voteService; }
    public SearchService getSearchService() { return searchService; }
}
