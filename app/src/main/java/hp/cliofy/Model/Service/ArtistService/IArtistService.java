package hp.cliofy.Model.Service.ArtistService;

import java.util.List;

import hp.cliofy.Model.Item.Album;
import hp.cliofy.Model.Item.Artist;
import hp.cliofy.Model.Item.Track;

public interface IArtistService {
    void hydrateArtist(Artist artist);
    List<Album> getArtistAlbums(Artist artist, String type);
    List<Track> getArtistTopTracks(Artist artist);
}
