package musicstreaming.services;

import musicstreaming.enums.Genre;
import musicstreaming.models.Artist;
import java.util.List;

/**
 * Service interface for artist operations.
 */
public interface ArtistService {
    
    Artist createArtist(String name, String bio);
    
    Artist getArtist(String artistId);
    
    List<Artist> getAllArtists();
    
    List<Artist> getArtistsByGenre(Genre genre);
    
    List<Artist> getTopArtists(int limit);
    
    List<Artist> getVerifiedArtists();
    
    Artist updateArtist(String artistId, String name, String bio);
    
    void verifyArtist(String artistId);
    
    void addGenreToArtist(String artistId, Genre genre);
    
    void deleteArtist(String artistId);
}



