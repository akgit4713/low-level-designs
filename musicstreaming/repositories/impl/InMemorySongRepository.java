package musicstreaming.repositories.impl;

import musicstreaming.enums.Genre;
import musicstreaming.models.Song;
import musicstreaming.repositories.SongRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of SongRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemorySongRepository implements SongRepository {
    
    private final Map<String, Song> songs = new ConcurrentHashMap<>();

    @Override
    public Song save(Song song) {
        songs.put(song.getId(), song);
        return song;
    }

    @Override
    public Optional<Song> findById(String id) {
        return Optional.ofNullable(songs.get(id));
    }

    @Override
    public List<Song> findAll() {
        return new ArrayList<>(songs.values());
    }

    @Override
    public List<Song> findByArtistId(String artistId) {
        return songs.values().stream()
                .filter(song -> song.getArtistId().equals(artistId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findByAlbumId(String albumId) {
        return songs.values().stream()
                .filter(song -> albumId.equals(song.getAlbumId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findByGenre(Genre genre) {
        return songs.values().stream()
                .filter(song -> genre.equals(song.getGenre()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findByTitleContaining(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return songs.values().stream()
                .filter(song -> song.getTitle().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findTopByPlayCount(int limit) {
        return songs.values().stream()
                .sorted((s1, s2) -> Integer.compare(s2.getPlayCount(), s1.getPlayCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        songs.remove(id);
    }
}



