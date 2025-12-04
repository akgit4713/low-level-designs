package musicstreaming.services.impl;

import musicstreaming.models.*;
import musicstreaming.strategies.search.*;
import musicstreaming.services.SearchService;
import java.util.List;

/**
 * Implementation of SearchService using strategy pattern.
 */
public class SearchServiceImpl implements SearchService {

    private final SearchStrategy<Song> songSearchStrategy;
    private final SearchStrategy<Artist> artistSearchStrategy;
    private final SearchStrategy<Album> albumSearchStrategy;
    private final SearchStrategy<Playlist> playlistSearchStrategy;

    public SearchServiceImpl(SearchStrategy<Song> songSearchStrategy,
                            SearchStrategy<Artist> artistSearchStrategy,
                            SearchStrategy<Album> albumSearchStrategy,
                            SearchStrategy<Playlist> playlistSearchStrategy) {
        this.songSearchStrategy = songSearchStrategy;
        this.artistSearchStrategy = artistSearchStrategy;
        this.albumSearchStrategy = albumSearchStrategy;
        this.playlistSearchStrategy = playlistSearchStrategy;
    }

    @Override
    public List<Song> searchSongs(String query, int limit) {
        return songSearchStrategy.search(query, limit);
    }

    @Override
    public List<Artist> searchArtists(String query, int limit) {
        return artistSearchStrategy.search(query, limit);
    }

    @Override
    public List<Album> searchAlbums(String query, int limit) {
        return albumSearchStrategy.search(query, limit);
    }

    @Override
    public List<Playlist> searchPlaylists(String query, int limit) {
        return playlistSearchStrategy.search(query, limit);
    }

    @Override
    public SearchResults searchAll(String query, int limitPerType) {
        // Execute all searches (could be parallelized for better performance)
        List<Song> songs = searchSongs(query, limitPerType);
        List<Artist> artists = searchArtists(query, limitPerType);
        List<Album> albums = searchAlbums(query, limitPerType);
        List<Playlist> playlists = searchPlaylists(query, limitPerType);

        return new SearchResults(songs, artists, albums, playlists);
    }
}



