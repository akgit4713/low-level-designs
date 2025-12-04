package musicstreaming.repositories;

import musicstreaming.enums.Genre;
import musicstreaming.models.Artist;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Artist persistence operations.
 */
public interface ArtistRepository {
    
    Artist save(Artist artist);
    
    Optional<Artist> findById(String id);
    
    List<Artist> findAll();
    
    List<Artist> findByNameContaining(String keyword);
    
    List<Artist> findByGenre(Genre genre);
    
    List<Artist> findVerifiedArtists();
    
    List<Artist> findTopByMonthlyListeners(int limit);
    
    void delete(String id);
}



