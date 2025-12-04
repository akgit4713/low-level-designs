package musicstreaming.repositories.impl;

import musicstreaming.enums.Genre;
import musicstreaming.models.Artist;
import musicstreaming.repositories.ArtistRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of ArtistRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryArtistRepository implements ArtistRepository {
    
    private final Map<String, Artist> artists = new ConcurrentHashMap<>();

    @Override
    public Artist save(Artist artist) {
        artists.put(artist.getId(), artist);
        return artist;
    }

    @Override
    public Optional<Artist> findById(String id) {
        return Optional.ofNullable(artists.get(id));
    }

    @Override
    public List<Artist> findAll() {
        return new ArrayList<>(artists.values());
    }

    @Override
    public List<Artist> findByNameContaining(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return artists.values().stream()
                .filter(artist -> artist.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<Artist> findByGenre(Genre genre) {
        return artists.values().stream()
                .filter(artist -> artist.getGenres().contains(genre))
                .collect(Collectors.toList());
    }

    @Override
    public List<Artist> findVerifiedArtists() {
        return artists.values().stream()
                .filter(Artist::isVerified)
                .collect(Collectors.toList());
    }

    @Override
    public List<Artist> findTopByMonthlyListeners(int limit) {
        return artists.values().stream()
                .sorted((a1, a2) -> Integer.compare(a2.getMonthlyListeners(), a1.getMonthlyListeners()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        artists.remove(id);
    }
}



