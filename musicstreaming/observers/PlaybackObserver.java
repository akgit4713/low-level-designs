package musicstreaming.observers;

import musicstreaming.models.Song;
import musicstreaming.models.User;

/**
 * Observer interface for playback events.
 * Implements the Observer pattern for loose coupling.
 */
public interface PlaybackObserver {
    
    void onSongStarted(User user, Song song);
    
    void onSongCompleted(User user, Song song);
    
    void onSongPaused(User user, Song song, int positionSeconds);
    
    void onSongSkipped(User user, Song song, int positionSeconds);
}



