package musicstreaming.repositories;

import musicstreaming.enums.Genre;
import musicstreaming.models.Song;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Song persistence operations.
 */
public interface SongRepository {
    
    Song save(Song song);
    
    Optional<Song> findById(String id);
    
    List<Song> findAll();
    
    List<Song> findByArtistId(String artistId);
    
    List<Song> findByAlbumId(String albumId);
    
    List<Song> findByGenre(Genre genre);
    
    List<Song> findByTitleContaining(String keyword);
    
    List<Song> findTopByPlayCount(int limit);
    
    void delete(String id);
}



