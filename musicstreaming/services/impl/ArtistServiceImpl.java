package musicstreaming.services.impl;

import musicstreaming.enums.Genre;
import musicstreaming.exceptions.MusicStreamingException;
import musicstreaming.models.Artist;
import musicstreaming.repositories.ArtistRepository;
import musicstreaming.services.ArtistService;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of ArtistService.
 */
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistServiceImpl(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public Artist createArtist(String name, String bio) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Artist name cannot be empty");
        }

        String artistId = UUID.randomUUID().toString();
        Artist artist = new Artist(artistId, name);
        artist.setBio(bio);
        return artistRepository.save(artist);
    }

    @Override
    public Artist getArtist(String artistId) {
        return artistRepository.findById(artistId)
                .orElseThrow(() -> new MusicStreamingException("Artist not found: " + artistId));
    }

    @Override
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    @Override
    public List<Artist> getArtistsByGenre(Genre genre) {
        return artistRepository.findByGenre(genre);
    }

    @Override
    public List<Artist> getTopArtists(int limit) {
        return artistRepository.findTopByMonthlyListeners(limit);
    }

    @Override
    public List<Artist> getVerifiedArtists() {
        return artistRepository.findVerifiedArtists();
    }

    @Override
    public Artist updateArtist(String artistId, String name, String bio) {
        Artist artist = getArtist(artistId);
        
        if (name != null && !name.trim().isEmpty()) {
            artist.setName(name);
        }
        if (bio != null) {
            artist.setBio(bio);
        }
        
        return artistRepository.save(artist);
    }

    @Override
    public void verifyArtist(String artistId) {
        Artist artist = getArtist(artistId);
        artist.setVerified(true);
        artistRepository.save(artist);
    }

    @Override
    public void addGenreToArtist(String artistId, Genre genre) {
        Artist artist = getArtist(artistId);
        artist.addGenre(genre);
        artistRepository.save(artist);
    }

    @Override
    public void deleteArtist(String artistId) {
        getArtist(artistId);
        artistRepository.delete(artistId);
    }
}



