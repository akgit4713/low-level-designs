package musicstreaming.observers;

import musicstreaming.models.Artist;
import musicstreaming.models.Song;
import musicstreaming.models.User;
import musicstreaming.repositories.ArtistRepository;

/**
 * Observer that updates artist statistics on playback events.
 */
public class ArtistStatsObserver implements PlaybackObserver {

    private final ArtistRepository artistRepository;

    public ArtistStatsObserver(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public void onSongStarted(User user, Song song) {
        artistRepository.findById(song.getArtistId())
                .ifPresent(Artist::incrementMonthlyListeners);
    }

    @Override
    public void onSongCompleted(User user, Song song) {
        // Could update additional artist stats
    }

    @Override
    public void onSongPaused(User user, Song song, int positionSeconds) {
        // No action needed
    }

    @Override
    public void onSongSkipped(User user, Song song, int positionSeconds) {
        // No action needed
    }
}



