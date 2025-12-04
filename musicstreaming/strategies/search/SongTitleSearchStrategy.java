package musicstreaming.strategies.search;

import musicstreaming.models.Song;
import musicstreaming.repositories.SongRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy for finding songs by title.
 */
public class SongTitleSearchStrategy implements SearchStrategy<Song> {

    private final SongRepository songRepository;

    public SongTitleSearchStrategy(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public List<Song> search(String query, int limit) {
        return songRepository.findByTitleContaining(query).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public String getSearchType() {
        return "SONG_TITLE";
    }
}



