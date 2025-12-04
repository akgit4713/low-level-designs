package musicstreaming.models;

import musicstreaming.enums.PlaybackState;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents an active playback session for a user.
 * Manages the current song, queue, and playback state.
 */
public class PlaybackSession {
    private final String id;
    private final String userId;
    private String currentSongId;
    private PlaybackState state;
    private int currentPositionSeconds;
    private final List<String> queue;
    private int currentQueueIndex;
    private boolean shuffleEnabled;
    private RepeatMode repeatMode;
    private int volume; // 0-100
    private final LocalDateTime createdAt;
    private LocalDateTime lastActivityAt;

    public enum RepeatMode {
        OFF,
        REPEAT_ONE,
        REPEAT_ALL
    }

    public PlaybackSession(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.state = PlaybackState.IDLE;
        this.currentPositionSeconds = 0;
        this.queue = new CopyOnWriteArrayList<>();
        this.currentQueueIndex = -1;
        this.shuffleEnabled = false;
        this.repeatMode = RepeatMode.OFF;
        this.volume = 50;
        this.createdAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getCurrentSongId() { return currentSongId; }
    public PlaybackState getState() { return state; }
    public int getCurrentPositionSeconds() { return currentPositionSeconds; }
    public List<String> getQueue() { return Collections.unmodifiableList(queue); }
    public int getCurrentQueueIndex() { return currentQueueIndex; }
    public boolean isShuffleEnabled() { return shuffleEnabled; }
    public RepeatMode getRepeatMode() { return repeatMode; }
    public int getVolume() { return volume; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }

    // Playback control
    public void play(String songId) {
        this.currentSongId = songId;
        this.state = PlaybackState.PLAYING;
        this.currentPositionSeconds = 0;
        updateActivity();
    }

    public void pause() {
        if (this.state == PlaybackState.PLAYING) {
            this.state = PlaybackState.PAUSED;
            updateActivity();
        }
    }

    public void resume() {
        if (this.state == PlaybackState.PAUSED) {
            this.state = PlaybackState.PLAYING;
            updateActivity();
        }
    }

    public void stop() {
        this.state = PlaybackState.STOPPED;
        this.currentPositionSeconds = 0;
        updateActivity();
    }

    public void seek(int positionSeconds) {
        this.currentPositionSeconds = Math.max(0, positionSeconds);
        updateActivity();
    }

    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        updateActivity();
    }

    // Queue management
    public void setQueue(List<String> songIds) {
        this.queue.clear();
        this.queue.addAll(songIds);
        this.currentQueueIndex = songIds.isEmpty() ? -1 : 0;
        updateActivity();
    }

    public void addToQueue(String songId) {
        this.queue.add(songId);
        if (currentQueueIndex == -1) {
            currentQueueIndex = 0;
        }
        updateActivity();
    }

    public void addToQueueNext(String songId) {
        if (currentQueueIndex >= 0 && currentQueueIndex < queue.size()) {
            queue.add(currentQueueIndex + 1, songId);
        } else {
            queue.add(songId);
            if (currentQueueIndex == -1) {
                currentQueueIndex = 0;
            }
        }
        updateActivity();
    }

    public void removeFromQueue(int index) {
        if (index >= 0 && index < queue.size()) {
            queue.remove(index);
            if (index < currentQueueIndex) {
                currentQueueIndex--;
            } else if (index == currentQueueIndex && currentQueueIndex >= queue.size()) {
                currentQueueIndex = queue.isEmpty() ? -1 : queue.size() - 1;
            }
            updateActivity();
        }
    }

    public void clearQueue() {
        queue.clear();
        currentQueueIndex = -1;
        updateActivity();
    }

    public String skipToNext() {
        if (queue.isEmpty()) {
            return null;
        }

        if (repeatMode == RepeatMode.REPEAT_ONE) {
            // Restart current song
            currentPositionSeconds = 0;
            return currentSongId;
        }

        if (currentQueueIndex < queue.size() - 1) {
            currentQueueIndex++;
        } else if (repeatMode == RepeatMode.REPEAT_ALL) {
            currentQueueIndex = 0;
        } else {
            return null;
        }

        currentSongId = queue.get(currentQueueIndex);
        currentPositionSeconds = 0;
        state = PlaybackState.PLAYING;
        updateActivity();
        return currentSongId;
    }

    public String skipToPrevious() {
        if (queue.isEmpty()) {
            return null;
        }

        // If more than 3 seconds into the song, restart it
        if (currentPositionSeconds > 3) {
            currentPositionSeconds = 0;
            return currentSongId;
        }

        if (currentQueueIndex > 0) {
            currentQueueIndex--;
        } else if (repeatMode == RepeatMode.REPEAT_ALL) {
            currentQueueIndex = queue.size() - 1;
        } else {
            currentPositionSeconds = 0;
            return currentSongId;
        }

        currentSongId = queue.get(currentQueueIndex);
        currentPositionSeconds = 0;
        state = PlaybackState.PLAYING;
        updateActivity();
        return currentSongId;
    }

    public void skipToIndex(int index) {
        if (index >= 0 && index < queue.size()) {
            currentQueueIndex = index;
            currentSongId = queue.get(currentQueueIndex);
            currentPositionSeconds = 0;
            state = PlaybackState.PLAYING;
            updateActivity();
        }
    }

    // Shuffle and repeat
    public void toggleShuffle() {
        this.shuffleEnabled = !this.shuffleEnabled;
        if (shuffleEnabled && !queue.isEmpty()) {
            shuffleQueue();
        }
        updateActivity();
    }

    private void shuffleQueue() {
        String currentSong = currentSongId;
        List<String> remaining = new ArrayList<>(queue);
        remaining.remove(currentSong);
        Collections.shuffle(remaining);
        queue.clear();
        if (currentSong != null) {
            queue.add(currentSong);
        }
        queue.addAll(remaining);
        currentQueueIndex = 0;
    }

    public void cycleRepeatMode() {
        switch (repeatMode) {
            case OFF:
                repeatMode = RepeatMode.REPEAT_ALL;
                break;
            case REPEAT_ALL:
                repeatMode = RepeatMode.REPEAT_ONE;
                break;
            case REPEAT_ONE:
                repeatMode = RepeatMode.OFF;
                break;
        }
        updateActivity();
    }

    public void setRepeatMode(RepeatMode mode) {
        this.repeatMode = mode;
        updateActivity();
    }

    private void updateActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }

    public boolean isPlaying() {
        return state == PlaybackState.PLAYING;
    }

    public boolean hasNextTrack() {
        return !queue.isEmpty() && 
               (currentQueueIndex < queue.size() - 1 || repeatMode == RepeatMode.REPEAT_ALL);
    }

    public boolean hasPreviousTrack() {
        return !queue.isEmpty() && 
               (currentQueueIndex > 0 || repeatMode == RepeatMode.REPEAT_ALL);
    }

    @Override
    public String toString() {
        return String.format("PlaybackSession{id='%s', userId='%s', song='%s', state=%s, pos=%ds}",
                id, userId, currentSongId, state, currentPositionSeconds);
    }
}



