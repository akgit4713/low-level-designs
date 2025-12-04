package musicstreaming.services.impl;

import musicstreaming.enums.PlaylistType;
import musicstreaming.exceptions.PlaylistNotFoundException;
import musicstreaming.exceptions.UnauthorizedException;
import musicstreaming.exceptions.UserNotFoundException;
import musicstreaming.models.Playlist;
import musicstreaming.models.Song;
import musicstreaming.models.User;
import musicstreaming.observers.PlaylistObserver;
import musicstreaming.repositories.PlaylistRepository;
import musicstreaming.repositories.SongRepository;
import musicstreaming.repositories.UserRepository;
import musicstreaming.services.PlaylistService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of PlaylistService with observer support.
 */
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final List<PlaylistObserver> observers = new ArrayList<>();

    public PlaylistServiceImpl(PlaylistRepository playlistRepository,
                              UserRepository userRepository,
                              SongRepository songRepository) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    public void addObserver(PlaylistObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(PlaylistObserver observer) {
        observers.remove(observer);
    }

    @Override
    public Playlist createPlaylist(String name, String ownerId, String description, boolean isPublic) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Playlist name cannot be empty");
        }

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UserNotFoundException(ownerId));

        String playlistId = UUID.randomUUID().toString();
        Playlist playlist = new Playlist.Builder(playlistId, name, ownerId)
                .description(description)
                .isPublic(isPublic)
                .type(PlaylistType.USER_CREATED)
                .build();

        Playlist savedPlaylist = playlistRepository.save(playlist);
        owner.addPlaylist(playlistId);
        userRepository.save(owner);

        notifyPlaylistCreated(savedPlaylist);
        return savedPlaylist;
    }

    @Override
    public Playlist getPlaylist(String playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistNotFoundException(playlistId));
    }

    @Override
    public List<Playlist> getUserPlaylists(String userId) {
        return playlistRepository.findByOwnerId(userId);
    }

    @Override
    public List<Playlist> getPublicPlaylists() {
        return playlistRepository.findPublicPlaylists();
    }

    @Override
    public Playlist updatePlaylist(String playlistId, String name, String description, boolean isPublic) {
        Playlist playlist = getPlaylist(playlistId);
        
        if (name != null && !name.trim().isEmpty()) {
            playlist.setName(name);
        }
        if (description != null) {
            playlist.setDescription(description);
        }
        playlist.setPublic(isPublic);
        
        return playlistRepository.save(playlist);
    }

    @Override
    public void addSongToPlaylist(String playlistId, String songId, String addedBy) {
        Playlist playlist = getPlaylist(playlistId);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found: " + songId));

        // Check if user can add (owner or collaborator)
        if (!canModifyPlaylist(playlist, addedBy)) {
            throw new UnauthorizedException("User not authorized to modify this playlist");
        }

        playlist.addTrack(songId, addedBy);
        playlistRepository.save(playlist);

        notifySongAdded(playlist, song);
    }

    @Override
    public void removeSongFromPlaylist(String playlistId, String songId) {
        Playlist playlist = getPlaylist(playlistId);
        Song song = songRepository.findById(songId).orElse(null);
        
        if (playlist.removeTrack(songId)) {
            playlistRepository.save(playlist);
            if (song != null) {
                notifySongRemoved(playlist, song);
            }
        }
    }

    @Override
    public void reorderPlaylistTrack(String playlistId, int fromIndex, int toIndex) {
        Playlist playlist = getPlaylist(playlistId);
        playlist.reorderTrack(fromIndex, toIndex);
        playlistRepository.save(playlist);
    }

    @Override
    public void deletePlaylist(String playlistId) {
        Playlist playlist = getPlaylist(playlistId);

        // Remove playlist from owner
        userRepository.findById(playlist.getOwnerId()).ifPresent(owner -> {
            owner.removePlaylist(playlistId);
            userRepository.save(owner);
        });

        playlistRepository.delete(playlistId);
        notifyPlaylistDeleted(playlist);
    }

    @Override
    public void followPlaylist(String playlistId, String userId) {
        Playlist playlist = getPlaylist(playlistId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!playlist.isPublic() && !playlist.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("Cannot follow a private playlist");
        }

        user.addPlaylist(playlistId);
        playlist.incrementFollowerCount();
        
        userRepository.save(user);
        playlistRepository.save(playlist);
    }

    @Override
    public void unfollowPlaylist(String playlistId, String userId) {
        Playlist playlist = getPlaylist(playlistId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.removePlaylist(playlistId);
        playlist.decrementFollowerCount();
        
        userRepository.save(user);
        playlistRepository.save(playlist);
    }

    private boolean canModifyPlaylist(Playlist playlist, String userId) {
        return playlist.getOwnerId().equals(userId) || 
               (playlist.isCollaborative() && playlist.isPublic());
    }

    private void notifyPlaylistCreated(Playlist playlist) {
        for (PlaylistObserver observer : observers) {
            observer.onPlaylistCreated(playlist);
        }
    }

    private void notifySongAdded(Playlist playlist, Song song) {
        for (PlaylistObserver observer : observers) {
            observer.onSongAddedToPlaylist(playlist, song);
        }
    }

    private void notifySongRemoved(Playlist playlist, Song song) {
        for (PlaylistObserver observer : observers) {
            observer.onSongRemovedFromPlaylist(playlist, song);
        }
    }

    private void notifyPlaylistDeleted(Playlist playlist) {
        for (PlaylistObserver observer : observers) {
            observer.onPlaylistDeleted(playlist);
        }
    }
}



