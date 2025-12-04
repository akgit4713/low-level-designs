package musicstreaming.enums;

/**
 * Represents the current state of playback for a user session.
 */
public enum PlaybackState {
    IDLE,       // No song loaded
    PLAYING,    // Song is actively playing
    PAUSED,     // Song is paused
    BUFFERING,  // Song is loading/buffering
    STOPPED     // Playback stopped completely
}



