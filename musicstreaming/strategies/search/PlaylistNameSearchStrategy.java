package musicstreaming.strategies.search;

import musicstreaming.models.Playlist;
import musicstreaming.repositories.PlaylistRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy for finding playlists by name.
 */
public class PlaylistNameSearchStrategy implements SearchStrategy<Playlist> {

    private final PlaylistRepository playlistRepository;

    public PlaylistNameSearchStrategy(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @Override
    public List<Playlist> search(String query, int limit) {
        return playlistRepository.findByNameContaining(query).stream()
                .filter(Playlist::isPublic) // Only return public playlists in search
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public String getSearchType() {
        return "PLAYLIST_NAME";
    }
}



