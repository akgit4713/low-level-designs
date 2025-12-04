package cricinfo.observers;

import cricinfo.models.Ball;
import cricinfo.models.Match;

import java.util.ArrayList;
import java.util.List;

/**
 * WebSocket-based observer that would push updates to connected clients.
 * This is a mock implementation for demonstration purposes.
 */
public class WebSocketScoreObserver implements MatchObserver {
    
    private final List<String> messageQueue = new ArrayList<>();
    private boolean connected = false;
    
    public void connect() {
        this.connected = true;
        System.out.println("[WebSocket] Connected to live score feed");
    }
    
    public void disconnect() {
        this.connected = false;
        System.out.println("[WebSocket] Disconnected from live score feed");
    }
    
    @Override
    public void onMatchStart(Match match) {
        pushMessage(createMessage("MATCH_START", match.getId(), match.getTitle()));
    }
    
    @Override
    public void onBallBowled(Match match, Ball ball) {
        String data = String.format("{\"ball\":\"%s\",\"runs\":%d,\"bowler\":\"%s\",\"batsman\":\"%s\"}",
                ball.getBallNotation(), 
                ball.getTotalRuns(),
                ball.getBowler().getName(),
                ball.getBatsman().getName());
        pushMessage(createMessage("BALL", match.getId(), data));
    }
    
    @Override
    public void onWicket(Match match, Ball ball) {
        String data = String.format("{\"dismissal\":\"%s\",\"player\":\"%s\",\"score\":\"%s\"}",
                ball.getDismissalType().getDisplayName(),
                ball.getDismissedPlayer().getName(),
                match.getLiveScore());
        pushMessage(createMessage("WICKET", match.getId(), data));
    }
    
    @Override
    public void onInningsEnd(Match match, int inningsNumber) {
        String data = String.format("{\"innings\":%d,\"score\":\"%s\"}",
                inningsNumber, match.getLiveScore());
        pushMessage(createMessage("INNINGS_END", match.getId(), data));
    }
    
    @Override
    public void onMatchEnd(Match match) {
        String data = String.format("{\"result\":\"%s\",\"winner\":\"%s\"}",
                match.getResultDescription(),
                match.getWinner() != null ? match.getWinner().getName() : "N/A");
        pushMessage(createMessage("MATCH_END", match.getId(), data));
    }
    
    @Override
    public void onScoreUpdate(Match match, String score) {
        pushMessage(createMessage("SCORE_UPDATE", match.getId(), score));
    }
    
    private String createMessage(String type, String matchId, String data) {
        return String.format("{\"type\":\"%s\",\"matchId\":\"%s\",\"data\":\"%s\",\"timestamp\":%d}",
                type, matchId, data, System.currentTimeMillis());
    }
    
    private void pushMessage(String message) {
        if (connected) {
            messageQueue.add(message);
            // In real implementation, this would push to WebSocket
            System.out.println("[WebSocket] Push: " + message);
        }
    }
    
    public List<String> getMessageQueue() {
        return new ArrayList<>(messageQueue);
    }
    
    public void clearQueue() {
        messageQueue.clear();
    }
    
    public boolean isConnected() {
        return connected;
    }
}



