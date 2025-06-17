package hp.cliofy.Model.Service.ArtistService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Track;

public interface IArtistService {
    void hydrateArtist(Artist artist);
    CompletableFuture<List<Album>> getArtistAlbums(Artist artist, String type);
    CompletableFuture<List<Track>> getArtistTopTracks(Artist artist);
}
