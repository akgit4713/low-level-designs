package musicstreaming.services.impl;

import musicstreaming.enums.Genre;
import musicstreaming.exceptions.SongNotFoundException;
import musicstreaming.models.Song;
import musicstreaming.repositories.SongRepository;
import musicstreaming.services.SongService;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of SongService.
 */
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    public SongServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public Song createSong(String title, String artistId, int durationSeconds, Genre genre) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Song title cannot be empty");
        }
        if (artistId == null || artistId.trim().isEmpty()) {
            throw new IllegalArgumentException("Artist ID cannot be empty");
        }
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        String songId = UUID.randomUUID().toString();
        Song song = new Song(songId, title, artistId, durationSeconds);
        song.setGenre(genre);
        return songRepository.save(song);
    }

    @Override
    public Song getSong(String songId) {
        return songRepository.findById(songId)
                .orElseThrow(() -> new SongNotFoundException(songId));
    }

    @Override
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    @Override
    public List<Song> getSongsByArtist(String artistId) {
        return songRepository.findByArtistId(artistId);
    }

    @Override
    public List<Song> getSongsByAlbum(String albumId) {
        return songRepository.findByAlbumId(albumId);
    }

    @Override
    public List<Song> getSongsByGenre(Genre genre) {
        return songRepository.findByGenre(genre);
    }

    @Override
    public List<Song> getTopSongs(int limit) {
        return songRepository.findTopByPlayCount(limit);
    }

    @Override
    public Song updateSong(String songId, String title, Genre genre) {
        Song song = getSong(songId);
        
        if (title != null && !title.trim().isEmpty()) {
            song.setTitle(title);
        }
        if (genre != null) {
            song.setGenre(genre);
        }
        
        return songRepository.save(song);
    }

    @Override
    public void deleteSong(String songId) {
        getSong(songId); // Verify it exists
        songRepository.delete(songId);
    }
}



