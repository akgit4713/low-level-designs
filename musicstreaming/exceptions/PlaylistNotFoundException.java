package musicstreaming.exceptions;

/**
 * Exception thrown when a requested playlist is not found.
 */
public class PlaylistNotFoundException extends MusicStreamingException {
    
    public PlaylistNotFoundException(String playlistId) {
        super("Playlist not found with ID: " + playlistId);
    }
}



