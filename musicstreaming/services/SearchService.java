package musicstreaming.services;

import musicstreaming.models.*;
import java.util.List;

/**
 * Service interface for search operations.
 */
public interface SearchService {
    
    /**
     * Search for songs by title.
     */
    List<Song> searchSongs(String query, int limit);
    
    /**
     * Search for artists by name.
     */
    List<Artist> searchArtists(String query, int limit);
    
    /**
     * Search for albums by title.
     */
    List<Album> searchAlbums(String query, int limit);
    
    /**
     * Search for playlists by name.
     */
    List<Playlist> searchPlaylists(String query, int limit);
    
    /**
     * Unified search across all types.
     */
    SearchResults searchAll(String query, int limitPerType);
    
    /**
     * Container for unified search results.
     */
    class SearchResults {
        private final List<Song> songs;
        private final List<Artist> artists;
        private final List<Album> albums;
        private final List<Playlist> playlists;

        public SearchResults(List<Song> songs, List<Artist> artists, 
                           List<Album> albums, List<Playlist> playlists) {
            this.songs = songs;
            this.artists = artists;
            this.albums = albums;
            this.playlists = playlists;
        }

        public List<Song> getSongs() { return songs; }
        public List<Artist> getArtists() { return artists; }
        public List<Album> getAlbums() { return albums; }
        public List<Playlist> getPlaylists() { return playlists; }
        
        public int getTotalCount() {
            return songs.size() + artists.size() + albums.size() + playlists.size();
        }
    }
}



