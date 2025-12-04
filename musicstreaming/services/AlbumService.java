package musicstreaming.services;

import musicstreaming.enums.Genre;
import musicstreaming.models.Album;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for album operations.
 */
public interface AlbumService {
    
    Album createAlbum(String title, String artistId, Genre genre, LocalDate releaseDate);
    
    Album getAlbum(String albumId);
    
    List<Album> getAllAlbums();
    
    List<Album> getAlbumsByArtist(String artistId);
    
    List<Album> getAlbumsByGenre(Genre genre);
    
    void addSongToAlbum(String albumId, String songId);
    
    void removeSongFromAlbum(String albumId, String songId);
    
    Album updateAlbum(String albumId, String title, String coverImageUrl);
    
    void deleteAlbum(String albumId);
}



