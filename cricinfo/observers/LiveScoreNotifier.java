package cricinfo.observers;

import cricinfo.models.Ball;
import cricinfo.models.Match;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of MatchSubject that manages observers and notifies them
 * of match events. Thread-safe implementation using CopyOnWriteArrayList.
 */
public class LiveScoreNotifier implements MatchSubject {
    
    private final List<MatchObserver> observers = new CopyOnWriteArrayList<>();
    
    @Override
    public void registerObserver(MatchObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    @Override
    public void removeObserver(MatchObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyMatchStart(Match match) {
        for (MatchObserver observer : observers) {
            try {
                observer.onMatchStart(match);
            } catch (Exception e) {
                // Log and continue to next observer
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void notifyBallBowled(Match match, Ball ball) {
        for (MatchObserver observer : observers) {
            try {
                observer.onBallBowled(match, ball);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void notifyWicket(Match match, Ball ball) {
        for (MatchObserver observer : observers) {
            try {
                observer.onWicket(match, ball);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void notifyInningsEnd(Match match, int inningsNumber) {
        for (MatchObserver observer : observers) {
            try {
                observer.onInningsEnd(match, inningsNumber);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void notifyMatchEnd(Match match) {
        for (MatchObserver observer : observers) {
            try {
                observer.onMatchEnd(match);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void notifyScoreUpdate(Match match, String score) {
        for (MatchObserver observer : observers) {
            try {
                observer.onScoreUpdate(match, score);
            } catch (Exception e) {
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }
    
    public int getObserverCount() {
        return observers.size();
    }
    
    public void clearObservers() {
        observers.clear();
    }
}



