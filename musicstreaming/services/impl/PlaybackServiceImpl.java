package musicstreaming.services.impl;

import musicstreaming.exceptions.PlaybackException;
import musicstreaming.exceptions.UserNotFoundException;
import musicstreaming.models.Album;
import musicstreaming.models.PlaybackSession;
import musicstreaming.models.Playlist;
import musicstreaming.models.Song;
import musicstreaming.models.User;
import musicstreaming.observers.PlaybackObserver;
import musicstreaming.repositories.*;
import musicstreaming.services.PlaybackService;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of PlaybackService with observer support.
 */
public class PlaybackServiceImpl implements PlaybackService {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final AlbumRepository albumRepository;
    private final Map<String, PlaybackSession> activeSessions = new ConcurrentHashMap<>();
    private final List<PlaybackObserver> observers = new ArrayList<>();

    public PlaybackServiceImpl(UserRepository userRepository,
                              SongRepository songRepository,
                              PlaylistRepository playlistRepository,
                              AlbumRepository albumRepository) {
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.playlistRepository = playlistRepository;
        this.albumRepository = albumRepository;
    }

    public void addObserver(PlaybackObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(PlaybackObserver observer) {
        observers.remove(observer);
    }

    @Override
    public PlaybackSession play(String userId, String songId) {
        User user = getUser(userId);
        Song song = getSong(songId);

        PlaybackSession session = getOrCreateSession(userId);
        
        // If there's a current song, notify it was skipped
        if (session.getCurrentSongId() != null && session.isPlaying()) {
            notifySongSkipped(user, getSong(session.getCurrentSongId()), session.getCurrentPositionSeconds());
        }

        session.play(songId);
        session.setQueue(Collections.singletonList(songId));
        
        notifySongStarted(user, song);
        return session;
    }

    @Override
    public PlaybackSession playPlaylist(String userId, String playlistId, int startIndex) {
        User user = getUser(userId);
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaybackException("Playlist not found: " + playlistId));

        List<String> songIds = playlist.getSongIds();
        if (songIds.isEmpty()) {
            throw new PlaybackException("Playlist is empty");
        }

        int index = Math.max(0, Math.min(startIndex, songIds.size() - 1));
        
        PlaybackSession session = getOrCreateSession(userId);
        session.setQueue(songIds);
        session.skipToIndex(index);

        Song song = getSong(session.getCurrentSongId());
        notifySongStarted(user, song);
        
        return session;
    }

    @Override
    public PlaybackSession playAlbum(String userId, String albumId) {
        User user = getUser(userId);
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new PlaybackException("Album not found: " + albumId));

        List<String> songIds = album.getSongIds();
        if (songIds.isEmpty()) {
            throw new PlaybackException("Album is empty");
        }

        PlaybackSession session = getOrCreateSession(userId);
        session.setQueue(songIds);
        session.skipToIndex(0);

        Song song = getSong(session.getCurrentSongId());
        notifySongStarted(user, song);
        
        return session;
    }

    @Override
    public void pause(String userId) {
        User user = getUser(userId);
        PlaybackSession session = getSession(userId);
        
        if (session == null || !session.isPlaying()) {
            throw new PlaybackException("Nothing is playing");
        }

        Song song = getSong(session.getCurrentSongId());
        session.pause();
        notifySongPaused(user, song, session.getCurrentPositionSeconds());
    }

    @Override
    public void resume(String userId) {
        User user = getUser(userId);
        PlaybackSession session = getSession(userId);
        
        if (session == null || session.getCurrentSongId() == null) {
            throw new PlaybackException("Nothing to resume");
        }

        session.resume();
        Song song = getSong(session.getCurrentSongId());
        notifySongStarted(user, song);
    }

    @Override
    public void stop(String userId) {
        PlaybackSession session = getSession(userId);
        if (session != null) {
            session.stop();
        }
    }

    @Override
    public String skipNext(String userId) {
        User user = getUser(userId);
        PlaybackSession session = getSession(userId);
        
        if (session == null) {
            throw new PlaybackException("No active session");
        }

        String currentSongId = session.getCurrentSongId();
        if (currentSongId != null) {
            notifySongSkipped(user, getSong(currentSongId), session.getCurrentPositionSeconds());
        }

        String nextSongId = session.skipToNext();
        if (nextSongId == null) {
            throw new PlaybackException("No more songs in queue");
        }

        notifySongStarted(user, getSong(nextSongId));
        return nextSongId;
    }

    @Override
    public String skipPrevious(String userId) {
        User user = getUser(userId);
        PlaybackSession session = getSession(userId);
        
        if (session == null) {
            throw new PlaybackException("No active session");
        }

        String previousSongId = session.skipToPrevious();
        if (previousSongId == null) {
            throw new PlaybackException("No previous song");
        }

        notifySongStarted(user, getSong(previousSongId));
        return previousSongId;
    }

    @Override
    public void seek(String userId, int positionSeconds) {
        PlaybackSession session = getSession(userId);
        if (session == null || session.getCurrentSongId() == null) {
            throw new PlaybackException("Nothing is playing");
        }
        session.seek(positionSeconds);
    }

    @Override
    public void setVolume(String userId, int volume) {
        PlaybackSession session = getOrCreateSession(userId);
        session.setVolume(volume);
    }

    @Override
    public void toggleShuffle(String userId) {
        PlaybackSession session = getOrCreateSession(userId);
        session.toggleShuffle();
    }

    @Override
    public void cycleRepeatMode(String userId) {
        PlaybackSession session = getOrCreateSession(userId);
        session.cycleRepeatMode();
    }

    @Override
    public void addToQueue(String userId, String songId) {
        getSong(songId); // Verify song exists
        PlaybackSession session = getOrCreateSession(userId);
        session.addToQueue(songId);
    }

    @Override
    public void addToQueueNext(String userId, String songId) {
        getSong(songId);
        PlaybackSession session = getOrCreateSession(userId);
        session.addToQueueNext(songId);
    }

    @Override
    public List<String> getQueue(String userId) {
        PlaybackSession session = getSession(userId);
        return session != null ? session.getQueue() : Collections.emptyList();
    }

    @Override
    public PlaybackSession getSession(String userId) {
        return activeSessions.get(userId);
    }

    private PlaybackSession getOrCreateSession(String userId) {
        return activeSessions.computeIfAbsent(userId, 
            id -> new PlaybackSession(UUID.randomUUID().toString(), id));
    }

    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private Song getSong(String songId) {
        return songRepository.findById(songId)
                .orElseThrow(() -> new PlaybackException("Song not found: " + songId));
    }

    private void notifySongStarted(User user, Song song) {
        for (PlaybackObserver observer : observers) {
            observer.onSongStarted(user, song);
        }
    }

    private void notifySongCompleted(User user, Song song) {
        for (PlaybackObserver observer : observers) {
            observer.onSongCompleted(user, song);
        }
    }

    private void notifySongPaused(User user, Song song, int position) {
        for (PlaybackObserver observer : observers) {
            observer.onSongPaused(user, song, position);
        }
    }

    private void notifySongSkipped(User user, Song song, int position) {
        for (PlaybackObserver observer : observers) {
            observer.onSongSkipped(user, song, position);
        }
    }
}



