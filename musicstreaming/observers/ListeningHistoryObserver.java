package musicstreaming.observers;

import musicstreaming.models.Song;
import musicstreaming.models.User;

/**
 * Observer that tracks listening history for recommendations.
 * Implements PlaybackObserver to receive playback events.
 */
public class ListeningHistoryObserver implements PlaybackObserver {

    @Override
    public void onSongStarted(User user, Song song) {
        // Record that user started listening to this song
        user.addToListeningHistory(song.getId());
    }

    @Override
    public void onSongCompleted(User user, Song song) {
        // Song completed - could update listening stats
        song.incrementPlayCount();
    }

    @Override
    public void onSongPaused(User user, Song song, int positionSeconds) {
        // Could track pausing patterns
    }

    @Override
    public void onSongSkipped(User user, Song song, int positionSeconds) {
        // Track skips for recommendation engine
        // Songs frequently skipped might be less relevant
    }
}



