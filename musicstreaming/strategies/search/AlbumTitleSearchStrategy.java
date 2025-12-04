package musicstreaming.strategies.search;

import musicstreaming.models.Album;
import musicstreaming.repositories.AlbumRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy for finding albums by title.
 */
public class AlbumTitleSearchStrategy implements SearchStrategy<Album> {

    private final AlbumRepository albumRepository;

    public AlbumTitleSearchStrategy(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public List<Album> search(String query, int limit) {
        return albumRepository.findByTitleContaining(query).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public String getSearchType() {
        return "ALBUM_TITLE";
    }
}



