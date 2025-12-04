package musicstreaming.repositories;

import musicstreaming.enums.PlaylistType;
import musicstreaming.models.Playlist;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Playlist persistence operations.
 */
public interface PlaylistRepository {
    
    Playlist save(Playlist playlist);
    
    Optional<Playlist> findById(String id);
    
    List<Playlist> findAll();
    
    List<Playlist> findByOwnerId(String ownerId);
    
    List<Playlist> findByType(PlaylistType type);
    
    List<Playlist> findPublicPlaylists();
    
    List<Playlist> findByNameContaining(String keyword);
    
    List<Playlist> findTopByFollowerCount(int limit);
    
    void delete(String id);
}



