package musicstreaming.services;

import musicstreaming.models.Playlist;
import java.util.List;

/**
 * Service interface for playlist operations.
 */
public interface PlaylistService {
    
    Playlist createPlaylist(String name, String ownerId, String description, boolean isPublic);
    
    Playlist getPlaylist(String playlistId);
    
    List<Playlist> getUserPlaylists(String userId);
    
    List<Playlist> getPublicPlaylists();
    
    Playlist updatePlaylist(String playlistId, String name, String description, boolean isPublic);
    
    void addSongToPlaylist(String playlistId, String songId, String addedBy);
    
    void removeSongFromPlaylist(String playlistId, String songId);
    
    void reorderPlaylistTrack(String playlistId, int fromIndex, int toIndex);
    
    void deletePlaylist(String playlistId);
    
    void followPlaylist(String playlistId, String userId);
    
    void unfollowPlaylist(String playlistId, String userId);
}



