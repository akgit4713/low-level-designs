package musicstreaming.repositories.impl;

import musicstreaming.enums.PlaylistType;
import musicstreaming.models.Playlist;
import musicstreaming.repositories.PlaylistRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of PlaylistRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryPlaylistRepository implements PlaylistRepository {
    
    private final Map<String, Playlist> playlists = new ConcurrentHashMap<>();

    @Override
    public Playlist save(Playlist playlist) {
        playlists.put(playlist.getId(), playlist);
        return playlist;
    }

    @Override
    public Optional<Playlist> findById(String id) {
        return Optional.ofNullable(playlists.get(id));
    }

    @Override
    public List<Playlist> findAll() {
        return new ArrayList<>(playlists.values());
    }

    @Override
    public List<Playlist> findByOwnerId(String ownerId) {
        return playlists.values().stream()
                .filter(playlist -> playlist.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Playlist> findByType(PlaylistType type) {
        return playlists.values().stream()
                .filter(playlist -> playlist.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Playlist> findPublicPlaylists() {
        return playlists.values().stream()
                .filter(Playlist::isPublic)
                .collect(Collectors.toList());
    }

    @Override
    public List<Playlist> findByNameContaining(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return playlists.values().stream()
                .filter(playlist -> playlist.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    @Override
    public List<Playlist> findTopByFollowerCount(int limit) {
        return playlists.values().stream()
                .sorted((p1, p2) -> Integer.compare(p2.getFollowerCount(), p1.getFollowerCount()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        playlists.remove(id);
    }
}



