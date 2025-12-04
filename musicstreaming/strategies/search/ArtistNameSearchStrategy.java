package musicstreaming.strategies.search;

import musicstreaming.models.Artist;
import musicstreaming.repositories.ArtistRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy for finding artists by name.
 */
public class ArtistNameSearchStrategy implements SearchStrategy<Artist> {

    private final ArtistRepository artistRepository;

    public ArtistNameSearchStrategy(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public List<Artist> search(String query, int limit) {
        return artistRepository.findByNameContaining(query).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public String getSearchType() {
        return "ARTIST_NAME";
    }
}



