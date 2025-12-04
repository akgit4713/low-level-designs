package musicstreaming.services;

import musicstreaming.models.PlaybackSession;
import java.util.List;

/**
 * Service interface for playback control operations.
 */
public interface PlaybackService {
    
    /**
     * Start playing a song for a user.
     */
    PlaybackSession play(String userId, String songId);
    
    /**
     * Play a playlist from the beginning or a specific index.
     */
    PlaybackSession playPlaylist(String userId, String playlistId, int startIndex);
    
    /**
     * Play an album from the beginning.
     */
    PlaybackSession playAlbum(String userId, String albumId);
    
    /**
     * Pause the current playback.
     */
    void pause(String userId);
    
    /**
     * Resume paused playback.
     */
    void resume(String userId);
    
    /**
     * Stop playback completely.
     */
    void stop(String userId);
    
    /**
     * Skip to the next song in the queue.
     */
    String skipNext(String userId);
    
    /**
     * Skip to the previous song or restart current.
     */
    String skipPrevious(String userId);
    
    /**
     * Seek to a specific position in the current song.
     */
    void seek(String userId, int positionSeconds);
    
    /**
     * Set the volume level.
     */
    void setVolume(String userId, int volume);
    
    /**
     * Toggle shuffle mode.
     */
    void toggleShuffle(String userId);
    
    /**
     * Cycle through repeat modes.
     */
    void cycleRepeatMode(String userId);
    
    /**
     * Add a song to the queue.
     */
    void addToQueue(String userId, String songId);
    
    /**
     * Add a song to play next.
     */
    void addToQueueNext(String userId, String songId);
    
    /**
     * Get the current queue.
     */
    List<String> getQueue(String userId);
    
    /**
     * Get the current playback session for a user.
     */
    PlaybackSession getSession(String userId);
}



