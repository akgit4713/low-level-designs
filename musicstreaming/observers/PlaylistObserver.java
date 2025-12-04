package musicstreaming.observers;

import musicstreaming.models.Playlist;
import musicstreaming.models.Song;

/**
 * Observer interface for playlist events.
 */
public interface PlaylistObserver {
    
    void onPlaylistCreated(Playlist playlist);
    
    void onSongAddedToPlaylist(Playlist playlist, Song song);
    
    void onSongRemovedFromPlaylist(Playlist playlist, Song song);
    
    void onPlaylistDeleted(Playlist playlist);
}



