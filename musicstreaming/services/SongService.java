package musicstreaming.services;

import musicstreaming.enums.Genre;
import musicstreaming.models.Song;
import java.util.List;

/**
 * Service interface for song operations.
 */
public interface SongService {
    
    Song createSong(String title, String artistId, int durationSeconds, Genre genre);
    
    Song getSong(String songId);
    
    List<Song> getAllSongs();
    
    List<Song> getSongsByArtist(String artistId);
    
    List<Song> getSongsByAlbum(String albumId);
    
    List<Song> getSongsByGenre(Genre genre);
    
    List<Song> getTopSongs(int limit);
    
    Song updateSong(String songId, String title, Genre genre);
    
    void deleteSong(String songId);
}



