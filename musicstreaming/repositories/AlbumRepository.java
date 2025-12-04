package musicstreaming.repositories;

import musicstreaming.enums.Genre;
import musicstreaming.models.Album;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Album persistence operations.
 */
public interface AlbumRepository {
    
    Album save(Album album);
    
    Optional<Album> findById(String id);
    
    List<Album> findAll();
    
    List<Album> findByArtistId(String artistId);
    
    List<Album> findByTitleContaining(String keyword);
    
    List<Album> findByGenre(Genre genre);
    
    void delete(String id);
}



