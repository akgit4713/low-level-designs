package musicstreaming.strategies.search;

import musicstreaming.enums.Genre;
import musicstreaming.models.Song;
import musicstreaming.repositories.SongRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy for finding songs by genre.
 */
public class GenreSearchStrategy implements SearchStrategy<Song> {

    private final SongRepository songRepository;

    public GenreSearchStrategy(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public List<Song> search(String query, int limit) {
        try {
            Genre genre = Genre.valueOf(query.toUpperCase().replace(" ", "_"));
            return songRepository.findByGenre(genre).stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public String getSearchType() {
        return "GENRE";
    }
}



