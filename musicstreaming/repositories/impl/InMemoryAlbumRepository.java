package musicstreaming.repositories.impl;

import musicstreaming.enums.Genre;
import musicstreaming.models.Album;
import musicstreaming.repositories.AlbumRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of AlbumRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryAlbumRepository implements AlbumRepository {
    
    private final Map<String, Album> albums = new ConcurrentHashMap<>();

    @Override
    public Album save(Album album) {
        albums.put(album.getId(), album);
        return album;
    }

    @Override
    public Optional<Album> findById(String id) {
        return Optional.ofNullable(albums.get(id));
    }

    @Override
    public List<Album> findAll() {
        return new ArrayList<>(albums.values());
    }

    @Override
    public List<Album> findByArtistId(String artistId) {
        return albums.values().stream()
                .filter(album -> album.getArtistId().equals(artistId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Album> findByTitleContaining(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return albums.values().stream()
                .filter(album -> album.getTitle().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<Album> findByGenre(Genre genre) {
        return albums.values().stream()
                .filter(album -> genre.equals(album.getGenre()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        albums.remove(id);
    }
}



