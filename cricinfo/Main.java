package cricinfo;

import cricinfo.enums.*;
import cricinfo.models.*;
import cricinfo.observers.ConsoleScoreObserver;
import cricinfo.observers.WebSocketScoreObserver;

import java.time.LocalDateTime;

/**
 * Main class demonstrating the CricInfo system functionality.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=" .repeat(60));
        System.out.println("         CRICINFO - Cricket Information System");
        System.out.println("=".repeat(60));
        System.out.println();
        
        // Get CricInfo instance
        CricInfo cricInfo = CricInfo.getInstance();
        
        // Setup observers for live score updates
        ConsoleScoreObserver consoleObserver = new ConsoleScoreObserver("ConsoleClient");
        WebSocketScoreObserver webSocketObserver = new WebSocketScoreObserver();
        webSocketObserver.connect();
        
        cricInfo.subscribeToLiveScores(consoleObserver);
        cricInfo.subscribeToLiveScores(webSocketObserver);
        
        System.out.println("Observers registered: " + cricInfo.getSubscriberCount());
        System.out.println();
        
        // Create teams
        System.out.println("--- Creating Teams ---");
        Team india = cricInfo.createTeam("India", "India");
        india.setShortName("IND");
        Team australia = cricInfo.createTeam("Australia", "Australia");
        australia.setShortName("AUS");
        
        // Create players for India
        Player kohli = cricInfo.createPlayer("Virat Kohli", "India");
        kohli.setRole(PlayerRole.BATSMAN);
        kohli.setBattingStyle(BattingStyle.RIGHT_HANDED);
        
        Player rohit = cricInfo.createPlayer("Rohit Sharma", "India");
        rohit.setRole(PlayerRole.BATSMAN);
        rohit.setBattingStyle(BattingStyle.RIGHT_HANDED);
        
        Player bumrah = cricInfo.createPlayer("Jasprit Bumrah", "India");
        bumrah.setRole(PlayerRole.BOWLER);
        bumrah.setBowlingStyle(BowlingStyle.RIGHT_ARM_FAST);
        
        Player pandya = cricInfo.createPlayer("Hardik Pandya", "India");
        pandya.setRole(PlayerRole.ALL_ROUNDER);
        
        // Create players for Australia
        Player smith = cricInfo.createPlayer("Steve Smith", "Australia");
        smith.setRole(PlayerRole.BATSMAN);
        smith.setBattingStyle(BattingStyle.RIGHT_HANDED);
        
        Player warner = cricInfo.createPlayer("David Warner", "Australia");
        warner.setRole(PlayerRole.BATSMAN);
        warner.setBattingStyle(BattingStyle.LEFT_HANDED);
        
        Player cummins = cricInfo.createPlayer("Pat Cummins", "Australia");
        cummins.setRole(PlayerRole.BOWLER);
        cummins.setBowlingStyle(BowlingStyle.RIGHT_ARM_FAST);
        
        Player starc = cricInfo.createPlayer("Mitchell Starc", "Australia");
        starc.setRole(PlayerRole.BOWLER);
        starc.setBowlingStyle(BowlingStyle.LEFT_ARM_FAST);
        
        // Add players to teams
        cricInfo.addPlayerToTeam(india.getId(), rohit);
        cricInfo.addPlayerToTeam(india.getId(), kohli);
        cricInfo.addPlayerToTeam(india.getId(), bumrah);
        cricInfo.addPlayerToTeam(india.getId(), pandya);
        
        cricInfo.addPlayerToTeam(australia.getId(), warner);
        cricInfo.addPlayerToTeam(australia.getId(), smith);
        cricInfo.addPlayerToTeam(australia.getId(), cummins);
        cricInfo.addPlayerToTeam(australia.getId(), starc);
        
        System.out.println("Created team: " + india);
        System.out.println("Created team: " + australia);
        System.out.println();
        
        // Create venue
        Venue venue = new Venue("Melbourne Cricket Ground", "Melbourne", "Australia");
        venue.setCapacity(100000);
        
        // Create a T20 match
        System.out.println("--- Creating Match ---");
        Match match = cricInfo.createMatch(india, australia, MatchFormat.T20,
                venue, LocalDateTime.now(), "Border-Gavaskar Trophy T20");
        
        System.out.println("Match created: " + match);
        System.out.println();
        
        // Start the match
        System.out.println("--- Starting Match ---");
        cricInfo.startMatch(match.getId());
        
        // Set toss
        match.setToss(india, "bat first");
        System.out.println("Toss: " + india.getName() + " won and elected to " + match.getTossDecision());
        System.out.println();
        
        // Start first innings
        System.out.println("--- First Innings ---");
        Innings firstInnings = cricInfo.startInnings(match, india, australia, rohit, kohli);
        
        // Simulate some balls
        simulateOver(cricInfo, match, cummins, rohit, kohli, new int[]{1, 4, 0, 2, 6, 1});
        simulateOver(cricInfo, match, starc, kohli, rohit, new int[]{0, 0, 4, 1, 2, 0});
        
        // Wicket!
        Ball wicketBall = new Ball(3, 1, cummins, rohit, kohli);
        wicketBall.setRuns(0);
        wicketBall.setWicket(true);
        wicketBall.setDismissalType(DismissalType.CAUGHT);
        wicketBall.setDismissedPlayer(rohit);
        wicketBall.setFielder(smith);
        cricInfo.recordBall(match, wicketBall);
        
        // Send new batsman
        cricInfo.sendNewBatsman(match, pandya);
        
        // More balls
        simulateBalls(cricInfo, match, cummins, pandya, kohli, new int[]{1, 2, 4, 0, 1});
        
        System.out.println();
        System.out.println("--- Live Score ---");
        System.out.println(cricInfo.getLiveScore(match));
        System.out.println("Projected Score: " + cricInfo.getProjectedScore(match));
        System.out.println();
        
        // End first innings (for demo purposes)
        cricInfo.endInnings(match);
        
        // Start second innings
        System.out.println("--- Second Innings ---");
        Innings secondInnings = cricInfo.startInnings(match, australia, india, warner, smith);
        
        // Simulate some balls
        simulateOver(cricInfo, match, bumrah, warner, smith, new int[]{0, 1, 0, 0, 2, 0});
        simulateOver(cricInfo, match, pandya, smith, warner, new int[]{4, 1, 6, 0, 1, 2});
        
        System.out.println();
        System.out.println("--- Live Score ---");
        System.out.println(cricInfo.getLiveScore(match));
        System.out.println("Required Run Rate: " + String.format("%.2f", cricInfo.getRequiredRunRate(match)));
        System.out.println("Win Probability (Batting): " + String.format("%.1f%%", cricInfo.getWinProbability(match) * 100));
        System.out.println();
        
        // Get scorecard
        System.out.println("--- Full Scorecard ---");
        Scorecard scorecard = cricInfo.getScorecard(match.getId());
        System.out.println(scorecard);
        
        // Search functionality demo
        System.out.println("--- Search Demo ---");
        System.out.println("Searching for 'kohli':");
        cricInfo.searchPlayers("kohli").forEach(p -> System.out.println("  Found: " + p));
        
        System.out.println("Searching for 'India':");
        cricInfo.searchTeams("India").forEach(t -> System.out.println("  Found: " + t));
        
        System.out.println("Searching for 'T20':");
        cricInfo.searchMatches("T20").forEach(m -> System.out.println("  Found: " + m.getTitle()));
        
        System.out.println();
        
        // Get commentary
        System.out.println("--- Recent Commentary (last 5 entries) ---");
        cricInfo.getCommentary(match.getId()).stream()
                .limit(5)
                .forEach(System.out::println);
        
        // Cleanup
        cricInfo.unsubscribeFromLiveScores(consoleObserver);
        cricInfo.unsubscribeFromLiveScores(webSocketObserver);
        webSocketObserver.disconnect();
        
        System.out.println();
        System.out.println("=".repeat(60));
        System.out.println("         Demo Complete!");
        System.out.println("=".repeat(60));
    }
    
    private static void simulateOver(CricInfo cricInfo, Match match, Player bowler,
                                     Player striker, Player nonStriker, int[] runs) {
        cricInfo.changeBowler(match, bowler);
        for (int i = 0; i < runs.length && i < 6; i++) {
            Ball ball = new Ball(match.getCurrentInnings().getOvers().size(), i + 1,
                    bowler, striker, nonStriker);
            ball.setRuns(runs[i]);
            cricInfo.recordBall(match, ball);
            
            // Rotate strike on odd runs
            if (runs[i] % 2 == 1) {
                Player temp = striker;
                striker = nonStriker;
                nonStriker = temp;
            }
        }
    }
    
    private static void simulateBalls(CricInfo cricInfo, Match match, Player bowler,
                                      Player striker, Player nonStriker, int[] runs) {
        Innings innings = match.getCurrentInnings();
        int currentBall = innings.getCurrentOver() != null ? 
                innings.getCurrentOver().getLegalBallCount() : 0;
        
        for (int i = 0; i < runs.length; i++) {
            Ball ball = new Ball(innings.getOvers().size(), currentBall + i + 1,
                    bowler, striker, nonStriker);
            ball.setRuns(runs[i]);
            cricInfo.recordBall(match, ball);
            
            if (runs[i] % 2 == 1) {
                Player temp = striker;
                striker = nonStriker;
                nonStriker = temp;
            }
        }
    }
}



