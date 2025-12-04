package musicstreaming.exceptions;

/**
 * Exception thrown when a requested song is not found.
 */
public class SongNotFoundException extends MusicStreamingException {
    
    public SongNotFoundException(String songId) {
        super("Song not found with ID: " + songId);
    }
}



